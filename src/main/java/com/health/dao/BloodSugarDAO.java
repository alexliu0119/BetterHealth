package com.health.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.health.model.BloodSugar;
import com.health.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodSugarDAO extends JpaRepository<BloodSugar, Long> {
  
    @Query("SELECT b FROM BloodSugar b WHERE b.user = :user AND DATE(b.measureTime) = CURRENT_DATE")
    Optional<BloodSugar> findTodayRecord(@Param("user") User user);

    @Query("SELECT b FROM BloodSugar b WHERE b.user = :user AND DATE(b.measureTime) = CURRENT_DATE ORDER BY b.measureTime DESC")
    List<BloodSugar> findByUserOrderByMeasureTimeDesc(@Param("user") User user);
    
    
    
    @Modifying
    @Query("DELETE FROM BloodSugar b WHERE b.user = :user AND DATE(b.measureTime) = CURRENT_DATE")
    int deleteTodayRecord(@Param("user") User user);
}