package com.example.awsMachineLearningExam.repository;

import com.example.awsMachineLearningExam.model.CategoryMastery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryMasteryRepository extends JpaRepository<CategoryMastery, Long> {

    // Finds a specific category stat for a specific user (Used when updating scores)
    Optional<CategoryMastery> findByUserIdAndCategory(Long userId, String category);

    // Grabs ALL category stats for a specific user (Used to build the Radar Chart)
    List<CategoryMastery> findAllByUserId(Long userId);
}