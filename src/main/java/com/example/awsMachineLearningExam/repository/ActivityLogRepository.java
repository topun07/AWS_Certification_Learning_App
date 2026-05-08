package com.example.awsMachineLearningExam.repository;

import com.example.awsMachineLearningExam.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // User's personal activity
    List<ActivityLog> findByUsernameOrderByTimestampDesc(String username);

    // Count events by type (global)
    long countByEventType(String eventType);

    // Count events by type for a specific user
    long countByUsernameAndEventType(String username, String eventType);

    // Most popular certs (global)
    @Query("SELECT a.certCode, COUNT(a) FROM ActivityLog a WHERE a.certCode IS NOT NULL GROUP BY a.certCode ORDER BY COUNT(a) DESC")
    List<Object[]> findCertPopularity();

    // Game mode popularity (global)
    @Query("SELECT a.eventType, COUNT(a) FROM ActivityLog a WHERE a.eventType LIKE '%_start' GROUP BY a.eventType ORDER BY COUNT(a) DESC")
    List<Object[]> findGameModePopularity();

    // User's cert engagement
    @Query("SELECT a.certCode, COUNT(a) FROM ActivityLog a WHERE a.username = ?1 AND a.certCode IS NOT NULL GROUP BY a.certCode ORDER BY COUNT(a) DESC")
    List<Object[]> findUserCertEngagement(String username);

    // User's game mode breakdown
    @Query("SELECT a.eventType, COUNT(a) FROM ActivityLog a WHERE a.username = ?1 AND a.eventType LIKE '%_start' GROUP BY a.eventType ORDER BY COUNT(a) DESC")
    List<Object[]> findUserGameModeBreakdown(String username);

    // Total game sessions (global)
    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.eventType LIKE '%_start'")
    long countTotalGameSessions();

    // Total study material views (global)
    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.eventType = 'study_material_view'")
    long countStudyViews();
}
