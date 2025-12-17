package com.example.officerdutymanagement.model;

import com.google.gson.annotations.SerializedName;

public class Officer {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("department")
    private String department;
    
    @SerializedName("userId")
    private Integer userId;

    public Officer() {
    }

    public Officer(String name, String department) {
        this.name = name;
        this.department = department;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}

