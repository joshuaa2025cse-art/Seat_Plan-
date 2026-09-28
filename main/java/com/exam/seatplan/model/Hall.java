package com.exam.seatplan.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "halls")
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hall_number", nullable = false, unique = true)
    private String hallNumber;

    @Column(name = "hall_name", nullable = false)
    private String name;

    @Column(name = "total_rows", nullable = false)
    private int totalRows;

    @Column(name = "total_columns", nullable = false)
    private int totalColumns;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Seat> seats = new ArrayList<>();

    public Hall() {
    }

    public Hall(String hallNumber, String name, int totalRows, int totalColumns) {
        this.hallNumber = hallNumber;
        this.name = name;
        this.totalRows = totalRows;
        this.totalColumns = totalColumns;
        this.capacity = totalRows * totalColumns;
        this.isActive = true;
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
        this.capacity = this.totalRows * this.totalColumns;
    }

    public int getTotalColumns() {
        return totalColumns;
    }

    public void setTotalColumns(int totalColumns) {
        this.totalColumns = totalColumns;
        this.capacity = this.totalRows * this.totalColumns;
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

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "Hall{" +
                "id=" + id +
                ", hallNumber='" + hallNumber + '\'' +
                ", name='" + name + '\'' +
                ", totalRows=" + totalRows +
                ", totalColumns=" + totalColumns +
                ", capacity=" + capacity +
                ", isActive=" + isActive +
                '}';
    }
}
