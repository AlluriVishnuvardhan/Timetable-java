package com.college.timetable.service;

import com.college.timetable.scheduler.pipeline.SchedulingPipeline;
import com.college.timetable.scheduler.stage.LabSchedulingStage;
import com.college.timetable.scheduler.stage.TheorySchedulingStage;
import com.college.timetable.scheduler.stage.ValidationStage;
import com.college.timetable.model.TimetableSlot;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TimetableService {

    private final SchedulingPipeline pipeline;
    private final LabSchedulingStage labStage;
    private final TheorySchedulingStage theoryStage;
    private final ValidationStage validationStage;

    public TimetableService(SchedulingPipeline pipeline,
                            LabSchedulingStage labStage,
                            TheorySchedulingStage theoryStage,
                            ValidationStage validationStage) {
        this.pipeline = pipeline;
        this.labStage = labStage;
        this.theoryStage = theoryStage;
        this.validationStage = validationStage;
    }

    public List<TimetableSlot> generateTimetable(TimetableContext context) {

        pipeline.addStage(labStage);
        pipeline.addStage(theoryStage);
        pipeline.addStage(validationStage);

        pipeline.execute(context);

        return context.getTimetableSlots();
    }
}