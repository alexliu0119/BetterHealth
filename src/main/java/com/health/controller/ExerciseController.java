package com.health.controller;

import com.health.model.Exercise;
import com.health.model.User;
import com.health.service.ExerciseService;
import com.health.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exercise")
public class ExerciseController {

    private final UserService userService;
    private final ExerciseService exerciseService;

    public ExerciseController(UserService userService, ExerciseService exerciseService) {
        this.userService = userService;
        this.exerciseService = exerciseService;
    }

    @PostMapping
    public ResponseEntity<?> recordExercise(@RequestBody Exercise exercise) {
        try {
        
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userService.findByUsername(username);
        
            if (exercise.getExerciseType() == null || exercise.getExerciseType().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("缺少運動類型");
            }
            if (exercise.getDurationMinutes() == null || exercise.getDurationMinutes() <= 0) {
                return ResponseEntity.badRequest().body("運動時長必須大於0");
            }
            if (exercise.getIntensity() == null || exercise.getIntensity().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("缺少運動強度");
            }
            if (exercise.getCaloriesBurned() == null || exercise.getCaloriesBurned() <= 0) {
                return ResponseEntity.badRequest().body("消耗卡路里必須大於0");
            }

            exercise.setUser(user);
            
          
            Exercise savedExercise =exerciseService.saveExercise(exercise);
            return ResponseEntity.ok(savedExercise);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/today/{userId}")
    public ResponseEntity<?> deleteTodayExercise(@PathVariable Long userId) {
        try {
            exerciseService.deleteTodayExercise(userId);
            return ResponseEntity.ok(Map.of("message", "今日運動記錄已清除"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "清除運動記錄失敗: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}/today")
    public ResponseEntity<?> getTodayExercises(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId);
            List<Exercise> exercises = exerciseService.getDailyExercises(user);
            return ResponseEntity.ok(exercises);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "獲取運動記錄失敗",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Exercise> recordExercise(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> request) {
        try {
            String exerciseType = (String) request.get("exerciseType");
            int durationMinutes = Integer.parseInt(request.get("durationMinutes").toString());
            int heartRate = Integer.parseInt(request.get("heartRate").toString());
          

            Exercise exercise = exerciseService.recordExercise(userId, exerciseType,
                    durationMinutes, heartRate);
            return ResponseEntity.ok(exercise);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/history")
    public ResponseEntity<List<Exercise>> getExerciseHistory() {
        try {
           
            Long userId = 1L; 
            List<Exercise> history = exerciseService.getExerciseHistory(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getExerciseHistory(@PathVariable Long userId) {
        try {
            List<Exercise> history = exerciseService.getExerciseHistory(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/daily/{userId}")
    public ResponseEntity<List<Exercise>> getDailyExercises(@PathVariable Long userId) {
        try {
            List<Exercise> exercises = exerciseService.getDailyExercises(userId);
            exercises.forEach(exercise -> {
                if (exercise.getCaloriesBurned() == null) {
                    exercise.setCaloriesBurned(exercise.calculateCaloriesBurned());
                }
            });
            return ResponseEntity.ok(exercises);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/statistics/{userId}")
    public ResponseEntity<Map<String, Object>> getStatistics(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = exerciseService.getExerciseStatistics(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}