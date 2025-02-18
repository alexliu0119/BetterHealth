package com.health.service;

import com.health.dao.CalorieTrackerDAO;
import com.health.dao.MealRecordRepository;
import com.health.dao.UserDAO;
import com.health.dto.DailySummaryDTO;
import com.health.dto.FoodRecordDTO;
import com.health.model.CalorieTracker;
import com.health.model.MealRecord;
import com.health.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class CalorieTrackerService {
    @Autowired
    private CalorieTrackerDAO calorieTrackerDAO;

    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private MealRecordRepository mealRecordRepository;
    
    @Autowired
    private UserService userService;
    

    public CalorieTracker initializeDailyTracker(Long userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        
        Optional<CalorieTracker> existingTracker = calorieTrackerDAO.findTodayRecord(user);
        if (existingTracker.isPresent()) {
            return existingTracker.get();
        }

        CalorieTracker tracker = new CalorieTracker();
        tracker.setUser(user);
        tracker.setRecordDate(new Date());

       
        int tdee = calculateTDEE(user);
        tracker.setTargetCalories(tdee);
        tracker.setRemainingCalories(tdee);

       
        Integer waterTarget = calculateRecommendedWater(user.getWeight());
        tracker.setWaterTarget(waterTarget);

        
        tracker.setCaloriesIntake(0);
        tracker.setCaloriesBurned(0);
        tracker.setWaterIntake(0);
        tracker.setCarbohydrates(0.0);
        tracker.setProtein(0.0);
        tracker.setFat(0.0);

        return calorieTrackerDAO.save(tracker);
    }

    public MealRecord recordMeal(Long userId, String mealType, String foodName, 
            double portion, double calories, double protein, double carbs, double fat) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

        MealRecord mealRecord = new MealRecord();
        mealRecord.setUser(user);
        mealRecord.setMealType(mealType);
        mealRecord.setFoodId(foodName);
        mealRecord.setPortion(portion);
        mealRecord.setCalories(calories);
        mealRecord.setProtein(protein);
        mealRecord.setCarbs(carbs);
        mealRecord.setFat(fat);
        mealRecord.setRecordTime(LocalDateTime.now());

       
        CalorieTracker tracker = getTodayTracker(userId);
        tracker.setCaloriesIntake(tracker.getCaloriesIntake() + (int)calories);
        tracker.setRemainingCalories(tracker.getTargetCalories() - tracker.getCaloriesIntake());
        tracker.setProtein(tracker.getProtein() + protein);
        tracker.setCarbohydrates(tracker.getCarbohydrates() + carbs);
        tracker.setFat(tracker.getFat() + fat);
        
        calorieTrackerDAO.save(tracker);
        return mealRecordRepository.save(mealRecord);
    }

    private Integer calculateRecommendedWater(Double weightKg) {
        if (weightKg == null) return 2000; 
        return (int) Math.round(weightKg * 33); 
    }

    public int calculateTDEE(User user) {
        if (user.getWeight() == null || user.getHeight() == null || 
            user.getAge() == null || user.getGender() == null) {
            return 2000; 
        }

       
        double bmr;
        if ("MALE".equalsIgnoreCase(user.getGender())) {
            bmr = 88.362 + (13.397 * user.getWeight()) + 
                  (4.799 * user.getHeight()) - (5.677 * user.getAge());
        } else {
            bmr = 447.593 + (9.247 * user.getWeight()) + 
                  (3.098 * user.getHeight()) - (4.330 * user.getAge());
        }

      
        double activityMultiplier;
        switch (user.getActivityLevel().toUpperCase()) {
            case "SEDENTARY" -> activityMultiplier = 1.2;
            case "LIGHTLY_ACTIVE" -> activityMultiplier = 1.375;
            case "MODERATELY_ACTIVE" -> activityMultiplier = 1.55;
            case "VERY_ACTIVE" -> activityMultiplier = 1.725;
            case "EXTRA_ACTIVE" -> activityMultiplier = 1.9;
            default -> activityMultiplier = 1.2;
        }

     
        int tdee = (int) Math.round(bmr * activityMultiplier);
        System.out.println("BMR: " + bmr);
        System.out.println("活動倍率: " + activityMultiplier);
        System.out.println("TDEE: " + tdee);
        
        return tdee;
    }

    public CalorieTracker updateWaterIntake(Long userId, Integer waterAmount) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
            
        CalorieTracker tracker = calorieTrackerDAO.findTodayRecord(user)
            .orElseGet(() -> createNewDailyTracker(user));
            
        tracker.setWaterIntake(waterAmount);
        return calorieTrackerDAO.save(tracker);
    }
    
    public CalorieTracker updateNutrients(Long userId, double carbs, double protein, double fat) {
        CalorieTracker tracker = getTodayTracker(userId);
        tracker.setCarbohydrates(carbs);
        tracker.setProtein(protein);
        tracker.setFat(fat);
        return calorieTrackerDAO.save(tracker);
    }

    public CalorieTracker updateCalorieIntake(Long userId, double calories, double carbs, double protein, double fat) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        
        CalorieTracker tracker = getTodayTracker(userId);
        

        tracker.setCaloriesIntake(tracker.getCaloriesIntake() + (int)calories);
        tracker.setCarbohydrates(tracker.getCarbohydrates() + carbs);
        tracker.setProtein(tracker.getProtein() + protein);
        tracker.setFat(tracker.getFat() + fat);
        
    
        tracker.setRemainingCalories(tracker.getTargetCalories() - tracker.getCaloriesIntake());
        
        return calorieTrackerDAO.save(tracker);
    }

   
  
    public int calculateDietScore(Long userId) {
        CalorieTracker tracker = getTodayTracker(userId);
        int calorieScore = calculateCalorieScore(tracker);
        int nutrientScore = calculateNutrientScore(tracker);
        return calorieScore + nutrientScore;
    }

    private int calculateCalorieScore(CalorieTracker tracker) {
        if (tracker.getCaloriesIntake() == 0) return 0;

        double targetCalories = tracker.getTargetCalories();
        double actualCalories = tracker.getCaloriesIntake();
        double ratio = actualCalories / targetCalories;

        if (ratio >= 0.9 && ratio <= 1.1) return 15;
        else if (ratio >= 0.8 && ratio <= 1.2) return 10;
        else if (ratio >= 0.7 && ratio <= 1.3) return 5;
        else return 0;
    }

    private int calculateNutrientScore(CalorieTracker tracker) {
        double totalCalories = tracker.getCaloriesIntake();
        if (totalCalories == 0) return 0;

        double carbRatio = (tracker.getCarbohydrates() * 4) / totalCalories;
        double proteinRatio = (tracker.getProtein() * 4) / totalCalories;
        double fatRatio = (tracker.getFat() * 9) / totalCalories;

        int score = 0;
        if (carbRatio >= 0.5 && carbRatio <= 0.6) score += 5;
        if (proteinRatio >= 0.15 && proteinRatio <= 0.25) score += 5;
        if (fatRatio >= 0.2 && fatRatio <= 0.3) score += 5;

        return score;
    }

    @Transactional
    public CalorieTracker recordWater(String username, Integer amount) {
        try {
            User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
                
          
            CalorieTracker tracker = getTodayTracker(user.getUserId());
            if (tracker == null) {
                tracker = new CalorieTracker();
                tracker.setUser(user);
                tracker.setRecordDate(new Date()); 
                tracker.setWaterTarget(user.getWaterTarget());
            }
            
          
            Integer currentWater = tracker.getWaterIntake() != null ? tracker.getWaterIntake() : 0;
            tracker.setWaterIntake(currentWater + amount);
            
            return calorieTrackerDAO.save(tracker);
        } catch (Exception e) {
            throw new RuntimeException("記錄水分攝入失敗: " + e.getMessage());
        }
    }

 
    public CalorieTracker getTodayTracker(Long userId) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
        
     
        if (user.getWeight() != null) {
            int waterTarget = calculateWaterTarget(user.getWeight());
            if (user.getWaterTarget() == null || user.getWaterTarget() != waterTarget) {
                user.setWaterTarget(waterTarget);
                userDAO.save(user);
            }
        
        }
        
     
        Optional<CalorieTracker> todayTracker = calorieTrackerDAO.findTodayRecord(user);
        
    
        if (!todayTracker.isPresent()) {
            CalorieTracker newTracker = new CalorieTracker();
            newTracker.setUser(user);
            newTracker.setRecordDate(new Date());
            newTracker.setTargetCalories(user.getTdee());
            newTracker.setWaterTarget(user.getWaterTarget() != null ? user.getWaterTarget() : calculateWaterTarget(user.getWeight()));
            newTracker.setCaloriesIntake(0);
            newTracker.setWaterIntake(0);
            newTracker.setRemainingCalories(user.getTdee());
            return calorieTrackerDAO.save(newTracker);
        }
        CalorieTracker tracker = todayTracker.get();
        if (user.getWaterTarget() != null && !user.getWaterTarget().equals(tracker.getWaterTarget())) {
            tracker.setWaterTarget(user.getWaterTarget());
            return calorieTrackerDAO.save(tracker);
        }
        return todayTracker.get();
    }

    private int calculateWaterTarget(Double weight) {
        if (weight == null) return 2000;
        return (int) (weight * 33);
    }
	public DailySummaryDTO getDailySummary(Long userId) {
		CalorieTracker tracker = getTodayTracker(userId);
        DailySummaryDTO summary = new DailySummaryDTO();
        
        summary.setTotalCalories((double)tracker.getCaloriesIntake());
        summary.setCalorieTarget((double)tracker.getTargetCalories());
        summary.setWaterIntake((double)tracker.getWaterIntake());
        summary.setWaterTarget((double)tracker.getWaterTarget());
        summary.setProtein(tracker.getProtein());
        summary.setCarbs(tracker.getCarbohydrates());
        summary.setFat(tracker.getFat());
        
        return summary;
    }
	 private CalorieTracker createNewDailyTracker(User user) {
	        CalorieTracker tracker = new CalorieTracker();
	        tracker.setUser(user);
	        tracker.setWaterTarget(user.getWaterTarget());
	        tracker.setRecordDate(new Date());
	        return tracker;
	    }

	public void addFoodRecord(FoodRecordDTO foodRecord) {
	
		
	}
	public void resetDailyRecord(Long userId) {
	    User user = userDAO.findById(userId)
	        .orElseThrow(() -> new RuntimeException("用户不存在"));
	    
	    CalorieTracker today = calorieTrackerDAO.findTodayRecord(user)
	        .orElseGet(() -> new CalorieTracker());
	    

	    today.setCaloriesIntake(0);  
	    today.setRemainingCalories(today.getTargetCalories()); 
	    today.setProtein(0.0);
	    today.setCarbohydrates(0.0);
	    today.setFat(0.0);
	    today.setWaterIntake(0);
	    today.setUser(user);
	    today.setRecordDate(new Date());
	    
	    calorieTrackerDAO.save(today);
	}
 
	
	}

	



