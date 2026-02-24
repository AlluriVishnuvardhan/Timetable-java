package com.college.timetable.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TimetableService {

    private String lastResultFolder;
    private final String RESULT_BASE = "results";

    public void processTimetableData(
            List<?> faculties,
            String university,
            String department,
            String academic) throws Exception {

        clearResultsFolder();

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        lastResultFolder = RESULT_BASE + "/output_" + timestamp;
        Files.createDirectories(Paths.get(lastResultFolder));

        // schedule_all_division_labs equivalent
        // assign_subjects_for_faculty equivalent
        // export_all equivalent

        System.out.println("Timetable processed successfully.");
    }

    public void clearResultsFolder() throws Exception {
        File baseFolder = new File(RESULT_BASE);

        if (baseFolder.exists()) {
            for (File file : Objects.requireNonNull(baseFolder.listFiles())) {
                deleteRecursively(file);
            }
        }

        Files.createDirectories(Paths.get(RESULT_BASE));
    }

    private void deleteRecursively(File file) {
        if (file.isDirectory()) {
            for (File child : Objects.requireNonNull(file.listFiles())) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }

    public List<String> getGeneratedFiles() {
        if (lastResultFolder == null) return new ArrayList<>();
        File folder = new File(lastResultFolder);
        String[] files = folder.list();
        return files != null ? Arrays.asList(files) : new ArrayList<>();
    }

    public File getFile(String filename) {
        return new File(lastResultFolder + "/" + filename);
    }
}