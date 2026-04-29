package com.example.awsMachineLearningExam.model;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String certCode;
    private String domain;

    // 🚨 THE FIX: Expand from 255 to 2000 characters!
    @Column(length = 2000)
    private String questionText;

    @Column(length = 1000)
    private String optionA;

    @Column(length = 1000)
    private String optionB;

    @Column(length = 1000)
    private String optionC;

    @Column(length = 1000)
    private String optionD;

    @Column(length = 1000)
    private String optionF;

    @Column(length = 1000)
    private String optionE;

    @Column(length = 1000)
    private String correctOption;

    // 🚨 THE FIX: Explanations are long! Give them 2000 characters.
    @Column(length = 2000)
    private String explanation;

    // Default Constructor
    public QuizQuestion() {
    }

    // Parameterized Constructor
    public QuizQuestion(String certCode, String domain, String questionText, String optionA, String optionB, String optionC, String optionD, String optionE, String optionF, String correctOption, String explanation) {
        this.certCode = certCode;
        this.domain = domain;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.optionE = optionE;
        this.optionF = optionF;
        this.correctOption = correctOption;
        this.explanation = explanation;
    }

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCertCode() { return certCode; }
    public void setCertCode(String certCode) { this.certCode = certCode; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    public String getOptionE() { return optionE; }
    public void setOptionE(String optionE) { this.optionE = optionE; }
    public String getOptionF() { return optionF; }
    public void setOptionF(String optionF) { this.optionF = optionF; }
    public String getCorrectOption() { return correctOption; }
    public void setCorrectOption(String correctOption) { this.correctOption = correctOption; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}