package com.health.model;

import jakarta.persistence.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "calorie_tracker")
public class CalorieTracker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trackerId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date recordDate; 

   
    @Column(nullable = false)
    private Integer targetCalories = 0; 
    @Column(nullable = false)
    private Integer caloriesIntake = 0; 
    @Column(nullable = false)
    private Integer caloriesBurned = 0;  
    
    @Column(nullable = false)
    private Integer remainingCalories = 0;  
    
    
    @Column(nullable = false)
    private Double carbohydrates = 0.0;
    @Column(nullable = false)
    private Double protein = 0.0;    
    @Column(nullable = false)
    private Double fat = 0.0;         
    
  
    @Column(nullable = false)
    private Integer waterTarget = 0;  
    @Column(nullable = false)
    private Integer waterIntake = 0;  

    public CalorieTracker() {
        this.recordDate = new Date(); 
        this.targetCalories = 0;
        this.protein = 0.0;
        this.carbohydrates = 0.0;
        this.fat = 0.0;
        this.waterIntake = 0;
    }

    public void initializeFromProfile(User user) {
        this.trackerId = user.getUserId();
        this.targetCalories = user.getTdee();
        this.waterTarget = user.getWaterTarget();
        this.remainingCalories = this.targetCalories;
    }


    public void calculateRemainingCalories() {
        this.remainingCalories = targetCalories - (caloriesIntake - caloriesBurned);
    }

	public Long getTrackerId() {
		return trackerId;
	}

	public void setTrackerId(Long trackerId) {
		this.trackerId = trackerId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	public Integer getTargetCalories() {
		return targetCalories;
	}

	public void setTargetCalories(Integer targetCalories) {
		this.targetCalories = targetCalories;
	}

	public Integer getCaloriesIntake() {
		return caloriesIntake;
	}

	public void setCaloriesIntake(Integer caloriesIntake) {
		this.caloriesIntake = caloriesIntake;
	}

	public Integer getCaloriesBurned() {
		return caloriesBurned;
	}

	public void setCaloriesBurned(Integer caloriesBurned) {
		this.caloriesBurned = caloriesBurned;
	}

	public Integer getRemainingCalories() {
		return remainingCalories;
	}

	public void setRemainingCalories(Integer remainingCalories) {
		this.remainingCalories = remainingCalories;
	}

	public Double getCarbohydrates() {
		return carbohydrates;
	}

	public void setCarbohydrates(Double carbohydrates) {
		this.carbohydrates = carbohydrates;
	}

	public Double getProtein() {
		return protein;
	}

	public void setProtein(Double protein) {
		this.protein = protein;
	}

	public Double getFat() {
		return fat;
	}

	public void setFat(Double fat) {
		this.fat = fat;
	}

	public Integer getWaterTarget() {
		return waterTarget;
	}

	public void setWaterTarget(Integer waterTarget) {
		this.waterTarget = waterTarget;
	}

	public Integer getWaterIntake() {
		return waterIntake;
	}

	public void setWaterIntake(Integer waterIntake) {
		this.waterIntake = waterIntake;
	}


}
