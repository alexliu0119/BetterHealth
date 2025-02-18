package com.health.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.health.model.Exercise;
import com.health.model.User;

import java.util.Date;
import java.util.List;

@Repository
public interface ExerciseDAO extends JpaRepository<Exercise, Long> {
    List<Exercise> findByUserAndExerciseDateBetween(User user, Date startDate, Date endDate);
    List<Exercise> findByUserOrderByExerciseDateDesc(User user);
    @Query("SELECT e FROM Exercise e WHERE e.user = :user AND DATE(e.exerciseDate) = CURRENT_DATE")
    List<Exercise> findTodayExercises(User user);
}