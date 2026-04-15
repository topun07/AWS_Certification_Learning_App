package com.example.awsMachineLearningExam.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "course_modules")
public class CourseModule {

    @Id
    private String id; // We will use your frontend IDs here (e.g., "compute", "security")
    private String certCode;

    private String title;
    private String icon;
    private String description;

    @Column(columnDefinition = "TEXT") // Allows for a massive study guide text
    private String studyGuideContent;

    // We use ElementCollection to tell Hibernate to make a mini-table to hold your multiple video IDs
    @ElementCollection
    @CollectionTable(name = "course_module_videos", joinColumns = @JoinColumn(name = "module_id"))
    private List<String> videoIds;

    // --- Constructors ---
    public CourseModule() {}

    public CourseModule(String id, String certCode, String title, String icon, String description, String studyGuideContent, List<String> videoIds) {
        this.id = id;
        this.certCode = certCode; // <-- This is the bridge!
        this.title = title;
        this.icon = icon;
        this.description = description;
        this.studyGuideContent = studyGuideContent;
        this.videoIds = videoIds;
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStudyGuideContent() { return studyGuideContent; }
    public void setStudyGuideContent(String studyGuideContent) { this.studyGuideContent = studyGuideContent; }

    public List<String> getVideoIds() { return videoIds; }
    public void setVideoIds(List<String> videoIds) { this.videoIds = videoIds; }

    public String getCertCode() { return certCode; }
    public void setCertCode(String certCode) { this.certCode = certCode; }
}