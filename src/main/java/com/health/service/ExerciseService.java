package com.health.service;

import com.health.dao.CalorieTrackerDAO;
import com.health.dao.ExerciseDAO;
import com.health.dao.UserDAO;
import com.health.model.CalorieTracker;
import com.health.model.Exercise;
import com.health.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@Transactional
public class ExerciseService {
	  
    @Autowired
    private ExerciseDAO exerciseDAO;
    private CalorieTrackerService calorieTrackerService;
    private CalorieTrackerDAO calorieTrackerDAO;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private UserDAO userDAO;
    
   
    public Exercise recordExercise(Long userId, String exerciseType, 
            int durationMinutes, int heartRate) {
    
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("運動時間必須大於0分鐘");
        }
        if (heartRate <= 0) {
            throw new IllegalArgumentException("心率必須大於0");
        }

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        
        Exercise exercise = new Exercise();
        exercise.setUser(user);
        exercise.setExerciseType(exerciseType);
        exercise.setDurationMinutes(durationMinutes);
        exercise.setHeartRate(heartRate);
        exercise.setExerciseDate(new Date());
        
   
        double caloriesBurned = calculateCaloriesBurned(exercise);
        exercise.setCaloriesBurned(caloriesBurned);
        
     
        updateDailyCalories(userId, caloriesBurned);
        
        return exerciseDAO.save(exercise);
    }
    private double calculateCaloriesBurned(Exercise exercise) {
        User user = exercise.getUser();
        if (user == null || exercise.getDurationMinutes() == null || exercise.getHeartRate() == null) {
            return 0.0;
        }

       
        double bmr;
        if ("男".equals(user.getGender())) {
            bmr = 66 + (13.7 * user.getWeight()) + (5 * user.getHeight()) - (6.8 * user.getAge());
        } else {
            bmr = 655 + (9.6 * user.getWeight()) + (1.8 * user.getHeight()) - (4.7 * user.getAge());
        }

       
        double intensityFactor = getIntensityFactor(exercise.getHeartRate());
        
      
        double typeMultiplier = getExerciseTypeMultiplier(exercise.getExerciseType());

      
        return (bmr / 24.0 / 60.0) * exercise.getDurationMinutes() * intensityFactor * typeMultiplier;
    }

    public Exercise recordExercise(Exercise exercise) {
        if(exercise.getDurationMinutes() == null) {
            throw new IllegalArgumentException("運動時長不能為空");
        }
     
        return exerciseDAO.save(exercise);
    }
    private double getIntensityFactor(int heartRate) {
        if (heartRate >= 160) return 2.0;      
        else if (heartRate >= 140) return 1.5; 
        else if (heartRate >= 120) return 1.2;  
        else return 1.0;                        
    }

    private double getExerciseTypeMultiplier(String exerciseType) {
        Map<String, Double> multipliers = new HashMap<>();
        multipliers.put("跑步", 2.5);
        multipliers.put("游泳", 2.3);
        multipliers.put("騎車", 2.0);
        multipliers.put("健身", 1.8);
        multipliers.put("走路", 1.2);
        
        return multipliers.getOrDefault(exerciseType, 1.0);
    }
    public Exercise recordExercise(Long userId, Exercise exercise) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
            
        exercise.setUser(user);
       
        double caloriesBurned = exercise.calculateCaloriesBurned();
        exercise.setCaloriesBurned(caloriesBurned);
        
        return exerciseDAO.save(exercise);
    }
    public List<Exercise> getTodayExercises(User user) {
        return exerciseDAO.findTodayExercises(user);
    }
    public List<Exercise> getExerciseHistory(Long userId) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
        return exerciseDAO.findByUserOrderByExerciseDateDesc(user);
    }
    public void deleteTodayExercise(Long userId) {
        try {
            User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
                
    
            String jpql = "DELETE FROM Exercise e WHERE e.user.id = :userId " +
                    "AND FUNCTION('DATE', e.exerciseDate) = CURRENT_DATE";
            
            
            Query query = entityManager.createQuery(jpql)
                .setParameter("userId", userId);
            
            int deletedCount = query.executeUpdate();
            
            if (deletedCount == 0) {
                throw new RuntimeException("今日無運動記錄可刪除");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("刪除今日運動記錄失敗: " + e.getMessage());
        }
    }


    public Map<String, Object> getExerciseStatistics(Long userId) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
            
        List<Exercise> exercises = exerciseDAO.findByUserOrderByExerciseDateDesc(user);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalExerciseMinutes", 
            exercises.stream().mapToInt(Exercise::getDurationMinutes).sum());
        stats.put("totalCaloriesBurned", 
            exercises.stream().mapToDouble(Exercise::getCaloriesBurned).sum());
        stats.put("averageHeartRate", 
            exercises.stream().mapToInt(Exercise::getHeartRate).average().orElse(0));
        stats.put("exerciseTypes", 
            exercises.stream().map(Exercise::getExerciseType).distinct().toList());
        
        return stats;
    }
    

    private void updateDailyCalories(Long userId, double caloriesBurned) {
        CalorieTracker tracker = calorieTrackerService.getTodayTracker(userId);
        if (tracker != null) {
            tracker.setCaloriesBurned(tracker.getCaloriesBurned() + (int)caloriesBurned);
            tracker.setRemainingCalories(tracker.getTargetCalories() - tracker.getCaloriesIntake() + tracker.getCaloriesBurned());

            calorieTrackerDAO.save(tracker);
        }
    }

    public List<Exercise> getDailyExercises(Long userId) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
    
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startOfDay = cal.getTime();
        
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date endOfDay = cal.getTime();
        
        return exerciseDAO.findByUserAndExerciseDateBetween(user, startOfDay, endOfDay);
    }

    public int calculateExerciseScore(Long userId) {
        List<Exercise> dailyExercises = getDailyExercises(userId);
        
        if (dailyExercises.isEmpty()) {
            return 0;
        }
        
        int totalMinutes = dailyExercises.stream()
                .mapToInt(Exercise::getDurationMinutes)
                .sum();
        
        double avgHeartRate = dailyExercises.stream()
                .mapToInt(Exercise::getHeartRate)
                .average()
                .orElse(0);
        
        int timeScore = calculateTimeScore(totalMinutes);
        int intensityScore = calculateHeartRateScore(avgHeartRate);  
        
        return timeScore + intensityScore;
    }

    private int calculateTimeScore(int totalMinutes) {
        if (totalMinutes >= 60) return 10;
        else if (totalMinutes >= 30) return 7;
        else if (totalMinutes >= 15) return 4;
        else return 0;
    }

    private int calculateHeartRateScore(double avgHeartRate) {  // 改名為更合適的名稱
        if (avgHeartRate >= 140) return 10;
        else if (avgHeartRate >= 120) return 7;
        else if (avgHeartRate >= 100) return 4;
        else return 0;
    }

    public Exercise saveExercise(Exercise exercise) {
  
        if (exercise.getCaloriesBurned() == null || exercise.getCaloriesBurned() <= 0) {
      
            double caloriesBurned = calculateCaloriesBurned(exercise);
            exercise.setCaloriesBurned(caloriesBurned);
        }

      
        if (exercise.getExerciseDate() == null) {
            exercise.setExerciseDate(new Date());
        }

   

        return exerciseDAO.save(exercise);
    }


    public List<Exercise> getDailyExercises(User user) {
        LocalDate today = LocalDate.now();
        Date startOfDay = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        return exerciseDAO.findByUserAndExerciseDateBetween(user, startOfDay, endOfDay);
    }


}