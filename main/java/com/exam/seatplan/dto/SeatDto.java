package com.exam.seatplan.dto;

public class SeatDto {
    private Long id;
    private String seatNumber;
    private int rowNum;
    private int colNum;
    private Long hallId;
    private String hallNumber;
    private String hallName;
    private Long examSessionId;
    private String examSessionName;
    private Long studentId;
    private String studentRollNumber;
    private String studentName;
    private String studentDepartment;
    private String studentSection;
    private String studentClassSection;
    private boolean isOccupied;
    private String remarks;

    public SeatDto() {
    }

    public SeatDto(Long id, String seatNumber, int rowNum, int colNum, Long hallId, String hallNumber, String hallName,
                   Long examSessionId, String examSessionName, Long studentId, String studentRollNumber,
                   String studentName, String studentDepartment, String studentSection, String studentClassSection,
                   boolean isOccupied, String remarks) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.hallId = hallId;
        this.hallNumber = hallNumber;
        this.hallName = hallName;
        this.examSessionId = examSessionId;
        this.examSessionName = examSessionName;
        this.studentId = studentId;
        this.studentRollNumber = studentRollNumber;
        this.studentName = studentName;
        this.studentDepartment = studentDepartment;
        this.studentSection = studentSection;
        this.studentClassSection = studentClassSection;
        this.isOccupied = isOccupied;
        this.remarks = remarks;
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

    public Long getHallId() {
        return hallId;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
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

    public Long getExamSessionId() {
        return examSessionId;
    }

    public void setExamSessionId(Long examSessionId) {
        this.examSessionId = examSessionId;
    }

    public String getExamSessionName() {
        return examSessionName;
    }

    public void setExamSessionName(String examSessionName) {
        this.examSessionName = examSessionName;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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

    public String getStudentClassSection() {
        return studentClassSection;
    }

    public void setStudentClassSection(String studentClassSection) {
        this.studentClassSection = studentClassSection;
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
}
