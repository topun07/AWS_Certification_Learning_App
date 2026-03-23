package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.Question;
import com.example.awsMachineLearningExam.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuestionControllerTest {

    @Autowired
    private QuestionRepository repository;

    @Autowired
    private QuestionController questionController;

    @Test
    void testReviewDataIntegrity() {
        // Now 'questionController' is recognized and this call will work
        ResponseEntity<List<Question>> response = questionController.getReviewData(1L);

        assertNotNull(response.getBody());
        // For Spring Boot 3+, use .value() to get the integer status code
        assertEquals(200, response.getStatusCode().value());
    }
}