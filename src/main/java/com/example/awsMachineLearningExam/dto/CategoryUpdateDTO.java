package com.example.awsMachineLearningExam.dto;

public class CategoryUpdateDTO {
    private String category;
    private boolean isCorrect;

    // Getters and Setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
}