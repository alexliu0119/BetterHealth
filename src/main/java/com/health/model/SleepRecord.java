package com.health.model;

import jakarta.persistence.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@Entity
@Table(name = "sleep_record")
public class SleepRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long sleepId;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
  
    private User user;
    private Double totalSleepHours;
    @Column(name = "sleep_start")
    private Date sleepStart;
    @Column(name = "sleep_end")  
    private Date sleepEnd;
    @Column(name = "deep_sleep_minutes") 
    private int deepSleepMinutes;
    @Column(name = "light_sleep_minutes", nullable = false)
    private int lightSleepMinutes = 0;
    @Enumerated(EnumType.STRING)           
    @Column(name = "sleep_quality", columnDefinition = "TINYINT")
    private SleepQuality sleepQuality;
	
   

    
    public enum SleepQuality {
        GOOD("良好"),
        FAIR("普通"),
        POOR("不佳");
        
        private final String description;
        
        SleepQuality(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    public SleepQuality getSleepQuality() {
        return sleepQuality;
    }

    public void setSleepQuality(SleepQuality sleepQuality) {
        this.sleepQuality = sleepQuality;
    }
    
    public int getTotalSleepMinutes() {
    	  return (int) ((sleepEnd.getTime() - sleepStart.getTime()) / (1000 * 60));
    }

    
    
  
    public Long getSleepId() {
        return sleepId;
    }

    public void setSleepId(Long sleepId) {
        this.sleepId = sleepId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getSleepStart() {
        return sleepStart;
    }

    public void setSleepStart(Date sleepStart) {
        this.sleepStart = sleepStart;
    }

    public Date getSleepEnd() {
        return sleepEnd;
    }

    public void setSleepEnd(Date sleepEnd) {
        this.sleepEnd = sleepEnd;
    }

    public int getDeepSleepMinutes() {
        return deepSleepMinutes;
    }

    public void setDeepSleepMinutes(int deepSleepMinutes) {
        this.deepSleepMinutes = deepSleepMinutes;
    }

    public int getLightSleepMinutes() {
        return lightSleepMinutes;
    }

    public void setLightSleepMinutes(int lightSleepMinutes) {
        this.lightSleepMinutes = lightSleepMinutes;
    }


    public Double getTotalSleepHours() {
        return totalSleepHours;
    }
    public void setTotalSleepHours(Double totalSleepHours) {
        this.totalSleepHours = totalSleepHours;
    }
    
}
    

	

