package com.health.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.health.dao.CalorieTrackerDAO;
import com.health.dao.MealRecordRepository;
import com.health.dao.UserDAO;
import com.health.dto.DailySummaryDTO;
import com.health.dto.FoodRecordDTO;
import com.health.dto.MealRecordRequest;
import com.health.dto.WaterIntakeDTO;
import com.health.model.CalorieTracker;
import com.health.model.MealRecord;
import com.health.model.User;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@Transactional
public class DailyRecordService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DailyRecordService.class);
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private UserService userService;

    @Autowired
    private MealRecordRepository mealRecordRepository;
    
    @Autowired
    private CalorieTrackerDAO calorieTrackerDAO;
     @Autowired
private CalorieTrackerService calorieTrackerService;
    public DailySummaryDTO recordWaterIntake(Long userId, WaterIntakeDTO waterData) {
        LOGGER.info("開始記錄水分攝入，用戶ID: {}, 水量: {}", userId, waterData.getCurrentAmount());
        
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));

        CalorieTracker tracker = calorieTrackerDAO.findTodayRecord(user)
            .orElseGet(() -> {
                CalorieTracker newTracker = new CalorieTracker();
                newTracker.setUser(user);
                newTracker.setRecordDate(new Date());
                newTracker.setTargetCalories(user.getTdee());
                newTracker.setWaterTarget(user.getWaterTarget());
                newTracker.setCaloriesIntake(0);
                newTracker.setWaterIntake(0);
                return newTracker;
            });

       
        int currentWater = tracker.getWaterIntake() != null ? tracker.getWaterIntake() : 0;
        int newAmount = currentWater + waterData.getCurrentAmount();
        LOGGER.info("當前水分: {}, 新增: {}, 總計: {}", currentWater, waterData.getCurrentAmount(), newAmount);
        
        tracker.setWaterIntake(newAmount);
        
  
        tracker = calorieTrackerDAO.save(tracker);
        LOGGER.info("保存成功，記錄ID: {}, 最新水分攝入量: {}", tracker.getTrackerId(), tracker.getWaterIntake());

        return createDailySummaryDTO(tracker);
    }


    private DailySummaryDTO createDailySummaryDTO(CalorieTracker tracker) {
        User user = tracker.getUser();
        DailySummaryDTO summary = new DailySummaryDTO();
        summary.setCalorieTarget((double) user.getTdee());
        summary.setTotalCalories((double) tracker.getCaloriesIntake());
        summary.setWaterTarget((double) user.getWaterTarget());
        summary.setWaterIntake((double) tracker.getWaterIntake());
        summary.setProtein(tracker.getProtein());
        summary.setCarbs(tracker.getCarbohydrates());
        summary.setFat(tracker.getFat());
        
        LOGGER.info("創建每日總結 DTO: 卡路里={}/{}, 水分={}/{}", 
            summary.getTotalCalories(), summary.getCalorieTarget(),
            summary.getWaterIntake(), summary.getWaterTarget());
            
        return summary;
    }

    public void recordMeal(MealRecordRequest request) {
        User user = userDAO.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("用戶不存在"));

        MealRecord mealRecord = new MealRecord();
        mealRecord.setUser(user);
        mealRecord.setFoodId(request.getFoodName());
        mealRecord.setPortion(request.getPortion());
        mealRecord.setMealType(request.getMealType());
        mealRecord.setRecordTime(LocalDateTime.now());

        mealRecordRepository.save(mealRecord);
        updateDailyCalorieTracker(user);
    }

    private void updateDailyCalorieTracker(User user) {
        CalorieTracker tracker = calorieTrackerDAO.findTodayRecord(user)
            .orElseGet(() -> {
                CalorieTracker newTracker = new CalorieTracker();
                newTracker.setUser(user);
                newTracker.setRecordDate(new Date());
                newTracker.setTargetCalories(user.getTdee());
                newTracker.setWaterTarget(user.getWaterTarget());
                return newTracker;
            });

        calculateAndUpdateNutrients(tracker);
        calorieTrackerDAO.save(tracker);
    }

    private void calculateAndUpdateNutrients(CalorieTracker tracker) {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime end = LocalDateTime.now().withHour(23).withMinute(59);

        List<MealRecord> todayMeals = mealRecordRepository
            .findByUser_UserIdAndRecordTimeBetween(tracker.getUser().getUserId(), start, end);

        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;

        for (MealRecord meal : todayMeals) {
            totalCalories += meal.getCalories();
            totalProtein += meal.getProtein();
            totalCarbs += meal.getCarbs();
            totalFat += meal.getFat();
        }

        tracker.setCaloriesIntake((int) totalCalories);
        tracker.setProtein(totalProtein);
        tracker.setCarbohydrates(totalCarbs);
        tracker.setFat(totalFat);
    }
    

    public DailySummaryDTO getDailySummary(Long userId) {
        try {
            User user = userService.findById(userId);
            CalorieTracker tracker = calorieTrackerService.getTodayTracker(userId);
            
            DailySummaryDTO summary = new DailySummaryDTO();
            
            if (tracker == null) {
                
                tracker = new CalorieTracker();
                tracker.setUser(user);
                tracker.setRecordDate(new Date());
              
                tracker.setCaloriesIntake(0);
                tracker.setCaloriesBurned(0);
                tracker.setWaterIntake(0);
                tracker.setWaterTarget(user.getWaterTarget() != null ? user.getWaterTarget() : 2000); // 設置默認值
                tracker.setProtein(0.0);
                tracker.setFat(0.0);
                tracker.setCarbohydrates(0.0);
                tracker.setTargetCalories(user.getTdee() != null ? user.getTdee() : 2000);
                
       
                tracker = calorieTrackerDAO.save(tracker);
            }
            
      
            summary.setCalorieTarget(user.getTdee() != null ? user.getTdee().doubleValue() : 2000.0);
            summary.setTotalCalories(tracker.getCaloriesIntake() != null ? tracker.getCaloriesIntake().doubleValue() : 0.0);
            summary.setRemainingCalories(
                (user.getTdee() != null ? user.getTdee().doubleValue() : 2000.0) - 
                (tracker.getCaloriesIntake() != null ? tracker.getCaloriesIntake().doubleValue() : 0.0)
            );
            summary.setWaterTarget(tracker.getWaterTarget() != null ? tracker.getWaterTarget().doubleValue() : 2000.0);
            summary.setWaterIntake(tracker.getWaterIntake() != null ? tracker.getWaterIntake().doubleValue() : 0.0);
            summary.setProtein(tracker.getProtein() != null ? tracker.getProtein() : 0.0);
            summary.setCarbs(tracker.getCarbohydrates() != null ? tracker.getCarbohydrates() : 0.0);
            summary.setFat(tracker.getFat() != null ? tracker.getFat() : 0.0);
            summary.setCaloriesBurnt(tracker.getCaloriesBurned() != null ? tracker.getCaloriesBurned().doubleValue() : 0.0);
            
            return summary;
            
        } catch (Exception e) {
            LOGGER.error("獲取每日總結失敗，用戶ID: {}, 錯誤: {}", userId, e.getMessage());
            throw new RuntimeException("獲取每日總結失敗", e);
        }
    }
    public CalorieTracker createDailyRecord(User user) {
        CalorieTracker tracker = new CalorieTracker();
        tracker.setUser(user);
        tracker.setTargetCalories(user.getTdee()); 
        tracker.setWaterTarget(user.getWaterTarget());
        tracker.setRecordDate(new Date());
        tracker.setCaloriesIntake(0);
        tracker.setWaterIntake(0);
        tracker.setProtein(0.0);
        tracker.setCarbohydrates(0.0);
        tracker.setFat(0.0);
        
        LOGGER.info("創建新的每日記錄，用戶ID: {}, TDEE: {}", user.getUserId(), user.getTdee());
        return calorieTrackerDAO.save(tracker);
    }
    public Map<String, Object> getDailyProgress(Long userId) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));

        CalorieTracker tracker = calorieTrackerDAO.findTodayRecord(user)
            .orElseGet(() -> createDailyRecord(user));

        Map<String, Object> progress = new HashMap<>();
        progress.put("caloriesIntake", tracker.getCaloriesIntake());
        progress.put("calorieTarget", tracker.getTargetCalories());
        progress.put("waterIntake", tracker.getWaterIntake());
        progress.put("waterTarget", tracker.getWaterTarget());
        progress.put("protein", tracker.getProtein());
        progress.put("carbohydrates", tracker.getCarbohydrates());
        progress.put("fat", tracker.getFat());
        
     
        double calorieProgress = (tracker.getCaloriesIntake() * 100.0) / tracker.getTargetCalories();
        double waterProgress = (tracker.getWaterIntake() * 100.0) / tracker.getWaterTarget();
        
        progress.put("calorieProgress", Math.min(100, calorieProgress));
        progress.put("waterProgress", Math.min(100, waterProgress));

        return progress;
    }


    public void recordFood(FoodRecordDTO foodRecord) {
        User user = userDAO.findById(foodRecord.getUserId())
            .orElseThrow(() -> new RuntimeException("用戶不存在"));

        CalorieTracker tracker = calorieTrackerDAO.findTodayRecord(user)
            .orElseGet(() -> createDailyRecord(user));

        tracker.setCaloriesIntake(tracker.getCaloriesIntake() + foodRecord.getCalories().intValue());
        tracker.setProtein(tracker.getProtein() + foodRecord.getProtein());
        tracker.setCarbohydrates(tracker.getCarbohydrates() + foodRecord.getCarbs());
        tracker.setFat(tracker.getFat() + foodRecord.getFat());

        MealRecord mealRecord = new MealRecord();
        mealRecord.setUser(user);
        mealRecord.setFoodId(foodRecord.getFoodName());
        mealRecord.setCalories(foodRecord.getCalories());
        mealRecord.setProtein(foodRecord.getProtein());
        mealRecord.setCarbs(foodRecord.getCarbs());
        mealRecord.setFat(foodRecord.getFat());
        mealRecord.setRecordTime(LocalDateTime.now());

        mealRecordRepository.save(mealRecord);
        calorieTrackerDAO.save(tracker);
    }
}