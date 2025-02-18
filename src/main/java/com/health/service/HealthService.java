package com.health.service;

import com.health.dao.*;
import com.health.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class HealthService {
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private HealthScoreService healthScoreService;
    
    @Autowired
    private CalorieTrackerService calorieTrackerService;
    public boolean checkUsernameExists(String username) {
        return userDAO.findByUsername(username).isPresent();
    }

    public User registerUser(String username, String password, String email) {
        if (userDAO.findByUsername(username).isPresent()) {
            throw new RuntimeException("用戶名已存在");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
      
        user.setCreateDate(new Date());
        
        return userDAO.save(user);
    }

    public void updateUserProfile(Long userId, String gender, int age, 
            double height, double weight, String activityLevel) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        
        user.setGender(gender);
        user.setAge(age);
        user.setHeight(height);
        user.setWeight(weight);
        user.setActivityLevel(activityLevel);
        
        
        int tdee = calculateTDEE(user);
        user.setTdee(tdee);
        
       
        Integer waterTarget = (int) (weight * 32.5); 
        user.setWaterTarget(waterTarget);
        
        userDAO.save(user);
        
      
        calorieTrackerService.initializeDailyTracker(userId);
    }

    private int calculateTDEE(User user) {
       
        double bmr;
        if ("男".equals(user.getGender())) {
            bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() + 5;
        } else {
            bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() - 161;
        }
        
        // 根據活動程度調整
        double activityMultiplier = switch (user.getActivityLevel()) {
            case "SEDENTARY" -> 1.2;
            case "LIGHTLY_ACTIVE" -> 1.375;
            case "MODERATELY_ACTIVE" -> 1.55;
            case "VERY_ACTIVE" -> 1.725;
            default -> 1.2;
        };
        
        return (int) (bmr * activityMultiplier);
    }
    

    public Map<String, Object> getUserDashboard(Long userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("userProfile", user);
        dashboard.put("healthScore", healthScoreService.getLatestScore(userId));
        dashboard.put("calorieTracker", calorieTrackerService.getTodayTracker(userId));
        
        return dashboard;
    }
}