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

    @PostMapping("/{username}/award-xp")
    public ResponseEntity<?> awardXp(@PathVariable String username, @RequestBody java.util.Map<String, Integer> payload) {
        try {
            // Extract the quiz data sent from Vue
            int correctCount = payload.getOrDefault("correctCount", 0);
            int totalQuestions = payload.getOrDefault("totalQuestions", 1);

            // Calculate the exact XP earned for the frontend pop-up
            int earnedXp = (correctCount * 10) + (correctCount == totalQuestions && totalQuestions > 0 ? 500 : 0);

            // Tell the brain to save it
            User updatedUser = userService.awardXp(username, correctCount, totalQuestions);

            // Send the updated stats back to Vue
            return ResponseEntity.ok(java.util.Map.of(
                    "message", "XP Awarded!",
                    "totalXp", updatedUser.getTotalXp(),
                    "earnedXp", earnedXp
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to award XP: " + e.getMessage());
        }
    }
}