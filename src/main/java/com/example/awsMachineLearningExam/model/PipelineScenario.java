package com.example.awsMachineLearningExam.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "pipeline_scenarios")
public class PipelineScenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which exam does this belong to? (e.g., "AIF-C01", "SAA-C03")
    @Column(nullable = false)
    private String examCode;

    // The mission prompt
    @Column(nullable = false, length = 1000)
    private String scenarioDescription;

    // The scrambled puzzle pieces (including trick answers!)
    @ElementCollection
    @CollectionTable(name = "pipeline_toolbox", joinColumns = @JoinColumn(name = "scenario_id"))
    @Column(name = "service_name")
    private List<String> toolboxServices;

    // The actual solution in the exact correct order
    @ElementCollection
    @CollectionTable(name = "pipeline_solution", joinColumns = @JoinColumn(name = "scenario_id"))
    @Column(name = "correct_service")
    private List<String> correctPipelineOrder;

    @Column(length = 1000)
    private String hint;

    @Column(length = 1000)
    private String hint2;

    @Column(length = 2000)
    private String explanation;

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getExamCode() { return examCode; }
    public void setExamCode(String examCode) { this.examCode = examCode; }

    public String getScenarioDescription() { return scenarioDescription; }
    public void setScenarioDescription(String scenarioDescription) { this.scenarioDescription = scenarioDescription; }

    public List<String> getToolboxServices() { return toolboxServices; }
    public void setToolboxServices(List<String> toolboxServices) { this.toolboxServices = toolboxServices; }

    public List<String> getCorrectPipelineOrder() { return correctPipelineOrder; }
    public void setCorrectPipelineOrder(List<String> correctPipelineOrder) { this.correctPipelineOrder = correctPipelineOrder; }

    public String getHint() { return hint; }
    public void setHint(String hint) { this.hint = hint; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getHint2() { return hint2; }
    public void setHint2(String hint2) { this.hint2 = hint2; }
}