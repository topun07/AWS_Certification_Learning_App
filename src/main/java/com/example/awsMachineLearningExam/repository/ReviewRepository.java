package com.example.awsMachineLearningExam.repository;

import com.example.awsMachineLearningExam.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 🚨 The Magic Query: Get top 3 most recent reviews with a rating of 4 or 5!
    List<Review> findTop3ByRatingGreaterThanEqualOrderByCreatedAtDesc(int rating);
}