package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.service.QuestionImporterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173") // Ensure this matches your Vue port!
public class QuestionAdminController {

    @Autowired
    private QuestionImporterService importerService;

    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Transmission Error: File payload is empty.");
        }

        try {
            importerService.importQuestions(file.getInputStream());
            return ResponseEntity.ok("Archive Sync Complete: Exam questions successfully imported.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Uplink Failed: " + e.getMessage());
        }
    }
}