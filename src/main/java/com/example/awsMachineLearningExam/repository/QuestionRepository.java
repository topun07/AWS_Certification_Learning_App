package com.example.awsMachineLearningExam.repository;

import com.example.awsMachineLearningExam.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Use Native Query for the random selector (it's more reliable for RANDOM())
    @Query(value = "SELECT * FROM questions WHERE exam_code = :examCode " +
            "AND (:category = 'All' OR category = :category) " +
            "ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Question findRandom(@Param("examCode") String examCode, @Param("category") String category);

    // Use a simpler JPQL count query
    @Query("SELECT COUNT(q) FROM Question q WHERE q.examCode = :examCode " +
            "AND (:category = 'All' OR q.category = :category)")
    long countByExamAndCategory(@Param("examCode") String examCode, @Param("category") String category);
}

