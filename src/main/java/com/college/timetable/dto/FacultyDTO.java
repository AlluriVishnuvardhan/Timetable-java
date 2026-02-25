package com.college.timetable.dto;

import java.time.LocalTime;

public class FacultyDTO {

    private String id;
    private String name;
    private LocalTime shiftStart;
    private LocalTime shiftEnd;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalTime getShiftStart() {
        return shiftStart;
    }

    public LocalTime getShiftEnd() {
        return shiftEnd;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShiftStart(LocalTime shiftStart) {
        this.shiftStart = shiftStart;
    }

    public void setShiftEnd(LocalTime shiftEnd) {
        this.shiftEnd = shiftEnd;
    }
}