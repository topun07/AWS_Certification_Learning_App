package com.example.awsMachineLearningExam.model;

import jakarta.persistence.*;

@Entity
@Table(name = "study_materials")
public class StudyMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which exam does this belong to? (e.g., "AIF-C01")
    @Column(nullable = false)
    private String certCode;

    // Which category? (e.g., "Fundamentals of AI")
    @Column(nullable = false)
    private String domain;

    // Title of the reading material
    @Column(nullable = false)
    private String title;

    // 🚨 THE FIX: "TEXT" allows for massive Markdown documents!
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCertCode() { return certCode; }
    public void setCertCode(String certCode) { this.certCode = certCode; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}