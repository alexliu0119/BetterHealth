package com.health.controller;

import com.health.dto.*;
import com.health.model.CalorieTracker;
import com.health.model.User;
import com.health.service.CalorieTrackerService;
import com.health.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/calorie-tracker/")
public class CalorieTrackerController {
    @Autowired
    private CalorieTrackerService calorieTrackerService;
    
    @Autowired
    private UserService userService;

    @PostMapping("/meals")
    public ResponseEntity<?> recordMeal(@RequestBody Map<String, Object> mealData) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "未登入"
                ));
            }
            
            String username = auth.getName();
            User user = userService.findByUsername(username);
            
   
            double calories = Double.parseDouble(mealData.get("calories").toString());
            double protein = Double.parseDouble(mealData.get("protein").toString());
            double carbs = Double.parseDouble(mealData.get("carbs").toString());
            double fat = Double.parseDouble(mealData.get("fat").toString());
            
            CalorieTracker tracker = calorieTrackerService.updateCalorieIntake(
                user.getUserId(), calories, carbs, protein, fat);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "記錄成功",
                "data", tracker
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/food")
    public ResponseEntity<?> recordFood(@RequestBody FoodRecordDTO foodDTO) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "未登入"
                ));
            }
            
            String username = auth.getName();
            User user = userService.findByUsername(username);
            foodDTO.setUserId(user.getUserId());
            
            CalorieTracker tracker = calorieTrackerService.updateCalorieIntake(
                user.getUserId(), 
                foodDTO.getCalories(),
                foodDTO.getCarbs(),
                foodDTO.getProtein(),
                foodDTO.getFat()
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "食物記錄成功",
                "data", tracker
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/record")
    public ResponseEntity<?> addCalorieRecord(@RequestBody FoodRecordDTO foodRecord) {
        try {
            calorieTrackerService.addFoodRecord(foodRecord);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/water")
    public ResponseEntity<?> recordWater(@RequestBody WaterIntakeDTO waterData) {
        try {
	        String username = userService.findById(waterData.getUserid())
	            .getUsername();
	        Integer amount = waterData.getCurrentAmount();
	        CalorieTracker tracker = calorieTrackerService.recordWater(username, amount);
	        return ResponseEntity.ok(tracker);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/today")
    public ResponseEntity<?> getTodayTracker() {
        try {
           
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userService.findByUsername(username);
            
            
            CalorieTracker tracker = calorieTrackerService.getTodayTracker(user.getUserId());
            return ResponseEntity.ok(tracker);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/reset/{userId}")
    public ResponseEntity<?> resetDailyRecord(@PathVariable Long userId) {
        try {
            calorieTrackerService.resetDailyRecord(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

  
   
    
    @PostMapping("/carorieTracker/{userId}/water")
    public ResponseEntity<?> recordWater(@PathVariable String userId, @RequestBody Map<String, Integer> waterData) {
        try {
            int amount = waterData.get("amount");
            CalorieTracker updated = calorieTrackerService.recordWater(userId, amount);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    

    @GetMapping("/summary/current")
    public ResponseEntity<?> getDailySummary() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "未登入"
                ));
            }
            
            String username = auth.getName();
            User user = userService.findByUsername(username);
            DailySummaryDTO summary = calorieTrackerService.getDailySummary(user.getUserId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", summary
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    @GetMapping("/daily-summary/{userId}")
    public ResponseEntity<?> getDailySummary(@PathVariable Long userId) {
        try {
            DailySummaryDTO summary = calorieTrackerService.getDailySummary(userId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    

    @GetMapping("/today/{userId}")
    public ResponseEntity<CalorieTracker> getTodayTracker(@PathVariable Long userId) {
        try {
            CalorieTracker tracker = calorieTrackerService.getTodayTracker(userId);
            return ResponseEntity.ok(tracker);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/diet-score/{userId}")
    public ResponseEntity<Integer> getDietScore(@PathVariable Long userId) {
        try {
            int score = calorieTrackerService.calculateDietScore(userId);
            return ResponseEntity.ok(score);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}