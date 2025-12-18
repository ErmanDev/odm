package com.example.officerdutymanagement;

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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;

import com.example.officerdutymanagement.model.Attendance;
import com.example.officerdutymanagement.model.ClockAvailability;
import com.example.officerdutymanagement.model.ClockSettings;
import com.example.officerdutymanagement.repository.AttendanceRepository;
import com.example.officerdutymanagement.repository.ClockSettingsRepository;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ClockInOutActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_ADMIN_NAME = "admin_name";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageViewMenu;
    private TextView textViewCurrentTime;
    private TextView textViewCurrentDate;
    private TextView textViewStatus;
    private TextView textViewAvailability;
    private Button buttonClockAction;
    private Button buttonBack;

    private AttendanceRepository attendanceRepository;
    private ClockSettingsRepository clockSettingsRepository;
    private boolean isClockedIn = false;
    private boolean canClockIn = false;
    private boolean canClockOut = false;
    private android.os.Handler timeUpdateHandler;
    private Runnable timeUpdateRunnable;
    private int lastCheckedMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clock_in_out);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        initializeRepository();
        loadCurrentAttendance();
        checkClockAvailability();
        updateUI();
        setupDrawer();
        setupClickListeners();
        startTimeUpdate();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        imageViewMenu = findViewById(R.id.imageViewMenu);
        textViewCurrentTime = findViewById(R.id.textViewCurrentTime);
        textViewCurrentDate = findViewById(R.id.textViewCurrentDate);
        textViewStatus = findViewById(R.id.textViewStatus);
        textViewAvailability = findViewById(R.id.textViewAvailability);
        buttonClockAction = findViewById(R.id.buttonClockAction);
        buttonBack = findViewById(R.id.buttonBack);
    }

    private void initializeRepository() {
        attendanceRepository = AttendanceRepository.getInstance();
        clockSettingsRepository = ClockSettingsRepository.getInstance();
        
        // Observe current attendance
        attendanceRepository.getCurrentAttendance().observe(this, attendance -> {
            if (attendance != null) {
                isClockedIn = "clocked-in".equals(attendance.getStatus());
                updateUI();
            }
        });
        
        // Observe error messages
        attendanceRepository.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                // Only show real errors, not empty list messages
                // Empty attendance list is normal if officer hasn't clocked in today
                if (!errorMessage.equals("Failed to fetch attendance")) {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Observe clock availability
        clockSettingsRepository.getClockAvailability().observe(this, availability -> {
            if (availability != null) {
                canClockIn = Boolean.TRUE.equals(availability.getCanClockIn());
                canClockOut = Boolean.TRUE.equals(availability.getCanClockOut());
                updateAvailabilityUI(availability);
            }
        });

        // Observe clock settings error messages
        clockSettingsRepository.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty() && !errorMessage.contains("404")) {
                // Don't show 404 errors as they just mean settings aren't configured yet
                textViewAvailability.setText("Clock settings not configured");
            }
        });
    }

    private void loadCurrentAttendance() {
        // Fetch today's attendance to determine current state
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = dateFormat.format(calendar.getTime());
        
        attendanceRepository.getMyAttendance(today, today);
        
        // Also observe the attendance list to check for today's record
        attendanceRepository.getAttendanceList().observe(this, attendanceList -> {
            if (attendanceList != null && !attendanceList.isEmpty()) {
                // Find today's attendance record
                for (Attendance attendance : attendanceList) {
                    if (attendance.getDate() != null) {
                        String attendanceDate = dateFormat.format(attendance.getDate());
                        if (today.equals(attendanceDate) && "clocked-in".equals(attendance.getStatus())) {
                            isClockedIn = true;
                            updateUI();
                            break;
                        }
                    }
                }
            }
        });
    }

    private void checkClockAvailability() {
        clockSettingsRepository.fetchClockAvailability();
    }

    private void updateAvailabilityUI(ClockAvailability availability) {
        if (availability == null) {
            textViewAvailability.setText("Checking availability...");
            return;
        }

        ClockSettings settings = availability.getClockSettings();

        // Before clock-in: only talk about clock-in
        if (!isClockedIn) {
            if (Boolean.TRUE.equals(availability.getCanClockIn())) {
                textViewAvailability.setText("You can clock in now");
            } else if (settings != null) {
                textViewAvailability.setText(String.format("Clock-in starts at %s | Clock-out starts at %s",
                    settings.getClockInStartTime(), settings.getClockOutStartTime()));
            } else {
                textViewAvailability.setText("Clock settings not configured");
            }
        } else {
            // After clock-in: only talk about clock-out
            if (Boolean.TRUE.equals(availability.getCanClockOut())) {
                textViewAvailability.setText("You can clock out now");
            } else if (settings != null) {
                textViewAvailability.setText(String.format("Clock-out starts at %s",
                    settings.getClockOutStartTime()));
            } else {
                textViewAvailability.setText("Clock settings not configured");
            }
        }

        updateButtonState();
    }

    private void updateUI() {
        if (isClockedIn) {
            buttonClockAction.setText("Clock Out");
            Attendance currentAttendance = attendanceRepository.getCurrentAttendance().getValue();
            if (currentAttendance != null && currentAttendance.getClockIn() != null) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                String clockInTime = timeFormat.format(currentAttendance.getClockIn());
                textViewStatus.setText("On duty since " + clockInTime);
            } else {
                textViewStatus.setText("Currently on duty");
            }
        } else {
            buttonClockAction.setText("Clock In");
            textViewStatus.setText("Not started");
        }
        
        updateButtonState();
        updateTimeAndDate();
    }

    private void updateButtonState() {
        if (isClockedIn) {
            // Can clock out if availability says so
            buttonClockAction.setEnabled(canClockOut);
            buttonClockAction.setAlpha(canClockOut ? 1.0f : 0.5f);
        } else {
            // Always allow attempting to clock in; backend will enforce time rules
            buttonClockAction.setEnabled(true);
            buttonClockAction.setAlpha(1.0f);
        }
    }

    private void updateTimeAndDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        
        textViewCurrentTime.setText(timeFormat.format(calendar.getTime()));
        textViewCurrentDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void startTimeUpdate() {
        // Initialize lastCheckedMinute to current minute to avoid immediate redundant check
        Calendar initialCalendar = Calendar.getInstance();
        lastCheckedMinute = initialCalendar.get(Calendar.MINUTE);
        
        timeUpdateHandler = new android.os.Handler(android.os.Looper.getMainLooper());
        timeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateTimeAndDate();
                
                // Check if minute changed, then refresh availability
                Calendar calendar = Calendar.getInstance();
                int currentMinute = calendar.get(Calendar.MINUTE);
                if (currentMinute != lastCheckedMinute) {
                    lastCheckedMinute = currentMinute;
                    checkClockAvailability();
                }
                
                timeUpdateHandler.postDelayed(this, 1000);
            }
        };
        timeUpdateHandler.post(timeUpdateRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeUpdateHandler != null && timeUpdateRunnable != null) {
            timeUpdateHandler.removeCallbacks(timeUpdateRunnable);
        }
    }

    private void setupDrawer() {
        // Setup drawer header with user info
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            TextView navHeaderGreeting = headerView.findViewById(R.id.navHeaderGreeting);
            TextView navHeaderName = headerView.findViewById(R.id.navHeaderName);
            
            if (navHeaderGreeting != null) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
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
                String userName = prefs.getString(KEY_ADMIN_NAME, "Officer");
                navHeaderName.setText(userName);
            }
        }

        // Setup navigation item selection
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_dashboard) {
                Intent intent = new Intent(ClockInOutActivity.this, OfficerActivity.class);
                startActivity(intent);
                finish();
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_clock_in_out) {
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_duty_schedule) {
                Intent intent = new Intent(ClockInOutActivity.this, DutyScheduleActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_notifications) {
                Intent intent = new Intent(ClockInOutActivity.this, NotificationsActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_logout) {
                handleLogout();
                drawerLayout.closeDrawer(navigationView);
                return true;
            }
            
            return false;
        });
    }

    private void handleLogout() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(ClockInOutActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupClickListeners() {
        imageViewMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(navigationView);
        });

        buttonClockAction.setOnClickListener(v -> {
            if (isClockedIn) {
                // Clock out
                attendanceRepository.checkOut();
            } else {
                // Clock in
                attendanceRepository.checkIn();
            }
        });

        buttonBack.setOnClickListener(v -> {
            finish();
        });
    }
}

