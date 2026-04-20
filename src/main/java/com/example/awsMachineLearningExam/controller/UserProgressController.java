package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.AppUser;
import com.example.awsMachineLearningExam.model.UserProgress;
import com.example.awsMachineLearningExam.repository.UserProgressRepository;
import com.example.awsMachineLearningExam.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/progress")
// Note: Your SecurityConfig already handles CORS globally, so you actually don't need the @CrossOrigin tag here anymore, but leaving it doesn't hurt!
public class UserProgressController {

    private final UserProgressRepository repository;
    private final UserService userService;

    public UserProgressController(UserProgressRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    // POST: Save completion to the database
    @PostMapping("/complete")
    // 🚨 THE FIX: Changed String moduleId to Long moduleId! 🚨
    public ResponseEntity<String> markModuleComplete(@RequestParam String username, @RequestParam Long moduleId) {
        if (!repository.existsByUsernameAndModuleId(username, moduleId)) {
            repository.save(new UserProgress(username, moduleId));
            return ResponseEntity.ok("Module mastered and saved!");
        }
        return ResponseEntity.ok("Module already mastered.");
    }

    // POST: Calculate and save the user's score
    @PostMapping("/award-xp")
    public ResponseEntity<AppUser> awardXp(
            @RequestParam String username,
            @RequestParam int correctCount,
            @RequestParam int totalQuestions) {

        AppUser updatedUser = userService.awardXp(username, correctCount, totalQuestions);
        return ResponseEntity.ok(updatedUser);
    }

    // GET: Fetch a user's completed modules
    @GetMapping("/{username}")
    public List<Long> getUserProgress(@PathVariable String username) {
        // 🚨 Make sure this returns Longs so Vue can match the IDs perfectly!
        return repository.findByUsername(username).stream()
                .map(UserProgress::getModuleId)
                .collect(Collectors.toList());
    }
}