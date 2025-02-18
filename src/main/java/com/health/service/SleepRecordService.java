package com.health.service;

import com.health.Repository.SleepRecordRepository;
import com.health.dao.SleepRecordDAO;
import com.health.dao.UserDAO;
import com.health.model.SleepRecord;
import com.health.model.User;
import com.health.model.SleepRecord.SleepQuality;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import java.util.*;
@Service
@Transactional
public class SleepRecordService {
	 @PersistenceContext
	    private EntityManager entityManager;  
	    
	    private final SleepRecordDAO sleepRecordDAO;
	    private final UserDAO userDAO;
   
	    private final UserService userService;
	    private static final Logger logger = LoggerFactory.getLogger(SleepRecordService.class);
	    @Autowired
	    public SleepRecordService(SleepRecordDAO sleepRecordDAO, 
	                             UserDAO userDAO,
	                             UserService userService) {
	        this.sleepRecordDAO = sleepRecordDAO;
	        this.userDAO = userDAO;
	        this.userService = userService;
	    }

	    public SleepRecord recordSleep(Long userId, SleepRecord sleepRecord) {
	        try {
	            User user = userDAO.findById(userId)
	                .orElseThrow(() -> new RuntimeException("用戶不存在"));
	            logger.info("查詢到的今日記錄: {}", sleepRecord); 
	            sleepRecord.setUser(user);
	            
	       
	            double totalHours = (sleepRecord.getSleepEnd().getTime() - 
	                               sleepRecord.getSleepStart().getTime()) / (1000.0 * 60 * 60);
	            sleepRecord.setTotalSleepHours(totalHours);
	            
	 
	            double deepSleepPercentage = (sleepRecord.getDeepSleepMinutes() / (totalHours * 60)) * 100;
	            if (deepSleepPercentage >= 25) {
	                sleepRecord.setSleepQuality(SleepRecord.SleepQuality.GOOD);
	            } else if (deepSleepPercentage >= 20) {
	                sleepRecord.setSleepQuality(SleepRecord.SleepQuality.FAIR);
	            } else {
	                sleepRecord.setSleepQuality(SleepRecord.SleepQuality.POOR);
	            }
	            
	            System.out.println("準備保存的記錄: " + sleepRecord);
	            
	            SleepRecord savedRecord = sleepRecordDAO.save(sleepRecord);
	            entityManager.flush();
	            

	            System.out.println("保存成功，ID: " + savedRecord.getSleepId());
	            
	            return savedRecord;
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new RuntimeException("保存睡眠記錄失敗: " + e.getMessage());
	        }
	    }


	    public SleepRecord recordSleep(Long userId, Map<String, Object> request) {
	        try {
	            User user = userDAO.findById(userId)
	                .orElseThrow(() -> new RuntimeException("用戶不存在"));
	            
	            SleepRecord sleepRecord = new SleepRecord();
	            sleepRecord.setUser(user);
	            sleepRecord.setSleepStart(new Date((Long) request.get("sleepStart")));
	            sleepRecord.setSleepEnd(new Date((Long) request.get("sleepEnd")));
	            sleepRecord.setDeepSleepMinutes((Integer) request.get("deepSleepMinutes"));
	            if (request.get("lightSleepMinutes") != null) {
	                sleepRecord.setLightSleepMinutes((Integer) request.get("lightSleepMinutes"));
	            }
	            
	            return recordSleep(userId, sleepRecord);
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new RuntimeException("保存睡眠記錄失敗: " + e.getMessage());
	        }
	    }

	
	    public SleepRecord recordSleep(Long userId, Date sleepStart, Date sleepEnd, 
	            int deepSleepMinutes, int lightSleepMinutes) {
	        try {
	        	System.out.println("Service層接收到的數據: " + 
	                    "userId=" + userId + 
	                    ", sleepStart=" + sleepStart + 
	                    ", sleepEnd=" + sleepEnd + 
	                    ", deepSleepMinutes=" + deepSleepMinutes + 
	                    ", lightSleepMinutes=" + lightSleepMinutes);
	            User user = userDAO.findById(userId)
	                .orElseThrow(() -> new RuntimeException("用戶不存在"));
	            
	            SleepRecord sleepRecord = new SleepRecord();
	            sleepRecord.setUser(user);
	            sleepRecord.setSleepStart(sleepStart);
	            sleepRecord.setSleepEnd(sleepEnd);
	            sleepRecord.setDeepSleepMinutes(deepSleepMinutes);
	            sleepRecord.setLightSleepMinutes(lightSleepMinutes);
	            
	            return recordSleep(userId, sleepRecord);
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new RuntimeException("保存睡眠記錄失敗: " + e.getMessage());
	        }
	    }
	
