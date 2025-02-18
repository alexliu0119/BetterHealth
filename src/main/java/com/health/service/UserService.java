package com.health.service;

import com.health.Repository.UserRepository;
import com.health.dao.UserDAO;
import com.health.dto.UserProfileDTO;
import com.health.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    
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
    
    @Transactional
    public User updateProfile(String username, UserProfileDTO profileDTO) {
        User user = findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用戶不存在");
        }
     
        if (user.getWeight() != null) {
            int waterTarget = calculateWaterTarget(user.getWeight());
            user.setWaterTarget(waterTarget);
        }
    
        if (profileDTO.getAge() != null) user.setAge(profileDTO.getAge());
        if (profileDTO.getGender() != null) user.setGender(profileDTO.getGender());
        if (profileDTO.getHeight() != null) user.setHeight(profileDTO.getHeight());
        if (profileDTO.getWeight() != null) user.setWeight(profileDTO.getWeight());
        if (profileDTO.getActivityLevel() != null) user.setActivityLevel(profileDTO.getActivityLevel());

        
        updateHealthMetrics(user);

        return userDAO.save(user);
    }

   
    public User findById(Long userId) {
        return userDAO.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在: " + userId));
    }

  
    public User findByUsername(String username) {
        return userDAO.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("用戶不存在: " + username));
    }

  
    @Transactional
    public User updateProfile(UserProfileDTO profileDTO) {
        logger.info("開始更新用戶資料: {}", profileDTO.getUsername());
        User user = findByUsername(profileDTO.getUsername());
    
        updateBasicInfo(user, profileDTO);
     
        updateHealthMetrics(user);
        
  
        User savedUser = userDAO.save(user);
        logger.info("用戶資料更新完成，TDEE: {}", savedUser.getTdee());
        return savedUser;
    }

 
