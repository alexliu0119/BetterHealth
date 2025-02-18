package com.health.model;

import jakarta.persistence.*;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@Table(name = "exercises")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "duration_minutes")  
    
    private Integer durationMinutes ;
    private Integer heartRate ;    
    private Double caloriesBurned ;
    @Column(name = "intensity")
   private String intensity;
    
  
    
    public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	@Column(name = "exercise_type")
    private String exerciseType;
    
  
    
    @Column(name = "exercise_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date exerciseDate;

	 public double calculateCaloriesBurned() {
	     
	        User user = this.getUser();
	        if (user == null || durationMinutes == null || heartRate == null) {
	            return 0.0;
	        }

	        boolean isMale = "男".equals(user.getGender());
	        double weight = user.getWeight();
	        int age = user.getAge();
	        
	       
	        String intensity;
	        if (heartRate < 100) {
	            intensity = "low";
	        } else if (heartRate < 140) {
	            intensity = "medium";
	        } else {
	            intensity = "high";
	        }
	        
	     
	        double baseRate;
	        switch (intensity) {
	            case "high":
	                baseRate = 10.0;
	                break;
	            case "medium":
	                baseRate = 7.0;
	                break;
	            case "low":
	                baseRate = 4.0;
	                break;
	            default:
	                baseRate = 5.0;
	        }
	        
	   
	        double activityMultiplier = getActivityMultiplier(exerciseType);
	        double intensityMultiplier = calculateIntensityMultiplier(heartRate);
	        
	        return (durationMinutes / 60.0) * baseRate * activityMultiplier * intensityMultiplier;
	    }
	  private double calculateIntensityMultiplier(int heartRate) {
	        
	        if (heartRate < 100) return 1.0;      
	        if (heartRate < 120) return 1.5;     
	        if (heartRate < 140) return 2.0;    
	        if (heartRate < 160) return 2.5;     
	        return 3.0;                          
	    }

	    private double getActivityMultiplier(String exerciseType) {
	      
	        switch (exerciseType.toLowerCase()) {
            case "跑步":
                return 9.8;  
            case "游泳":
                return 8.0;  
            case "騎車":
                return 7.5;  
            case "健身":
                return 4.5;  
            case "走路":
                return 3.8;  
            default:
                return 1.0;  
        }
    }

	   
	    @JsonProperty("formattedCaloriesBurned")
	    public String getFormattedCaloriesBurned() {
	        if (this.caloriesBurned == null) return "0 卡路里";
	        return String.format("%.1f 卡路里", this.caloriesBurned);
	    }
	
 
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getExerciseDate() {
		return exerciseDate;
	}

	public void setExerciseDate(Date exerciseDate) {
		this.exerciseDate = exerciseDate;
	}

	public String getExerciseType() {
		return exerciseType;
	}

	public void setExerciseType(String exerciseType) {
		this.exerciseType = exerciseType;
	}

	public Integer getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(Integer durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	public Integer getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(Integer heartRate) {
		this.heartRate = heartRate;
	}

	public Double getCaloriesBurned() {
		return caloriesBurned;
	}

	public void setCaloriesBurned(Double caloriesBurned) {
		this.caloriesBurned = caloriesBurned;
	}

	 public String getIntensity() {
	        return intensity;
	    }

	    public void setIntensity(String intensity) {
	        this.intensity = intensity;
	    }

	
  
}