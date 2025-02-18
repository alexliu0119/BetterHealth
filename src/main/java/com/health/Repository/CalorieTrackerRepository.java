package com.health.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.health.model.CalorieTracker;
import com.health.model.User;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Repository
public interface CalorieTrackerRepository extends JpaRepository<CalorieTracker, Long> {
    default Optional<CalorieTracker> findByUserAndRecordDate(User user, LocalDate recordDate) {
     
        Date date = Date.from(recordDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return findByUserAndRecordDate(user, date);
    }
    
    Optional<CalorieTracker> findByUserAndRecordDate(User user, Date recordDate);
}