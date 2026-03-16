package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.Option;
import com.example.awsMachineLearningExam.model.ExamHistory;
import com.example.awsMachineLearningExam.model.Question;
import com.example.awsMachineLearningExam.repository.ExamHistoryRepository;
import com.example.awsMachineLearningExam.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionRepository repository;

    @Autowired
    private ExamHistoryRepository examHistoryRepository;

    // --- 1. Question Retrieval ---

    @GetMapping
    public List<Question> getAllQuestions() {
        return repository.findAll();
    }

    @GetMapping("/random")
    public Question getRandom(@RequestParam String examCode,
                              @RequestParam(defaultValue = "All") String category,
                              @RequestParam(required = false) Long userId) {
        Question q;
        if (userId != null && userId > 0) {
            q = repository.findRandom(examCode, category);
        } else {
            q = repository.findRandomGuest(examCode, category);
        }

        if (q != null && q.getOptions() != null) {
            List<Option> newShuffledList = new ArrayList<>(q.getOptions());
            Collections.shuffle(newShuffledList, new java.security.SecureRandom());
            q.setOptions(newShuffledList);
        }
        return q;
    }

    @GetMapping("/count")
    public Long getTotalCount(@RequestParam String examCode,
                              @RequestParam(defaultValue = "All") String category) {
        if ("All".equals(category)) {
            return repository.countByExamCode(examCode);
        } else {
            return repository.countByExamCodeAndCategory(examCode, category);
        }
    }

    // --- 2. Exam History & Leaderboard ---

    @PostMapping("/history/save")
    public ResponseEntity<?> saveHistory(@RequestBody ExamHistory history) {
        try {
            if (history.getCompletedAt() == null) {
                history.setCompletedAt(LocalDateTime.now());
            }
            examHistoryRepository.save(history);
            return ResponseEntity.ok("History saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving history: " + e.getMessage());
        }
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<?> deleteHistory(@PathVariable Long id) {
        examHistoryRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public List<ExamHistory> getHistory() {
        return examHistoryRepository.findTop10ByOrderByCompletedAtDesc();
    }

    @GetMapping("/leaderboard")
    public List<Map<String, Object>> getLeaderboard() {
        List<Object[]> rawData = examHistoryRepository.findTopScores();

        // CHECK YOUR INTELLIJ/VS CODE TERMINAL FOR THIS LOG:
        System.out.println("DEBUG: Leaderboard rows found: " + (rawData != null ? rawData.size() : 0));

        List<Map<String, Object>> formattedData = new ArrayList<>();
        if (rawData == null || rawData.isEmpty()) {
            return formattedData;
        }

        for (Object[] row : rawData) {
            Map<String, Object> map = new HashMap<>();
            map.put("fullName", row[0]);
            map.put("scorePercentage", row[1]);
            map.put("examCode", row[2]);
            formattedData.add(map);
        }
        return formattedData;
    }

    // --- 3. Review Room Logic ---

    @GetMapping("/review/{attemptId}")
    public List<Question> getReviewQuestions(@PathVariable Long attemptId) {
        ExamHistory history = examHistoryRepository.findById(attemptId).orElse(null);
        if (history == null || history.getMissedQuestionIds() == null || history.getMissedQuestionIds().isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> ids = Arrays.stream(history.getMissedQuestionIds().split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());

        return repository.findAllById(ids);
    }
}