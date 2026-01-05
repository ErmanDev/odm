package com.example.officerdutymanagement.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.officerdutymanagement.model.ApiResponse;
import com.example.officerdutymanagement.model.DashboardStats;
import com.example.officerdutymanagement.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardRepository {
    private static DashboardRepository instance;
    private final MutableLiveData<DashboardStats> dashboardStats = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private DashboardRepository() {
    }

    public static synchronized DashboardRepository getInstance() {
        if (instance == null) {
            instance = new DashboardRepository();
        }
        return instance;
    }

    public MutableLiveData<DashboardStats> getDashboardStats() {
        return dashboardStats;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadDashboardStats() {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().getDashboardStats().enqueue(new Callback<ApiResponse<DashboardStats>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardStats>> call, Response<ApiResponse<DashboardStats>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    dashboardStats.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to load dashboard stats";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DashboardStats>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void loadSupervisorDashboardStats() {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().getSupervisorDashboardStats().enqueue(new Callback<ApiResponse<DashboardStats>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardStats>> call, Response<ApiResponse<DashboardStats>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    DashboardStats stats = response.body().getData();
                    if (stats != null) {
                        dashboardStats.setValue(stats);
                        errorMessage.setValue(null);
                    } else {
                        // If stats is null, create default stats with zeros
                        DashboardStats defaultStats = new DashboardStats();
                        defaultStats.setLateCheckInCount(0);
                        defaultStats.setPresentTodayCount(0);
                        defaultStats.setAbsenceRequestCount(0);
                        defaultStats.setActiveDutyAssignmentsCount(0);
                        defaultStats.setTotalOfficersCount(0);
                        dashboardStats.setValue(defaultStats);
                        errorMessage.setValue(null);
                    }
                } else {
                    // Handle different error codes
                    if (response.code() == 403) {
                        String errorMsg = response.body() != null ? response.body().getMessage() : "Access denied. Supervisor must have a department assigned.";
                        errorMessage.setValue(errorMsg);
                    } else if (response.code() == 401) {
                        String errorMsg = response.body() != null ? response.body().getMessage() : "Authentication failed. Please login again.";
                        errorMessage.setValue(errorMsg);
                    } else {
                        String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to load dashboard stats (Error: " + response.code() + ")";
                        errorMessage.setValue(errorMsg);
                    }
                    // Set default stats on error
                    DashboardStats defaultStats = new DashboardStats();
                    defaultStats.setLateCheckInCount(0);
                    defaultStats.setPresentTodayCount(0);
                    defaultStats.setAbsenceRequestCount(0);
                    defaultStats.setActiveDutyAssignmentsCount(0);
                    defaultStats.setTotalOfficersCount(0);
                    dashboardStats.setValue(defaultStats);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DashboardStats>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
                // Set default stats on network error
                DashboardStats defaultStats = new DashboardStats();
                defaultStats.setLateCheckInCount(0);
                defaultStats.setPresentTodayCount(0);
                defaultStats.setAbsenceRequestCount(0);
                defaultStats.setActiveDutyAssignmentsCount(0);
                defaultStats.setTotalOfficersCount(0);
                dashboardStats.setValue(defaultStats);
            }
        });
    }
}

