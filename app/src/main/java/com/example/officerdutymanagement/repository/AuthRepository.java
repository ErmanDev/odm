package com.example.officerdutymanagement.repository;

import android.content.Context;
import android.util.Log;

import com.example.officerdutymanagement.model.ApiResponse;
import com.example.officerdutymanagement.model.LoginRequest;
import com.example.officerdutymanagement.model.LoginResponse;
import com.example.officerdutymanagement.model.User;
import com.example.officerdutymanagement.network.ApiService;
import com.example.officerdutymanagement.network.AuthInterceptor;
import com.example.officerdutymanagement.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private ApiService apiService;
    private Context context;

    public AuthRepository(Context context) {
        this.context = context;
        // Set context for AuthInterceptor
        RetrofitClient.getInstance().setContext(context);
        this.apiService = RetrofitClient.getInstance().getApiService();
    }

    public interface LoginCallback {
        void onSuccess(LoginResponse loginResponse);
        void onError(String errorMessage);
    }

    public interface RegisterCallback {
        void onSuccess(LoginResponse loginResponse);
        void onError(String errorMessage);
    }

    public interface GetMeCallback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }

    public void login(String username, String password, LoginCallback callback) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        Call<ApiResponse<LoginResponse>> call = apiService.login(loginRequest);

        call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        LoginResponse loginResponse = apiResponse.getData();
                        // Save token
                        if (loginResponse.getToken() != null) {
                            AuthInterceptor.saveToken(context, loginResponse.getToken());
                        }
                        callback.onSuccess(loginResponse);
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Login failed";
                        callback.onError(errorMsg);
                    }
                } else {
                    String errorMsg = "Login failed. Please check your credentials.";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                Log.e(TAG, "Login request failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void register(User user, RegisterCallback callback) {
        Call<ApiResponse<LoginResponse>> call = apiService.register(user);

        call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        LoginResponse loginResponse = apiResponse.getData();
                        // Save token
                        if (loginResponse.getToken() != null) {
                            AuthInterceptor.saveToken(context, loginResponse.getToken());
                        }
                        callback.onSuccess(loginResponse);
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Registration failed";
                        callback.onError(errorMsg);
                    }
                } else {
                    String errorMsg = "Registration failed";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                Log.e(TAG, "Register request failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getMe(GetMeCallback callback) {
        String token = AuthInterceptor.getToken(context);
        if (token == null) {
            callback.onError("No authentication token found");
            return;
        }

        Call<ApiResponse<User>> call = apiService.getMe();

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Failed to get user info");
                    }
                } else {
                    callback.onError("Failed to get user info");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e(TAG, "GetMe request failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void logout() {
        AuthInterceptor.clearToken(context);
    }
}

