package com.college.timetable.scheduler.service;

import com.college.timetable.model.*;
import com.college.timetable.scheduler.load.core.DivisionScheduleMatrix;
import com.college.timetable.scheduler.load.core.FacultyScheduleMatrix;
import com.college.timetable.scheduler.load.core.SchedulingTask;
import com.college.timetable.scheduler.load.core.SlotConstants;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlotAllocationService {

    // ✅ THEORY
    public TimetableSlot allocateTheory(Division division,
                                        Slot slot,
                                        Subject subject,
                                        Faculty faculty) {
        if (isLunchSlot(slot)) {
            return null;
        }

        TimetableSlot entry =
                new TimetableSlot(subject, faculty, slot, null);

        division.assignSlot(slot, entry);

        return entry;
    }


    // ✅ LAB
    public List<TimetableSlot> allocateLab(Division division,
                                           Slot slot1,
                                           Slot slot2,
                                           Subject subject,
                                           Faculty faculty,
                                           String batchName) {
        if (isLunchSlot(slot1) || isLunchSlot(slot2)) {
            return List.of();
        }

        TimetableSlot entry1 =
                new TimetableSlot(subject, faculty, slot1, batchName);

        TimetableSlot entry2 =
                new TimetableSlot(subject, faculty, slot2, batchName);

        division.assignSlot(slot1, entry1);
        division.assignSlot(slot2, entry2);

        return List.of(entry1, entry2);
    }

    public boolean canAllocateContinuous(SchedulingTask task,
                                         DivisionScheduleMatrix divisionMatrix,
                                         FacultyScheduleMatrix facultyMatrix,
                                         int day,
                                         int slot) {

        if (!task.isRequiresContinuous()) {
            return true;
        }

        if (isLunchSlot(slot) || isLunchSlot(slot + 1)) {
            return false;
        }

        if (slot + 1 >= SlotConstants.SLOTS_PER_DAY) {
            return false;
        }

        if (!divisionMatrix.canAssign(task, day, slot)) return false;
        if (!divisionMatrix.canAssign(task, day, slot + 1)) return false;

        if (!facultyMatrix.isFree(day, slot)) return false;
        if (!facultyMatrix.isFree(day, slot + 1)) return false;

        return true;
    }

    public boolean isLunchSlot(Slot slot) {
        if (slot == null) {
            return false;
        }
        return (slot.getSlotNumber() - 1) == SlotConstants.LUNCH_SLOT;
    }

    public boolean isLunchSlot(int slot) {
        return slot == SlotConstants.LUNCH_SLOT;
    }
}
