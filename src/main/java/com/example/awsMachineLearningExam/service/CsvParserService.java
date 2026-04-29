package com.example.awsMachineLearningExam.service;

import com.example.awsMachineLearningExam.model.Flashcard;
import com.example.awsMachineLearningExam.model.QuizQuestion;
import com.example.awsMachineLearningExam.repository.FlashcardRepository;
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

    private final QuizQuestionRepository quizRepository;
    private final FlashcardRepository flashcardRepository;

    public CsvParserService(QuizQuestionRepository quizRepository, FlashcardRepository flashcardRepository) {
        this.quizRepository = quizRepository;
        this.flashcardRepository = flashcardRepository;
    }

    // ==========================================
    // 1. THE 16-COLUMN QUIZ PIPELINE
    // ==========================================
    public String processCsvFile(MultipartFile file) {
        if (file.isEmpty()) return "Error: File is empty.";

        List<QuizQuestion> questionsToSave = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstRow = true;

            while ((line = reader.readLine()) != null) {
                // Strip hidden UTF-8 BOM characters and ignore completely blank rows
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;

                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                // Split by comma, ignoring commas wrapped in quotes
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                // The new CSV structure (Category, ExamCode, Question, Explanation, Opt1, isOpt1, etc.)
                if (data.length >= 6) {
                    String domain = data[0].replace("\"", "").trim();
                    String certCode = data[1].replace("\"", "").trim();
                    String questionText = data[2].replace("\"", "").trim();
                    String explanation = data[3].replace("\"", "").trim();

                    // Safely extract options and their True/False flags
                    String optionA = data.length > 4 ? data[4].replace("\"", "").trim() : "";
                    boolean isCorrectA = data.length > 5 && data[5].replace("\"", "").trim().equalsIgnoreCase("true");

                    String optionB = data.length > 6 ? data[6].replace("\"", "").trim() : "";
                    boolean isCorrectB = data.length > 7 && data[7].replace("\"", "").trim().equalsIgnoreCase("true");

                    String optionC = data.length > 8 ? data[8].replace("\"", "").trim() : "";
                    boolean isCorrectC = data.length > 9 && data[9].replace("\"", "").trim().equalsIgnoreCase("true");

                    String optionD = data.length > 10 ? data[10].replace("\"", "").trim() : "";
                    boolean isCorrectD = data.length > 11 && data[11].replace("\"", "").trim().equalsIgnoreCase("true");

                    String optionE = data.length > 12 ? data[12].replace("\"", "").trim() : "";
                    boolean isCorrectE = data.length > 13 && data[13].replace("\"", "").trim().equalsIgnoreCase("true");

                    String optionF = data.length > 14 ? data[14].replace("\"", "").trim() : "";
                    boolean isCorrectF = data.length > 15 && data[15].replace("\"", "").trim().equalsIgnoreCase("true");

                    // Gather all correct answers and separate them with a pipe (||)
                    List<String> correctAnswers = new ArrayList<>();
                    if (isCorrectA) correctAnswers.add(optionA);
                    if (isCorrectB) correctAnswers.add(optionB);
                    if (isCorrectC) correctAnswers.add(optionC);
                    if (isCorrectD) correctAnswers.add(optionD);
                    if (isCorrectE) correctAnswers.add(optionE);
                    if (isCorrectF) correctAnswers.add(optionF);

                    String finalCorrectOption = String.join("||", correctAnswers);

                    QuizQuestion question = new QuizQuestion(
                            certCode, domain, questionText, optionA, optionB, optionC, optionD, optionE, optionF, finalCorrectOption, explanation
                    );
                    questionsToSave.add(question);
                }
            }

            quizRepository.saveAll(questionsToSave);
            return "Success! Loaded " + questionsToSave.size() + " questions into the Jedi Archives.";

        } catch (Exception e) {
            return "Error processing CSV: " + e.getMessage();
        }
    }

    // ==========================================
    // 2. THE 4-COLUMN FLASHCARD PIPELINE
    // ==========================================
    public String processFlashcardCsvFile(MultipartFile file) {
        if (file.isEmpty()) return "Error: File is empty.";
        List<Flashcard> flashcardsToSave = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstRow = true;

            while ((line = reader.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;
                if (isFirstRow) { isFirstRow = false; continue; }

                // 🚨 THE FIX: Handle both Pipes (|) and Commas (,) gracefully!
                String[] data;
                if (line.contains("|")) {
                    data = line.split("\\|", -1);
                } else {
                    data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                }

                if (data.length >= 4) {
                    Flashcard flashcard = new Flashcard();
                    flashcard.setExamCode(data[0].replace("\"", "").trim());
                    flashcard.setCategory(data[1].replace("\"", "").trim());
                    flashcard.setTerm(data[2].replace("\"", "").trim());
                    flashcard.setDefinition(data[3].replace("\"", "").trim());
                    flashcardsToSave.add(flashcard);
                }
            }
            flashcardRepository.saveAllAndFlush(flashcardsToSave);
            return "Success! Injected " + flashcardsToSave.size() + " flashcards into the Matrix.";
        } catch (Exception e) {
            return "Error processing Flashcard CSV: " + e.getMessage();
        }
    }
}