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

    @Column(name = "question_text")
    private String questionText;

    @Column(name = "explanation")
    private String explanation;

    @Column(name = "category")
    private String category;

    @Column(name = "is_multi_select")
    private boolean isMultiSelect;

    @Column(name = "exam_code")
    private String examCode;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Option> options;

    // Default Constructor
    public Question() {}

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
}