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
}

