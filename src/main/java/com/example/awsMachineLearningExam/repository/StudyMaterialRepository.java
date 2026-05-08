package com.example.awsMachineLearningExam.repository;

import com.example.awsMachineLearningExam.model.StudyMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {
    // 🚨 This lets us search for the exact document by Cert and Domain
    Optional<StudyMaterial> findByCertCodeAndDomain(String certCode, String domain);
}