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
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "http://localhost:5173")
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

        // 1. One single database call enforcing the 65-question minimum
        List<Object[]> rawData = examHistoryRepository.findTopScores(65);

        // 2. Keep the debug log so you can see it working in the console
        System.out.println("DEBUG: Leaderboard rows found: " + (rawData != null ? rawData.size() : 0));

        // 3. Format the data for the Vue frontend
        List<Map<String, Object>> formattedData = new ArrayList<>();

        if (rawData == null || rawData.isEmpty()) {
            return formattedData; // Return empty list if no one qualifies yet
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
    public ResponseEntity<List<Question>> getReviewData(@PathVariable Long attemptId) {

        List<Question> questions = repository.findByExamHistory_Id(attemptId);

        if (questions == null || questions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(questions);
    }

}