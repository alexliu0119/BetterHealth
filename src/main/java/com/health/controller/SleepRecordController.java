package com.health.controller;

import com.health.dao.SleepRecordDAO;
import com.health.dao.UserDAO;
import com.health.model.SleepRecord;
import com.health.model.User;
import com.health.service.SleepRecordService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sleep")
public class SleepRecordController {
    @Autowired
    private SleepRecordService sleepRecordService;
    private UserDAO userDAO;
  
    @PostMapping
    public ResponseEntity<?> createSleepRecord(@RequestBody SleepRecord sleepRecord) {
        try {
            SleepRecord saved = sleepRecordService.recordSleep(sleepRecord.getUser().getUserId(), sleepRecord);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("保存睡眠記錄失敗: " + e.getMessage());
        }
    }
    @PostMapping("/{userId}")
    public ResponseEntity<?> recordSleep(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> request) {
        try {
            System.out.println("接收到的請求數據: " + request);
            
   
            if (!request.containsKey("sleepStart") || 
                !request.containsKey("sleepEnd") || 
                !request.containsKey("deepSleepMinutes")) {
                return ResponseEntity.badRequest()
                    .body("缺少必要參數：sleepStart、sleepEnd、deepSleepMinutes");
            }

     
            Date sleepStart = new Date((Long) request.get("sleepStart"));
            Date sleepEnd = new Date((Long) request.get("sleepEnd"));
            int deepSleepMinutes = Integer.parseInt(request.get("deepSleepMinutes").toString());
            
          
            int lightSleepMinutes = 0;
            if (request.containsKey("lightSleepMinutes") && request.get("lightSleepMinutes") != null) {
                lightSleepMinutes = Integer.parseInt(request.get("lightSleepMinutes").toString());
            } else {
                
                long totalMinutes = (sleepEnd.getTime() - sleepStart.getTime()) / (1000 * 60);
                lightSleepMinutes = (int) totalMinutes - deepSleepMinutes;
            }

            System.out.println("處理後的數據: " + 
                "sleepStart=" + sleepStart + 
                ", sleepEnd=" + sleepEnd + 
                ", deepSleepMinutes=" + deepSleepMinutes + 
                ", lightSleepMinutes=" + lightSleepMinutes);

            SleepRecord record = sleepRecordService.recordSleep(userId, sleepStart, sleepEnd,
                    deepSleepMinutes, lightSleepMinutes);
                    
            if (record != null) {
                return ResponseEntity.ok(record);
            } else {
                return ResponseEntity.badRequest().body("保存睡眠記錄失敗");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("錯誤：" + e.getMessage());
        }
    }
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getSleepHistory(@PathVariable Long userId) {
        try {
            List<SleepRecord> history = sleepRecordService.getSleepHistory(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("獲取睡眠歷史記錄失敗: " + e.getMessage());
        }
    }
    @GetMapping("/today/{userId}")
    public ResponseEntity<?> getTodaySleep(@PathVariable Long userId) {
        try {
            Optional<SleepRecord> record = sleepRecordService.getTodayRecord(userId);
            
            if (record.isPresent()) {
                SleepRecord sleepRecord = record.get();
                Map<String, Object> response = new HashMap<>();
                response.put("sleepId", sleepRecord.getSleepId());
                response.put("totalSleepHours", sleepRecord.getTotalSleepHours());
                response.put("deepSleepMinutes", sleepRecord.getDeepSleepMinutes());
                response.put("lightSleepMinutes", sleepRecord.getLightSleepMinutes());
                response.put("sleepQuality", sleepRecord.getSleepQuality());
                response.put("sleepStart", sleepRecord.getSleepStart());
                response.put("sleepEnd", sleepRecord.getSleepEnd());
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.ok(Map.of("message", "尚未有今日睡眠記錄"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "獲取今日睡眠記錄失敗",
                "message", e.getMessage()
            ));
        }
    }
    @DeleteMapping("/today/{userId}")
    @Transactional
    public ResponseEntity<?> deleteTodaySleep(@PathVariable Long userId) {
        try {
            sleepRecordService.deleteTodayRecord(userId);
            return ResponseEntity.ok(Map.of("message", "成功刪除睡眠記錄"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "刪除失敗",
                "message", e.getMessage()
            ));
        }
    }


    @GetMapping("/statistics/{userId}")
    public ResponseEntity<?> getStatistics(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = sleepRecordService.getSleepStatistics(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("獲取睡眠統計失敗: " + e.getMessage());
        }
    }

    @GetMapping("/score/{userId}")
    public ResponseEntity<?> getSleepScore(@PathVariable Long userId) {
        try {
            int score = sleepRecordService.calculateSleepScore(userId);
            return ResponseEntity.ok(score);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("計算睡眠分數失敗: " + e.getMessage());
        }
    }
}