package com.health.dao;

import com.health.model.SleepRecord;
import com.health.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface SleepRecordDAO extends JpaRepository<SleepRecord, Long> {
    List<SleepRecord> findByUserOrderBySleepStartDesc(User user);
    


    @Query(value = "SELECT * FROM sleep_record WHERE user_id = :#{#user.userId} " +
    	       "AND (DATE(sleep_start) = CURRENT_DATE() " +
    	       "OR DATE(sleep_end) = CURRENT_DATE()) " +
    	       "ORDER BY sleep_start DESC LIMIT 1", 
    	       nativeQuery = true)
    	Optional<SleepRecord> findTodayRecord(User user);
 
    @Modifying
    @Query(value = "DELETE FROM sleep_record WHERE user_id = :userId " +
           "AND id = (SELECT id FROM (SELECT id FROM sleep_record " +
           "WHERE user_id = :userId " +
           "AND (DATE(sleep_start) = CURRENT_DATE() " +
           "OR DATE(sleep_end) = CURRENT_DATE()) " +
           "ORDER BY sleep_start DESC LIMIT 1) as temp)", 
           nativeQuery = true)
    void deleteTodayRecord(@Param("userId") Long userId);

  
 
    List<SleepRecord> findByUserAndSleepStartBetween(User user, Date start, Date end);
    
    Optional<SleepRecord> findTopByUserOrderBySleepStartDesc(User user);
    
    @Query("SELECT s FROM SleepRecord s WHERE s.user = :user AND s.sleepStart BETWEEN :startDate AND :endDate")
    List<SleepRecord> findSleepRecordsInRange(User user, Date startDate, Date endDate);
}