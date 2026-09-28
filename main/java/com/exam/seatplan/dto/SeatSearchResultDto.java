package com.exam.seatplan.dto;

import java.time.LocalDate;

public class SeatSearchResultDto {
    private String studentRollNumber;
    private String studentName;
    private String studentDepartment;
    private String studentSection;
    private String classSection;
    private int semester;
    private String hallNumber;
    private String hallName;
    private String seatNumber;
    private int rowNum;
    private int colNum;
    private Long examSessionId;
    private String sessionName;
    private String subjectCode;
    private String subjectName;
    private LocalDate examDate;
    private String sessionTime;

    public SeatSearchResultDto() {
    }

    public SeatSearchResultDto(String studentRollNumber, String studentName, String studentDepartment,
                               String studentSection, String classSection, int semester,
                               String hallNumber, String hallName, String seatNumber, int rowNum, int colNum,
                               Long examSessionId, String sessionName, String subjectCode, String subjectName,
                               LocalDate examDate, String sessionTime) {
        this.studentRollNumber = studentRollNumber;
        this.studentName = studentName;
        this.studentDepartment = studentDepartment;
        this.studentSection = studentSection;
        this.classSection = classSection;
        this.semester = semester;
        this.hallNumber = hallNumber;
        this.hallName = hallName;
        this.seatNumber = seatNumber;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.examSessionId = examSessionId;
        this.sessionName = sessionName;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.examDate = examDate;
        this.sessionTime = sessionTime;
    }

    public String getStudentRollNumber() {
        return studentRollNumber;
    }

    public void setStudentRollNumber(String studentRollNumber) {
        this.studentRollNumber = studentRollNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentDepartment() {
        return studentDepartment;
    }

    public void setStudentDepartment(String studentDepartment) {
        this.studentDepartment = studentDepartment;
    }

    public String getStudentSection() {
        return studentSection;
    }

    public void setStudentSection(String studentSection) {
        this.studentSection = studentSection;
    }

    public String getClassSection() {
        return classSection;
    }

    public void setClassSection(String classSection) {
        this.classSection = classSection;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getHallNumber() {
        return hallNumber;
    }

    public void setHallNumber(String hallNumber) {
        this.hallNumber = hallNumber;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
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

    public Long getExamSessionId() {
        return examSessionId;
    }

    public void setExamSessionId(Long examSessionId) {
        this.examSessionId = examSessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public String getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(String sessionTime) {
        this.sessionTime = sessionTime;
    }
}
