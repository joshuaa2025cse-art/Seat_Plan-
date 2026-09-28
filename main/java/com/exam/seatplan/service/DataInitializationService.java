package com.exam.seatplan.service;

import com.exam.seatplan.dto.ArrangementRequestDto;
import com.exam.seatplan.model.ExamSession;
import com.exam.seatplan.model.Hall;
import com.exam.seatplan.model.Student;
import com.exam.seatplan.repo.ExamSessionRepository;
import com.exam.seatplan.repo.HallRepository;
import com.exam.seatplan.repo.SeatRepository;
import com.exam.seatplan.repo.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DataInitializationService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializationService.class);

    private final HallRepository hallRepository;
    private final StudentRepository studentRepository;
    private final ExamSessionRepository examSessionRepository;
    private final SeatRepository seatRepository;
    private final SeatingArrangementService seatingArrangementService;

    public DataInitializationService(HallRepository hallRepository,
                                     StudentRepository studentRepository,
                                     ExamSessionRepository examSessionRepository,
                                     SeatRepository seatRepository,
                                     SeatingArrangementService seatingArrangementService) {
        this.hallRepository = hallRepository;
        this.studentRepository = studentRepository;
        this.examSessionRepository = examSessionRepository;
        this.seatRepository = seatRepository;
        this.seatingArrangementService = seatingArrangementService;
    }

    @Override
    public void run(String... args) {
        try {
            if (hallRepository.count() == 0 && studentRepository.count() == 0) {
                log.info("Database is empty. Seeding initial demo data for SeatPlan...");
                seedData();
                log.info("SeatPlan initial demo data seeded successfully!");
            }
        } catch (Exception e) {
            log.warn("Database initialization skipped or deferred: {}", e.getMessage());
        }
    }

    @Transactional
    public void resetAndSeedSampleData() {
        seatRepository.deleteAllInBatch();
        seatRepository.flush();
        examSessionRepository.deleteAllInBatch();
        examSessionRepository.flush();
        studentRepository.deleteAllInBatch();
        studentRepository.flush();
        hallRepository.deleteAllInBatch();
        hallRepository.flush();
        seedData();
    }

    private void seedData() {
        // 1. Seed Examination Halls with declared capacities
        Hall hall1 = new Hall("LH-101", "Lecture Hall A - Main Block", 5, 6); // 30 capacity
        Hall hall2 = new Hall("LH-102", "Lecture Hall B - Science Block", 4, 6); // 24 capacity
        Hall hall3 = new Hall("AUDI-1", "Central Auditorium Hall", 6, 6); // 36 capacity

        hallRepository.saveAll(Arrays.asList(hall1, hall2, hall3));

        // 2. Seed Students appearing for exams with Class/Branch and Section
        List<Student> students = new ArrayList<>();

        // Computer Science - Section A (6 students)
        String[] csANames = { "Aarav Sharma", "Diya Patel", "Rohan Mehta", "Ananya Iyer", "Kabir Verma", "Pooja Reddy" };
        for (int i = 0; i < csANames.length; i++) {
            students.add(new Student(
                String.format("2024CS-A-%02d", i + 1),
                csANames[i],
                "Computer Science",
                "A",
                4,
                csANames[i].toLowerCase().replace(" ", ".") + "@university.edu"
            ));
        }

        // Computer Science - Section B (6 students)
        String[] csBNames = { "Vivaan Joshi", "Sneha Nair", "Aditya Kulkarni", "Ishita Gupta", "Rahul Chawla", "Tanvi Rao" };
        for (int i = 0; i < csBNames.length; i++) {
            students.add(new Student(
                String.format("2024CS-B-%02d", i + 1),
                csBNames[i],
                "Computer Science",
                "B",
                4,
                csBNames[i].toLowerCase().replace(" ", ".") + "@university.edu"
            ));
        }

        // Electronics & Communication - Section A (6 students)
        String[] ecANames = { "Karan Malhotra", "Rhea Sengupta", "Siddharth Das", "Meera Pillai", "Arjun Kapoor", "Bhavna Mishra" };
        for (int i = 0; i < ecANames.length; i++) {
            students.add(new Student(
                String.format("2024EC-A-%02d", i + 1),
                ecANames[i],
                "Electronics & Communication",
                "A",
                4,
                ecANames[i].toLowerCase().replace(" ", ".") + "@university.edu"
            ));
        }

        // Electronics & Communication - Section B (6 students)
        String[] ecBNames = { "Nikhil Rao", "Shreya Bhat", "Vikram Chauhan", "Lavanya Menon", "Gaurav Pandey", "Divya Deshmukh" };
        for (int i = 0; i < ecBNames.length; i++) {
            students.add(new Student(
                String.format("2024EC-B-%02d", i + 1),
                ecBNames[i],
                "Electronics & Communication",
                "B",
                4,
                ecBNames[i].toLowerCase().replace(" ", ".") + "@university.edu"
            ));
        }

        // Mechanical Engineering - Section A (8 students)
        String[] meNames = { "Devansh Bhatt", "Ritu Saxena", "Manish Tiwari", "Kavita Yadav", "Harsh Vardhan", "Neha Agarwal", "Pranav Hegde", "Priyanka Soni" };
        for (int i = 0; i < meNames.length; i++) {
            students.add(new Student(
                String.format("2024ME-A-%02d", i + 1),
                meNames[i],
                "Mechanical Engineering",
                "A",
                4,
                meNames[i].toLowerCase().replace(" ", ".") + "@university.edu"
            ));
        }

        // Civil Engineering - Section A (6 students)
        String[] ceNames = { "Alok Trivedi", "Komal Sinha", "Varun Nambiar", "Swati Mukherjee", "Chirag Solanki", "Archana Dubey" };
        for (int i = 0; i < ceNames.length; i++) {
            students.add(new Student(
                String.format("2024CE-A-%02d", i + 1),
                ceNames[i],
                "Civil Engineering",
                "A",
                4,
                ceNames[i].toLowerCase().replace(" ", ".") + "@university.edu"
            ));
        }

        studentRepository.saveAll(students);

        // 3. Seed Exam Sessions
        ExamSession session1 = new ExamSession(
            "Mid-Term Examination Fall 2026",
            "CS301 / EC301",
            "Data Structures & Circuit Analysis",
            LocalDate.now().plusDays(2),
            "09:30 AM - 12:30 PM"
        );

        ExamSession session2 = new ExamSession(
            "Final Semester Examination 2026",
            "EN201",
            "Engineering Mathematics & Statistics",
            LocalDate.now().plusDays(6),
            "02:00 PM - 05:00 PM"
        );

        examSessionRepository.saveAll(Arrays.asList(session1, session2));

        // 4. Auto-generate seating arrangement for session 1 enforcing anti-cheating & capacity limits
        ArrangementRequestDto autoReq = new ArrangementRequestDto();
        autoReq.setExamSessionId(session1.getId());
        autoReq.setHallIds(Arrays.asList(hall1.getId(), hall2.getId()));
        seatingArrangementService.generateArrangement(autoReq);
    }
}
