package com.exam.seatplan.dto;

import java.util.List;

public class ArrangementSummaryDto {
    private boolean success;
    private String message;
    private Long examSessionId;
    private String sessionName;
    private int totalStudents;
    private int totalAllocated;
    private int totalUnallocated;
    private int totalHalls;
    private int totalCapacity;
    private String strategyUsed;
    private List<HallDto> halls;

    public ArrangementSummaryDto() {
    }

    public ArrangementSummaryDto(boolean success, String message, Long examSessionId, String sessionName,
                                 int totalStudents, int totalAllocated, int totalUnallocated, int totalHalls,
                                 int totalCapacity, String strategyUsed, List<HallDto> halls) {
        this.success = success;
        this.message = message;
        this.examSessionId = examSessionId;
        this.sessionName = sessionName;
        this.totalStudents = totalStudents;
        this.totalAllocated = totalAllocated;
        this.totalUnallocated = totalUnallocated;
        this.totalHalls = totalHalls;
        this.totalCapacity = totalCapacity;
        this.strategyUsed = strategyUsed;
        this.halls = halls;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getTotalAllocated() {
        return totalAllocated;
    }

    public void setTotalAllocated(int totalAllocated) {
        this.totalAllocated = totalAllocated;
    }

    public int getTotalUnallocated() {
        return totalUnallocated;
    }

    public void setTotalUnallocated(int totalUnallocated) {
        this.totalUnallocated = totalUnallocated;
    }

    public int getTotalHalls() {
        return totalHalls;
    }

    public void setTotalHalls(int totalHalls) {
        this.totalHalls = totalHalls;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public String getStrategyUsed() {
        return strategyUsed;
    }

    public void setStrategyUsed(String strategyUsed) {
        this.strategyUsed = strategyUsed;
    }

    public List<HallDto> getHalls() {
        return halls;
    }

    public void setHalls(List<HallDto> halls) {
        this.halls = halls;
    }
}
