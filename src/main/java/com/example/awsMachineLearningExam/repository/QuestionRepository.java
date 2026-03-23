package com.example.awsMachineLearningExam.repository;

import com.example.awsMachineLearningExam.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Add this specific line so Spring Data JPA can build the query
    List<Question> findByExamHistory_Id(Long id);


    // 1. Keep this for the Random Question logic
    @Query(value = "SELECT * FROM questions WHERE exam_code = :examCode " +
            "AND (:category = 'All' OR category = :category) " +
            "ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Question findRandom(@Param("examCode") String examCode, @Param("category") String category);

    // 2. Keep this for Guest Mode
    @Query(value = "SELECT * FROM (SELECT * FROM questions WHERE exam_code = :examCode " +
            "AND (:category = 'All' OR category = :category) " +
            "ORDER BY id ASC LIMIT 10) as sub " +
            "ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Question findRandomGuest(@Param("examCode") String examCode, @Param("category") String category);

    // 3. Use these standard methods (Spring writes the SQL for you)
    // These must match exactly what your Controller calls
    long countByExamCode(String examCode);

    long countByExamCodeAndCategory(String examCode, String category);
}

