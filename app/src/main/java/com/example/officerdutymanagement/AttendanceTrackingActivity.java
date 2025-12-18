package com.example.officerdutymanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import com.example.officerdutymanagement.adapter.AttendanceAdapter;
import com.example.officerdutymanagement.model.Attendance;
import com.example.officerdutymanagement.model.ClockSettings;
import com.example.officerdutymanagement.repository.AttendanceRepository;
import com.example.officerdutymanagement.repository.ClockSettingsRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AttendanceTrackingActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_USER_ROLE = "user_role";

    private RecyclerView recyclerViewAttendance;
    private AttendanceAdapter attendanceAdapter;
    private AttendanceRepository attendanceRepository;
    private ClockSettingsRepository clockSettingsRepository;
    private TextView textViewPlaceholder;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageViewMenu;
    private ImageView imageViewBack;
    private boolean isAdmin = false;
    
    private static final String PREFS_NAME_ADMIN = "OfficerDutyPrefs";
    private static final String KEY_ADMIN_NAME = "admin_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendance_tracking);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        checkUserRole();
        setupRecyclerView();
        initializeRepository();
        if (isAdmin) {
            setupDrawer();
        }
        loadAttendance();
    }

    private void initializeViews() {
        recyclerViewAttendance = findViewById(R.id.recyclerViewAttendance);
        textViewPlaceholder = findViewById(R.id.textViewPlaceholder);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        imageViewMenu = findViewById(R.id.imageViewMenu);
        imageViewBack = findViewById(R.id.imageViewBack);
        
        if (imageViewBack != null) {
            imageViewBack.setOnClickListener(v -> finish());
        }
    }

    private void checkUserRole() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userRole = prefs.getString(KEY_USER_ROLE, "admin");
        isAdmin = "admin".equalsIgnoreCase(userRole);
    }

    private void setupRecyclerView() {
        attendanceAdapter = new AttendanceAdapter(new ArrayList<>());
        recyclerViewAttendance.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAttendance.setAdapter(attendanceAdapter);
    }

    private void initializeRepository() {
        attendanceRepository = AttendanceRepository.getInstance();
        clockSettingsRepository = ClockSettingsRepository.getInstance();
        
        attendanceRepository.getAttendanceList().observe(this, attendanceList -> {
            if (attendanceList != null && !attendanceList.isEmpty()) {
                attendanceAdapter.updateAttendanceList(attendanceList);
                if (textViewPlaceholder != null) {
                    textViewPlaceholder.setVisibility(android.view.View.GONE);
                }
                recyclerViewAttendance.setVisibility(android.view.View.VISIBLE);
            } else {
                if (textViewPlaceholder != null) {
                    textViewPlaceholder.setVisibility(android.view.View.VISIBLE);
                }
                recyclerViewAttendance.setVisibility(android.view.View.GONE);
            }
        });

        attendanceRepository.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Observe clock settings for late detection
        clockSettingsRepository.getCurrentClockSettings().observe(this, clockSettings -> {
            if (clockSettings != null) {
                attendanceAdapter.setClockSettings(clockSettings);
            }
        });
    }

    private void loadAttendance() {
        // Load attendance for the current month by default
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        // Start of month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = dateFormat.format(calendar.getTime());
        
        // End of month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = dateFormat.format(calendar.getTime());

        if (isAdmin) {
            attendanceRepository.getAllAttendance(startDate, endDate, null, null);
        } else {
            attendanceRepository.getMyAttendance(startDate, endDate);
        }
        
        // Load clock settings for late detection
        clockSettingsRepository.getClockSettings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAttendance();
    }

    private void setupDrawer() {
        if (drawerLayout == null || navigationView == null || imageViewMenu == null) {
            return;
        }

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
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME_ADMIN, MODE_PRIVATE);
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
            } else if (itemId == R.id.nav_duty_assignment) {
                startActivity(new Intent(this, DutyAssignmentActivity.class));
                finish();
            } else if (itemId == R.id.nav_pending_activities) {
                startActivity(new Intent(this, PendingActivitiesActivity.class));
                finish();
            } else if (itemId == R.id.nav_attendance_tracking) {
                // Already on this screen, just close drawer
            } else if (itemId == R.id.nav_notifications) {
                startActivity(new Intent(this, AdminNotificationsActivity.class));
                finish();
            } else if (itemId == R.id.nav_absence_requests) {
                startActivity(new Intent(this, AbsenceRequestsActivity.class));
                finish();
            } else if (itemId == R.id.nav_logout) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME_ADMIN, MODE_PRIVATE);
                prefs.edit().clear().apply();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            return true;
        });
    }
}


