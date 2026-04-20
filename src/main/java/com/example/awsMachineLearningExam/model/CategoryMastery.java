package com.example.awsMachineLearningExam.model;

import jakarta.persistence.*;

@Entity
@Table(name = "category_mastery")
public class CategoryMastery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The ID of the recruit this stat belongs to
    @Column(nullable = false)
    private Long userId;

    // The AWS Domain (e.g., "Security", "Networking", "Database")
    @Column(nullable = false)
    private String category;

    // Lifetime tracking numbers
    private int totalAttempted = 0;
    private int totalCorrect = 0;

    // Default Constructor required by JPA
    public CategoryMastery() {}

    public CategoryMastery(Long userId, String category) {
        this.userId = userId;
        this.category = category;
    }

    // --- Getters & Setters ---

    public Long getId() { return id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getTotalAttempted() { return totalAttempted; }
    public void setTotalAttempted(int totalAttempted) { this.totalAttempted = totalAttempted; }
    public void addAttempt() { this.totalAttempted++; }

    public int getTotalCorrect() { return totalCorrect; }
    public void setTotalCorrect(int totalCorrect) { this.totalCorrect = totalCorrect; }
    public void addCorrect() { this.totalCorrect++; }

    // Calculates the exact percentage on the fly!
    public int getMasteryPercentage() {
        if (totalAttempted == 0) return 0;
        return (int) Math.round(((double) totalCorrect / totalAttempted) * 100);
    }
}