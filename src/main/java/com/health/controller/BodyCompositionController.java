package com.health.controller;

import com.health.model.BodyComposition;
import com.health.service.BodyCompositionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/body-composition")
public class BodyCompositionController {
    @Autowired
    private BodyCompositionService bodyCompositionService;

    @PostMapping("/{userId}")
    public ResponseEntity<BodyComposition> recordBodyComposition(
            @PathVariable Long userId,
            @RequestParam double weight,
            @RequestParam double height,
            @RequestParam(required = false) Double bodyFat,
            @RequestParam(required = false) Double muscleMass) {
        try {
            BodyComposition record = bodyCompositionService.recordBodyComposition(
                userId, weight, height, 
                bodyFat != null ? bodyFat : 0.0, 
                muscleMass != null ? muscleMass : 0.0
            );
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/statistics/{userId}")
    public ResponseEntity<Map<String, Object>> getStatistics(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = bodyCompositionService.getBodyCompositionStatistics(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/bmi-score")
    public ResponseEntity<Integer> getBMIScore(@RequestParam double bmi) {
        try {
            int score = bodyCompositionService.calculateBMIScore(bmi);
            return ResponseEntity.ok(score);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}