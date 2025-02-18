package com.health.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "blood_sugar")
public class BloodSugar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne  
    @JoinColumn(name = "user_id") 
    private User user; 

    @Column(name = "blood_sugar")
    private Double bloodSugar;
    
    @Column(name = "measure_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date measureTime;

 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user; 
    }

 

    public Date getMeasureTime() {
        return measureTime;
    }

    public void setMeasureTime(Date measureTime) {
        this.measureTime = measureTime;
    }

	public Double getBloodSugar() {
		return bloodSugar;
	}

	public void setBloodSugar(Double bloodSugar) {
		this.bloodSugar = bloodSugar;
	}
}
  

