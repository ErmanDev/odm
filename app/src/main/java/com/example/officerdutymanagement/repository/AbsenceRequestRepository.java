package com.example.officerdutymanagement.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.officerdutymanagement.model.AbsenceRequest;
import com.example.officerdutymanagement.model.ApiResponse;
import com.example.officerdutymanagement.network.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AbsenceRequestRepository {
    private static AbsenceRequestRepository instance;
    private final MutableLiveData<AbsenceRequest> currentAbsenceRequest = new MutableLiveData<>();
    private final MutableLiveData<List<AbsenceRequest>> absenceRequestList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private AbsenceRequestRepository() {
    }

    public static synchronized AbsenceRequestRepository getInstance() {
        if (instance == null) {
            instance = new AbsenceRequestRepository();
        }
        return instance;
    }

    public MutableLiveData<AbsenceRequest> getCurrentAbsenceRequest() {
        return currentAbsenceRequest;
    }

    public MutableLiveData<List<AbsenceRequest>> getAbsenceRequestList() {
        return absenceRequestList;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void createAbsenceRequest(AbsenceRequest absenceRequest) {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().createAbsenceRequest(absenceRequest).enqueue(new Callback<ApiResponse<AbsenceRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<AbsenceRequest>> call, Response<ApiResponse<AbsenceRequest>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentAbsenceRequest.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to create absence request";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AbsenceRequest>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void getMyAbsenceRequests() {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().getMyAbsenceRequests().enqueue(new Callback<ApiResponse<List<AbsenceRequest>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<AbsenceRequest>>> call, Response<ApiResponse<List<AbsenceRequest>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    absenceRequestList.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    if (response.code() == 404 || (response.body() != null && response.body().getData() != null && response.body().getData().isEmpty())) {
                        absenceRequestList.setValue(new java.util.ArrayList<>());
                        errorMessage.setValue(null);
                    } else {
                        String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to fetch absence requests";
                        errorMessage.setValue(errorMsg);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<AbsenceRequest>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void getAllAbsenceRequests(String status) {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().getAllAbsenceRequests(status).enqueue(new Callback<ApiResponse<List<AbsenceRequest>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<AbsenceRequest>>> call, Response<ApiResponse<List<AbsenceRequest>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<AbsenceRequest> requests = response.body().getData();
                    if (requests != null && !requests.isEmpty()) {
                        absenceRequestList.setValue(requests);
                    } else {
                        absenceRequestList.setValue(new java.util.ArrayList<>());
                    }
                    errorMessage.setValue(null);
                } else {
                    // Handle different error codes
                    if (response.code() == 403) {
                        String errorMsg = response.body() != null ? response.body().getMessage() : "Access denied. You don't have permission to view absence requests.";
                        errorMessage.setValue(errorMsg);
                        absenceRequestList.setValue(new java.util.ArrayList<>());
                    } else if (response.code() == 401) {
                        String errorMsg = response.body() != null ? response.body().getMessage() : "Authentication failed. Please login again.";
                        errorMessage.setValue(errorMsg);
                        absenceRequestList.setValue(new java.util.ArrayList<>());
                    } else if (response.code() == 404 || (response.body() != null && response.body().getData() != null && response.body().getData().isEmpty())) {
                        absenceRequestList.setValue(new java.util.ArrayList<>());
                        errorMessage.setValue(null);
                    } else {
                        String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to fetch absence requests (Error: " + response.code() + ")";
                        errorMessage.setValue(errorMsg);
                        absenceRequestList.setValue(new java.util.ArrayList<>());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<AbsenceRequest>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
                absenceRequestList.setValue(new java.util.ArrayList<>());
            }
        });
    }

    public void updateAbsenceRequestStatus(int id, String status) {
        isLoading.setValue(true);
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("status", status);
        
        RetrofitClient.getInstance().getApiService().updateAbsenceRequestStatus(id, statusMap).enqueue(new Callback<ApiResponse<AbsenceRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<AbsenceRequest>> call, Response<ApiResponse<AbsenceRequest>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentAbsenceRequest.setValue(response.body().getData());
                    errorMessage.setValue(null);
                    // Refresh the list after status update
                    getAllAbsenceRequests(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to update absence request status";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AbsenceRequest>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}

