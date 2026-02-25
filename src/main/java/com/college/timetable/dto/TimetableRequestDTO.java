package com.college.timetable.dto;

import java.util.List;

public class TimetableRequestDTO {

    private List<DivisionDTO> divisions;
    private List<FacultyDTO> faculties;
    private List<SubjectDTO> subjects;

    public List<DivisionDTO> getDivisions() {
        return divisions;
    }

    public void setDivisions(List<DivisionDTO> divisions) {
        this.divisions = divisions;
    }

    public List<FacultyDTO> getFaculties() {
        return faculties;
    }

    public void setFaculties(List<FacultyDTO> faculties) {
        this.faculties = faculties;
    }

    public List<SubjectDTO> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectDTO> subjects) {
        this.subjects = subjects;
    }
}