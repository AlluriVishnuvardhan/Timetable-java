package com.college.timetable.controller;

import com.college.timetable.service.TimetableService;
import com.college.timetable.service.ExcelParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Controller
public class TimetableController {

    @Autowired
    private TimetableService timetableService;

    @Autowired
    private ExcelParserService excelParserService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/builder")
    public String builder() {
        return "builder";
    }

    @PostMapping("/upload_excel")
    public String uploadExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String university,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String academic,
            Model model) throws Exception {

        List<?> faculties = excelParserService.parseExcel(file);

        timetableService.processTimetableData(
                faculties,
                university,
                department,
                academic
        );

        return "redirect:/success";
    }

    @PostMapping("/generate")
    @ResponseBody
    public String generate(@RequestBody List<?> faculties) throws Exception {

        timetableService.processTimetableData(
                faculties,
                "", "", ""
        );

        return "Generated";
    }

    @GetMapping("/success")
    public String success(Model model) {
        List<String> files = timetableService.getGeneratedFiles();
        model.addAttribute("files", files);
        return "success";
    }

    @GetMapping("/download/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> download(@PathVariable String filename) throws Exception {

        File file = timetableService.getFile(filename);
        Path path = file.toPath();

        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}