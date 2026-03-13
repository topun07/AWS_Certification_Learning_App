package com.example.awsMachineLearningExam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_history")
public class ExamHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String examCode;
    private int scorePercentage;
    private int correctCount;
    private int totalQuestions;
    private int timeSpentSeconds;
    private String weakestCategory;
    private LocalDateTime completedAt = LocalDateTime.now();

    // Getters and Setters
    public void setExamCode(String examCode) { this.examCode = examCode; }
    public void setScorePercentage(int scorePercentage) { this.scorePercentage = scorePercentage; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }
    public void setWeakestCategory(String weakestCategory) { this.weakestCategory = weakestCategory; }
}