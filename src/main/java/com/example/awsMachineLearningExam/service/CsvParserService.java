package com.example.awsMachineLearningExam.service;

import com.example.awsMachineLearningExam.model.QuizQuestion;
import com.example.awsMachineLearningExam.repository.QuizQuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvParserService {

    private final QuizQuestionRepository repository;

    public CsvParserService(QuizQuestionRepository repository) {
        this.repository = repository;
    }

    public String processCsvFile(MultipartFile file) {
        if (file.isEmpty()) {
            return "Error: File is empty.";
        }

        List<QuizQuestion> questionsToSave = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstRow = true;

            while ((line = reader.readLine()) != null) {
                // Skip the header row
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                // This regex splits by comma, but ignores commas that are wrapped in quotes!
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // Check if the row has exactly 9 columns
                if (data.length >= 9) {
                    QuizQuestion question = new QuizQuestion(
                            data[0].replace("\"", "").trim(), // certCode
                            data[1].replace("\"", "").trim(), // domain
                            data[2].replace("\"", "").trim(), // questionText
                            data[3].replace("\"", "").trim(), // optionA
                            data[4].replace("\"", "").trim(), // optionB
                            data[5].replace("\"", "").trim(), // optionC
                            data[6].replace("\"", "").trim(), // optionD
                            data[7].replace("\"", "").trim(), // correctOption
                            data[8].replace("\"", "").trim()  // explanation
                    );
                    questionsToSave.add(question);
                }
            }

            // Mass save to the H2 Database!
            repository.saveAll(questionsToSave);
            return "Success! Loaded " + questionsToSave.size() + " questions into the Jedi Archives.";

        } catch (Exception e) {
            return "Error processing CSV: " + e.getMessage();
        }
    }
}