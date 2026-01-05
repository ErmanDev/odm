package com.example.officerdutymanagement.model;

import com.google.gson.annotations.SerializedName;

public class Officer {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("fullName")
    private String fullName;
    
    @SerializedName("role")
    private String role;
    
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    // Helper method to get display name (prefer fullName, fallback to username, then name)
    public String getDisplayName() {
        if (fullName != null && !fullName.isEmpty()) {
            return fullName;
        }
        if (username != null && !username.isEmpty()) {
            return username;
        }
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return "Unknown";
    }
}

