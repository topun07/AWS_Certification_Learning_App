package com.example.awsMachineLearningExam.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Set to TEXT to handle long AWS questions (Fixes the 255 char error)
    @Column(columnDefinition = "TEXT")
    private String questionText;

    // FIXED: Only define explanation ONCE
    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "category")
    private String category;

    @Column(name = "is_multi_select")
    private boolean isMultiSelect;

    @Column(name = "exam_code")
    private String examCode;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options;

    // --- NEW: This links the Question to the Exam History for the PDF Review ---
    // If your DB doesn't have this relationship yet, Hibernate will try to create it.
    @ManyToOne
    @JoinColumn(name = "exam_history_id")
    private ExamHistory examHistory;

    public Question() {}

    private Integer correctAnswerCount = 1;

    // --- GETTERS AND SETTERS (The "Keys") ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isMultiSelect() { return isMultiSelect; }
    public void setMultiSelect(boolean multiSelect) { isMultiSelect = multiSelect; }

    public List<Option> getOptions() { return options; }
    public void setOptions(List<Option> options) { this.options = options; }

    public String getExamCode() { return examCode; }
    public void setExamCode(String examCode) { this.examCode = examCode; }

    public ExamHistory getExamHistory() { return examHistory; }
    public void setExamHistory(ExamHistory examHistory) { this.examHistory = examHistory; }

    public Integer getCorrectAnswerCount() {
        return correctAnswerCount;
    }

    public void setCorrectAnswerCount(Integer correctAnswerCount) {
        this.correctAnswerCount = correctAnswerCount;
    }

}