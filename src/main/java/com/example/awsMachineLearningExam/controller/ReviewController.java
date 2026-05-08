package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.Review;
import com.example.awsMachineLearningExam.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
// 🚨 Mirroring the exact CORS settings that work on your other Controller!
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/public/reviews")
    public ResponseEntity<?> getTopReviews() {
        try {
            // Attempt to fetch the top 3 reviews
            List<Review> topReviews = reviewRepository.findTop3ByRatingGreaterThanEqualOrderByCreatedAtDesc(4);
            return ResponseEntity.ok(topReviews);

        } catch (Exception e) {
            // 🚨 IF IT CRASHES, PRINT THE EXACT SQL ERROR TO THE CONSOLE AND BROWSER!
            System.err.println("❌ REVIEW DB CRASH: " + e.getMessage());

            return ResponseEntity.status(500).body(Map.of(
                    "error", "The backend crashed while looking for the Reviews table.",
                    "reason", e.getMessage() != null ? e.getMessage() : "Unknown Exception"
            ));
        }
    }

    // 🚨 THE IN-HOUSE SUBMISSION ENDPOINT
    @PostMapping("/reviews")
    public ResponseEntity<?> submitReview(@RequestBody Review newReview) {
        try {
            // The Review.java entity automatically sets the CreatedAt timestamp,
            // so all we have to do is save the text, rating, and author name!
            reviewRepository.save(newReview);
            return ResponseEntity.ok(java.util.Map.of("message", "Review successfully forged."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to save review."));
        }
    }
}