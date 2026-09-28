package com.exam.seatplan.dto;

import java.time.LocalDate;

public class ExamSessionDto {
    private Long id;
    private String sessionName;
    private String subjectCode;
    private String subjectName;
    private LocalDate examDate;
    private String sessionTime;
    private String status;
    private int allocatedCount;

    public ExamSessionDto() {
    }

    public ExamSessionDto(Long id, String sessionName, String subjectCode, String subjectName, LocalDate examDate, String sessionTime, String status, int allocatedCount) {
        this.id = id;
        this.sessionName = sessionName;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.examDate = examDate;
        this.sessionTime = sessionTime;
        this.status = status;
        this.allocatedCount = allocatedCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAllocatedCount() {
        return allocatedCount;
    }

    public void setAllocatedCount(int allocatedCount) {
        this.allocatedCount = allocatedCount;
    }
}
