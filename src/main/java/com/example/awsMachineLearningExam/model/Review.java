package com.example.awsMachineLearningExam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authorName;
    private int rating; // 1 to 5

    @Column(length = 1000)
    private String reviewText;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public Review() {}
    public Review(String authorName, int rating, String reviewText) {
        this.authorName = authorName;
        this.rating = rating;
        this.reviewText = reviewText;
    }

    // Getters
    public Long getId() { return id; }
    public String getAuthorName() { return authorName; }
    public int getRating() { return rating; }
    public String getReviewText() { return reviewText; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setRating(int rating) { this.rating = rating; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
}