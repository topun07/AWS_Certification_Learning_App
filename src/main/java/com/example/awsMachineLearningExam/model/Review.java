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

    // 🚨 NEW COLUMN
    private String jobTitle;

    private LocalDateTime createdAt = LocalDateTime.now();

    // 🚨 CONSTRUCTOR 1: Default constructor required by Spring Data JPA
    public Review() {}

    // 🚨 CONSTRUCTOR 2: The exact 4-argument constructor your Seeder is looking for!
    public Review(String authorName, int rating, String reviewText, String jobTitle) {
        this.authorName = authorName;
        this.rating = rating;
        this.reviewText = reviewText;
        this.jobTitle = jobTitle;
    }

    // --- GETTERS ---
    public Long getId() { return id; }
    public String getAuthorName() { return authorName; }
    public int getRating() { return rating; }
    public String getReviewText() { return reviewText; }
    public String getJobTitle() { return jobTitle; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // --- SETTERS ---
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setRating(int rating) { this.rating = rating; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
}