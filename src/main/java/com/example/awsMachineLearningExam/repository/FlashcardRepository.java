package com.example.awsMachineLearningExam.repository;

import com.example.awsMachineLearningExam.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {

    // 🚨 This custom method is critical! It allows your Vue frontend
    // to fetch cards matching ONLY the cert the user clicked on.
    List<Flashcard> findByExamCode(String examCode);

}