    private void setupSleepRecord(SleepRecord record) {
  
        double totalHours = calculateSleepHours(record.getSleepStart(), record.getSleepEnd());
        record.setTotalSleepHours(totalHours);
        
   
        SleepQuality quality = calculateSleepQuality(totalHours, record.getDeepSleepMinutes());
        record.setSleepQuality(quality);
    }

    public List<SleepRecord> getSleepHistory(Long userId) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
        return sleepRecordDAO.findByUserOrderBySleepStartDesc(user);
    }

    public Optional<SleepRecord> getTodayRecord(Long userId) {
        try {
            
        	   String sql = "SELECT * FROM sleep_record " +
                       "WHERE user_id = :userId " +
                       "AND (DATE(sleep_start) = CURRENT_DATE() " +
                       "OR DATE(sleep_end) = CURRENT_DATE()) " +
                       "ORDER BY sleep_start DESC LIMIT 1";
           
           
            Query query = entityManager.createNativeQuery(sql, SleepRecord.class);
            query.setParameter("userId", userId);
            
            try {
                SleepRecord result = (SleepRecord) query.getSingleResult();
                return Optional.of(result);
            } catch (Exception e) {
                return Optional.empty();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private SleepQuality calculateSleepQuality(double totalHours, int deepSleepMinutes) {
       
        
        boolean goodDuration = totalHours >= 7 && totalHours <= 9;
        boolean fairDuration = (totalHours >= 6 && totalHours < 7) || (totalHours > 9 && totalHours <= 10);
        
        double deepSleepHours = deepSleepMinutes / 60.0;
        double deepSleepPercentage = (deepSleepHours / totalHours) * 100;
        
        boolean goodDeepSleep = deepSleepPercentage >= 20 && deepSleepPercentage <= 25;
        boolean fairDeepSleep = deepSleepPercentage >= 15 && deepSleepPercentage < 20;
        
        if (goodDuration && goodDeepSleep) {
            return SleepQuality.GOOD;
        } else if (fairDuration || fairDeepSleep) {
            return SleepQuality.FAIR;
        } else {
            return SleepQuality.POOR;
        }
    }

    public int calculateSleepScore(Long userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        
        Optional<SleepRecord> latestRecord = sleepRecordDAO.findTopByUserOrderBySleepStartDesc(user);
        if (latestRecord.isEmpty()) {
            return 0;
        }
        
        SleepRecord record = latestRecord.get();
        double totalHours = record.getTotalSleepHours();
        
        int durationScore = calculateDurationScore(totalHours);
        int qualityScore = calculateQualityScore(record.getSleepQuality());
        
        return durationScore + qualityScore;
    }

    private int calculateDurationScore(double hours) {
        if (hours >= 7 && hours <= 9) return 10;
        else if (hours >= 6 && hours < 7) return 7;
        else if (hours > 9 && hours <= 10) return 7;
        else if (hours >= 5 && hours < 6) return 4;
        else return 0;
    }

    private int calculateQualityScore(SleepQuality quality) {
        switch (quality) {
            case GOOD: return 10;
            case FAIR: return 5;
            case POOR: return 0;
            default: return 0;
        }
    }

    public Map<String, Object> getSleepStatistics(Long userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date weekAgo = cal.getTime();
        
        List<SleepRecord> records = sleepRecordDAO
                .findByUserAndSleepStartBetween(user, weekAgo, new Date());
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageSleepDuration", 
                records.stream().mapToDouble(SleepRecord::getTotalSleepHours).average().orElse(0));
        stats.put("averageDeepSleep", 
                records.stream().mapToInt(SleepRecord::getDeepSleepMinutes).average().orElse(0));
        stats.put("sleepHistory", records);
        
        return stats;
    }
    
    private double calculateSleepHours(Date start, Date end) {
        long diff = end.getTime() - start.getTime();
        return diff / (1000.0 * 60 * 60); // 轉換為小時
    }

    public void deleteTodayRecord(Long userId) {
        try {
          
            String sql = "DELETE FROM sleep_record WHERE user_id = :userId " + 
                    "AND id = (SELECT id FROM (SELECT id FROM sleep_record " + 
                    "WHERE user_id = :userId " + 
                    "AND (DATE(sleep_start) = CURRENT_DATE() " +
                    "OR DATE(sleep_end) = CURRENT_DATE()) " +
                    "ORDER BY sleep_start DESC LIMIT 1) as temp)";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("userId", userId);
            query.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("刪除睡眠記錄失敗: " + e.getMessage());
        }
    }
}


