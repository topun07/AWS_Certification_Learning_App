package com.example.awsMachineLearningExam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_history")
public class ExamHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String missedQuestionIds;

    private String examCode;
    private int scorePercentage;
    private int correctCount;
    private int totalQuestions;
    private int timeSpentSeconds;
    private String weakestCategory;
    private LocalDateTime completedAt = LocalDateTime.now();

    // --- GETTERS (CRITICAL FOR JSON) ---
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Long getUserId() { return userId; } // Added for convenience
    public String getExamCode() { return examCode; }
    public int getScorePercentage() { return scorePercentage; }
    public int getCorrectCount() { return correctCount; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public String getWeakestCategory() { return weakestCategory; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public String getMissedQuestionIds() { return missedQuestionIds; }

    // --- SETTERS ---
    public void setUser(User user) { this.user = user; }
    public void setUserId(Long userId) { this.userId = userId; } // Added for convenience
    public void setExamCode(String examCode) { this.examCode = examCode; }
    public void setScorePercentage(int scorePercentage) { this.scorePercentage = scorePercentage; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }
    public void setWeakestCategory(String weakestCategory) { this.weakestCategory = weakestCategory; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public void setMissedQuestionIds(String missedQuestionIds) { this.missedQuestionIds = missedQuestionIds; }
}

