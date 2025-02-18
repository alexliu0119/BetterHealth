package com.health.Repository;

import com.health.model.SleepRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface SleepRecordRepository extends JpaRepository<SleepRecord, Long> {
    
  
    @Query(value = "SELECT * FROM sleep_record WHERE user_id = :userId " +
           "AND DATE(sleep_start) = DATE(NOW()) " +
           "ORDER BY id DESC LIMIT 1", 
           nativeQuery = true)
    SleepRecord findTodayRecord(@Param("userId") Long userId);
}