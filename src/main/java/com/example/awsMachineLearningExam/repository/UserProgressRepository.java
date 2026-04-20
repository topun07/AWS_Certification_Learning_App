package com.example.awsMachineLearningExam.repository;

import com.example.awsMachineLearningExam.model.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    // Find all modules completed by a specific user
    List<UserProgress> findByUsername(String username);

    // Check if they already completed it (so they can't spam the button for infinite XP!)
    boolean existsByUsernameAndModuleId(String username, Long moduleId);
}