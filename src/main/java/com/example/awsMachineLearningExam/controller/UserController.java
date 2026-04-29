package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.AppUser;
import com.example.awsMachineLearningExam.repository.AppUserRepository;
import com.example.awsMachineLearningExam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Make sure your Vue ports are correct here!
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 1. We inject the Repository so we can search the database directly
    @Autowired
    private AppUserRepository userRepository;

    // 2. The Missing Profile Endpoint!
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Principal principal) {
        // If there is no user logged in, reject it
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // Find the user in the database using their username
        Optional<AppUser> optionalUser = userRepository.findByUsername(principal.getName());

        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();

            // Package up the exact data Vue is asking for
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("username", user.getUsername());
            profileData.put("isPremium", user.isPremium()); // Grabs their premium status

            return ResponseEntity.ok(profileData);
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    // 🚨 THE MASTER SYNC BRIDGE: Allows Vue to request the official User ID by Username
     @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {

        // 🚨 THE FIX: Tell Java to look for AppUser, not User!
        java.util.Optional<AppUser> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{username}/record-study")
    public ResponseEntity<?> recordStudySession(@PathVariable String username) {
        try {
            AppUser updatedUser = userService.recordStudySession(username);
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
            int correctCount = payload.getOrDefault("correctCount", 0);
            int totalQuestions = payload.getOrDefault("totalQuestions", 1);
            int earnedXp = (correctCount * 10) + (correctCount == totalQuestions && totalQuestions > 0 ? 500 : 0);

            AppUser updatedUser = userService.awardXp(username, correctCount, totalQuestions);

            return ResponseEntity.ok(java.util.Map.of(
                    "message", "XP Awarded!",
                    "totalXp", updatedUser.getXp(),
                    "earnedXp", earnedXp
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to award XP: " + e.getMessage());
        }
    }
}