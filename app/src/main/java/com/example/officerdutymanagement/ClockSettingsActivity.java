package com.example.officerdutymanagement;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;

import com.example.officerdutymanagement.model.ClockSettings;
import com.example.officerdutymanagement.repository.ClockSettingsRepository;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.Locale;

public class ClockSettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_ADMIN_NAME = "admin_name";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageViewMenu;
    private TextView textViewClockInStartTime;
    private TextView textViewClockOutStartTime;
    private SwitchCompat switchActive;
    private Button buttonSave;

    private ClockSettingsRepository clockSettingsRepository;
    private ClockSettings currentSettings;
    private int clockInStartHour = 8;
    private int clockInStartMinute = 0;
    private int clockOutStartHour = 17;
    private int clockOutStartMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clock_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        initializeRepository();
        setupDrawer();
        setupClickListeners();
        loadClockSettings();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        imageViewMenu = findViewById(R.id.imageViewMenu);
        textViewClockInStartTime = findViewById(R.id.textViewClockInStartTime);
        textViewClockOutStartTime = findViewById(R.id.textViewClockOutStartTime);
        switchActive = findViewById(R.id.switchActive);
        buttonSave = findViewById(R.id.buttonSave);
    }

    private void initializeRepository() {
        clockSettingsRepository = ClockSettingsRepository.getInstance();

        // Observe clock settings
        clockSettingsRepository.getCurrentClockSettings().observe(this, settings -> {
            if (settings != null) {
                currentSettings = settings;
                loadSettingsIntoUI(settings);
            }
        });

        // Observe error messages
        clockSettingsRepository.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe loading state
        clockSettingsRepository.getIsLoading().observe(this, isLoading -> {
            buttonSave.setEnabled(!isLoading);
            buttonSave.setText(isLoading ? "Saving..." : "Save Settings");
        });
    }

    private void setupDrawer() {
        // Setup drawer header with user info
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            TextView navHeaderGreeting = headerView.findViewById(R.id.navHeaderGreeting);
            TextView navHeaderName = headerView.findViewById(R.id.navHeaderName);
            
            if (navHeaderGreeting != null) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
                String greeting;
                if (hour < 12) {
                    greeting = getString(R.string.good_morning);
                } else if (hour < 17) {
                    greeting = getString(R.string.good_afternoon);
                } else {
                    greeting = getString(R.string.good_evening);
                }
                navHeaderGreeting.setText(greeting);
            }
            
            if (navHeaderName != null) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                String userName = prefs.getString(KEY_ADMIN_NAME, "Admin");
                navHeaderName.setText(userName);
            }
        }

        imageViewMenu.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            drawerLayout.closeDrawer(navigationView);

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else if (itemId == R.id.nav_officer_list) {
                startActivity(new Intent(this, OfficerListManagementActivity.class));
                finish();
            } else if (itemId == R.id.nav_edit_profile) {
                startActivity(new Intent(this, EditEmployeeProfileActivity.class));
                finish();
            } else if (itemId == R.id.nav_duty_assignment) {
                startActivity(new Intent(this, DutyAssignmentActivity.class));
                finish();
            } else if (itemId == R.id.nav_pending_activities) {
                startActivity(new Intent(this, PendingActivitiesActivity.class));
                finish();
            } else if (itemId == R.id.nav_attendance_tracking) {
                startActivity(new Intent(this, AttendanceTrackingActivity.class));
                finish();
            } else if (itemId == R.id.nav_notifications) {
                startActivity(new Intent(this, AdminNotificationsActivity.class));
                finish();
            } else if (itemId == R.id.nav_logout) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                prefs.edit().clear().apply();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            return true;
        });
    }

    private void setupClickListeners() {
        textViewClockInStartTime.setOnClickListener(v -> showTimePicker(true, true));
        textViewClockOutStartTime.setOnClickListener(v -> showTimePicker(false, true));

        buttonSave.setOnClickListener(v -> saveClockSettings());
    }

    private void showTimePicker(boolean isClockIn, boolean isStart) {
        int hour, minute;
        if (isClockIn) {
            hour = clockInStartHour;
            minute = clockInStartMinute;
        } else {
            hour = clockOutStartHour;
            minute = clockOutStartMinute;
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, selectedHour, selectedMinute) -> {
                if (isClockIn) {
                    clockInStartHour = selectedHour;
                    clockInStartMinute = selectedMinute;
                    textViewClockInStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute));
                } else {
                    clockOutStartHour = selectedHour;
                    clockOutStartMinute = selectedMinute;
                    textViewClockOutStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute));
                }
            },
            hour,
            minute,
            true
        );
        timePickerDialog.show();
    }

    private void loadClockSettings() {
        clockSettingsRepository.getClockSettings();
    }

    private void loadSettingsIntoUI(ClockSettings settings) {
        if (settings == null) return;

        // Parse time strings (format: HH:MM:SS or HH:MM)
        String clockInStart = settings.getClockInStartTime();
        String clockOutStart = settings.getClockOutStartTime();

        if (clockInStart != null && !clockInStart.isEmpty()) {
            String[] parts = clockInStart.split(":");
            if (parts.length >= 2) {
                clockInStartHour = Integer.parseInt(parts[0]);
                clockInStartMinute = Integer.parseInt(parts[1]);
                textViewClockInStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", clockInStartHour, clockInStartMinute));
            }
        }

        if (clockOutStart != null && !clockOutStart.isEmpty()) {
            String[] parts = clockOutStart.split(":");
            if (parts.length >= 2) {
                clockOutStartHour = Integer.parseInt(parts[0]);
                clockOutStartMinute = Integer.parseInt(parts[1]);
                textViewClockOutStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", clockOutStartHour, clockOutStartMinute));
            }
        }

        if (settings.getIsActive() != null) {
            switchActive.setChecked(settings.getIsActive());
        }
    }

    private void saveClockSettings() {
        ClockSettings settings = new ClockSettings();
        
        if (currentSettings != null && currentSettings.getId() != null) {
            settings.setId(currentSettings.getId());
        }

        settings.setClockInStartTime(String.format(Locale.getDefault(), "%02d:%02d:00", clockInStartHour, clockInStartMinute));
        settings.setClockOutStartTime(String.format(Locale.getDefault(), "%02d:%02d:00", clockOutStartHour, clockOutStartMinute));
        settings.setIsActive(switchActive.isChecked());

        if (currentSettings == null || currentSettings.getId() == null) {
            clockSettingsRepository.createClockSettings(settings);
        } else {
            clockSettingsRepository.updateClockSettings(settings);
        }

        // Observe success
        clockSettingsRepository.getCurrentClockSettings().observe(this, updatedSettings -> {
            if (updatedSettings != null && updatedSettings.getId() != null) {
                Toast.makeText(this, "Clock settings saved successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

