package com.example.officerdutymanagement.model;

public class PendingActivity {
    private String officerName;
    private String department;
    private String activity;
    private String status;

    public PendingActivity() {
    }

    public PendingActivity(String officerName, String department, String activity, String status) {
        this.officerName = officerName;
        this.department = department;
        this.activity = activity;
        this.status = status;
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

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

