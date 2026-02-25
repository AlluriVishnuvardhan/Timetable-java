package com.college.timetable.scheduler.pipeline;

import com.college.timetable.service.TimetableContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SchedulingPipeline {

    private final List<SchedulingStage> stages = new ArrayList<>();

    public void addStage(SchedulingStage stage) {
        stages.add(stage);
    }

    public void execute(TimetableContext context) {
        for (SchedulingStage stage : stages) {
            stage.execute(context);
        }
    }
}