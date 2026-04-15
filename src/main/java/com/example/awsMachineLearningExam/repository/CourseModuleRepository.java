package com.example.awsMachineLearningExam.repository;


import com.example.awsMachineLearningExam.model.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseModuleRepository extends JpaRepository<CourseModule, String> {
    List<CourseModule> findByCertCode(String certCode);
}