package com.example.officerdutymanagement.network;

import com.example.officerdutymanagement.model.AbsenceRequest;
import com.example.officerdutymanagement.model.ApiResponse;
import com.example.officerdutymanagement.model.Attendance;
import com.example.officerdutymanagement.model.ClockAvailability;
import com.example.officerdutymanagement.model.ClockSettings;
import com.example.officerdutymanagement.model.DashboardStats;
import com.example.officerdutymanagement.model.DutyAssignment;
import com.example.officerdutymanagement.model.LoginRequest;
import com.example.officerdutymanagement.model.LoginResponse;
import com.example.officerdutymanagement.model.Officer;
import com.example.officerdutymanagement.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    
    @GET("duty-assignments/me")
    Call<ApiResponse<List<DutyAssignment>>> getMyDutyAssignments();
    
    @GET("duty-assignments/{id}")
    Call<ApiResponse<DutyAssignment>> getDutyAssignment(@Path("id") int id);
    
    @POST("duty-assignments")
    Call<ApiResponse<DutyAssignment>> createDutyAssignment(@Body DutyAssignment dutyAssignment);
    
    @PUT("duty-assignments/{id}")
    Call<ApiResponse<DutyAssignment>> updateDutyAssignment(@Path("id") int id, @Body DutyAssignment dutyAssignment);
    
    @DELETE("duty-assignments/{id}")
    Call<ApiResponse<Void>> deleteDutyAssignment(@Path("id") int id);
    
    // Attendance endpoints
    @POST("attendance/checkin")
    Call<ApiResponse<Attendance>> checkIn();
    
    @POST("attendance/checkout")
    Call<ApiResponse<Attendance>> checkOut();
    
    @GET("attendance/me")
    Call<ApiResponse<List<Attendance>>> getMyAttendance(@Query("startDate") String startDate, @Query("endDate") String endDate);
    
    @GET("attendance")
    Call<ApiResponse<List<Attendance>>> getAllAttendance(@Query("startDate") String startDate, @Query("endDate") String endDate, @Query("officerId") Integer officerId, @Query("status") String status);
    
    // Dashboard endpoints
    @GET("dashboard/stats")
    Call<ApiResponse<DashboardStats>> getDashboardStats();
    
    @GET("dashboard/stats/supervisor")
    Call<ApiResponse<DashboardStats>> getSupervisorDashboardStats();
    
    // Absence Request endpoints
    @POST("absence-requests")
    Call<ApiResponse<AbsenceRequest>> createAbsenceRequest(@Body AbsenceRequest absenceRequest);
    
    @GET("absence-requests/me")
    Call<ApiResponse<List<AbsenceRequest>>> getMyAbsenceRequests();
    
    @GET("absence-requests")
    Call<ApiResponse<List<AbsenceRequest>>> getAllAbsenceRequests(@Query("status") String status);
    
    @PUT("absence-requests/{id}/status")
    Call<ApiResponse<AbsenceRequest>> updateAbsenceRequestStatus(@Path("id") int id, @Body Map<String, String> status);
    
    // Clock Settings endpoints
    @GET("clock-settings")
    Call<ApiResponse<ClockSettings>> getClockSettings();
    
    @POST("clock-settings")
    Call<ApiResponse<ClockSettings>> createClockSettings(@Body ClockSettings clockSettings);
    
    @PUT("clock-settings")
    Call<ApiResponse<ClockSettings>> updateClockSettings(@Body ClockSettings clockSettings);
    
    @GET("clock-settings/availability")
    Call<ApiResponse<ClockAvailability>> getClockAvailability();
}

