package com.exam.seatplan.repo;

import com.exam.seatplan.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRollNumber(String rollNumber);
    boolean existsByRollNumber(String rollNumber);

    List<Student> findByDepartmentOrderByRollNumberAsc(String department);
    List<Student> findByDepartmentAndSectionOrderByRollNumberAsc(String department, String section);
    List<Student> findAllByOrderByDepartmentAscSectionAscRollNumberAsc();

    @Query("SELECT DISTINCT s.department FROM Student s ORDER BY s.department")
    List<String> findDistinctDepartments();

    @Query("SELECT DISTINCT s.section FROM Student s ORDER BY s.section")
    List<String> findDistinctSections();

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.department) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.section) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY s.department ASC, s.section ASC, s.rollNumber ASC")
    List<Student> searchStudents(@Param("query") String query);

    List<Student> findByDepartmentInOrderByDepartmentAscSectionAscRollNumberAsc(List<String> departments);
}
