package com.example.officerdutymanagement.model;

import com.google.gson.annotations.SerializedName;

public class ClockAvailability {
    @SerializedName("canClockIn")
    private Boolean canClockIn;
    
    @SerializedName("canClockOut")
    private Boolean canClockOut;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("clockSettings")
    private ClockSettings clockSettings;

    public ClockAvailability() {
    }

    public Boolean getCanClockIn() {
        return canClockIn;
    }

    public void setCanClockIn(Boolean canClockIn) {
        this.canClockIn = canClockIn;
    }

    public Boolean getCanClockOut() {
        return canClockOut;
    }

    public void setCanClockOut(Boolean canClockOut) {
        this.canClockOut = canClockOut;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ClockSettings getClockSettings() {
        return clockSettings;
    }

    public void setClockSettings(ClockSettings clockSettings) {
        this.clockSettings = clockSettings;
    }
}

