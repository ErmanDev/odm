package com.example.officerdutymanagement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.officerdutymanagement.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_ADMIN_NAME = "admin_name";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USER_DEPARTMENT = "user_department";

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private TextView textViewUsernameError;
    private TextView textViewPasswordError;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        initializeViewModel();
        setupObservers();
        setupListeners();
    }

    private void initializeViews() {
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progressBar);
        textViewUsernameError = findViewById(R.id.textViewUsernameError);
        textViewPasswordError = findViewById(R.id.textViewPasswordError);
    }

    private void initializeViewModel() {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private void setupObservers() {
        // Observe login state
        loginViewModel.getLoginState().observe(this, loginState -> {
            switch (loginState) {
                case LOADING:
                    showLoading(true);
                    buttonLogin.setEnabled(false);
                    break;
                case SUCCESS:
                    showLoading(false);
                    buttonLogin.setEnabled(true);
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    // Navigate to main activity
                    navigateToMain();
                    break;
                case ERROR:
                    showLoading(false);
                    buttonLogin.setEnabled(true);
                    String errorMsg = loginViewModel.getErrorMessage().getValue();
                    Toast.makeText(this, 
                        errorMsg != null ? errorMsg : "Login failed. Please try again.", 
                        Toast.LENGTH_SHORT).show();
                    break;
                case IDLE:
                default:
                    showLoading(false);
                    buttonLogin.setEnabled(true);
                    break;
            }
        });

        // Observe username error
        loginViewModel.getUsernameError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                textViewUsernameError.setVisibility(View.VISIBLE);
                textViewUsernameError.setText(error);
                editTextUsername.setError(error);
            } else {
                textViewUsernameError.setVisibility(View.GONE);
                editTextUsername.setError(null);
            }
        });

        // Observe password error
        loginViewModel.getPasswordError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                textViewPasswordError.setVisibility(View.VISIBLE);
                textViewPasswordError.setText(error);
                editTextPassword.setError(error);
            } else {
                textViewPasswordError.setVisibility(View.GONE);
                editTextPassword.setError(null);
            }
        });

        // Observe navigation
        loginViewModel.getNavigateToMain().observe(this, shouldNavigate -> {
            if (shouldNavigate != null && shouldNavigate) {
                // Get role from ViewModel and navigate accordingly
                String role = loginViewModel.getUserRole().getValue();
                navigateBasedOnRole(role);
                loginViewModel.resetNavigation();
            }
        });
    }

    private void setupListeners() {
        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            loginViewModel.login(username, password);
        });

        // Clear errors when user starts typing
        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textViewUsernameError.getVisibility() == View.VISIBLE) {
                    textViewUsernameError.setVisibility(View.GONE);
                    editTextUsername.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textViewPasswordError.getVisibility() == View.VISIBLE) {
                    textViewPasswordError.setVisibility(View.GONE);
                    editTextPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        buttonLogin.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void navigateToMain() {
        // This method is kept for backward compatibility but now uses navigateBasedOnRole
        String role = loginViewModel.getUserRole().getValue();
        navigateBasedOnRole(role);
    }

    private void navigateBasedOnRole(String role) {
        // Save user info to SharedPreferences
        String username = editTextUsername.getText().toString().trim();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ADMIN_NAME, username);
        if (role != null) {
            editor.putString(KEY_USER_ROLE, role);
            // Save supervisor name and department if role is supervisor
            if ("supervisor".equalsIgnoreCase(role)) {
                editor.putString("supervisor_name", username);
                // Get department from login response if available
                String department = loginViewModel.getUserDepartment();
                if (department != null) {
                    editor.putString(KEY_USER_DEPARTMENT, department);
                }
            }
        }
        editor.apply();
        
        // Navigate based on role
        android.content.Intent intent;
        if ("admin".equalsIgnoreCase(role) || "ADMIN".equals(role)) {
            // Navigate to Admin Dashboard (MainActivity)
            intent = new android.content.Intent(this, MainActivity.class);
        } else if ("supervisor".equalsIgnoreCase(role)) {
            // Navigate to Supervisor Dashboard (SupervisorActivity)
            intent = new android.content.Intent(this, SupervisorActivity.class);
        } else {
            // Navigate to Officer Dashboard (OfficerActivity)
            intent = new android.content.Intent(this, OfficerActivity.class);
        }
        startActivity(intent);
        finish();
    }
}

