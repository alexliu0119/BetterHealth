package com.health.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.health.model.CalorieTracker;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
public class DailySummaryDTO {
    private Double totalCalories = 0.0;
    private Double calorieTarget = 0.0;
    private Double waterIntake = 0.0;
    private Double waterTarget = 0.0;
    private Double protein = 0.0;
    private Double carbs = 0.0;
    private Double fat = 0.0;
    private Double remainingCalories = 0.0;
    private Double caloriesBurnt = 0.0;

	public Double getTotalCalories() {
		return totalCalories;
	}
	public void setTotalCalories(Double totalCalories) {
		this.totalCalories = totalCalories;
	}
	public Double getCalorieTarget() {
		return calorieTarget;
	}
	public void setCalorieTarget(Double calorieTarget) {
		this.calorieTarget = calorieTarget;
	}
	public Double getWaterIntake() {
		return waterIntake;
	}
	public void setWaterIntake(Double waterIntake) {
		this.waterIntake = waterIntake;
	}
	public Double getWaterTarget() {
		return waterTarget;
	}
	public void setWaterTarget(Double waterTarget) {
		this.waterTarget = waterTarget;
	}
	public Double getProtein() {
		return protein;
	}
	public void setProtein(Double protein) {
		this.protein = protein;
	}
	public Double getCarbs() {
		return carbs;
	}
	public void setCarbs(Double carbs) {
		this.carbs = carbs;
	}
	public Double getFat() {
		return fat;
	}
	public void setFat(Double fat) {
		this.fat = fat;
	}
	public Double getRemainingCalories() {
		return remainingCalories;
	}
	public void setRemainingCalories(Double remainingCalories) {
		this.remainingCalories = remainingCalories;
	}
	public Double getCaloriesBurnt() {
		return caloriesBurnt;
	}
	public void setCaloriesBurnt(Double caloriesBurnt) {
		this.caloriesBurnt = caloriesBurnt;
	}
	
}