package com.health.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.health.dto.DailySummaryDTO;
import com.health.dto.MealRecordRequest;
import com.health.dto.WaterIntakeDTO;
import com.health.model.CalorieTracker;
import com.health.model.User;
import com.health.service.CalorieTrackerService;
import com.health.service.DailyRecordService;
import com.health.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/daily-record")
public class DailyRecordController {
	private static final Logger logger = LoggerFactory.getLogger(DailyRecordController.class);
    @Autowired
    private DailyRecordService dailyRecordService;
    
    @Autowired
    private CalorieTrackerService calorieTrackerService;
    
    @Autowired
    private UserService userService;

  
    
    @GetMapping("/{userId}")
    public ResponseEntity<?> getDailyRecord(@PathVariable Long userId) {
        try {
            DailySummaryDTO summary = dailyRecordService.getDailySummary(userId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
 
    @GetMapping("/api/daily-record/init/{userId}")
    public ResponseEntity<DailySummaryDTO> initializeDailyRecord(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId);
            DailySummaryDTO summary = new DailySummaryDTO();
            summary.setCalorieTarget((double) user.getTdee());
            summary.setWaterTarget((double) user.getWaterTarget());
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    

  
    @PostMapping(value = "/water-intake", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> recordWater(@RequestBody WaterIntakeDTO waterIntake) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new RuntimeException("用戶不存在");
            }
            
            CalorieTracker tracker = calorieTrackerService.recordWater(
                String.valueOf(user.getUserId()), 
                waterIntake.getCurrentAmount()
            );
            return ResponseEntity.ok(tracker);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

   
    

    @PostMapping("/water")
    public ResponseEntity<?> updateWaterIntake(@RequestBody WaterIntakeDTO request) {
        try {
            logger.info("接收到水分攝入請求: userId={}, amount={}", request.getUserId(), request.getCurrentAmount());
            DailySummaryDTO summary = dailyRecordService.recordWaterIntake(request.getUserId(), request);
            logger.info("更新水分攝入成功: {}", summary);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            logger.error("更新水分攝入失敗", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }




  
    @PostMapping("/meal")
    public ResponseEntity<?> addMealRecord(@RequestBody MealRecordRequest request) {
        try {
            dailyRecordService.recordMeal(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/progress/{userId}")
    public ResponseEntity<?> getProgress(@PathVariable Long userId) {
        try {
            Map<String, Object> progress = dailyRecordService.getDailyProgress(userId);
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
   
    @GetMapping("/daily-summary/{userId}")
    public ResponseEntity<DailySummaryDTO> getDailySummary(@PathVariable Long userId) {
        try {
            DailySummaryDTO summary = dailyRecordService.getDailySummary(userId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}


    

    
    


