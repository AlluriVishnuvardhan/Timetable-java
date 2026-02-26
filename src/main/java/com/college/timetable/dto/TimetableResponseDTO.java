package com.college.timetable.dto;

public class TimetableResponseDTO {

    private String subject;
    private String faculty;
    private String day;
    private int slotNumber;
    private String startTime;
    private String endTime;
    private String batch;

    public TimetableResponseDTO(String subject,
                                String faculty,
                                String day,
                                int slotNumber,
                                String startTime,
                                String endTime,
                                String batch) {
        this.subject = subject;
        this.faculty = faculty;
        this.day = day;
        this.slotNumber = slotNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.batch = batch;
    }

    public String getSubject() { return subject; }
    public String getFaculty() { return faculty; }
    public String getDay() { return day; }
    public int getSlotNumber() { return slotNumber; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getBatch() { return batch; }
}