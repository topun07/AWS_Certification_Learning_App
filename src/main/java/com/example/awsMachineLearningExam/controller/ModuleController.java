package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.Module;
import com.example.awsMachineLearningExam.repository.ModuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleRepository moduleRepository;

    public ModuleController(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    // Vue calls this to get all modules for a specific certification
    @GetMapping("/cert/{certCode}")
    public ResponseEntity<List<Module>> getModulesByCert(@PathVariable String certCode) {
        List<Module> modules = moduleRepository.findByCertCode(certCode);
        return ResponseEntity.ok(modules);
    }

    // Failsafe: Get absolutely all modules (used by the Teleporter search)
    @GetMapping
    public ResponseEntity<List<Module>> getAllModules() {
        return ResponseEntity.ok(moduleRepository.findAll());
    }
}