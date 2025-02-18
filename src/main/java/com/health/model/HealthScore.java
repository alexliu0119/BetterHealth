package com.health.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "health_scores")
public class HealthScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id")
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @Column(name = "record_date")
    private LocalDateTime recordDate;  
    
   
    private Integer bmiScore;          // 15分
    private Integer bloodSugarScore;   // 10分
    private Integer dietScore;         // 30分
    private Integer exerciseScore;     // 20分
    private Integer sleepScore;        // 20分
    private Integer waterScore;        // 5分
    private Integer totalScore;       //總分
    @Column(name = "period_type")
    private String periodType;

    private Double bmi;
    public String getPeriodType() {
		return periodType;
	}
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}
	private Double bloodSugar;
    private Integer caloriesIntake;
    private Integer exerciseMinutes;
    private Double sleepHours;
    private Integer waterIntake;



public void setId(Long id) {
		Id = id;
	}
public User getUser() {
	return user;
}
public void setUser(User user) {
	this.user = user;
}
public LocalDateTime getRecordDate() {
	return recordDate;
}
public void setRecordDate(LocalDateTime recordDate) {
	this.recordDate = recordDate;
}
public Integer getBmiScore() {
	return bmiScore;
}
public void setBmiScore(Integer bmiScore) {
	this.bmiScore = bmiScore;
}
public Integer getBloodSugarScore() {
	return bloodSugarScore;
}
public void setBloodSugarScore(Integer bloodSugarScore) {
	this.bloodSugarScore = bloodSugarScore;
}
public Integer getDietScore() {
	return dietScore;
}
public void setDietScore(Integer dietScore) {
	this.dietScore = dietScore;
}
public Integer getExerciseScore() {
	return exerciseScore;
}
public void setExerciseScore(Integer exerciseScore) {
	this.exerciseScore = exerciseScore;
}
public Integer getSleepScore() {
	return sleepScore;
}
public void setSleepScore(Integer sleepScore) {
	this.sleepScore = sleepScore;
}
public Integer getWaterScore() {
	return waterScore;
}
public void setWaterScore(Integer waterScore) {
	this.waterScore = waterScore;
}
public Integer getTotalScore() {
	return totalScore;
}
public void setTotalScore(Integer totalScore) {
	this.totalScore = totalScore;
}
public Double getBmi() {
	return bmi;
}
public void setBmi(Double bmi) {
	this.bmi = bmi;
}
public Double getBloodSugar() {
	return bloodSugar;
}
public void setBloodSugar(Double bloodSugar) {
	this.bloodSugar = bloodSugar;
}
public Integer getCaloriesIntake() {
	return caloriesIntake;
}
public void setCaloriesIntake(Integer caloriesIntake) {
	this.caloriesIntake = caloriesIntake;
}
public Integer getExerciseMinutes() {
	return exerciseMinutes;
}
public void setExerciseMinutes(Integer exerciseMinutes) {
	this.exerciseMinutes = exerciseMinutes;
}
public Double getSleepHours() {
	return sleepHours;
}
public void setSleepHours(Double sleepHours) {
	this.sleepHours = sleepHours;
}
public Integer getWaterIntake() {
	return waterIntake;
}
public void setWaterIntake(Integer waterIntake) {
	this.waterIntake = waterIntake;
}
public Object getId() {

	return null;
}
}