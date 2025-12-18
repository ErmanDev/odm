package com.example.officerdutymanagement.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.officerdutymanagement.model.DutyAssignment;
import com.example.officerdutymanagement.model.ApiResponse;
import com.example.officerdutymanagement.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DutyAssignmentRepository {
    private static DutyAssignmentRepository instance;
    private final MutableLiveData<DutyAssignment> currentDutyAssignment = new MutableLiveData<>();
    private final MutableLiveData<List<DutyAssignment>> dutyAssignmentList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private DutyAssignmentRepository() {
    }

    public static synchronized DutyAssignmentRepository getInstance() {
        if (instance == null) {
            instance = new DutyAssignmentRepository();
        }
        return instance;
    }

    public MutableLiveData<DutyAssignment> getCurrentDutyAssignment() {
        return currentDutyAssignment;
    }

    public MutableLiveData<List<DutyAssignment>> getDutyAssignmentList() {
        return dutyAssignmentList;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void getAllDutyAssignments() {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().getDutyAssignments().enqueue(new Callback<ApiResponse<List<DutyAssignment>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<DutyAssignment>>> call, Response<ApiResponse<List<DutyAssignment>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    dutyAssignmentList.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to fetch duty assignments";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<DutyAssignment>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void getMyDutyAssignments() {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().getMyDutyAssignments().enqueue(new Callback<ApiResponse<List<DutyAssignment>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<DutyAssignment>>> call, Response<ApiResponse<List<DutyAssignment>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    dutyAssignmentList.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to fetch my duty assignments";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<DutyAssignment>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void createDutyAssignment(DutyAssignment dutyAssignment) {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().createDutyAssignment(dutyAssignment).enqueue(new Callback<ApiResponse<DutyAssignment>>() {
            @Override
            public void onResponse(Call<ApiResponse<DutyAssignment>> call, Response<ApiResponse<DutyAssignment>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentDutyAssignment.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to create duty assignment";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DutyAssignment>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void updateDutyAssignment(int id, DutyAssignment dutyAssignment) {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().updateDutyAssignment(id, dutyAssignment).enqueue(new Callback<ApiResponse<DutyAssignment>>() {
            @Override
            public void onResponse(Call<ApiResponse<DutyAssignment>> call, Response<ApiResponse<DutyAssignment>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentDutyAssignment.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to update duty assignment";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DutyAssignment>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void deleteDutyAssignment(int id) {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().deleteDutyAssignment(id).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentDutyAssignment.setValue(null);
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to delete duty assignment";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}

