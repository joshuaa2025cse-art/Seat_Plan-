package com.exam.seatplan.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "seats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"hall_id", "exam_session_id", "seat_row", "seat_col"})
})
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "seat_row", nullable = false)
    private int rowNum;

    @Column(name = "seat_col", nullable = false)
    private int colNum;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hall_id", nullable = false)
    @JsonIgnoreProperties({"seats", "hibernateLazyInitializer", "handler"})
    private Hall hall;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    @JsonIgnoreProperties({"seats", "hibernateLazyInitializer", "handler"})
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exam_session_id")
    @JsonIgnoreProperties({"seats", "hibernateLazyInitializer", "handler"})
    private ExamSession examSession;

    @Column(name = "is_occupied", nullable = false)
    private boolean isOccupied = false;

    @Column(name = "remarks")
    private String remarks;

    public Seat() {
    }

    public Seat(String seatNumber, int rowNum, int colNum, Hall hall, ExamSession examSession, Student student, boolean isOccupied) {
        this.seatNumber = seatNumber;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.hall = hall;
        this.examSession = examSession;
        this.student = student;
        this.isOccupied = isOccupied;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public int getColNum() {
        return colNum;
    }

    public void setColNum(int colNum) {
        this.colNum = colNum;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
        this.isOccupied = (student != null);
    }

    public ExamSession getExamSession() {
        return examSession;
    }

    public void setExamSession(ExamSession examSession) {
        this.examSession = examSession;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", seatNumber='" + seatNumber + '\'' +
                ", rowNum=" + rowNum +
                ", colNum=" + colNum +
                ", isOccupied=" + isOccupied +
                '}';
    }
}
