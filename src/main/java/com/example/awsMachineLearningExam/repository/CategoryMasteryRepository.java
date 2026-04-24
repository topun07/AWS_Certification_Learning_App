package com.example.awsMachineLearningExam.repository;

import com.example.awsMachineLearningExam.model.CategoryMastery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryMasteryRepository extends JpaRepository<CategoryMastery, Long> {

    // 1. Used by ProgressController (The one we just fixed!)
    Optional<CategoryMastery> findByUserIdAndCategory(Long userId, String category);

    // 3. Used to grab everything
    List<CategoryMastery> findAllByUserId(Long userId);
}