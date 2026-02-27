package com.college.timetable.service;

import com.college.timetable.scheduler.pipeline.SchedulingPipeline;
import com.college.timetable.scheduler.stage.LabSchedulingStage;
import com.college.timetable.scheduler.stage.TheorySchedulingStage;
import com.college.timetable.scheduler.stage.ValidationStage;
import com.college.timetable.dto.TimetableRequestDTO;
import com.college.timetable.model.Division;
import com.college.timetable.model.Faculty;
import com.college.timetable.model.Shift;
import com.college.timetable.model.TimetableSlot;
import com.college.timetable.model.Subject;
import com.college.timetable.config.SlotConfig;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TimetableService {

    private final SchedulingPipeline pipeline;
    private final LabSchedulingStage labStage;
    private final TheorySchedulingStage theoryStage;
    private final ValidationStage validationStage;
    private final SlotConfig slotConfig;

    public TimetableService(SchedulingPipeline pipeline,
                            LabSchedulingStage labStage,
                            TheorySchedulingStage theoryStage,
                            ValidationStage validationStage,
                            SlotConfig slotConfig) {
        this.pipeline = pipeline;
        this.labStage = labStage;
        this.theoryStage = theoryStage;
        this.validationStage = validationStage;
        this.slotConfig = slotConfig;
    }

    public List<TimetableSlot> generateFromExcel(MultipartFile file) throws Exception {
        TimetableRequestDTO request = parseExcel(file);

        List<Division> divisions = request.getDivisions().stream()
                .map(d -> new Division(d.getName()))
                .toList();

        List<Faculty> faculties = request.getFaculties().stream()
                .map(f -> new Faculty(
                        f.getId(),
                        f.getName(),
                        new Shift(f.getShiftStart(), f.getShiftEnd())
                ))
                .toList();

        List<Subject> subjects = request.getSubjects().stream()
                .map(s -> new Subject(
                        s.getName(),
                        s.isLab(),
                        s.getWeeklyHours()
                ))
                .toList();

        TimetableContext context = new TimetableContext(
                divisions,
                faculties,
                subjects,
                slotConfig.getAllSlots()
        );

        return generateTimetable(context);
    }

    public List<TimetableSlot> generateTimetable(TimetableContext context) {
        pipeline.addStage(labStage);
        pipeline.addStage(theoryStage);
        pipeline.addStage(validationStage);

        pipeline.execute(context);

        return context.getTimetableSlots();
    }

    private TimetableRequestDTO parseExcel(MultipartFile file) throws Exception {
        // TODO: Implement Excel parsing logic
        throw new UnsupportedOperationException("Excel parsing not yet implemented");
    }
}