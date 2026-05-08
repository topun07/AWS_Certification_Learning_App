package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.Flashcard;
import com.example.awsMachineLearningExam.model.PipelineScenario;
import com.example.awsMachineLearningExam.model.Question;
import com.example.awsMachineLearningExam.repository.QuestionRepository;
import com.example.awsMachineLearningExam.model.Option;
import com.example.awsMachineLearningExam.repository.FlashcardRepository;
import com.example.awsMachineLearningExam.repository.PipelineScenarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/questions")
@CrossOrigin(origins = "http://localhost:5173")
public class QuestionAdminController {

    private final QuestionRepository questionRepository;

    // 🚨 1. WE MUST DECLARE THE REPOSITORIES HERE!
    private final PipelineScenarioRepository pipelineRepository;
    private final FlashcardRepository flashcardRepository;

    // 🚨 2. WE MUST INJECT THEM IN THE CONSTRUCTOR!
    public QuestionAdminController(QuestionRepository questionRepository,
                                   PipelineScenarioRepository pipelineRepository,
                                   FlashcardRepository flashcardRepository) {
        this.questionRepository = questionRepository;
        this.pipelineRepository = pipelineRepository;
        this.flashcardRepository = flashcardRepository;
    }

    // --- 1. READ: Download every question in the database ---
    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionRepository.findAll());
    }

    @GetMapping("/pipeline/all")
    public ResponseEntity<?> getAllPipelines() {
        try {
            List<PipelineScenario> pipelines = pipelineRepository.findAll();
            return ResponseEntity.ok(pipelines);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch pipelines."));
        }
    }

    @GetMapping("/flashcards/all")
    public ResponseEntity<?> getAllAdminFlashcards() {
        try {
            List<Flashcard> flashcards = flashcardRepository.findAll();
            return ResponseEntity.ok(flashcards);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch flashcards."));
        }
    }

    // --- 2. CREATE: Inject a brand new question ---
    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody Question newQuestion) {
        Question savedQuestion = questionRepository.save(newQuestion);
        return ResponseEntity.ok(savedQuestion);
    }

    // --- 3. UPDATE: Fix a typo or change a correct answer ---
    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id, @RequestBody Question updatedQuestion) {
        if (!questionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        updatedQuestion.setId(id);
        Question saved = questionRepository.save(updatedQuestion);
        return ResponseEntity.ok(saved);
    }

    // --- 4. DELETE: Vaporize a bad question from existence ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        if (!questionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        questionRepository.deleteById(id);
        return ResponseEntity.ok(java.util.Map.of("message", "Question vaporized successfully."));
    }

    // --- 5. DATA PIPELINE V3: The 16-Column Master Importer ---
    @PostMapping("/upload")
    public ResponseEntity<?> uploadCSVDatabase(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("CSV file is empty.");
        }

        List<Question> newQuestions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] columns = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (columns.length >= 6) {
                    Question q = new Question();
                    q.setCategory(cleanCSV(columns[0]));
                    q.setExamCode(cleanCSV(columns[1]));
                    q.setQuestionText(cleanCSV(columns[2]));
                    q.setExplanation(cleanCSV(columns[3]));

                    List<Option> optionsList = new ArrayList<>();
                    List<String> correctAnswersList = new ArrayList<>();

                    for (int i = 4; i < columns.length - 1; i += 2) {
                        String optText = cleanCSV(columns[i]);
                        String isCorrectStr = cleanCSV(columns[i + 1]);

                        if (!optText.isEmpty()) {
                            Option opt = new Option();
                            opt.setOptionText(optText);
                            opt.setQuestion(q);
                            optionsList.add(opt);

                            if (isCorrectStr.equalsIgnoreCase("true") || isCorrectStr.equals("1")) {
                                correctAnswersList.add(optText);
                            }
                        }
                    }

                    q.setOptions(optionsList);
                    q.setCorrectAnswerCount(correctAnswersList.size());
                    q.setMultiSelect(correctAnswersList.size() > 1);
                    q.setCorrectAnswer(String.join(" | ", correctAnswersList));

                    newQuestions.add(q);
                }
            }

            questionRepository.saveAll(newQuestions);

            return ResponseEntity.ok(java.util.Map.of(
                    "message", "Data Pipeline Successful!",
                    "count", newQuestions.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Pipeline Failure: " + e.getMessage());
        }
    }

    private String cleanCSV(String input) {
        if (input == null) return "";
        return input.replaceAll("^\"|\"$", "").trim();
    }
}