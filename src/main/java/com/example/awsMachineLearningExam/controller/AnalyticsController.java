package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.ActivityLog;
import com.example.awsMachineLearningExam.repository.ActivityLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AnalyticsController {

    private final ActivityLogRepository activityRepo;

    public AnalyticsController(ActivityLogRepository activityRepo) {
        this.activityRepo = activityRepo;
    }

    // 1. LOG AN EVENT (called by frontend on every game start / study view)
    @PostMapping("/event")
    public ResponseEntity<?> logEvent(@RequestBody Map<String, String> payload) {
        String username = payload.getOrDefault("username", "anonymous");
        String eventType = payload.get("eventType");
        String certCode = payload.getOrDefault("certCode", null);
        String detail = payload.getOrDefault("detail", null);

        if (eventType == null || eventType.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "eventType is required"));
        }

        ActivityLog log = new ActivityLog(username, eventType, certCode, detail);
        activityRepo.save(log);
        return ResponseEntity.ok(Map.of("status", "logged"));
    }

    // 2. ADMIN DASHBOARD: Global analytics summary
    @GetMapping("/admin/summary")
    public ResponseEntity<?> getAdminSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();

        // Total sessions
        summary.put("totalGameSessions", activityRepo.countTotalGameSessions());
        summary.put("totalStudyViews", activityRepo.countStudyViews());

        // Game mode popularity
        List<Object[]> gameModes = activityRepo.findGameModePopularity();
        Map<String, Long> gameModeMap = new LinkedHashMap<>();
        for (Object[] row : gameModes) {
            String mode = ((String) row[0]).replace("_start", "");
            gameModeMap.put(mode, (Long) row[1]);
        }
        summary.put("gameModePopularity", gameModeMap);

        // Cert popularity
        List<Object[]> certs = activityRepo.findCertPopularity();
        Map<String, Long> certMap = new LinkedHashMap<>();
        for (Object[] row : certs) {
            certMap.put((String) row[0], (Long) row[1]);
        }
        summary.put("certPopularity", certMap);

        return ResponseEntity.ok(summary);
    }

    // 3. USER PERSONAL STATS
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserStats(@PathVariable String username) {
        Map<String, Object> stats = new LinkedHashMap<>();

        // Total games played
        long exams = activityRepo.countByUsernameAndEventType(username, "exam_start");
        long amraps = activityRepo.countByUsernameAndEventType(username, "amrap_start");
        long suddenDeaths = activityRepo.countByUsernameAndEventType(username, "sudden_death_start");
        long flashcards = activityRepo.countByUsernameAndEventType(username, "flashcard_start");
        long studyViews = activityRepo.countByUsernameAndEventType(username, "study_material_view");

        stats.put("totalSessions", exams + amraps + suddenDeaths + flashcards);
        stats.put("studyMaterialViews", studyViews);

        // Game mode breakdown
        Map<String, Long> modeBreakdown = new LinkedHashMap<>();
        modeBreakdown.put("exam", exams);
        modeBreakdown.put("amrap", amraps);
        modeBreakdown.put("sudden_death", suddenDeaths);
        modeBreakdown.put("flashcard", flashcards);
        stats.put("gameModeBreakdown", modeBreakdown);

        // Cert engagement
        List<Object[]> certEngagement = activityRepo.findUserCertEngagement(username);
        Map<String, Long> certMap = new LinkedHashMap<>();
        for (Object[] row : certEngagement) {
            certMap.put((String) row[0], (Long) row[1]);
        }
        stats.put("certEngagement", certMap);

        return ResponseEntity.ok(stats);
    }
}
