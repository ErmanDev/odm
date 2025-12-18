package com.example.officerdutymanagement.model;

import com.google.gson.annotations.SerializedName;

public class DashboardStats {
    @SerializedName("lateCheckInCount")
    private Integer lateCheckInCount;
    
    @SerializedName("presentTodayCount")
    private Integer presentTodayCount;
    
    @SerializedName("absenceRequestCount")
    private Integer absenceRequestCount;

    public DashboardStats() {
    }

    public Integer getLateCheckInCount() {
        return lateCheckInCount;
    }

    public void setLateCheckInCount(Integer lateCheckInCount) {
        this.lateCheckInCount = lateCheckInCount;
    }

    public Integer getPresentTodayCount() {
        return presentTodayCount;
    }

    public void setPresentTodayCount(Integer presentTodayCount) {
        this.presentTodayCount = presentTodayCount;
    }

    public Integer getAbsenceRequestCount() {
        return absenceRequestCount;
    }

    public void setAbsenceRequestCount(Integer absenceRequestCount) {
        this.absenceRequestCount = absenceRequestCount;
    }
}

