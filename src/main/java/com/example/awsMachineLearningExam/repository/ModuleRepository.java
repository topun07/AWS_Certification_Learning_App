package com.example.awsMachineLearningExam.repository;

import com.example.awsMachineLearningExam.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    // Grabs all modules associated with a specific AWS certification
    List<Module> findByCertCode(String certCode);
}