private double calculateBMR(User user) {
    if ("男".equals(user.getGender())) {
        return 88.362 + (13.397 * user.getWeight()) + 
               (4.799 * user.getHeight()) - (5.677 * user.getAge());
    } else {
        return 447.593 + (9.247 * user.getWeight()) + 
               (3.098 * user.getHeight()) - (4.330 * user.getAge());
    }
}
    
    @Transactional
    public User updateProfile(Long userId, String gender, Integer age, 
                            Double height, Double weight, String activityLevel) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
            

        if (gender != null) user.setGender(gender);
        if (age != null) user.setAge(age);
        if (height != null) user.setHeight(height);
        if (weight != null) user.setWeight(weight);
        if (activityLevel != null) user.setActivityLevel(activityLevel);
        

        if (height != null && weight != null) {
            double bmi = weight / ((height/100) * (height/100));
            user.setBmi(bmi);
        }

        updateHealthMetrics(user);
       
        
     
        if (weight != null) {
            user.setWaterTarget(calculateWaterTarget(weight));
        }
        
        return userDAO.save(user);
    }
   

    public User updateUser(User user) {
        if (user.getUserId() == null) {
            throw new IllegalArgumentException("The given id must not be null");
        }
        User existingUser = userDAO.findById(user.getUserId())
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
        
       
        existingUser.setGender(user.getGender());
        existingUser.setAge(user.getAge());
        existingUser.setWeight(user.getWeight());
        existingUser.setHeight(user.getHeight());
        existingUser.setActivityLevel(user.getActivityLevel());
        updateHealthMetrics(user);
     
        
   
        Integer waterTarget = calculateWaterTarget(existingUser.getWeight());
        existingUser.setWaterTarget(waterTarget);
        if (existingUser.getHeight() != null && existingUser.getWeight() != null) {
            double heightInMeters = existingUser.getHeight() / 100.0;
            double bmi = existingUser.getWeight() / (heightInMeters * heightInMeters);
            existingUser.setBmi(Math.round(bmi * 10.0) / 10.0);
        }
        
     
        updateHealthMetrics(existingUser);
        
        return userDAO.save(existingUser);
    
      
    }
   
    private void updateBasicInfo(User user, UserProfileDTO profileDTO) {
        if (profileDTO.getGender() != null) user.setGender(profileDTO.getGender());
        if (profileDTO.getAge() != null) user.setAge(profileDTO.getAge());
        if (profileDTO.getHeight() != null) user.setHeight(profileDTO.getHeight());
        if (profileDTO.getWeight() != null) user.setWeight(profileDTO.getWeight());
        if (profileDTO.getActivityLevel() != null) user.setActivityLevel(profileDTO.getActivityLevel());
    }




   void updateHealthMetrics(User user) {
        logger.info("開始更新用戶健康指標");
        
    
        if (user.getHeight() != null && user.getWeight() != null) {
            double heightInMeters = user.getHeight() / 100.0;
            double bmi = user.getWeight() / (heightInMeters * heightInMeters);
            user.setBmi(bmi);
            logger.info("BMI 已更新: {}", bmi);
        }
        if (user.getWeight() != null) {
            int waterTarget = calculateWaterTarget(user.getWeight());
            user.setWaterTarget(waterTarget);
   }
     
        if (user.getWeight() != null && user.getHeight() != null && 
            user.getAge() != null && user.getGender() != null) {
            
            double bmr = calculateBMR(user);
            double activityMultiplier = getActivityMultiplier(user.getActivityLevel());
            
      
            logger.info("TDEE 計算詳情 - 用戶ID: {}, BMR: {}, 活動等級: {}, 活動倍率: {}", 
                       user.getUserId(), bmr, user.getActivityLevel(), activityMultiplier);
            
            int calculatedTDEE = (int) Math.round(bmr * activityMultiplier);
            user.setTdee(calculatedTDEE);
            
            logger.info("TDEE 已更新為: {}", calculatedTDEE);
        }
    }



    private boolean isProfileComplete(User user) {
        return user.getGender() != null && 
               user.getAge() != null && 
               user.getHeight() != null && 
               user.getWeight() != null && 
               user.getActivityLevel() != null;
    }
    

    private double getActivityMultiplier(String activityLevel) {
        logger.info("獲取活動倍率，輸入活動等級: {}", activityLevel);
        
        if (activityLevel == null) {
            return 1.2; 
        }
        
        try {
            
            double level = Double.parseDouble(activityLevel);
            if (level >= 1.2 && level <= 2.0) {
                return level;
            }
        } catch (NumberFormatException e) {
          
            return switch (activityLevel.trim()) {
                case "久坐" -> 1.2;
                case "輕度活動" -> 1.375;
                case "中度活動" -> 1.55;
                case "高度活動" -> 1.725;
                case "極高度活動" -> 1.9;
                default -> 1.2;
            };
        }
        
       
        return 1.2;
    }
 
  
    private Integer calculateWaterTarget(Double weight) {
        if (weight == null) return 2000;
        return (int) Math.round(weight * 33); 
    }
    
    @Transactional
    public void updateAllUsersTDEE() {
        logger.info("開始更新所有用戶的 TDEE");
        List<User> users = userDAO.findAll();
        logger.info("找到 {} 個用戶需要更新", users.size());
        
        for (User user : users) {
            logger.info("處理用戶 ID: {}, 用戶名: {}", user.getUserId(), user.getUsername());
            
            if (isProfileComplete(user)) {
                logger.info("用戶資料完整，開始計算 TDEE");
                logger.info("用戶資料 - 性別: {}, 年齡: {}, 身高: {}, 體重: {}, 活動等級: {}", 
                    user.getGender(), user.getAge(), user.getHeight(), 
                    user.getWeight(), user.getActivityLevel());
                
         
                double bmr;
                if ("男".equals(user.getGender())) {
                    bmr = 88.362 + (13.397 * user.getWeight()) + 
                          (4.799 * user.getHeight()) - (5.677 * user.getAge());
                } else {
                    bmr = 447.593 + (9.247 * user.getWeight()) + 
                          (3.098 * user.getHeight()) - (4.330 * user.getAge());
                }
                
           
                String activityLevel = user.getActivityLevel();
                if (activityLevel == null || activityLevel.trim().isEmpty()) {
                    logger.warn("活動等級為空，使用預設值");
                    activityLevel = "1.2";
                }
                
            
                double activityMultiplier;
                try {
                    activityMultiplier = Double.parseDouble(activityLevel);
                
                    if (activityMultiplier < 1.2 || activityMultiplier > 2.0) {
                        logger.warn("活動倍率超出範圍 (1.2-2.0)，使用預設值");
                        activityMultiplier = 1.2;
                    }
                } catch (NumberFormatException e) {
                 
                    logger.warn("活動等級格式錯誤，使用預設值");
                    activityMultiplier = 1.2;
                }
                
                int calculatedTDEE = (int) Math.round(bmr * activityMultiplier);
                int oldTDEE = user.getTdee();
                
                logger.info("計算結果 - BMR: {}, 活動倍率: {}", 
                    String.format("%.2f", bmr), 
                    String.format("%.2f", activityMultiplier));
                logger.info("TDEE 更新 - 舊值: {}, 新值: {}", oldTDEE, calculatedTDEE);
                
             
                user.setTdee(calculatedTDEE);
                userDAO.save(user);
                logger.info("用戶 TDEE 更新成功");
            } else {
                logger.warn("用戶資料不完整，跳過 TDEE 更新");
            }
        }
        logger.info("所有用戶 TDEE 更新完成");
    }
    
    
    public Map<String, Object> getUserDashboard(Long userId) {
        User user = findById(userId);
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("userProfile", user);
        dashboard.put("profileComplete", isProfileComplete(user));
        dashboard.put("registrationDays", 
            (new Date().getTime() - user.getCreateDate().getTime()) / (1000 * 60 * 60 * 24));
        
        return dashboard;
    }

    public User authenticateUser(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用戶不存在");
        }
        
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密碼錯誤");
        }
        
        return user;
    }
	}
