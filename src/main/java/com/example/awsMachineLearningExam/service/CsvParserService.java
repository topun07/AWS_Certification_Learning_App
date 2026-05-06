package com.example.awsMachineLearningExam.service;

import com.example.awsMachineLearningExam.model.Flashcard;
import com.example.awsMachineLearningExam.model.QuizQuestion;
import com.example.awsMachineLearningExam.repository.FlashcardRepository;
import com.example.awsMachineLearningExam.repository.QuizQuestionRepository;
import com.example.awsMachineLearningExam.repository.PipelineScenarioRepository; // 🚨 Added the import!
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

    // 🚨 THE FIX 1: Add the repository at the Class level
    private final PipelineScenarioRepository pipelineRepository;

    // 🚨 THE FIX 2: Inject it through the constructor so Spring Boot wires it up perfectly
    public CsvParserService(QuizQuestionRepository quizRepository,
                            FlashcardRepository flashcardRepository,
                            PipelineScenarioRepository pipelineRepository) {
        this.quizRepository = quizRepository;
        this.flashcardRepository = flashcardRepository;
        this.pipelineRepository = pipelineRepository;
    }

    // ==========================================
    // 1. THE 16-COLUMN QUIZ PIPELINE
    // ==========================================
    public String processCsvFile(MultipartFile file) {

        // 🚨 REMOVE THE @Autowired BLOCK THAT WAS SITTING HERE!

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
    public String processFlashcardCsvFile(org.springframework.web.multipart.MultipartFile file) {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            java.util.List<com.example.awsMachineLearningExam.model.Flashcard> cardsToSave = new java.util.ArrayList<>();

            while ((line = br.readLine()) != null) {
                // 1. Skip the header row completely! We don't trust it.
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // 2. Split the row by commas, but ignore commas that are inside quotation marks!
                String[] columns = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // 3. Map the data by strict column index (0, 1, 2, 3)
                if (columns.length >= 4) {
                    com.example.awsMachineLearningExam.model.Flashcard card = new com.example.awsMachineLearningExam.model.Flashcard();

                    // .trim() removes invisible spaces, .replace() removes rogue quotation marks
                    card.setExamCode(columns[0].trim().replace("\"", ""));
                    card.setCategory(columns[1].trim().replace("\"", ""));
                    card.setTerm(columns[2].trim().replace("\"", ""));
                    card.setDefinition(columns[3].trim().replace("\"", ""));

                    cardsToSave.add(card);
                }
            }

            // 4. Save the flawless data to the database
            flashcardRepository.saveAll(cardsToSave);
            return "✅ Successfully injected " + cardsToSave.size() + " flashcards into the Matrix!";

        } catch (Exception e) {
            return "❌ Error parsing CSV: " + e.getMessage();
        }
    }

    // 🚨 THE NEW PIPELINE PARSER
    public String processPipelineCsvFile(org.springframework.web.multipart.MultipartFile file) {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(file.getInputStream()))) {
            String line;
            int count = 0;
            br.readLine(); // Skip the header row

            while ((line = br.readLine()) != null) {
                // This regex ignores commas inside quotes!
                String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                if (data.length >= 7) {
                    com.example.awsMachineLearningExam.model.PipelineScenario scenario = new com.example.awsMachineLearningExam.model.PipelineScenario();

                    scenario.setExamCode(data[0].replace("\"", "").trim());
                    scenario.setScenarioDescription(data[1].replace("\"", "").trim());

                    String[] toolbox = data[2].replace("\"", "").split("\\|");
                    scenario.setToolboxServices(java.util.Arrays.asList(toolbox));

                    String[] solution = data[3].replace("\"", "").split("\\|");
                    scenario.setCorrectPipelineOrder(java.util.Arrays.asList(solution));

                    // 🚨 GRAB THE NEW COLUMNS
                    scenario.setHint(data[4].replace("\"", "").trim());
                    scenario.setExplanation(data[5].replace("\"", "").trim());

                    scenario.setHint2(data[5].replace("\"", "").trim());
                    scenario.setExplanation(data[6].replace("\"", "").trim());

                    pipelineRepository.save(scenario);
                    count++;
                }
            }
            return "Mission Accomplished: Injected " + count + " architecture pipelines into the Matrix.";
        } catch (Exception e) {
            return "Error parsing CSV: " + e.getMessage();
        }
    }
}