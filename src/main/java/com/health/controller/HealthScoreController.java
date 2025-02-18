package com.health.controller;

import com.health.dao.HealthScoreDAO;
import com.health.dao.UserDAO;
import com.health.model.HealthScore;
import com.health.model.User;
import com.health.service.HealthScoreService;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health-scores")
public class HealthScoreController {
	 private static final Logger LOGGER = LoggerFactory.getLogger(HealthScoreController.class);
    @Autowired
    private HealthScoreService healthScoreService;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private HealthScoreDAO healthScoreDAO;

    @GetMapping("/today/{userId}")
    public ResponseEntity<?> getTodayScore(@PathVariable Long userId) {
        try {
            HealthScore score = healthScoreService.calculateAndSaveTodayScore(userId);
            return ResponseEntity.ok(score);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @GetMapping("/weekly-trend/{userId}")
    public ResponseEntity<List<HealthScore>> getWeeklyTrend(@PathVariable Long userId) {
        try {
            LOGGER.info("獲取用戶 {} 的週趨勢數據", userId);
            
   
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(7);
            
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用戶不存在"));
            

            List<HealthScore> weeklyScores = healthScoreDAO
                    .findDailyLatestScoresByUserAndDateRange(
                        user, 
                        startDate, 
                        endDate
                    );
            
            return ResponseEntity.ok(weeklyScores);
        } catch (Exception e) {
            LOGGER.error("獲取週趨勢數據失敗，用戶ID: {}, 錯誤: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }



    @GetMapping("/latest/{userId}")
    public ResponseEntity<?> getLatestScore(@PathVariable Long userId) {
        try {
            HealthScore score = healthScoreService.getLatestScore(userId);
            return ResponseEntity.ok(score);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}