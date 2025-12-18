package com.example.officerdutymanagement.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.officerdutymanagement.model.ApiResponse;
import com.example.officerdutymanagement.model.Attendance;
import com.example.officerdutymanagement.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceRepository {
    private static AttendanceRepository instance;
    private final MutableLiveData<Attendance> currentAttendance = new MutableLiveData<>();
    private final MutableLiveData<List<Attendance>> attendanceList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private AttendanceRepository() {
    }

    public static synchronized AttendanceRepository getInstance() {
        if (instance == null) {
            instance = new AttendanceRepository();
        }
        return instance;
    }

    public MutableLiveData<Attendance> getCurrentAttendance() {
        return currentAttendance;
    }

    public MutableLiveData<List<Attendance>> getAttendanceList() {
        return attendanceList;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void checkIn() {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().checkIn().enqueue(new Callback<ApiResponse<Attendance>>() {
            @Override
            public void onResponse(Call<ApiResponse<Attendance>> call, Response<ApiResponse<Attendance>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentAttendance.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Check-in failed";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Attendance>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void checkOut() {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().checkOut().enqueue(new Callback<ApiResponse<Attendance>>() {
            @Override
            public void onResponse(Call<ApiResponse<Attendance>> call, Response<ApiResponse<Attendance>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentAttendance.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Check-out failed";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Attendance>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void getMyAttendance(String startDate, String endDate) {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().getMyAttendance(startDate, endDate).enqueue(new Callback<ApiResponse<List<Attendance>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Attendance>>> call, Response<ApiResponse<List<Attendance>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    attendanceList.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    // If response is 404 or empty list, that's okay - just means no attendance records
                    if (response.code() == 404 || (response.body() != null && response.body().getData() != null && response.body().getData().isEmpty())) {
                        attendanceList.setValue(new java.util.ArrayList<>());
                        errorMessage.setValue(null);
                    } else {
                        String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to fetch attendance";
                        errorMessage.setValue(errorMsg);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Attendance>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void getAllAttendance(String startDate, String endDate, Integer officerId, String status) {
        isLoading.setValue(true);
        RetrofitClient.getInstance().getApiService().getAllAttendance(startDate, endDate, officerId, status).enqueue(new Callback<ApiResponse<List<Attendance>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Attendance>>> call, Response<ApiResponse<List<Attendance>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    attendanceList.setValue(response.body().getData());
                    errorMessage.setValue(null);
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to fetch attendance";
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Attendance>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}

