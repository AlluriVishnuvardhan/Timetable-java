package com.college.timetable.scheduler.service;

import com.college.timetable.model.*;

import org.springframework.stereotype.Service;

@Service
public class FacultyAllocationService {

    public void allocate(Faculty faculty, Slot slot) {
        faculty.occupySlot(slot);
    }
}