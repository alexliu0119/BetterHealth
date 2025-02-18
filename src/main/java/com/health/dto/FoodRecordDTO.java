package com.health.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FoodRecordDTO {
	private Long userId;
    private String mealType;
    private String foodName;
    private Double portion;
    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fat;
	public String getMealType() {
		return mealType;
	}
	public void setMealType(String mealType) {
		this.mealType = mealType;
	}
	public String getFoodName() {
		return foodName;
	}
	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}
	public Double getPortion() {
		return portion;
	}
	public void setPortion(Double portion) {
		this.portion = portion;
	}
	public Double getCalories() {
		return calories;
	}
	public void setCalories(Double calories) {
		this.calories = calories;
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
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	
	

		
	}
