package com.exam.seatplan.dto;

import java.util.List;

public class ArrangementRequestDto {
    private Long examSessionId;
    private List<Long> hallIds;
    private List<String> departments;
    private String strategy; // ALTERNATE_DEPARTMENT, CHECKERBOARD, SEQUENTIAL

    public ArrangementRequestDto() {
    }

    public ArrangementRequestDto(Long examSessionId, List<Long> hallIds, List<String> departments, String strategy) {
        this.examSessionId = examSessionId;
        this.hallIds = hallIds;
        this.departments = departments;
        this.strategy = strategy;
    }

    public Long getExamSessionId() {
        return examSessionId;
    }

    public void setExamSessionId(Long examSessionId) {
        this.examSessionId = examSessionId;
    }

    public List<Long> getHallIds() {
        return hallIds;
    }

    public void setHallIds(List<Long> hallIds) {
        this.hallIds = hallIds;
    }

    public List<String> getDepartments() {
        return departments;
    }

    public void setDepartments(List<String> departments) {
        this.departments = departments;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
}
