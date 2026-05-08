package com.example.awsMachineLearningExam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String eventType; // exam_start, amrap_start, sudden_death_start, flashcard_start, study_material_view

    private String certCode; // e.g., SAA-C03, MLS-C01

    private String detail; // optional extra info (domain name, score, etc.)

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public ActivityLog() {}

    public ActivityLog(String username, String eventType, String certCode, String detail) {
        this.username = username;
        this.eventType = eventType;
        this.certCode = certCode;
        this.detail = detail;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getCertCode() { return certCode; }
    public void setCertCode(String certCode) { this.certCode = certCode; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
