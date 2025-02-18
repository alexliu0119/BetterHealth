package com.health.model; // 确保包路径正确

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.health.service.CalorieTrackerService;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    @Column(unique = true)
    private String username;
    private String password;
   
    
    private Date createDate;
  
    @Column(name = "weight")
    private Double weight; 
    
    @Column(name = "height")
    private Double height;  

    @Column(name = "age")
    private Integer age;    
    @Column(name = "bmi")
    private Double bmi;

    @Column(name = "gender")
    private String gender;  

    @Column(name = "activity_level")
    private String activityLevel; 

    @Column(name = "tdee")
    private Integer tdee;   

    @Column(name = "water_target")
    private Integer waterTarget; 
    @Column(name = "last_login_date")
    private Date lastLoginDate;
    
    public Date getLastLoginDate() {
        return lastLoginDate;
    }
    
    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
 
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<HealthScore> healthScores;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CalorieTracker> calorieTrackers;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getActivityLevel() {
		return activityLevel;
	}

	public void setActivityLevel(String activityLevel) {
		this.activityLevel = activityLevel;
	}

	public Integer getTdee() {
		return tdee;
	}

	public void setTdee(Integer tdee) {
		this.tdee = tdee;
	}

	public Integer getWaterTarget() {
		return waterTarget;
	}

	public void setWaterTarget(Integer waterTarget) {
		this.waterTarget = waterTarget;
	}

	public List<HealthScore> getHealthScores() {
		return healthScores;
	}

	public void setHealthScores(List<HealthScore> healthScores) {
		this.healthScores = healthScores;
	}

	public List<CalorieTracker> getCalorieTrackers() {
		return calorieTrackers;
	}

	public void setCalorieTrackers(List<CalorieTracker> calorieTrackers) {
		this.calorieTrackers = calorieTrackers;
	}
   public double getBmi() {
	return bmi;
   }
	public void setBmi(Double bmi) {
		this.bmi = bmi;
		
	
	}

    
}