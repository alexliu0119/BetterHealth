package com.health.controller;

import com.health.dao.BloodSugarDAO;
import com.health.dao.UserDAO;
import com.health.model.BloodSugar;
import com.health.model.User;  
import com.health.service.BloodSugarService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/blood-sugar") 
public class BloodSugarController {
    @Autowired
    private BloodSugarService bloodSugarService;
    
    @Autowired 
    private UserDAO userDAO;
    private BloodSugarDAO bloodSugarDAO;

    @PostMapping("")
    public ResponseEntity<?> recordBloodSugar(@RequestBody BloodSugar bloodSugar) {
        try {
            if (bloodSugar == null || bloodSugar.getUser() == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "無效的請求數據");
                return ResponseEntity.badRequest().body(response);
            }
            
            BloodSugar record = bloodSugarService.recordBloodSugar(
                bloodSugar.getUser().getUserId(),
                bloodSugar.getBloodSugar()
            );
            
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "記錄失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/today/{userId}")
    public ResponseEntity<?> getTodayRecord(@PathVariable Long userId) {
        try {
            BloodSugar record = bloodSugarService.getTodayBloodSugar(userId);
            if (record == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "尚未有今日血糖記錄");
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "獲取記錄失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @DeleteMapping("/today/{userId}")
    public ResponseEntity<?> deleteTodayRecord(@PathVariable Long userId) {
        try {
            bloodSugarService.deleteTodayBloodSugar(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "記錄已刪除");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "刪除失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/score/{userId}")
    public ResponseEntity<Integer> getBloodSugarScore(@PathVariable Long userId) {
        try {
            int score = bloodSugarService.calculateBloodSugarScore(userId);
            return ResponseEntity.ok(score);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}