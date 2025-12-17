package com.example.officerdutymanagement.model;

public class OngoingActivity {
    private String date;
    private String task;
    private String location;
    private String status;
    private String action;

    public OngoingActivity() {
    }

    public OngoingActivity(String date, String task, String location, String status, String action) {
        this.date = date;
        this.task = task;
        this.location = location;
        this.status = status;
        this.action = action;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

