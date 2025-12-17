package com.example.officerdutymanagement.model;

import com.google.gson.annotations.SerializedName;

public class DutyAssignment {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("date")
    private String date;
    
    @SerializedName("officerName")
    private String officerName;
    
    @SerializedName("department")
    private String department;
    
    @SerializedName("taskLocation")
    private String taskLocation;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("officerId")
    private Integer officerId;

    public DutyAssignment() {
    }

    public DutyAssignment(String date, String officerName, String department, String taskLocation, String status) {
        this.date = date;
        this.officerName = officerName;
        this.department = department;
        this.taskLocation = taskLocation;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTaskLocation() {
        return taskLocation;
    }

    public void setTaskLocation(String taskLocation) {
        this.taskLocation = taskLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getOfficerId() {
        return officerId;
    }

    public void setOfficerId(Integer officerId) {
        this.officerId = officerId;
    }
}

