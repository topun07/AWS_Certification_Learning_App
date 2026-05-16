package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    // Using GET so you can easily test this in your browser URL bar
    @GetMapping("/test")
    public ResponseEntity<?> fireTestEmail(@RequestParam String to) {
        try {
            emailService.sendTestEmail(to);
            return ResponseEntity.ok("✅ SUCCESS! Check the inbox of: " + to);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("❌ FAILED: " + e.getMessage());
        }
    }
}