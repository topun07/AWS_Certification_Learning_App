package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.User;
import com.example.awsMachineLearningExam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Make sure your Vue ports are correct here!
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/{username}/record-study")
    public ResponseEntity<?> recordStudySession(@PathVariable String username) {
        try {
            // Call the brain we just built
            User updatedUser = userService.recordStudySession(username);

            // Send a JSON response back to Vue with the new streak number
            return ResponseEntity.ok(Map.of(
                    "message", "Study session recorded.",
                    "currentStreak", updatedUser.getCurrentStreak()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update streak: " + e.getMessage());
        }
    }
}