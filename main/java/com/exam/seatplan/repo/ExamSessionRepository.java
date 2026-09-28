package com.exam.seatplan.repo;

import com.exam.seatplan.model.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {
    List<ExamSession> findAllByOrderByExamDateDesc();
    List<ExamSession> findByStatus(String status);
}
