package com.exam.seatplan.service;

import com.exam.seatplan.dto.SeatDto;
import com.exam.seatplan.dto.SeatSearchResultDto;
import com.exam.seatplan.model.Seat;
import com.exam.seatplan.repo.SeatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public List<SeatDto> getSeatsByHallAndSession(Long hallId, Long sessionId) {
        return seatRepository.findByHallIdAndExamSessionIdOrderByRowNumAscColNumAsc(hallId, sessionId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SeatDto> getAllocationsBySession(Long sessionId) {
        return seatRepository.findByExamSessionIdOrderByHallHallNumberAscRowNumAscColNumAsc(sessionId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SeatSearchResultDto> searchByRollNumber(String rollNumber) {
        String trimmed = rollNumber != null ? rollNumber.trim() : "";
        List<Seat> seats = seatRepository.findAllByStudentRollNumber(trimmed);
        return seats.stream().map(this::toSearchResultDto).collect(Collectors.toList());
    }

    public List<SeatSearchResultDto> searchAllocations(String query) {
        String trimmed = query != null ? query.trim() : "";
        List<Seat> seats = seatRepository.searchAllocatedSeats(trimmed);
        return seats.stream().map(this::toSearchResultDto).collect(Collectors.toList());
    }

    public SeatDto toDto(Seat seat) {
        String section = seat.getStudent() != null ? seat.getStudent().getSection() : null;
        String classSection = seat.getStudent() != null ? seat.getStudent().getClassSection() : null;

        return new SeatDto(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getRowNum(),
                seat.getColNum(),
                seat.getHall() != null ? seat.getHall().getId() : null,
                seat.getHall() != null ? seat.getHall().getHallNumber() : null,
                seat.getHall() != null ? seat.getHall().getName() : null,
                seat.getExamSession() != null ? seat.getExamSession().getId() : null,
                seat.getExamSession() != null ? seat.getExamSession().getSessionName() : null,
                seat.getStudent() != null ? seat.getStudent().getId() : null,
                seat.getStudent() != null ? seat.getStudent().getRollNumber() : null,
                seat.getStudent() != null ? seat.getStudent().getName() : null,
                seat.getStudent() != null ? seat.getStudent().getDepartment() : null,
                section,
                classSection,
                seat.isOccupied(),
                seat.getRemarks()
        );
    }

    public SeatSearchResultDto toSearchResultDto(Seat seat) {
        String section = seat.getStudent() != null ? seat.getStudent().getSection() : "";
        String classSection = seat.getStudent() != null ? seat.getStudent().getClassSection() : "";

        return new SeatSearchResultDto(
                seat.getStudent() != null ? seat.getStudent().getRollNumber() : "",
                seat.getStudent() != null ? seat.getStudent().getName() : "",
                seat.getStudent() != null ? seat.getStudent().getDepartment() : "",
                section,
                classSection,
                seat.getStudent() != null ? seat.getStudent().getSemester() : 1,
                seat.getHall() != null ? seat.getHall().getHallNumber() : "",
                seat.getHall() != null ? seat.getHall().getName() : "",
                seat.getSeatNumber(),
                seat.getRowNum(),
                seat.getColNum(),
                seat.getExamSession() != null ? seat.getExamSession().getId() : null,
                seat.getExamSession() != null ? seat.getExamSession().getSessionName() : "",
                seat.getExamSession() != null ? seat.getExamSession().getSubjectCode() : "",
                seat.getExamSession() != null ? seat.getExamSession().getSubjectName() : "",
                seat.getExamSession() != null ? seat.getExamSession().getExamDate() : null,
                seat.getExamSession() != null ? seat.getExamSession().getSessionTime() : ""
        );
    }
}
