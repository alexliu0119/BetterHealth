package com.health.service;

import com.health.dao.BodyCompositionDAO;
import com.health.dao.UserDAO;
import com.health.model.BodyComposition;
import com.health.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class BodyCompositionService {
    @Autowired
    private BodyCompositionDAO bodyCompositionDAO;
    
    @Autowired
    private UserDAO userDAO;
    @Autowired
private UserService userService;
    public BodyComposition recordBodyComposition(Long userId, double weight, double height, 
            double bodyFat, double muscleMass) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        
        BodyComposition composition = new BodyComposition();
        composition.setUser(user);
        composition.setWeight(weight);
        composition.setHeight(height);
        composition.setBodyFat(bodyFat);
        composition.setMuscleMass(muscleMass);
        composition.setRecordDate(new Date());
        
      
        double bmi = calculateBMI(weight, height);
        composition.setBmi(bmi);
        
       
        updateUserMetrics(user, weight, height);
        
        return bodyCompositionDAO.save(composition);
    }

    private double calculateBMI(double weight, double height) {
        return weight / ((height / 100) * (height / 100));
    }

    private void updateUserMetrics(User user, double weight, double height) {
        user.setWeight(weight);
        user.setHeight(height);
        
     
        Integer waterTarget = (int) (weight * 32.5);
        user.setWaterTarget(waterTarget);
        
        userDAO.save(user);
    }

   
    public Map<String, Object> getBodyCompositionStatistics(Long userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        
        List<BodyComposition> records = bodyCompositionDAO.findByUserOrderByRecordDateDesc(user);
        
        Map<String, Object> stats = new HashMap<>();
        if (!records.isEmpty()) {
            BodyComposition latest = records.get(0);
            stats.put("currentBMI", latest.getBmi());
            stats.put("currentWeight", latest.getWeight());
            stats.put("currentBodyFat", latest.getBodyFat());
            stats.put("currentMuscleMass", latest.getMuscleMass());
            
            if (records.size() > 1) {
                BodyComposition previous = records.get(1);
                stats.put("weightChange", latest.getWeight() - previous.getWeight());
                stats.put("bodyFatChange", latest.getBodyFat() - previous.getBodyFat());
            }
        }
        
        return stats;
    }

    public int calculateBMIScore(double bmi) {
        if (bmi >= 18.5 && bmi < 24) {
            return 15; 
        } else if (bmi >= 24 && bmi < 27) {
            return 10;  // 過重
        } else if ((bmi >= 17 && bmi < 18.5) || (bmi >= 27 && bmi < 30)) {
            return 5;   
        } else {
            return 0;   
        }
    }
}