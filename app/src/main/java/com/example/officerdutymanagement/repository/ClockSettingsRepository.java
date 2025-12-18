package com.example.officerdutymanagement.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.officerdutymanagement.model.ApiResponse;
import com.example.officerdutymanagement.model.ClockAvailability;
import com.example.officerdutymanagement.model.ClockSettings;
import com.example.officerdutymanagement.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClockSettingsRepository {
    private static ClockSettingsRepository instance;
    private final MutableLiveData<ClockSettings> currentClockSettings = new MutableLiveData<>();
    private final MutableLiveData<ClockAvailability> clockAvailability = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private ClockSettingsRepository() {
    }

    public static synchronized ClockSettingsRepository getInstance() {
        if (instance == null) {
            instance = new ClockSettingsRepository();
        }
        return instance;
    }

    public MutableLiveData<ClockSettings> getCurrentClockSettings() {
        return currentClockSettings;
    }

    public MutableLiveData<ClockAvailability> getClockAvailability() {
        return clockAvailability;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void getClockSettings() {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().getClockSettings().enqueue(new Callback<ApiResponse<ClockSettings>>() {
            @Override
            public void onResponse(Call<ApiResponse<ClockSettings>> call, Response<ApiResponse<ClockSettings>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentClockSettings.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to fetch clock settings";
                    errorMessage.setValue(errorMsg);
                    currentClockSettings.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ClockSettings>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void createClockSettings(ClockSettings clockSettings) {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().createClockSettings(clockSettings).enqueue(new Callback<ApiResponse<ClockSettings>>() {
            @Override
            public void onResponse(Call<ApiResponse<ClockSettings>> call, Response<ApiResponse<ClockSettings>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentClockSettings.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to create clock settings";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ClockSettings>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void updateClockSettings(ClockSettings clockSettings) {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().updateClockSettings(clockSettings).enqueue(new Callback<ApiResponse<ClockSettings>>() {
            @Override
            public void onResponse(Call<ApiResponse<ClockSettings>> call, Response<ApiResponse<ClockSettings>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentClockSettings.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to update clock settings";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ClockSettings>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void fetchClockAvailability() {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().getClockAvailability().enqueue(new Callback<ApiResponse<ClockAvailability>>() {
            @Override
            public void onResponse(Call<ApiResponse<ClockAvailability>> call, Response<ApiResponse<ClockAvailability>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    clockAvailability.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to fetch clock availability";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ClockAvailability>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}

