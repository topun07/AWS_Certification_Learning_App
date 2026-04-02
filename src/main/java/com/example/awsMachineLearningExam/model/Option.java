package com.example.awsMachineLearningExam.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "options")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_text")
    private String optionText;

    @Column(name = "is_correct")
    @JsonProperty("isCorrect")
    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonBackReference
    private Question question;

    // Default Constructor
    public Option() {}

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getOptionText() { return optionText; }

    public void setOptionText(String optionText) { this.optionText = optionText; }

    // Unified Getter for Jackson and Vue
    public boolean getIsCorrect() {
        return isCorrect;
    }

    // Unified Setter for the Importer and Hibernate
    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Question getQuestion() { return question; }

    public void setQuestion(Question question) { this.question = question; }
}