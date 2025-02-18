package com.health.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WaterIntakeDTO {
	  private String username;
	
	 
	
	private long userId;
    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getUserid() {
		return userId;
	}

	public void setUserid(long userid) {
		this.userId = userid;
	}

	private Integer amount;
    private Integer currentAmount;
    private Integer target;
    
    public Integer getAmount() {
        return amount;
    }
    
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    
    public Integer getCurrentAmount() {
        return currentAmount;
    }
    
    public void setCurrentAmount(Integer currentAmount) {
        this.currentAmount = currentAmount;
    }
    
    public Integer getTarget() {
        return target;
    }
    
    public void setTarget(Integer target) {
        this.target = target;
    }

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}


	}

