package com.example.officerdutymanagement.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.officerdutymanagement.model.ApiResponse;
import com.example.officerdutymanagement.model.Officer;
import com.example.officerdutymanagement.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfficerRepository {
    private static OfficerRepository instance;
    private final MutableLiveData<List<Officer>> officerList = new MutableLiveData<>();
    private final MutableLiveData<Officer> currentOfficer = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private OfficerRepository() {
    }

    public static synchronized OfficerRepository getInstance() {
        if (instance == null) {
            instance = new OfficerRepository();
        }
        return instance;
    }

    public MutableLiveData<List<Officer>> getOfficerList() {
        return officerList;
    }

    public MutableLiveData<Officer> getCurrentOfficer() {
        return currentOfficer;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void getOfficers() {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().getOfficers().enqueue(new Callback<ApiResponse<List<Officer>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Officer>>> call, Response<ApiResponse<List<Officer>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    officerList.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to fetch officers";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Officer>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void getOfficerById(int id) {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().getOfficer(id).enqueue(new Callback<ApiResponse<Officer>>() {
            @Override
            public void onResponse(Call<ApiResponse<Officer>> call, Response<ApiResponse<Officer>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentOfficer.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to fetch officer";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Officer>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void createOfficer(Officer officer) {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().createOfficer(officer).enqueue(new Callback<ApiResponse<Officer>>() {
            @Override
            public void onResponse(Call<ApiResponse<Officer>> call, Response<ApiResponse<Officer>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentOfficer.setValue(response.body().getData());
                    // Refresh the list after creating
                    getOfficers();
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to create officer";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Officer>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void updateOfficer(int id, Officer officer) {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().updateOfficer(id, officer).enqueue(new Callback<ApiResponse<Officer>>() {
            @Override
            public void onResponse(Call<ApiResponse<Officer>> call, Response<ApiResponse<Officer>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentOfficer.setValue(response.body().getData());
                    // Refresh the list after updating
                    getOfficers();
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to update officer";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Officer>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void deleteOfficer(int id) {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().deleteOfficer(id).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Refresh the list after deleting
                    getOfficers();
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to delete officer";
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

