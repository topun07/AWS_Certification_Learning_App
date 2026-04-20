package com.example.awsMachineLearningExam.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_progress")
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    // 🚨 1. The Field Type 🚨
    private Long moduleId;

    // Default Constructor required by Spring
    public UserProgress() {}

    // 🚨 2. The Constructor (Fixes Error #1) 🚨
    public UserProgress(String username, Long moduleId) {
        this.username = username;
        this.moduleId = moduleId;
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    // 🚨 3. The Getter (Fixes Error #2) 🚨
    public Long getModuleId() { return moduleId; }

    // 🚨 4. The Setter 🚨
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
}