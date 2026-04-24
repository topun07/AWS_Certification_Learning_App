package com.example.awsMachineLearningExam.dto;

import java.util.List;

public class RadarScoreResponseDTO {
    private String certCode;
    private List<Integer> scores;

    public RadarScoreResponseDTO(String certCode, List<Integer> scores) {
        this.certCode = certCode;
        this.scores = scores;
    }


    // Getters and Setters
    public String getCertCode() { return certCode; }
    public void setCertCode(String certCode) { this.certCode = certCode; }

    public List<Integer> getScores() { return scores; }
    public void setScores(List<Integer> scores) { this.scores = scores; }
}