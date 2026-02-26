package com.college.timetable.model;

import java.util.HashSet;
import java.util.Set;

public class Faculty {

    private String id;
    private String name;
    private Shift shift;
    private Set<Slot> occupiedSlots = new HashSet<>();

    public Faculty(String id, String name, Shift shift) {
        this.id = id;
        this.name = name;
        this.shift = shift;
    }
    public Set<Slot> getOccupiedSlots() {
    return occupiedSlots;
}

    public boolean isAvailable(Slot slot) {
        return !occupiedSlots.contains(slot)
                && shift.isWithinShift(slot.getStartTime());
    }

    public void occupySlot(Slot slot) {
        occupiedSlots.add(slot);
    }

    public Shift getShift() {
        return shift;
    }

    public String getName() {
        return name;
    }
}