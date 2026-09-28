package com.exam.seatplan.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "roll_number", nullable = false, unique = true)
    private String rollNumber;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "department", nullable = false)
    private String department; // Class / Branch (e.g. "Computer Science", "CSE")

    @Column(name = "section", nullable = false)
    private String section = "A"; // Section (e.g. "A", "B", "C")

    @Column(name = "semester", nullable = false)
    private int semester = 1;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "student")
    @JsonIgnore
    private List<Seat> seats = new ArrayList<>();

    public Student() {
    }

    public Student(String rollNumber, String name, String department, String section, int semester, String email) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.department = department;
        this.section = section != null ? section.trim().toUpperCase() : "A";
        this.semester = semester;
        this.email = email;
    }

    public Student(String rollNumber, String name, String department, int semester, String email) {
        this(rollNumber, name, department, "A", semester, email);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section != null ? section.trim().toUpperCase() : "A";
    }

    public String getClassSection() {
        if (department == null || department.isEmpty()) return section != null ? section : "";
        if (section == null || section.isEmpty()) return department;
        return department + " - Sec " + section;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", rollNumber='" + rollNumber + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", section='" + section + '\'' +
                ", semester=" + semester +
                ", email='" + email + '\'' +
                '}';
    }
}
