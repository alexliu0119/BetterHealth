package com.health.dto;

import java.util.Arrays;

import com.health.model.User;

public class UserProfileDTO {
    private Long userId;
    private String username;
    private String gender;
    private Integer age;
    private Double height;
    private Double weight;
    private String activityLevel;
    private Integer tdee;       
    private Integer waterTarget;
    private Double bmi;
    
  
    public UserProfileDTO() {
    }
    
   
    public UserProfileDTO(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.gender = user.getGender();
        this.age = user.getAge();
        this.height = user.getHeight();
        this.weight = user.getWeight();
        this.activityLevel = user.getActivityLevel();
        this.tdee = user.getTdee();
        this.waterTarget = user.getWaterTarget();
        this.bmi = user.getBmi();
    }
    
   
    public static UserProfileDTO fromUser(User user) {
        return new UserProfileDTO(user);
    }
    
 
    public void updateUser(User user) {
        user.setGender(this.gender);
        user.setAge(this.age);
        user.setHeight(this.height);
        user.setWeight(this.weight);
        user.setActivityLevel(this.activityLevel);
        user.setTdee(this.tdee);
        user.setWaterTarget(this.waterTarget);
        user.setBmi(this.bmi);
    }
    
  
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getGender() { return gender; }
    public Integer getAge() { return age; }
    public Double getHeight() { return height; }
    public Double getWeight() { return weight; }
    public String getActivityLevel() { return activityLevel; }
    public Integer getTdee() { return tdee; }
    public Integer getWaterTarget() { return waterTarget; }
    public Double getBmi() { return bmi; }
    

    public void setUserId(Long userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setGender(String gender) { this.gender = gender; }
    public void setAge(Integer age) { this.age = age; }
    public void setHeight(Double height) { this.height = height; }
    public void setWeight(Double weight) { this.weight = weight; }
    public void setActivityLevel(String activityLevel) { 
        try {
            double level = Double.parseDouble(activityLevel);
            if (level < 1.2 || level > 2.0) {
                throw new IllegalArgumentException("活動倍率必須在 1.2 到 2.0 之間");
            }
        } catch (NumberFormatException e) {
          
            if (!Arrays.asList("久坐", "輕度活動", "中度活動", "高度活動", "極高度活動")
                    .contains(activityLevel)) {
                throw new IllegalArgumentException("無效的活動級別");
            }
        }
        this.activityLevel = activityLevel;
    }
    public void setTdee(Integer tdee) { this.tdee = tdee; }
    public void setWaterTarget(Integer waterTarget) { this.waterTarget = waterTarget; }
    public void setBmi(Double bmi) { this.bmi = bmi; }
}