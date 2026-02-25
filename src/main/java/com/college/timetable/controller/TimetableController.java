package com.college.timetable.controller;

import com.college.timetable.dto.TimetableRequestDTO;
import com.college.timetable.model.*;
import com.college.timetable.service.TimetableContext;
import com.college.timetable.service.TimetableService;
import com.college.timetable.config.SlotConfig;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

    private final TimetableService timetableService;
    private final SlotConfig slotConfig;

    public TimetableController(TimetableService timetableService,
                               SlotConfig slotConfig) {
        this.timetableService = timetableService;
        this.slotConfig = slotConfig;
    }

    // ✅ Simple GET endpoint to test in browser
    @GetMapping("/test")
    public String test() {
        return "Automatic Timetable Backend Running Successfully!";
    }

    // ✅ POST endpoint to generate timetable
    @PostMapping("/generate")
public List<TimetableSlot> generateTimetable(@RequestBody TimetableRequestDTO request) {

    List<Division> divisions = request.getDivisions().stream()
            .map(d -> new Division(d.getName()))
            .collect(Collectors.toList());

    List<Faculty> faculties = request.getFaculties().stream()
            .map(f -> new Faculty(
                    f.getId(),
                    f.getName(),
                    new Shift(f.getShiftStart(), f.getShiftEnd())
            ))
            .collect(Collectors.toList());

    List<Subject> subjects = request.getSubjects().stream()
            .map(s -> new Subject(
                    s.getName(),
                    s.isLab(),
                    s.getWeeklyHours()
            ))
            .collect(Collectors.toList());

    TimetableContext context = new TimetableContext(
            divisions,
            faculties,
            subjects,
            slotConfig.getAllSlots()
    );

    return timetableService.generateTimetable(context);
        }
}    