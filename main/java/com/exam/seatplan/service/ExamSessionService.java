package com.exam.seatplan.service;

import com.exam.seatplan.dto.ExamSessionDto;
import com.exam.seatplan.model.ExamSession;
import com.exam.seatplan.repo.ExamSessionRepository;
import com.exam.seatplan.repo.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamSessionService {

    private final ExamSessionRepository examSessionRepository;
    private final SeatRepository seatRepository;

    public ExamSessionService(ExamSessionRepository examSessionRepository, SeatRepository seatRepository) {
        this.examSessionRepository = examSessionRepository;
        this.seatRepository = seatRepository;
    }

    public List<ExamSessionDto> getAllSessions() {
        return examSessionRepository.findAllByOrderByExamDateDesc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ExamSessionDto getSessionById(Long id) {
        ExamSession session = examSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exam Session not found with id: " + id));
        return toDto(session);
    }

    public ExamSession getSessionEntity(Long id) {
        return examSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exam Session not found with id: " + id));
    }

    @Transactional
    public ExamSessionDto createSession(ExamSession session) {
        if (session.getSessionName() == null || session.getSessionName().trim().isEmpty()) {
            throw new IllegalArgumentException("Session name cannot be empty.");
        }
        if (session.getExamDate() == null) {
            throw new IllegalArgumentException("Exam date must be specified.");
        }
        if (session.getStatus() == null || session.getStatus().trim().isEmpty()) {
            session.setStatus("SCHEDULED");
        }
        ExamSession saved = examSessionRepository.save(session);
        return toDto(saved);
    }

    @Transactional
    public ExamSessionDto updateSession(Long id, ExamSession sessionDetails) {
        ExamSession session = examSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exam Session not found with id: " + id));

        session.setSessionName(sessionDetails.getSessionName());
        session.setSubjectCode(sessionDetails.getSubjectCode());
        session.setSubjectName(sessionDetails.getSubjectName());
        session.setExamDate(sessionDetails.getExamDate());
        session.setSessionTime(sessionDetails.getSessionTime());
        if (sessionDetails.getStatus() != null) {
            session.setStatus(sessionDetails.getStatus());
        }

        ExamSession updated = examSessionRepository.save(session);
        return toDto(updated);
    }

    @Transactional
    public void deleteSession(Long id) {
        if (!examSessionRepository.existsById(id)) {
            throw new IllegalArgumentException("Exam Session not found with id: " + id);
        }
        // First delete allocated seats for this session
        seatRepository.deleteByExamSessionId(id);
        examSessionRepository.deleteById(id);
    }

    public ExamSessionDto toDto(ExamSession session) {
        int allocatedCount = (int) seatRepository.countByExamSessionIdAndStudentIsNotNull(session.getId());
        return new ExamSessionDto(
                session.getId(),
                session.getSessionName(),
                session.getSubjectCode(),
                session.getSubjectName(),
                session.getExamDate(),
                session.getSessionTime(),
                session.getStatus(),
                allocatedCount
        );
    }
}
