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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

            List<String[]> rows = csvReader.readAll();
            for (String[] row : rows) {
                Question q = new Question();
                q.setCategory(row[0]);
                q.setExamCode(row[1]);
                q.setQuestionText(row[2]);
                q.setExplanation(row[3]);

                List<Option> options = new ArrayList<>();
                // Iterate through columns 4-11 (OptionText, IsCorrect pairs)
                for (int i = 4; i < 12; i += 2) {
                    if (i < row.length && row[i] != null && !row[i].isEmpty()) {
                        Option opt = new Option();
                        opt.setOptionText(row[i]);
                        opt.setCorrect(Boolean.parseBoolean(row[i + 1]));
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