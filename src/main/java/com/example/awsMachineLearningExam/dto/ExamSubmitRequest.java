package com.example.awsMachineLearningExam.dto;
import java.util.List;

public class ExamSubmitRequest {
    private Long userId;
    private String examCode;
    private List<AnswerRecord> answers;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getExamCode() { return examCode; }
    public void setExamCode(String examCode) { this.examCode = examCode; }
    public List<AnswerRecord> getAnswers() { return answers; }
    public void setAnswers(List<AnswerRecord> answers) { this.answers = answers; }

    public static class AnswerRecord {
        private Long questionId; // CRITICAL: We need this to track missed questions
        private String category;
        private boolean isCorrect;

        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public boolean isCorrect() { return isCorrect; }
        public void setCorrect(boolean correct) { this.isCorrect = correct; }
    }
}