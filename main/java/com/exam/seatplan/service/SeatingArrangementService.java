package com.exam.seatplan.service;

import com.exam.seatplan.dto.ArrangementRequestDto;
import com.exam.seatplan.dto.ArrangementSummaryDto;
import com.exam.seatplan.dto.HallDto;
import com.exam.seatplan.model.ExamSession;
import com.exam.seatplan.model.Hall;
import com.exam.seatplan.model.Seat;
import com.exam.seatplan.model.Student;
import com.exam.seatplan.repo.ExamSessionRepository;
import com.exam.seatplan.repo.HallRepository;
import com.exam.seatplan.repo.SeatRepository;
import com.exam.seatplan.repo.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeatingArrangementService {

    private final HallRepository hallRepository;
    private final StudentRepository studentRepository;
    private final ExamSessionRepository examSessionRepository;
    private final SeatRepository seatRepository;
    private final HallService hallService;

    public SeatingArrangementService(HallRepository hallRepository,
                                     StudentRepository studentRepository,
                                     ExamSessionRepository examSessionRepository,
                                     SeatRepository seatRepository,
                                     HallService hallService) {
        this.hallRepository = hallRepository;
        this.studentRepository = studentRepository;
        this.examSessionRepository = examSessionRepository;
        this.seatRepository = seatRepository;
        this.hallService = hallService;
    }

    @Transactional
    public ArrangementSummaryDto generateArrangement(ArrangementRequestDto request) {
        if (request.getExamSessionId() == null) {
            throw new IllegalArgumentException("Exam Session ID is required.");
        }

        ExamSession session = examSessionRepository.findById(request.getExamSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Exam Session not found with id: " + request.getExamSessionId()));

        // 1. Fetch Halls and calculate total declared capacity
        List<Hall> halls;
        if (request.getHallIds() != null && !request.getHallIds().isEmpty()) {
            halls = hallRepository.findAllById(request.getHallIds()).stream()
                    .filter(Hall::isActive)
                    .sorted(Comparator.comparing(Hall::getHallNumber))
                    .collect(Collectors.toList());
        } else {
            halls = hallRepository.findByIsActiveTrueOrderByHallNumberAsc();
        }

        if (halls.isEmpty()) {
            throw new IllegalArgumentException("No active examination halls selected or available.");
        }

        int totalCapacity = halls.stream().mapToInt(Hall::getCapacity).sum();

        // 2. Fetch Students appearing for the exam
        List<Student> students;
        if (request.getDepartments() != null && !request.getDepartments().isEmpty()) {
            students = studentRepository.findByDepartmentInOrderByDepartmentAscSectionAscRollNumberAsc(request.getDepartments());
        } else {
            students = studentRepository.findAllByOrderByDepartmentAscSectionAscRollNumberAsc();
        }

        if (students.isEmpty()) {
            throw new IllegalArgumentException("No eligible students found to allocate.");
        }

        int totalStudents = students.size();

        // 3. Clear existing allocations for this session
        seatRepository.deleteByExamSessionId(session.getId());

        // 4. Candidate Pool for allocation
        List<Student> candidatePool = new ArrayList<>(students);

        // 5. Allocate Seats per Hall strictly enforcing:
        // Rule 1: No student may be allocated a seat beyond the hall's declared capacity.
        // Rule 2: Allocation must avoid placing two students from the same section in adjacent seats whenever alternatives exist.
        List<Seat> allocatedSeats = new ArrayList<>();
        int totalAllocated = 0;

        for (Hall hall : halls) {
            int rows = hall.getTotalRows();
            int cols = hall.getTotalColumns();
            int hallDeclaredCapacity = hall.getCapacity();

            // 2D grid of placed students in this hall (1-indexed for rows and cols)
            Student[][] grid = new Student[rows + 1][cols + 1];
            int hallStudentsAllocated = 0;

            for (int r = 1; r <= rows; r++) {
                for (int c = 1; c <= cols; c++) {
                    Student assignedStudent = null;
                    boolean isOccupied = false;

                    // Enforce Rule 1: Check hall declared capacity & student availability
                    if (!candidatePool.isEmpty() && hallStudentsAllocated < hallDeclaredCapacity) {
                        Student leftNeighbor = (c > 1) ? grid[r][c - 1] : null;
                        Student topNeighbor = (r > 1) ? grid[r - 1][c] : null;

                        // Enforce Rule 2: Select candidate avoiding adjacent same section
                        assignedStudent = selectBestCandidate(candidatePool, leftNeighbor, topNeighbor);
                        if (assignedStudent != null) {
                            grid[r][c] = assignedStudent;
                            isOccupied = true;
                            hallStudentsAllocated++;
                            totalAllocated++;
                        }
                    }

                    String seatNumber = String.format("%s-R%dC%d", hall.getHallNumber(), r, c);
                    Seat seat = new Seat(seatNumber, r, c, hall, session, assignedStudent, isOccupied);
                    allocatedSeats.add(seat);
                }
            }
        }

        // 6. Batch save all allocated seats
        seatRepository.saveAll(allocatedSeats);

        // 7. Update ExamSession status
        session.setStatus("SEATS_ALLOCATED");
        examSessionRepository.save(session);

        int totalUnallocated = Math.max(0, totalStudents - totalAllocated);

        String message;
        if (totalUnallocated > 0) {
            message = String.format("Allocated %d students across %d halls. Reached declared capacity (%d students unallocated).",
                    totalAllocated, halls.size(), totalUnallocated);
        } else {
            message = String.format("Successfully allocated all %d students across %d halls with adjacent section separation enforced.",
                    totalAllocated, halls.size());
        }

        List<HallDto> hallDtos = halls.stream().map(hallService::toDto).collect(Collectors.toList());

        return new ArrangementSummaryDto(
                true,
                message,
                session.getId(),
                session.getSessionName(),
                totalStudents,
                totalAllocated,
                totalUnallocated,
                halls.size(),
                totalCapacity,
                "ADJACENT_SECTION_AVOIDANCE",
                hallDtos
        );
    }

    /**
     * Enforces Business Rule 2:
     * Allocation must avoid placing two students from the same section in adjacent seats
     * whenever alternatives exist. Also maximizes class/department separation.
     */
    private Student selectBestCandidate(List<Student> pool, Student leftNeighbor, Student topNeighbor) {
        if (pool.isEmpty()) return null;

        // Tier 1 Priority: Differs from BOTH neighbors in BOTH Section AND Department/Class
        for (int i = 0; i < pool.size(); i++) {
            Student cand = pool.get(i);
            if (differsSection(cand, leftNeighbor) && differsDepartment(cand, leftNeighbor)
                    && differsSection(cand, topNeighbor) && differsDepartment(cand, topNeighbor)) {
                return pool.remove(i);
            }
        }

        // Tier 2 Priority: Differs from BOTH neighbors in Section (Strict Rule 2)
        for (int i = 0; i < pool.size(); i++) {
            Student cand = pool.get(i);
            if (differsSection(cand, leftNeighbor) && differsSection(cand, topNeighbor)) {
                return pool.remove(i);
            }
        }

        // Tier 3 Priority: Differs from BOTH neighbors in Class/Section compound ("CSE - Sec A")
        for (int i = 0; i < pool.size(); i++) {
            Student cand = pool.get(i);
            if (differsClassSection(cand, leftNeighbor) && differsClassSection(cand, topNeighbor)) {
                return pool.remove(i);
            }
        }

        // Tier 4 Priority: Differs from BOTH neighbors in Department/Class
        for (int i = 0; i < pool.size(); i++) {
            Student cand = pool.get(i);
            if (differsDepartment(cand, leftNeighbor) && differsDepartment(cand, topNeighbor)) {
                return pool.remove(i);
            }
        }

        // Tier 5 Priority: Differs from immediate horizontal (Left) neighbor in Section
        if (leftNeighbor != null) {
            for (int i = 0; i < pool.size(); i++) {
                Student cand = pool.get(i);
                if (differsSection(cand, leftNeighbor)) {
                    return pool.remove(i);
                }
            }
        }

        // Tier 6 Priority: Differs from immediate vertical (Top) neighbor in Section
        if (topNeighbor != null) {
            for (int i = 0; i < pool.size(); i++) {
                Student cand = pool.get(i);
                if (differsSection(cand, topNeighbor)) {
                    return pool.remove(i);
                }
            }
        }

        // Tier 7 Priority: Differs from Left neighbor in ClassSection
        if (leftNeighbor != null) {
            for (int i = 0; i < pool.size(); i++) {
                Student cand = pool.get(i);
                if (differsClassSection(cand, leftNeighbor)) {
                    return pool.remove(i);
                }
            }
        }

        // Tier 8: Alternatives exhausted (all remaining students belong to the same section/class)
        return pool.remove(0);
    }

    private boolean differsSection(Student cand, Student neighbor) {
        if (neighbor == null) return true;
        if (cand.getSection() == null || neighbor.getSection() == null) {
            return differsClassSection(cand, neighbor);
        }
        return !cand.getSection().trim().equalsIgnoreCase(neighbor.getSection().trim());
    }

    private boolean differsDepartment(Student cand, Student neighbor) {
        if (neighbor == null) return true;
        if (cand.getDepartment() == null || neighbor.getDepartment() == null) return true;
        return !cand.getDepartment().trim().equalsIgnoreCase(neighbor.getDepartment().trim());
    }

    private boolean differsClassSection(Student cand, Student neighbor) {
        if (neighbor == null) return true;
        String s1 = cand.getClassSection();
        String s2 = neighbor.getClassSection();
        if (s1 == null || s2 == null) return true;
        return !s1.trim().equalsIgnoreCase(s2.trim());
    }
}
