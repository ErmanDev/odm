package com.example.officerdutymanagement.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("role")
    private String role;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

