package com.exam.seatplan.repo;

import com.exam.seatplan.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByHallIdAndExamSessionIdOrderByRowNumAscColNumAsc(Long hallId, Long examSessionId);

    List<Seat> findByExamSessionIdOrderByHallHallNumberAscRowNumAscColNumAsc(Long examSessionId);

    long countByExamSessionIdAndStudentIsNotNull(Long examSessionId);

    long countByHallIdAndExamSessionIdAndStudentIsNotNull(Long hallId, Long examSessionId);

    @Query("SELECT s FROM Seat s WHERE s.student.rollNumber = :rollNumber ORDER BY s.examSession.examDate DESC")
    List<Seat> findAllByStudentRollNumber(@Param("rollNumber") String rollNumber);

    @Query("SELECT s FROM Seat s WHERE s.examSession.id = :sessionId AND s.student.rollNumber = :rollNumber")
    Optional<Seat> findBySessionIdAndRollNumber(@Param("sessionId") Long sessionId, @Param("rollNumber") String rollNumber);

    @Query("SELECT s FROM Seat s WHERE s.student IS NOT NULL AND (" +
           "LOWER(s.student.rollNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.student.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.seatNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.hall.hallNumber) LIKE LOWER(CONCAT('%', :query, '%'))" +
           ") ORDER BY s.examSession.examDate DESC, s.hall.hallNumber ASC")
    List<Seat> searchAllocatedSeats(@Param("query") String query);

    @Modifying
    @Transactional
    @Query("DELETE FROM Seat s WHERE s.examSession.id = :sessionId")
    void deleteByExamSessionId(@Param("sessionId") Long sessionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Seat s WHERE s.hall.id = :hallId AND s.examSession.id = :sessionId")
    void deleteByHallIdAndExamSessionId(@Param("hallId") Long hallId, @Param("sessionId") Long sessionId);
}
