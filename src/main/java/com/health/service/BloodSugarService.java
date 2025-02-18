package com.health.service;

import com.health.dao.BloodSugarDAO;
import com.health.dao.UserDAO;
import com.health.model.BloodSugar;
import com.health.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class BloodSugarService {
    @Autowired
    private BloodSugarDAO bloodSugarDAO;
    
    @Autowired
    private UserDAO userDAO;

    public BloodSugar recordBloodSugar(Long userId, double bloodSugar) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

  
            BloodSugar todayRecord = getTodayBloodSugar(userId);
            if (todayRecord != null) {
            
                todayRecord.setBloodSugar(bloodSugar);
                todayRecord.setMeasureTime(new Date()); 
                return bloodSugarDAO.save(todayRecord);
            }

            
            BloodSugar newRecord = new BloodSugar();
            newRecord.setUser(user);
            newRecord.setBloodSugar(bloodSugar);
            newRecord.setMeasureTime(new Date()); 
            return bloodSugarDAO.save(newRecord);
        }



    


    public BloodSugar getTodayBloodSugar(Long userId) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
            
       
        List<BloodSugar> todayRecords = bloodSugarDAO.findByUserOrderByMeasureTimeDesc(user);
        if (todayRecords.isEmpty()) {
            return null;
        }
     
        return todayRecords.get(0);
    }




    public List<BloodSugar> getBloodSugarHistory(Long userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        return bloodSugarDAO.findByUserOrderByMeasureTimeDesc(user);
    }

    public void deleteTodayBloodSugar(Long userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        bloodSugarDAO.deleteTodayRecord(user);
    }


    // 保留評分功能
    public int calculateBloodSugarScore(Long userId) {
        BloodSugar todayRecord = getTodayBloodSugar(userId);
        if (todayRecord == null) {
            return 0;
        }
        
        double bloodSugar = todayRecord.getBloodSugar();
        if (bloodSugar >= 70 && bloodSugar <= 100) {
            return 10;  
        } else if (bloodSugar > 100 && bloodSugar <= 125) {
            return 5;   
        } else {
            return 0;   
        }
    }}








