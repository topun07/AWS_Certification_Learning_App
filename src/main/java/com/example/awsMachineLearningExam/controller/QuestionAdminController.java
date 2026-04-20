package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.Question; // Assumes your entity is named Question
import com.example.awsMachineLearningExam.repository.QuestionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import com.example.awsMachineLearningExam.model.Option;

import java.util.List;

@RestController
@RequestMapping("/api/admin/questions")
public class QuestionAdminController {

    private final QuestionRepository questionRepository;

    public QuestionAdminController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    // --- 1. READ: Download every question in the database ---
    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionRepository.findAll());
    }

    // --- 2. CREATE: Inject a brand new question ---
    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody Question newQuestion) {
        // Spring Boot automatically generates a new ID and saves it
        Question savedQuestion = questionRepository.save(newQuestion);
        return ResponseEntity.ok(savedQuestion);
    }

    // --- 3. UPDATE: Fix a typo or change a correct answer ---
    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id, @RequestBody Question updatedQuestion) {
        if (!questionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Force the ID to match the URL so we overwrite the existing row instead of making a duplicate
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

    // --- 5. DATA PIPELINE V2: Bulk CSV Importer (With Options!) ---
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
                // Skip the header row
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Split by comma, but ignore commas inside quotation marks
                String[] columns = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                // Ensure the row has at least the minimum columns to avoid crashing on blank lines
                if (columns.length >= 6) {
                    Question q = new Question();

                    // Map the first 4 standard columns
                    q.setCategory(cleanCSV(columns[0]));
                    q.setExamCode(cleanCSV(columns[1]));
                    q.setQuestionText(cleanCSV(columns[2]));
                    q.setExplanation(cleanCSV(columns[3]));

                    List<Option> optionsList = new ArrayList<>();
                    List<String> correctAnswersList = new ArrayList<>();

                    // Dynamically loop through the 6 potential Option/IsCorrect pairs
                    // (Columns 4/5, 6/7, 8/9, 10/11, 12/13, 14/15)
                    for (int i = 4; i < columns.length - 1; i += 2) {
                        String optText = cleanCSV(columns[i]);
                        String isCorrectStr = cleanCSV(columns[i + 1]);

                        // Only build an option if the text isn't blank
                        if (!optText.isEmpty()) {
                            Option opt = new Option();
                            opt.setOptionText(optText); // (Change to .setText(optText) if your Option.java uses that)
                            opt.setQuestion(q);
                            optionsList.add(opt);

                            // Check if the CSV marked this as a correct answer (True, true, 1, etc.)
                            if (isCorrectStr.equalsIgnoreCase("true") || isCorrectStr.equals("1")) {
                                correctAnswersList.add(optText);
                            }
                        }
                    }

                    q.setOptions(optionsList);

                    // Auto-configure the multi-select behavior based on how many "True" answers were found
                    q.setCorrectAnswerCount(correctAnswersList.size());
                    q.setMultiSelect(correctAnswersList.size() > 1);

                    // Store all correct answers in the string (separated by a pipe | if there are multiple)
                    q.setCorrectAnswer(String.join(" | ", correctAnswersList));

                    newQuestions.add(q);
                }
            }

            // Save the massive batch in one transaction
            questionRepository.saveAll(newQuestions);

            return ResponseEntity.ok(java.util.Map.of(
                    "message", "Data Pipeline Successful!",
                    "count", newQuestions.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Pipeline Failure: " + e.getMessage());
        }
    }

    // Helper method to strip messy quotation marks from Excel/CSV exports
    private String cleanCSV(String input) {
        if (input == null) return "";
        return input.replaceAll("^\"|\"$", "").trim();
    }
}