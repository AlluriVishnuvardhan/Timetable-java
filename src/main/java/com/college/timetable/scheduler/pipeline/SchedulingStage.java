package com.college.timetable.scheduler.pipeline;

import com.college.timetable.service.TimetableContext;

public interface SchedulingStage {

    void execute(TimetableContext context);

}