package com.college.timetable.scheduler.service;

import com.college.timetable.model.*;

import org.springframework.stereotype.Service;

@Service
public class SlotAllocationService {

    public void allocateTheory(Division division,
                               Slot slot,
                               Subject subject,
                               Faculty faculty) {

        TimetableSlot entry =
                new TimetableSlot(subject, faculty, null);

        division.assignSlot(slot, entry);
    }

    public void allocateLab(Division division,
                            Slot slot1,
                            Slot slot2,
                            Subject subject,
                            Faculty faculty,
                            String batchName) {

        TimetableSlot entry1 =
                new TimetableSlot(subject, faculty, batchName);

        TimetableSlot entry2 =
                new TimetableSlot(subject, faculty, batchName);

        division.assignSlot(slot1, entry1);
        division.assignSlot(slot2, entry2);
    }
}