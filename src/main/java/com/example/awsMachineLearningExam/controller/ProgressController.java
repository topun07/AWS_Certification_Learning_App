package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.dto.CategoryUpdateDTO;
import com.example.awsMachineLearningExam.model.CategoryMastery;
import com.example.awsMachineLearningExam.model.AppUser;
import com.example.awsMachineLearningExam.repository.CategoryMasteryRepository;
import com.example.awsMachineLearningExam.repository.AppUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "http://localhost:5173")
public class ProgressController {

    private final CategoryMasteryRepository masteryRepo;
    private final AppUserRepository userRepo;

    public ProgressController(CategoryMasteryRepository masteryRepo, AppUserRepository userRepo) {
        this.masteryRepo = masteryRepo;
        this.userRepo = userRepo;
    }

    // --- 1. THE DOWNLOAD: Get data for the Vue Radar Chart ---
    @GetMapping("/{username}/radar")
    public ResponseEntity<Map<String, Integer>> getUserRadarStats(@PathVariable String username) {
        Optional<AppUser> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Long userId = userOpt.get().getId();
        List<CategoryMastery> stats = masteryRepo.findAllByUserId(userId);

        // Convert the database rows into a simple Map: { "Security": 85, "Compute": 40 }
        Map<String, Integer> radarData = new HashMap<>();
        for (CategoryMastery stat : stats) {
            radarData.put(stat.getCategory(), stat.getMasteryPercentage());
        }

        return ResponseEntity.ok(radarData);
    }

    // --- 2. THE UPLOAD: Update stats after an exam ---
    @PostMapping("/{username}/radar/update")
    public ResponseEntity<?> updateRadarStats(
            @PathVariable String username,
            @RequestBody List<CategoryUpdateDTO> updates) {

        Optional<AppUser> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Long userId = userOpt.get().getId();

        // Loop through every question the user just answered
        for (CategoryUpdateDTO update : updates) {

            // Look for an existing stat tracking row for this category
            CategoryMastery stat = masteryRepo.findByUserIdAndCategory(userId, update.getCategory())
                    .orElse(new CategoryMastery(userId, update.getCategory()));

            // Add the attempt
            stat.addAttempt();

            // If they got it right, add to the correct total
            if (update.isCorrect()) {
                stat.addCorrect();
            }

            // Save it back to the database
            masteryRepo.save(stat);
        }

        return ResponseEntity.ok(Map.of("message", "Lifetime stats updated successfully!"));
    }
}