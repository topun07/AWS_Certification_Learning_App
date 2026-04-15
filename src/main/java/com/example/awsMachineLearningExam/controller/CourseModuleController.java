package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.CourseModule;
import com.example.awsMachineLearningExam.repository.CourseModuleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@CrossOrigin(origins = "http://localhost:5173") // <-- Allows your Vue app to talk to this server!
public class CourseModuleController {

    private final CourseModuleRepository repository;

    // Inject the repository we just made
    public CourseModuleController(CourseModuleRepository repository) {
        this.repository = repository;
    }

    // GET: Fetch all modules for the Library Dashboard
    @GetMapping
    public List<CourseModule> getAllModules() {
        return repository.findAll();
    }

    // GET: Fetch a specific module (and its videos) when they click a card
    @GetMapping("/{id}")
    public CourseModule getModuleById(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course Module not found in Jedi Archives: " + id));
    }

    @GetMapping("/cert/{certCode}")
    public List<CourseModule> getModulesByCert(@PathVariable String certCode) {
        return repository.findByCertCode(certCode);
    }
}