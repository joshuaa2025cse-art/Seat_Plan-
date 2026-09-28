package com.exam.seatplan.service;

import com.exam.seatplan.dto.StudentDto;
import com.exam.seatplan.model.Student;
import com.exam.seatplan.repo.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<StudentDto> getAllStudents(String department, String query) {
        List<Student> students;
        if (query != null && !query.trim().isEmpty()) {
            students = studentRepository.searchStudents(query.trim());
        } else if (department != null && !department.trim().isEmpty()) {
            students = studentRepository.findByDepartmentOrderByRollNumberAsc(department.trim());
        } else {
            students = studentRepository.findAllByOrderByDepartmentAscSectionAscRollNumberAsc();
        }
        return students.stream().map(this::toDto).collect(Collectors.toList());
    }

    public StudentDto getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + id));
        return toDto(student);
    }

    public Student getStudentEntity(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + id));
    }

    public List<String> getDistinctDepartments() {
        return studentRepository.findDistinctDepartments();
    }

    public List<String> getDistinctSections() {
        return studentRepository.findDistinctSections();
    }

    @Transactional
    public StudentDto createStudent(Student student) {
        if (student.getRollNumber() == null || student.getRollNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Roll number cannot be empty.");
        }
        String roll = student.getRollNumber().trim().toUpperCase();
        if (studentRepository.existsByRollNumber(roll)) {
            throw new IllegalArgumentException("Roll number '" + roll + "' already exists.");
        }
        student.setRollNumber(roll);
        if (student.getSection() == null || student.getSection().trim().isEmpty()) {
            student.setSection("A");
        } else {
            student.setSection(student.getSection().trim().toUpperCase());
        }
        Student saved = studentRepository.save(student);
        return toDto(saved);
    }

    @Transactional
    public List<StudentDto> bulkCreateStudents(List<Student> students) {
        List<StudentDto> savedList = new ArrayList<>();
        for (Student s : students) {
            if (s.getRollNumber() != null && !s.getRollNumber().trim().isEmpty()) {
                String roll = s.getRollNumber().trim().toUpperCase();
                if (!studentRepository.existsByRollNumber(roll)) {
                    s.setRollNumber(roll);
                    if (s.getSection() == null || s.getSection().trim().isEmpty()) {
                        s.setSection("A");
                    } else {
                        s.setSection(s.getSection().trim().toUpperCase());
                    }
                    savedList.add(toDto(studentRepository.save(s)));
                }
            }
        }
        return savedList;
    }

    @Transactional
    public StudentDto updateStudent(Long id, Student studentDetails) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + id));

        String newRoll = studentDetails.getRollNumber() != null ? studentDetails.getRollNumber().trim().toUpperCase() : student.getRollNumber();
        if (!student.getRollNumber().equalsIgnoreCase(newRoll) && studentRepository.existsByRollNumber(newRoll)) {
            throw new IllegalArgumentException("Roll number '" + newRoll + "' already exists.");
        }

        student.setRollNumber(newRoll);
        student.setName(studentDetails.getName());
        student.setDepartment(studentDetails.getDepartment());
        if (studentDetails.getSection() != null) {
            student.setSection(studentDetails.getSection().trim().toUpperCase());
        }
        student.setSemester(studentDetails.getSemester());
        student.setEmail(studentDetails.getEmail());

        Student updated = studentRepository.save(student);
        return toDto(updated);
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new IllegalArgumentException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    public StudentDto toDto(Student student) {
        return new StudentDto(
                student.getId(),
                student.getRollNumber(),
                student.getName(),
                student.getDepartment(),
                student.getSection(),
                student.getClassSection(),
                student.getSemester(),
                student.getEmail()
        );
    }
}
