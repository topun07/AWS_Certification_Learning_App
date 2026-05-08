package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.Flashcard;
import com.example.awsMachineLearningExam.model.QuizQuestion;
import com.example.awsMachineLearningExam.model.Review;
import com.example.awsMachineLearningExam.repository.ExamHistoryRepository;
import com.example.awsMachineLearningExam.repository.FlashcardRepository;
import com.example.awsMachineLearningExam.repository.QuizQuestionRepository;
import com.example.awsMachineLearningExam.service.CsvParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class QuizQuestionController {

    private final CsvParserService csvParserService;
    private final QuizQuestionRepository quizRepository;
    private final FlashcardRepository flashcardRepository; // 🚨 NEW: Flashcard DB Access

    public QuizQuestionController(CsvParserService csvParserService, QuizQuestionRepository quizRepository, FlashcardRepository flashcardRepository) {
        this.csvParserService = csvParserService;
        this.quizRepository = quizRepository;
        this.flashcardRepository = flashcardRepository;
    }

    @Autowired
    private ExamHistoryRepository examHistoryRepository;

    @Autowired
    private com.example.awsMachineLearningExam.repository.AppUserRepository appUserRepository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Autowired
    private com.example.awsMachineLearningExam.repository.PipelineScenarioRepository pipelineRepository;

    @Autowired
    private com.example.awsMachineLearningExam.repository.ReviewRepository reviewRepository;

    @Autowired
    private com.example.awsMachineLearningExam.repository.QuestionRepository questionRepository;

    // --- QUIZ QUESTION ENDPOINTS ---
    @GetMapping("/all")
    public ResponseEntity<?> getAllQuestions() {
        return ResponseEntity.ok(quizRepository.findAll());
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadCsv(@RequestHeader("X-Admin-Key") String adminKey, @RequestParam("file") MultipartFile file) {
        if (!"masterkey".equals(adminKey)) {
            return ResponseEntity.status(403).body(Map.of("error", "Security Breach: Unauthorized Admin Access."));
        }

        String result = csvParserService.processCsvFile(file);
        if (result.startsWith("Error")) return ResponseEntity.badRequest().body(Map.of("error", result));
        return ResponseEntity.ok(Map.of("message", result));
    }

    // --- 🎴 FLASHCARD ENDPOINTS ---
    @GetMapping("/flashcards/all")
    public ResponseEntity<?> getAllFlashcards() {
        return ResponseEntity.ok(flashcardRepository.findAll());
    }

    @PostMapping("/flashcards/upload")
    public ResponseEntity<?> uploadFlashcards(@RequestHeader("X-Admin-Key") String adminKey, @RequestParam("file") MultipartFile file) {
        if (!"masterkey".equals(adminKey)) {
            return ResponseEntity.status(403).body(Map.of("error", "Security Breach: Unauthorized Admin Access."));
        }

        String result = csvParserService.processFlashcardCsvFile(file);
        if (result.startsWith("Error")) return ResponseEntity.badRequest().body(Map.of("error", result));
        return ResponseEntity.ok(Map.of("message", result));
    }

    @DeleteMapping("/flashcards/{id}")
    public ResponseEntity<?> deleteFlashcard(@PathVariable Long id) {
        flashcardRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Vaporized"));
    }

    // Batch fetch questions by IDs (for Review Room)
    @GetMapping("/batch")
    public ResponseEntity<?> getQuestionsByIds(@RequestParam String ids) {
        try {
            List<Long> idList = java.util.Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .toList();
            List<com.example.awsMachineLearningExam.model.Question> questions = questionRepository.findByIdIn(idList);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid question IDs: " + e.getMessage()));
        }
    }

    // 1. Fetches a random question the user hasn't seen yet
    @GetMapping("/random")
    public ResponseEntity<?> getRandomQuestion(
            @RequestParam String examCode,
            @RequestParam(required = false) java.util.List<Long> excludeIds) {

        // Get all questions for this specific cert
        java.util.List<QuizQuestion> allQuestions = quizRepository.findByCertCode(examCode);

        // Filter out the ones the user has already answered this session
        if (excludeIds != null && !excludeIds.isEmpty()) {
            allQuestions.removeIf(q -> excludeIds.contains(q.getId()));
        }

        // If the bank is actually empty, tell Vue to end the game!
        if (allQuestions.isEmpty()) {
            return ResponseEntity.noContent().build(); // Triggers the 204 status
        }

        // Pick a random question from the remaining list
        int randomIndex = (int) (Math.random() * allQuestions.size());
        return ResponseEntity.ok(allQuestions.get(randomIndex));
    }

    // 2. Fetches all flashcards for the matching game (BULLETPROOF VERSION)
    @GetMapping("/flashcards/game")
    public ResponseEntity<?> getFlashcardsForGame(@RequestParam String examCode) {
        // Fetch ALL flashcards from the database
        java.util.List<com.example.awsMachineLearningExam.model.Flashcard> allCards = flashcardRepository.findAll();

        // Safely filter them: Ignore uppercase/lowercase and strip out invisible spaces!
        java.util.List<com.example.awsMachineLearningExam.model.Flashcard> matchingCards = allCards.stream()
                .filter(card -> card.getExamCode() != null &&
                        card.getExamCode().trim().equalsIgnoreCase(examCode.trim()))
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(matchingCards);
    }

    // 1. Fetch User Exam History
    @GetMapping("/history")
    public ResponseEntity<?> getUserHistory(@RequestParam Long userId) {
        List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        var historyList = examHistoryRepository.findByUser_Id(userId);

        for (var h : historyList) {
            java.util.Map<String, Object> entry = new java.util.LinkedHashMap<>();
            entry.put("id", h.getId());
            entry.put("examCode", h.getExamCode());
            entry.put("scorePercentage", h.getScorePercentage());
            entry.put("totalQuestions", h.getTotalQuestions());
            entry.put("correctCount", h.getCorrectCount());
            entry.put("timeSpentSeconds", h.getTimeSpentSeconds());
            entry.put("weakestCategory", h.getWeakestCategory());
            entry.put("missedQuestionIds", h.getMissedQuestionIds());
            entry.put("completedAt", h.getCompletedAt() != null ? h.getCompletedAt().toString() : null);
            result.add(entry);
        }

        return ResponseEntity.ok(result);
    }

    // 2. Save New Exam Results
    @PostMapping("/history/save")
    public ResponseEntity<?> saveExamHistory(
            @RequestParam Long userId,
            @RequestBody com.example.awsMachineLearningExam.model.ExamHistory historyData) {

        // 1. Fetch your official profile from the database
        com.example.awsMachineLearningExam.model.AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. FORCE the connection! Bolt the exam to your identity.
        historyData.setUser(user);
        historyData.setCompletedAt(java.time.LocalDateTime.now());

        examHistoryRepository.save(historyData);
        return ResponseEntity.ok().build();
    }

    // 2b. Delete a specific exam history entry
    @DeleteMapping("/history/{id}")
    public ResponseEntity<?> deleteHistoryEntry(@PathVariable Long id) {
        if (!examHistoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        examHistoryRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // 3. Fetch Ascended Masters Leaderboard
    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard() {
        // Fetches the top scores. If you don't have a custom query yet, this returns an empty list to stop the 404 crash!
        try {
            java.util.List<?> topScores = examHistoryRepository.findTop10ByOrderByScorePercentageDesc();
            return ResponseEntity.ok(topScores);
        } catch (Exception e) {
            return ResponseEntity.ok(java.util.List.of()); // Failsafe
        }
    }

    // 🚨 1. THE SECURED ADMIN UPLOAD
    @PostMapping("/pipeline/upload")
    public ResponseEntity<?> uploadPipelineScenarios(
            @RequestHeader(value = "X-Admin-Key", defaultValue = "none") String adminKey,
            @RequestParam("file") MultipartFile file) {

        if (!"masterkey".equals(adminKey)) { // Use your real master key here!
            return ResponseEntity.status(403).body(Map.of("error", "Security Breach: Unauthorized Admin Access."));
        }

        String result = csvParserService.processPipelineCsvFile(file);
        if (result.startsWith("Error")) return ResponseEntity.badRequest().body(Map.of("error", result));
        return ResponseEntity.ok(Map.of("message", result));
    }

    // 🚨 2. THE GAMEPLAY FETCH (Pulls a random scenario for the active exam)
    @GetMapping("/pipeline/random")
    public ResponseEntity<?> getRandomScenario(@RequestParam String examCode) {
        java.util.List<com.example.awsMachineLearningExam.model.PipelineScenario> scenarios = pipelineRepository.findByExamCode(examCode);

        if (scenarios.isEmpty()) {
            return ResponseEntity.notFound().build(); // No scenarios exist for this exam yet!
        }

        // Pick a random scenario from the list
        int randomIndex = new java.util.Random().nextInt(scenarios.size());
        return ResponseEntity.ok(scenarios.get(randomIndex));
    }
}