package com.health.dto;

public class MealRecordRequest {
    private Long userId;
    private String foodName;
    private Double portion;
    private String mealType;
    // getters, setters
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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
	public String getMealType() {
		return mealType;
	}
	public void setMealType(String mealType) {
		this.mealType = mealType;
	}
}