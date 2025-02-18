package com.health.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "BodyComposition")
public class BodyComposition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private Double muscleMass;
    private double height;  
    private double weight; 
    private int age;
    private double bodyFat;
    private double bmi;
    private Date recordDate;

    public void calculateBMI() {
        if (height <= 0 || weight <= 0) {
            throw new IllegalArgumentException("Height and weight must be positive values.");
        }
      
        this.bmi = weight / ((height / 100) * (height / 100)); 
    }
    
    public double calculateBMR() {
        if (user == null || user.getGender() == null) {
            throw new NullPointerException("User or user gender is null");
        }
        
        String gender = user.getGender().toUpperCase().trim(); 
        if ("M".equals(gender)) {  
            return 66 + (13.7 * weight) + (5 * height) - (6.8 * age);
        } else if ("F".equals(gender)) { 
            return 655 + (9.6 * weight) + (1.8 * height) - (4.7 * age);
        } else {
            throw new IllegalArgumentException("Invalid gender value: " + gender);  
        }
    }

   
    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(double bodyFat) {
        this.bodyFat = bodyFat;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }
    public Double getMuscleMass() {
        return muscleMass;
    }
    
    public void setMuscleMass(Double muscleMass) {
        this.muscleMass = muscleMass;
    }
}
	
		
	

