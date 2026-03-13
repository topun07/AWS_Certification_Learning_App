package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.ExamHistory;
import com.example.awsMachineLearningExam.model.Question;
import com.example.awsMachineLearningExam.repository.ExamHistoryRepository;
import com.example.awsMachineLearningExam.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "http://localhost:5173")
public class QuestionController {

    // --- 1. Repositories (Only one of each!) ---
    @Autowired
    private QuestionRepository repository;

    @Autowired
    private ExamHistoryRepository historyRepository;

    // --- 2. Question Logic ---

    @GetMapping("/random")
    public Question getRandom(@RequestParam String examCode,
                              @RequestParam(defaultValue = "All") String category) {
        return repository.findRandom(examCode, category);
    }

    @GetMapping("/count")
    public long getCount(@RequestParam String examCode,
                         @RequestParam(defaultValue = "All") String category) {
        return repository.countByExamAndCategory(examCode, category);
    }

    @GetMapping
    public List<Question> getAllQuestions() {
        return repository.findAll();
    }

    // --- 3. Exam History Logic ---

    @PostMapping("/history")
    public ExamHistory saveHistory(@RequestBody ExamHistory history) {
        return historyRepository.save(history);
    }

    @GetMapping("/history")
    public List<ExamHistory> getHistory() {
        return historyRepository.findTop10ByOrderByCompletedAtDesc();
    }
}