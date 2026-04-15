package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.UserProgress;
import com.example.awsMachineLearningExam.repository.UserProgressRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "http://localhost:5173")
public class UserProgressController {

    private final UserProgressRepository repository;

    public UserProgressController(UserProgressRepository repository) {
        this.repository = repository;
    }

    // POST: Save completion to the database
    @PostMapping("/complete")
    public ResponseEntity<String> markModuleComplete(@RequestParam String username, @RequestParam String moduleId) {
        if (!repository.existsByUsernameAndModuleId(username, moduleId)) {
            repository.save(new UserProgress(username, moduleId));
            return ResponseEntity.ok("Module mastered and saved!");
        }
        return ResponseEntity.ok("Module already mastered.");
    }

    // GET: Fetch a user's completed modules
    @GetMapping("/{username}")
    public List<String> getUserProgress(@PathVariable String username) {
        return repository.findByUsername(username).stream()
                .map(UserProgress::getModuleId)
                .collect(Collectors.toList());
    }
}