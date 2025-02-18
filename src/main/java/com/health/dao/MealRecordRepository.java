package com.health.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.health.model.MealRecord;

@Repository
public interface MealRecordRepository extends JpaRepository<MealRecord, Long> {

    List<MealRecord> findByUser_UserIdAndRecordTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
}