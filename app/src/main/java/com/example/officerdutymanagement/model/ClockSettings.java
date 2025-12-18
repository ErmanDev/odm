package com.example.officerdutymanagement.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ClockSettings {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("clockInStartTime")
    private String clockInStartTime;
    
    @SerializedName("clockOutStartTime")
    private String clockOutStartTime;
    
    @SerializedName("isActive")
    private Boolean isActive;
    
    @SerializedName("createdAt")
    private Date createdAt;
    
    @SerializedName("updatedAt")
    private Date updatedAt;

    public ClockSettings() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClockInStartTime() {
        return clockInStartTime;
    }

    public void setClockInStartTime(String clockInStartTime) {
        this.clockInStartTime = clockInStartTime;
    }

    public String getClockOutStartTime() {
        return clockOutStartTime;
    }

    public void setClockOutStartTime(String clockOutStartTime) {
        this.clockOutStartTime = clockOutStartTime;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}

