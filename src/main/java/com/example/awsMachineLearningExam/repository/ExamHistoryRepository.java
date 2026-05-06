package com.example.awsMachineLearningExam.repository;

import com.example.awsMachineLearningExam.model.ExamHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamHistoryRepository extends JpaRepository<ExamHistory, Long> {

    List<ExamHistory> findByUser_Id(Long userId);

    List<ExamHistory> findTop10ByOrderByScorePercentageDesc();

    List<ExamHistory> findTop10ByOrderByCompletedAtDesc();

    List<ExamHistory> findTop5ByTotalQuestionsGreaterThanEqualOrderByScorePercentageDesc(int minQuestions);

    List<ExamHistory> findByExamCodeOrderByCompletedAtDesc(String examCode);

    @Query("SELECT h FROM ExamHistory h WHERE h.user.id = :userId ORDER BY h.completedAt DESC")
    List<ExamHistory> findMyExams(@org.springframework.data.repository.query.Param("userId") Long userId);

    @Query(value = "SELECT " +
            "COALESCE(u.username, 'Anonymous Ninja') as fullName, " +
            "MAX(eh.score_percentage) as topScore, " +
            "COALESCE(eh.exam_code, 'AWS-CERT') as examCode " +
            "FROM exam_history eh " +
            "LEFT JOIN users u ON eh.user_id = u.id " +
            "WHERE eh.total_questions >= :minQuestions " + // <-- The Anti-Cheat Tripwire
            "GROUP BY u.username, eh.exam_code " +
            "ORDER BY topScore DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTopScores(@org.springframework.data.repository.query.Param("minQuestions") int minQuestions);
}