package com.health.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.health.model.CalorieTracker;
import com.health.model.User;

import java.util.Date;
import java.util.Optional;

@Repository
public interface CalorieTrackerDAO extends JpaRepository<CalorieTracker, Long> {
    
    @Query("SELECT c FROM CalorieTracker c WHERE c.user.userId = :userId AND DATE(c.recordDate) = DATE(:date)")
    Optional<CalorieTracker> findByUser_UserIdAndRecordDate(@Param("userId") Long userId, @Param("date") Date date);

    @Query("SELECT c FROM CalorieTracker c WHERE c.user = :user AND DATE(c.recordDate) = CURRENT_DATE")
    Optional<CalorieTracker> findTodayRecord(@Param("user") User user);
}