package com.health.dao;

import com.health.model.HealthScore;
import com.health.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthScoreDAO extends JpaRepository<HealthScore, Long> {
    
    // 查詢每天最新的一筆記錄
    @Query("SELECT h FROM HealthScore h WHERE h.user = :user " +
           "AND h.recordDate = (SELECT MAX(h2.recordDate) FROM HealthScore h2 " +
           "WHERE h2.user = h.user AND DATE(h2.recordDate) = DATE(h.recordDate)) " +
           "AND h.recordDate BETWEEN :startDate AND :endDate " +
           "ORDER BY h.recordDate DESC")
    List<HealthScore> findDailyLatestScoresByUserAndDateRange(
        @Param("user") User user,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    // 查詢當天最新的一筆記錄
    @Query("SELECT h FROM HealthScore h WHERE h.user = :user " +
           "AND DATE(h.recordDate) = CURRENT_DATE " +
           "ORDER BY h.recordDate DESC LIMIT 1")
    Optional<HealthScore> findTodayLatestScore(@Param("user") User user);

    @Query("SELECT h FROM HealthScore h WHERE h.user.id = :userId ORDER BY h.recordDate DESC LIMIT 1")
    Optional<HealthScore> findFirstByUserOrderByRecordDateDesc(@Param("userId") Long userId);
}
