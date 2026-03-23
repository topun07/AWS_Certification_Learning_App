package com.example.awsMachineLearningExam.repository;

import com.example.awsMachineLearningExam.model.ExamHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamHistoryRepository extends JpaRepository<ExamHistory, Long> {
    
    // This will allow us to fetch the most recent scores for the dashboard
    List<ExamHistory> findTop10ByOrderByCompletedAtDesc();

    List<ExamHistory> findTop5ByTotalQuestionsGreaterThanEqualOrderByScorePercentageDesc(int minQuestions);
    
    // Optional: Fetch history for a specific exam type
    List<ExamHistory> findByExamCodeOrderByCompletedAtDesc(String examCode);

    @Query(value = "SELECT " +
            "COALESCE(u.full_name, 'Anonymous Ninja') as fullName, " +
            "MAX(eh.score_percentage) as topScore, " +
            "COALESCE(eh.exam_code, 'AWS-CERT') as examCode " +
            "FROM exam_history eh " +
            "LEFT JOIN users u ON eh.user_id = u.id " +
            "WHERE eh.total_questions >= :minQuestions " + // <-- The Anti-Cheat Tripwire
            "GROUP BY u.full_name, eh.exam_code " +
            "ORDER BY topScore DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTopScores(@org.springframework.data.repository.query.Param("minQuestions") int minQuestions);
}