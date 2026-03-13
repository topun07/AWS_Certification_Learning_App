package com.example.awsMachineLearningExam.repository;

import com.example.awsMachineLearningExam.model.ExamHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamHistoryRepository extends JpaRepository<ExamHistory, Long> {
    
    // This will allow us to fetch the most recent scores for the dashboard
    List<ExamHistory> findTop10ByOrderByCompletedAtDesc();
    
    // Optional: Fetch history for a specific exam type
    List<ExamHistory> findByExamCodeOrderByCompletedAtDesc(String examCode);
}