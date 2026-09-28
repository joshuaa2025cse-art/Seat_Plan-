package com.exam.seatplan.repo;

import com.exam.seatplan.model.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HallRepository extends JpaRepository<Hall, Long> {
    Optional<Hall> findByHallNumber(String hallNumber);
    boolean existsByHallNumber(String hallNumber);
    List<Hall> findByIsActiveTrueOrderByHallNumberAsc();
}
