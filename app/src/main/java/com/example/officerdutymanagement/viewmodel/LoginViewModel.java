package com.example.officerdutymanagement.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.officerdutymanagement.model.LoginResponse;
import com.example.officerdutymanagement.network.RetrofitClient;
import com.example.officerdutymanagement.repository.AuthRepository;

public class LoginViewModel extends AndroidViewModel {
    
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MIN_PASSWORD_LENGTH = 6;

    private MutableLiveData<LoginState> loginState = new MutableLiveData<>();
    private MutableLiveData<String> usernameError = new MutableLiveData<>();
    private MutableLiveData<String> passwordError = new MutableLiveData<>();
    private MutableLiveData<Boolean> navigateToMain = new MutableLiveData<>();
    private MutableLiveData<String> userRole = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private AuthRepository authRepository;

    public LoginViewModel(Application application) {
        super(application);
        loginState.setValue(LoginState.IDLE);
        usernameError.setValue(null);
        passwordError.setValue(null);
        navigateToMain.setValue(false);
        userRole.setValue(null);
        errorMessage.setValue(null);
        
        // Initialize RetrofitClient with context
        RetrofitClient.getInstance().setContext(application);
        authRepository = new AuthRepository(application);
    }

    public LiveData<LoginState> getLoginState() {
        return loginState;
    }

    public LiveData<String> getUsernameError() {
        return usernameError;
    }

    public LiveData<String> getPasswordError() {
        return passwordError;
    }

    public LiveData<Boolean> getNavigateToMain() {
        return navigateToMain;
    }

    public LiveData<String> getUserRole() {
        return userRole;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void login(String username, String password) {
        // Reset errors
        usernameError.setValue(null);
        passwordError.setValue(null);
        errorMessage.setValue(null);

        // Validate username
        if (username == null || username.trim().isEmpty()) {
            usernameError.setValue("Username is required");
            return;
        }

        if (username.trim().length() < MIN_USERNAME_LENGTH) {
            usernameError.setValue("Username must be at least " + MIN_USERNAME_LENGTH + " characters");
            return;
        }

        // Validate password
        if (password == null || password.isEmpty()) {
            passwordError.setValue("Password is required");
            return;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            passwordError.setValue("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
            return;
        }

        // If validation passes, proceed with login
        performLogin(username.trim(), password);
    }

    private void performLogin(String username, String password) {
        loginState.setValue(LoginState.LOADING);

        authRepository.login(username, password, new AuthRepository.LoginCallback() {
            @Override
            public void onSuccess(LoginResponse loginResponse) {
                // Determine user role from response
                String role = loginResponse.getRole() != null 
                    ? loginResponse.getRole().toUpperCase() 
                    : "OFFICER";
                userRole.setValue(role);
                loginState.setValue(LoginState.SUCCESS);
                navigateToMain.setValue(true);
            }

            @Override
            public void onError(String errorMessage) {
                LoginViewModel.this.errorMessage.setValue(errorMessage);
                loginState.setValue(LoginState.ERROR);
            }
        });
    }

    public void resetNavigation() {
        navigateToMain.setValue(false);
    }

    public enum LoginState {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }
}

