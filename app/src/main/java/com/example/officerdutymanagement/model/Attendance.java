package com.example.officerdutymanagement.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Attendance {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("officerId")
    private Integer officerId;
    
    @SerializedName("clockIn")
    private Date clockIn;
    
    @SerializedName("clockOut")
    private Date clockOut;
    
    @SerializedName("date")
    private Date date;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("user")
    private User user;
    
    @SerializedName("officer")
    private Officer officer; // Keep for backward compatibility

    public Attendance() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOfficerId() {
        return officerId;
    }

    public void setOfficerId(Integer officerId) {
        this.officerId = officerId;
    }

    public Date getClockIn() {
        return clockIn;
    }

    public void setClockIn(Date clockIn) {
        this.clockIn = clockIn;
    }

    public Date getClockOut() {
        return clockOut;
    }

    public void setClockOut(Date clockOut) {
        this.clockOut = clockOut;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Officer getOfficer() {
        return officer;
    }

    public void setOfficer(Officer officer) {
        this.officer = officer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

