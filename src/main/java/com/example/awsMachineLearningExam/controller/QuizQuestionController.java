package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.service.CsvParserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "http://localhost:5173") // Allow Vue to upload!
public class QuizQuestionController {

    private final CsvParserService csvParserService;

    public QuizQuestionController(CsvParserService csvParserService) {
        this.csvParserService = csvParserService;
    }

    // Endpoint specifically for receiving files
    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        String result = csvParserService.processCsvFile(file);

        if (result.startsWith("Error")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }
}