package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.ExamHistory;
import com.example.awsMachineLearningExam.model.Option;
import com.example.awsMachineLearningExam.model.Question;
import com.example.awsMachineLearningExam.repository.ExamHistoryRepository;
import com.example.awsMachineLearningExam.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, maxAge = 3600)
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionRepository repository;

    @Autowired
    private ExamHistoryRepository examHistoryRepository;

    @Autowired
    private QuestionRepository questionRepository;

           // --- 1. Question Retrieval ---

    @GetMapping
    public List<Question> getAllQuestions() {
        return repository.findAll();
    }

    @GetMapping("/random")
    public ResponseEntity<Question> getRandomQuestion(
            @RequestParam String examCode,
            @RequestParam(required = false) List<Long> excludeIds) {

        Question q;

        // If the list is empty/null, run the standard query
        if (excludeIds == null || excludeIds.isEmpty()) {
            q = questionRepository.findRandomByExamCode(examCode);
        } else {
            // If we have IDs to exclude, use the new memory filter query!
            q = questionRepository.findRandomByExamCodeExcluding(examCode, excludeIds);
        }

        if (q == null) {
            return ResponseEntity.noContent().build(); // Bank is empty!
        }
        return ResponseEntity.ok(q);
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
        List<Object[]> rawData = examHistoryRepository.findTopScores(3);

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

        // 1. Fetch the ExamHistory record first to get the string of missed IDs
        // (Note: ensure you have examHistoryRepository autowired at the top of your class!)
        Optional<ExamHistory> historyOpt = examHistoryRepository.findById(attemptId);

        if (historyOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ExamHistory history = historyOpt.get();
        String missedIdsString = history.getMissedQuestionIds();

        // 2. Safety Check: If they got a 100% and missed nothing!
        if (missedIdsString == null || missedIdsString.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // 3. Convert the string "12,45,88" into a List<Long> [12, 45, 88]
        List<Long> questionIds = Arrays.stream(missedIdsString.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toList());

        // 4. Fetch the questions using our new bulletproof repository method!
        List<Question> questions = questionRepository.findByIdIn(questionIds);

        if (questions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(questions);
    }

    // --- The Bulletproof Batch Fetcher ---
    @GetMapping("/batch")
    public ResponseEntity<?> getQuestionsBatch(@RequestParam("ids") String idsString) {
        try {
            // 1. Safety check: Did Vue accidentally send an empty string?
            if (idsString == null || idsString.trim().isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            // 2. Chop up the "12,15,18" string safely, ignoring spaces and trailing commas
            List<Long> idsList = new ArrayList<>();
            for (String idPart : idsString.split(",")) {
                String cleanId = idPart.trim();
                if (!cleanId.isEmpty()) {
                    idsList.add(Long.parseLong(cleanId));
                }
            }

            // 3. Fetch from the database!
            // (Using .forEach here prevents a crash if your repository returns an Iterable instead of a List)
            List<Question> missedQuestions = new ArrayList<>();
            repository.findAllById(idsList).forEach(missedQuestions::add);

            return ResponseEntity.ok(missedQuestions);

        } catch (NumberFormatException e) {
            System.err.println("CRITICAL FAILURE: Vue sent a bad ID format: " + idsString);
            return ResponseEntity.badRequest().body("Error: Invalid ID format sent from Vue.");
        } catch (Exception e) {
            e.printStackTrace(); // This prints the EXACT error line in your terminal if it crashes!
            return ResponseEntity.status(500).body("Error retrieving Jedi Archives: " + e.getMessage());
        }
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Upload failed: The file is empty.");
        }

        List<Question> questionsToSave = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // We still only REQUIRE 12 columns (4 options) to proceed
                if (data.length >= 12) {
                    Question q = new Question();
                    q.setCategory(data[0].replace("\"", "").trim());
                    q.setExamCode(data[1].replace("\"", "").trim());
                    q.setQuestionText(data[2].replace("\"", "").trim());
                    q.setExplanation(data[3].replace("\"", "").trim());

                    List<Option> options = new ArrayList<>();

                    // Option 1
                    Option o1 = new Option();
                    o1.setOptionText(data[4].replace("\"", "").trim());
                    o1.setCorrect(Boolean.parseBoolean(data[5].replace("\"", "").trim()));
                    o1.setQuestion(q);
                    options.add(o1);

                    // Option 2
                    Option o2 = new Option();
                    o2.setOptionText(data[6].replace("\"", "").trim());
                    o2.setCorrect(Boolean.parseBoolean(data[7].replace("\"", "").trim()));
                    o2.setQuestion(q);
                    options.add(o2);

                    // Option 3
                    Option o3 = new Option();
                    o3.setOptionText(data[8].replace("\"", "").trim());
                    o3.setCorrect(Boolean.parseBoolean(data[9].replace("\"", "").trim()));
                    o3.setQuestion(q);
                    options.add(o3);

                    // Option 4
                    Option o4 = new Option();
                    o4.setOptionText(data[10].replace("\"", "").trim());
                    o4.setCorrect(Boolean.parseBoolean(data[11].replace("\"", "").trim()));
                    o4.setQuestion(q);
                    options.add(o4);

                    // --- THE UPGRADE: Dynamic Option 5 ---
                    // Check if the CSV row actually has 14 columns AND the 5th option isn't just an empty string
                    if (data.length >= 14) {
                        String opt5Text = data[12].replace("\"", "").trim();
                        if (!opt5Text.isEmpty()) {
                            Option o5 = new Option();
                            o5.setOptionText(opt5Text);
                            o5.setCorrect(Boolean.parseBoolean(data[13].replace("\"", "").trim()));
                            o5.setQuestion(q);
                            options.add(o5);
                        }
                    }

                    // --- THE UPGRADE: Dynamic Option 6 ---
                    // Check if the CSV row has 16 columns for a 6th option
                    if (data.length >= 16) {
                        String opt6Text = data[14].replace("\"", "").trim();
                        if (!opt6Text.isEmpty()) {
                            Option o6 = new Option();
                            o6.setOptionText(opt6Text);
                            o6.setCorrect(Boolean.parseBoolean(data[15].replace("\"", "").trim()));
                            o6.setQuestion(q);
                            options.add(o6);
                        }
                    }

                    q.setOptions(options);
                    questionsToSave.add(q);
                }
            }

            // 5. Save the entire batch to the database at once!
            repository.saveAll(questionsToSave);

            System.out.println("Jedi Archives Expanded: " + questionsToSave.size() + " records added.");
            return ResponseEntity.ok("Success");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to parse CSV: " + e.getMessage());
        }
    }

}