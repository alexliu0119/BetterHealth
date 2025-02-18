package com.health.controller;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.health.dto.FoodRecordDTO;
import com.health.service.DailyRecordService;
import com.health.service.UsdaFoodService;

@RestController
@RequestMapping("/api/food")
public class FoodController {
    
    @Value("${usda.api.key}")
    private String usdaApiKey;
    @Autowired
    private DailyRecordService dailyRecordService;  
    @Value("${usda.api.base-url}")
    private String usdaBaseUrl;
    @Autowired
    private UsdaFoodService usdaFoodService;
    @GetMapping("/search")
    public ResponseEntity<?> searchFood(@RequestParam String query) {
        try {
            List<Map<String, Object>> results = usdaFoodService.searchFood(query);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/record")  
    public ResponseEntity<?> recordFood(@RequestBody FoodRecordDTO foodRecord) {
        try {
            dailyRecordService.recordFood(foodRecord);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

    
