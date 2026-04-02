package com.example.awsMachineLearningExam.service;

import com.example.awsMachineLearningExam.model.Option;
import com.example.awsMachineLearningExam.model.Question;
import com.example.awsMachineLearningExam.repository.QuestionRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionImporterService {

    @Autowired
    private QuestionRepository questionRepository;

    @Transactional
    public void importQuestions(InputStream inputStream) throws Exception {
        // Clear old bad data before importing the new batch!
        // (Optional: remove this line if you want to keep adding to the DB instead of replacing)
        questionRepository.deleteAll();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

            List<String[]> rows = csvReader.readAll();
            for (String[] row : rows) {
                // Skip completely empty rows
                if (row == null || row.length < 4) continue;

                Question q = new Question();
                q.setCategory(row[0].trim());
                q.setExamCode(row[1].trim());
                q.setQuestionText(row[2].trim());
                q.setExplanation(row[3].trim());

                List<Option> options = new ArrayList<>();

                // Iterate through columns 4-11 (OptionText, IsCorrect pairs)
                for (int i = 4; i < 12; i += 2) {
                    // Make sure the option text exists
                    if (i < row.length && row[i] != null && !row[i].trim().isEmpty()) {
                        Option opt = new Option();
                        opt.setOptionText(row[i].trim());

                        // THE FIX: Aggressively clean the true/false string before parsing
                        boolean isCorrect = false;
                        if (i + 1 < row.length && row[i + 1] != null) {
                            String rawBool = row[i + 1].trim().toLowerCase().replace("\"", "");

                            System.out.println("Scanning Option: [" + opt.getOptionText() + "] | Found True/False value: [" + rawBool + "]");

                            isCorrect = Boolean.parseBoolean(rawBool);
                        }

                        opt.setCorrect(isCorrect);
                        opt.setQuestion(q);
                        options.add(opt);
                    }
                }
                q.setOptions(options);
                questionRepository.save(q);
            }
        }
    }
}