package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.dto.ExamSubmitRequest;
import com.example.awsMachineLearningExam.model.ExamHistory;
import com.example.awsMachineLearningExam.model.Question;
import com.example.awsMachineLearningExam.repository.ExamHistoryRepository;
import com.example.awsMachineLearningExam.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/questions/history")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}) // Added 5174 just in case Vite changes ports
public class ExamHistoryController {

    @Autowired
    private ExamHistoryRepository historyRepository;

    @Autowired
    private QuestionRepository questionRepository;

    // YOUR EXISTING DIRECT SAVE
    @PostMapping("/save")
    public ResponseEntity<ExamHistory> saveHistory(@RequestBody ExamHistory history) {
        ExamHistory savedRecord = historyRepository.save(history);
        return ResponseEntity.ok(savedRecord);
    }

    // THE NEW CALCULATION ENGINE
    @PostMapping("/grade")
    public ResponseEntity<ExamHistory> gradeAndSaveExam(@RequestBody ExamSubmitRequest request) {
        Map<String, int[]> categoryStats = new HashMap<>();
        List<String> missedIdsList = new ArrayList<>();

        int totalQuestions = request.getAnswers().size();
        int totalCorrect = 0;

        // 1. Process every answer
        for (ExamSubmitRequest.AnswerRecord answer : request.getAnswers()) {
            categoryStats.putIfAbsent(answer.getCategory(), new int[]{0, 0});
            categoryStats.get(answer.getCategory())[0]++; // Increment total for category

            if (answer.isCorrect()) {
                categoryStats.get(answer.getCategory())[1]++; // Increment correct for category
                totalCorrect++;
            } else {
                // If wrong, add to our missed IDs list
                missedIdsList.add(String.valueOf(answer.getQuestionId()));
            }
        }

        // 2. Find the Weakest Category
        String weakestCategory = "MIXED";
        double lowestPercentage = 100.0;

        for (Map.Entry<String, int[]> entry : categoryStats.entrySet()) {
            int total = entry.getValue()[0];
            int correct = entry.getValue()[1];
            if (total == 0) continue;

            double percentage = ((double) correct / total) * 100;
            if (percentage < lowestPercentage) {
                lowestPercentage = percentage;
                weakestCategory = entry.getKey();
            }
        }

        // 3. Calculate Overall Score
        int overallScore = totalQuestions == 0 ? 0 : (int) Math.round(((double) totalCorrect / totalQuestions) * 100);
        String missedIdsString = String.join(",", missedIdsList);

        // 4. Build and Save the Entity
        ExamHistory attempt = new ExamHistory();
        attempt.setUserId(request.getUserId());
        attempt.setExamCode(request.getExamCode());
        attempt.setScorePercentage(overallScore);
        attempt.setWeakestCategory(weakestCategory);
        attempt.setMissedQuestionIds(missedIdsString);
        attempt.setCompletedAt(LocalDateTime.now());
        attempt.setTotalQuestions(totalQuestions);

        ExamHistory savedRecord = historyRepository.save(attempt);

        return ResponseEntity.ok(savedRecord);
    }

    // YOUR EXISTING GET METHOD
    @GetMapping("/{id}")
    public ResponseEntity<List<Question>> getReviewQuestions(@PathVariable Long id) {
        java.util.Optional<ExamHistory> historyOpt = historyRepository.findById(id);

        if (historyOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ExamHistory history = historyOpt.get();
        String missedIdsStr = history.getMissedQuestionIds();

        if (missedIdsStr == null || missedIdsStr.trim().isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<Long> ids = Arrays.stream(missedIdsStr.split(","))
                .filter(s -> !s.isEmpty())
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());

        List<Question> questions = questionRepository.findAllById(ids);

        return ResponseEntity.ok(questions);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<Object[]>> getLeaderboard() {
        // 1. Fetch the data and assign it immediately
        List<Object[]> topScores = historyRepository.findTopScores(65);

        // 2. Send it to the frontend
        return ResponseEntity.ok(topScores);
    }
}