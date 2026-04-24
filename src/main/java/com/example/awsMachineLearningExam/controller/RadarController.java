package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.dto.RadarScoreResponseDTO;
import com.example.awsMachineLearningExam.service.RadarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/radar")
@CrossOrigin(origins = "http://localhost:5173") // Allow Vue to talk to Spring Boot!
public class RadarController {

    @Autowired
    private RadarService radarService;

    // GET Request: /api/radar/user/{userId}/cert/{certCode}
    @GetMapping("/user/{userId}/cert/{certCode}")
    public ResponseEntity<RadarScoreResponseDTO> getUserRadarScores(
            @PathVariable Long userId,
            @PathVariable String certCode) {

        RadarScoreResponseDTO response = radarService.calculateUserProficiency(userId, certCode);
        return ResponseEntity.ok(response);
    }
}