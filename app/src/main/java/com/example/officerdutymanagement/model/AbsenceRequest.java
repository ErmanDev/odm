package com.example.officerdutymanagement.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class AbsenceRequest {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("officerId")
    private Integer officerId;
    
    @SerializedName("startDate")
    private Date startDate;
    
    @SerializedName("endDate")
    private Date endDate;
    
    @SerializedName("reason")
    private String reason;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("officer")
    private Officer officer;

    public AbsenceRequest() {
    }

    public AbsenceRequest(Date startDate, Date endDate, String reason) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
}

