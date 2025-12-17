package com.example.officerdutymanagement.network;

import com.example.officerdutymanagement.model.ApiResponse;
import com.example.officerdutymanagement.model.DutyAssignment;
import com.example.officerdutymanagement.model.LoginRequest;
import com.example.officerdutymanagement.model.LoginResponse;
import com.example.officerdutymanagement.model.Officer;
import com.example.officerdutymanagement.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    
    // Authentication endpoints
    @POST("auth/register")
    Call<ApiResponse<LoginResponse>> register(@Body User user);
    
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest loginRequest);
    
    @GET("auth/me")
    Call<ApiResponse<User>> getMe();
    
    // Officer endpoints
    @GET("officers")
    Call<ApiResponse<List<Officer>>> getOfficers();
    
    @GET("officers/{id}")
    Call<ApiResponse<Officer>> getOfficer(@Path("id") int id);
    
    @POST("officers")
    Call<ApiResponse<Officer>> createOfficer(@Body Officer officer);
    
    @PUT("officers/{id}")
    Call<ApiResponse<Officer>> updateOfficer(@Path("id") int id, @Body Officer officer);
    
    @DELETE("officers/{id}")
    Call<ApiResponse<Void>> deleteOfficer(@Path("id") int id);
    
    // Duty Assignment endpoints
    @GET("duty-assignments")
    Call<ApiResponse<List<DutyAssignment>>> getDutyAssignments();
    
    @GET("duty-assignments/{id}")
    Call<ApiResponse<DutyAssignment>> getDutyAssignment(@Path("id") int id);
    
    @POST("duty-assignments")
    Call<ApiResponse<DutyAssignment>> createDutyAssignment(@Body DutyAssignment dutyAssignment);
    
    @PUT("duty-assignments/{id}")
    Call<ApiResponse<DutyAssignment>> updateDutyAssignment(@Path("id") int id, @Body DutyAssignment dutyAssignment);
    
    @DELETE("duty-assignments/{id}")
    Call<ApiResponse<Void>> deleteDutyAssignment(@Path("id") int id);
}

