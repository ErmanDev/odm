package com.example.officerdutymanagement.model;

public class DutySchedule {
    private String date;
    private String duty;

    public DutySchedule() {
    }

    public DutySchedule(String date, String duty) {
        this.date = date;
        this.duty = duty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }
}

