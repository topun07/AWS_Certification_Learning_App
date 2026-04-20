package com.example.awsMachineLearningExam.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "modules")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which certification does this belong to? (e.g., "SCS-C02")
    private String certCode;

    private String title;
    private String description;
    private String icon;

    // This tells Spring Boot to create a separate linked table just for the YouTube IDs
    @ElementCollection
    private List<String> videoIds;

    // We use TEXT here because study guides can be massively long HTML strings
    @Column(columnDefinition = "TEXT")
    private String studyGuideContent;

    // Default Constructor
    public Module() {}

    public Module(String certCode, String title, String description, String icon, List<String> videoIds, String studyGuideContent) {
        this.certCode = certCode;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.videoIds = videoIds;
        this.studyGuideContent = studyGuideContent;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }

    public String getCertCode() { return certCode; }
    public void setCertCode(String certCode) { this.certCode = certCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public List<String> getVideoIds() { return videoIds; }
    public void setVideoIds(List<String> videoIds) { this.videoIds = videoIds; }

    public String getStudyGuideContent() { return studyGuideContent; }
    public void setStudyGuideContent(String studyGuideContent) { this.studyGuideContent = studyGuideContent; }
}