package com.example.officerdutymanagement.model;

import com.google.gson.annotations.SerializedName;

public class DashboardStats {
    @SerializedName("lateCheckInCount")
    private Integer lateCheckInCount;
    
    @SerializedName("presentTodayCount")
    private Integer presentTodayCount;
    
    @SerializedName("absenceRequestCount")
    private Integer absenceRequestCount;
    
    @SerializedName("activeDutyAssignmentsCount")
    private Integer activeDutyAssignmentsCount;
    
    @SerializedName("totalOfficersCount")
    private Integer totalOfficersCount;

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

    public Integer getActiveDutyAssignmentsCount() {
        return activeDutyAssignmentsCount;
    }

    public void setActiveDutyAssignmentsCount(Integer activeDutyAssignmentsCount) {
        this.activeDutyAssignmentsCount = activeDutyAssignmentsCount;
    }

    public Integer getTotalOfficersCount() {
        return totalOfficersCount;
    }

    public void setTotalOfficersCount(Integer totalOfficersCount) {
        this.totalOfficersCount = totalOfficersCount;
    }
}

