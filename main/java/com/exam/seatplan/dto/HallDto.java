package com.exam.seatplan.dto;

public class HallDto {
    private Long id;
    private String hallNumber;
    private String name;
    private int totalRows;
    private int totalColumns;
    private int capacity;
    private boolean isActive;
    private int allocatedSeatsCount;

    public HallDto() {
    }

    public HallDto(Long id, String hallNumber, String name, int totalRows, int totalColumns, int capacity, boolean isActive, int allocatedSeatsCount) {
        this.id = id;
        this.hallNumber = hallNumber;
        this.name = name;
        this.totalRows = totalRows;
        this.totalColumns = totalColumns;
        this.capacity = capacity;
        this.isActive = isActive;
        this.allocatedSeatsCount = allocatedSeatsCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHallNumber() {
        return hallNumber;
    }

    public void setHallNumber(String hallNumber) {
        this.hallNumber = hallNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getTotalColumns() {
        return totalColumns;
    }

    public void setTotalColumns(int totalColumns) {
        this.totalColumns = totalColumns;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getAllocatedSeatsCount() {
        return allocatedSeatsCount;
    }

    public void setAllocatedSeatsCount(int allocatedSeatsCount) {
        this.allocatedSeatsCount = allocatedSeatsCount;
    }
}
