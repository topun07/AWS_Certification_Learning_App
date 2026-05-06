package com.example.awsMachineLearningExam.repository;

import com.example.awsMachineLearningExam.model.PipelineScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PipelineScenarioRepository extends JpaRepository<PipelineScenario, Long> {
    // 🚨 This tells Java to find scenarios for a specific exam (like "AIF-C01")
    List<PipelineScenario> findByExamCode(String examCode);
}