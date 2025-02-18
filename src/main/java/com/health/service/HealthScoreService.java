package com.health.service;

import com.health.dao.BloodSugarDAO;
import com.health.dao.CalorieTrackerDAO;
import com.health.dao.ExerciseDAO;
import com.health.dao.HealthScoreDAO;
import com.health.dao.SleepRecordDAO;
import com.health.dao.UserDAO;
import com.health.model.BloodSugar;
import com.health.model.CalorieTracker;
import com.health.model.Exercise;
import com.health.model.HealthScore;
import com.health.model.SleepRecord;
import com.health.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class HealthScoreService {
	 private static final Logger LOGGER = LoggerFactory.getLogger(HealthScoreService.class);
    @Autowired
    private HealthScoreDAO healthScoreDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private BloodSugarDAO bloodSugarDAO;
    @Autowired
    private CalorieTrackerDAO calorieTrackerDAO;
    @Autowired
    private ExerciseDAO exerciseDAO;
    @Autowired
    private SleepRecordDAO sleepRecordDAO;
    @Transactional
    public HealthScore saveHealthScore(Long userId, Map<String, Integer> scores) {
        try {
            LOGGER.info("開始保存健康評分，用戶ID: {}", userId);
            
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用戶不存在"));
            
            Optional<HealthScore> todayScore = healthScoreDAO.findTodayLatestScore(user);
            
            HealthScore healthScore;
            if (todayScore.isPresent()) {
                healthScore = todayScore.get();
                LOGGER.info("更新今日記錄，ID: {}", healthScore.getId());
            } else {
                healthScore = new HealthScore();
                healthScore.setUser(user);
                healthScore.setRecordDate(LocalDateTime.now());
                LOGGER.info("創建新記錄");
            }

            // 獲取當日數據
            BloodSugar bloodSugar = bloodSugarDAO.findTodayRecord(user).orElse(null);
            CalorieTracker calorieTracker = calorieTrackerDAO.findTodayRecord(user).orElse(null);
            List<Exercise> exercises = exerciseDAO.findTodayExercises(user);
            SleepRecord sleepRecord = sleepRecordDAO.findTodayRecord(user).orElse(null);

            // 設置當日數據
            double bloodSugarValue = bloodSugar != null ? bloodSugar.getBloodSugar() : 0.0;
            int caloriesIntake = calorieTracker != null ? calorieTracker.getCaloriesIntake() : 0;
            int exerciseMinutes = exercises != null ? 
                exercises.stream().mapToInt(Exercise::getDurationMinutes).sum() : 0;
            double sleepHours = sleepRecord != null ? sleepRecord.getTotalSleepHours() : 0.0;
            int waterIntake = calorieTracker != null ? calorieTracker.getWaterIntake() : 0;

            // 使用計算公式計算分數
            int bmiScore = calculateBMIScore(user.getBmi());
            int bloodSugarScore = calculateBloodSugarScore(bloodSugarValue);
            int dietScore = calculateDietScore(caloriesIntake, user.getTdee());
            int exerciseScore = calculateExerciseScore(exerciseMinutes);
            int sleepScore = calculateSleepScore(sleepHours);
            int waterScore = calculateWaterScore(waterIntake, user.getWeight());

            // 更新分數
            healthScore.setBmiScore(bmiScore);
            healthScore.setBloodSugarScore(bloodSugarScore);
            healthScore.setDietScore(dietScore);
            healthScore.setExerciseScore(exerciseScore);
            healthScore.setSleepScore(sleepScore);
            healthScore.setWaterScore(waterScore);
            
            // 計算總分
            int totalScore = bmiScore + bloodSugarScore + dietScore + 
                            exerciseScore + sleepScore + waterScore;
            healthScore.setTotalScore(totalScore);
            
            // 保存並返回
            HealthScore saved = healthScoreDAO.save(healthScore);
            LOGGER.info("保存成功，ID: {}, 日期: {}, 總分: {}", 
                saved.getId(), saved.getRecordDate(), saved.getTotalScore());
            return saved;
            
        } catch (Exception e) {
            LOGGER.error("保存健康評分失敗，用戶ID: {}, 錯誤: {}", userId, e.getMessage(), e);
            throw new RuntimeException("保存健康評分失敗", e);
        }
    }
    // 計算BMI分數 (15分)
    private int calculateBMIScore(double bmi) {
        if (bmi >= 18.5 && bmi < 24) return 15;
        if (bmi >= 24 && bmi < 27) return 10;
        if ((bmi >= 17 && bmi < 18.5) || (bmi >= 27 && bmi < 30)) return 5;
        return 0;
    }

    // 計算血糖分數 (10分)
    private int calculateBloodSugarScore(double bloodSugar) {
        if (bloodSugar >= 70 && bloodSugar <= 100) return 10;
        if (bloodSugar > 100 && bloodSugar <= 125) return 5;
        return 0;
    }
   
    // 計算飲食分數 (30分)
    private int calculateDietScore(int caloriesIntake, int tdee) {
        double ratio = (double) caloriesIntake / tdee;
        System.out.println("卡路里攝入: " + caloriesIntake);
        System.out.println("TDEE: " + tdee);
        System.out.println("比率: " + ratio);
        
        if (ratio >= 0.9 && ratio <= 1.1) {
            System.out.println("飲食得分: 30");
            return 30;
        }
        if ((ratio >= 0.8 && ratio < 0.9) || (ratio > 1.1 && ratio <= 1.2)) {
            System.out.println("飲食得分: 20");
            return 20;
        }
        if ((ratio >= 0.7 && ratio < 0.8) || (ratio > 1.2 && ratio <= 1.3)) {
            System.out.println("飲食得分: 10");
            return 10;
        }
        System.out.println("飲食得分: 0");
        return 0;
    }

    // 計算運動分數 (20分)
    private int calculateExerciseScore(int minutes) {
        if (minutes >= 30) return 20;
        if (minutes >= 20) return 15;
        if (minutes >= 10) return 10;
        return 0;
    }

    // 計算睡眠分數 (20分)
    private int calculateSleepScore(double hours) {
        if (hours >= 7 && hours <= 9) return 20;
        if ((hours >= 6 && hours < 7) || (hours > 9 && hours <= 10)) return 15;
        if ((hours >= 5 && hours < 6) || (hours > 10 && hours <= 11)) return 10;
        return 0;
    }

    // 計算飲水分數 (5分)
    private int calculateWaterScore(int waterIntake, double weightKg) {
        double targetWater = weightKg * 30;
        double ratio = waterIntake / targetWater;
        if (ratio >= 0.9) return 5;
        if (ratio >= 0.7) return 3;
        if (ratio >= 0.5) return 1;
        return 0;
    }

    // 計算並保存今日評分
    @Transactional
    public HealthScore calculateAndSaveTodayScore(Long userId) {
        try {
            LOGGER.info("開始計算今日健康評分，用戶ID: {}", userId);
            
            User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

            // 獲取當日數據
            BloodSugar bloodSugar = bloodSugarDAO.findTodayRecord(user)
                    .orElse(null);
            LOGGER.info("血糖數據: {}", bloodSugar != null ? bloodSugar.getBloodSugar() : "無數據");
                
            CalorieTracker calorieTracker = calorieTrackerDAO.findTodayRecord(user)
                    .orElse(null);
            LOGGER.info("卡路里追蹤: 攝入={}, 飲水={}", 
                calorieTracker != null ? calorieTracker.getCaloriesIntake() : "無數據",
                calorieTracker != null ? calorieTracker.getWaterIntake() : "無數據");
                
            List<Exercise> exercises = exerciseDAO.findTodayExercises(user);
            LOGGER.info("運動時間: {}", 
                exercises != null ? exercises.stream().mapToInt(Exercise::getDurationMinutes).sum() : "無數據");
                
            SleepRecord sleepRecord = sleepRecordDAO.findTodayRecord(user)
                    .orElse(null);
            LOGGER.info("睡眠時間: {}", sleepRecord != null ? sleepRecord.getTotalSleepHours() : "無數據");

            // 創建或更新評分記錄
            HealthScore score = healthScoreDAO.findTodayLatestScore(user)
                .orElse(new HealthScore());
            
            // 設置基本信息
            if (score.getId() == null) {
                score.setUser(user);
                score.setRecordDate(LocalDateTime.now());
            }

            // 設置並記錄各項數據
            double bloodSugarValue = bloodSugar != null ? bloodSugar.getBloodSugar() : 0.0;
            score.setBloodSugar(bloodSugarValue);
            
            int caloriesIntake = calorieTracker != null ? calorieTracker.getCaloriesIntake() : 0;
            score.setCaloriesIntake(caloriesIntake);
            
            int exerciseMinutes = exercises != null ? 
                exercises.stream().mapToInt(Exercise::getDurationMinutes).sum() : 0;
            score.setExerciseMinutes(exerciseMinutes);
            
            double sleepHours = sleepRecord != null ? sleepRecord.getTotalSleepHours() : 0.0;
            score.setSleepHours(sleepHours);
            
            int waterIntake = calorieTracker != null ? calorieTracker.getWaterIntake() : 0;
            score.setWaterIntake(waterIntake);
            
            score.setBmi(user.getBmi());

            // 計算並記錄各項分數
            int bmiScore = calculateBMIScore(user.getBmi());
            LOGGER.info("BMI分數: {}, BMI值: {}", bmiScore, user.getBmi());
            
            int bloodSugarScore = calculateBloodSugarScore(bloodSugarValue);
            LOGGER.info("血糖分數: {}, 血糖值: {}", bloodSugarScore, bloodSugarValue);
            
            int dietScore = calculateDietScore(caloriesIntake, user.getTdee());
            LOGGER.info("飲食分數: {}, 攝入卡路里: {}, TDEE: {}", dietScore, caloriesIntake, user.getTdee());
            
            int exerciseScore = calculateExerciseScore(exerciseMinutes);
            LOGGER.info("運動分數: {}, 運動時間: {}", exerciseScore, exerciseMinutes);
            
            int sleepScore = calculateSleepScore(sleepHours);
            LOGGER.info("睡眠分數: {}, 睡眠時間: {}", sleepScore, sleepHours);
            
            int waterScore = calculateWaterScore(waterIntake, user.getWeight());
            LOGGER.info("飲水分數: {}, 飲水量: {}, 目標: {}", waterScore, waterIntake, user.getWeight() * 30);

            // 設置分數
            score.setBmiScore(bmiScore);
            score.setBloodSugarScore(bloodSugarScore);
            score.setDietScore(dietScore);
            score.setExerciseScore(exerciseScore);
            score.setSleepScore(sleepScore);
            score.setWaterScore(waterScore);
            
            // 計算總分
            int totalScore = bmiScore + bloodSugarScore + dietScore + 
                            exerciseScore + sleepScore + waterScore;
            score.setTotalScore(totalScore);
            LOGGER.info("總分: {}", totalScore);

            // 保存並返回
            return healthScoreDAO.save(score);
            
        } catch (Exception e) {
            LOGGER.error("計算健康評分失敗，用戶ID: {}, 錯誤: {}", userId, e.getMessage(), e);
            throw new RuntimeException("計算健康評分失敗", e);
        }
    }

    // 獲取週評分趨勢
    public Map<String, Object> getWeeklyTrend(Long userId) {
        try {
            LOGGER.info("獲取用戶 {} 的週趨勢數據", userId);
            
            User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

            // 使用 LocalDateTime 處理日期範圍
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(6);  // 獲取7天前的日期

            // 使用正確的方法名稱
            List<HealthScore> scores = healthScoreDAO
                .findDailyLatestScoresByUserAndDateRange(user, startDate, endDate);

    
            // 整理數據
            List<String> dates = new ArrayList<>();
            List<Integer> totalScores = new ArrayList<>();
            Map<String, List<Integer>> componentScores = new HashMap<>();

            // 初始化各組件分數列表
            componentScores.put("bmi", new ArrayList<>());
            componentScores.put("bloodSugar", new ArrayList<>());
            componentScores.put("diet", new ArrayList<>());
            componentScores.put("exercise", new ArrayList<>());
            componentScores.put("sleep", new ArrayList<>());
            componentScores.put("water", new ArrayList<>());

            // 使用 DateTimeFormatter 格式化日期
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
            
            for (HealthScore score : scores) {
                // 使用 LocalDateTime 的格式化方法
                dates.add(score.getRecordDate().format(formatter));
                totalScores.add(score.getTotalScore());
                
                componentScores.get("bmi").add(score.getBmiScore());
                componentScores.get("bloodSugar").add(score.getBloodSugarScore());
                componentScores.get("diet").add(score.getDietScore());
                componentScores.get("exercise").add(score.getExerciseScore());
                componentScores.get("sleep").add(score.getSleepScore());
                componentScores.get("water").add(score.getWaterScore());
            }

            Map<String, Object> result = new HashMap<>();
            result.put("dates", dates);
            result.put("totalScores", totalScores);
            result.put("componentScores", componentScores);

            LOGGER.info("成功獲取到 {} 條記錄", scores.size());
            return result;
            
        } catch (Exception e) {
            LOGGER.error("獲取週趨勢數據失敗，用戶ID: {}, 錯誤: {}", userId, e.getMessage(), e);
            throw new RuntimeException("獲取週趨勢數據失敗", e);
        }
    }

    // 獲取最新評分
    public HealthScore getLatestScore(Long userId) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
            
        return healthScoreDAO.findTodayLatestScore(user)
            .orElseGet(() -> calculateAndSaveTodayScore(userId));
    }
}