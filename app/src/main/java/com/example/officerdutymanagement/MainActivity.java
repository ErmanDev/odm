package com.example.officerdutymanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.officerdutymanagement.viewmodel.DashboardViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_ADMIN_NAME = "admin_name";

    private TextView textViewGreeting;
    private TextView textViewDate;
    private TextView textViewLateCheckInValue;
    private TextView textViewLateCheckInLabel;
    private TextView textViewRequestAbsenceValue;
    private TextView textViewRequestAbsenceLabel;
    private TextView textViewPresentTodayValue;
    private TextView textViewPresentTodayLabel;
    
    private View cardOfficerList;
    private View cardDutyAssignment;
    private View cardPendingActivities;
    private View cardAttendanceTracking;
    private View cardNotifications;
    private View cardClockSettings;
    private View cardAbsenceRequests;
    
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageViewMenu;

    private DashboardViewModel dashboardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        initializeViewModel();
        setupDashboard();
        setupDrawer();
        setupClickListeners();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        imageViewMenu = findViewById(R.id.imageViewMenu);
        textViewGreeting = findViewById(R.id.textViewGreeting);
        textViewDate = findViewById(R.id.textViewDate);
        
        // Metrics
        View metricLateCheckIn = findViewById(R.id.metricLateCheckIn);
        textViewLateCheckInValue = metricLateCheckIn.findViewById(R.id.textViewMetricValue);
        textViewLateCheckInLabel = metricLateCheckIn.findViewById(R.id.textViewMetricLabel);
        
        View metricRequestAbsence = findViewById(R.id.metricRequestAbsence);
        textViewRequestAbsenceValue = metricRequestAbsence.findViewById(R.id.textViewMetricValue);
        textViewRequestAbsenceLabel = metricRequestAbsence.findViewById(R.id.textViewMetricLabel);
        
        View metricPresentToday = findViewById(R.id.metricPresentToday);
        textViewPresentTodayValue = metricPresentToday.findViewById(R.id.textViewMetricValue);
        textViewPresentTodayLabel = metricPresentToday.findViewById(R.id.textViewMetricLabel);
        
        // Cards
        cardOfficerList = findViewById(R.id.cardOfficerList);
        cardDutyAssignment = findViewById(R.id.cardDutyAssignment);
        cardPendingActivities = findViewById(R.id.cardPendingActivities);
        cardAttendanceTracking = findViewById(R.id.cardAttendanceTracking);
        cardNotifications = findViewById(R.id.cardNotifications);
        cardClockSettings = findViewById(R.id.cardClockSettings);
        cardAbsenceRequests = findViewById(R.id.cardAbsenceRequests);
    }

    private void initializeViewModel() {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
    }

    private void setupDashboard() {
        // Load admin name from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String adminName = prefs.getString(KEY_ADMIN_NAME, "Admin");
        
        // Set greeting
        String greeting = getString(R.string.dashboard_greeting, adminName);
        textViewGreeting.setText(greeting);
        
        // Set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        textViewDate.setText(currentDate);
        
        // Set metrics labels
        textViewLateCheckInLabel.setText(R.string.late_check_in);
        textViewRequestAbsenceLabel.setText(R.string.request_for_absence);
        textViewRequestAbsenceLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        textViewPresentTodayLabel.setText(R.string.present_today);
        
        // Observe LiveData from ViewModel
        observeViewModel();
        
        // Load dashboard stats
        dashboardViewModel.loadDashboardStats();
        
        // Setup cards with icons and titles
        setupCard(cardOfficerList, R.drawable.ic_officer_list, R.string.officer_list_management);
        setupCard(cardDutyAssignment, R.drawable.ic_duty_assignment, R.string.duty_assignment);
        setupCard(cardPendingActivities, R.drawable.ic_pending_activities, R.string.pending_activities);
        setupCard(cardAttendanceTracking, R.drawable.ic_attendance_tracking, R.string.attendance_performance_tracking);
        setupCard(cardNotifications, R.drawable.ic_notifications, R.string.notifications_alert);
        setupCard(cardClockSettings, R.drawable.ic_clock, R.string.clock_settings);
        setupCard(cardAbsenceRequests, R.drawable.ic_edit_profile, R.string.absence_requests);
    }

    private void setupCard(View cardView, int iconResId, int titleResId) {
        ImageView iconView = cardView.findViewById(R.id.imageViewCardIcon);
        TextView titleView = cardView.findViewById(R.id.textViewCardTitle);
        
        if (iconView != null) {
            iconView.setImageResource(iconResId);
        }
        if (titleView != null) {
            titleView.setText(titleResId);
        }
    }

    private void observeViewModel() {
        dashboardViewModel.getLateCheckInCount().observe(this, count -> {
            if (count != null) {
                textViewLateCheckInValue.setText(String.valueOf(count));
            }
        });
        
        dashboardViewModel.getRequestForAbsenceCount().observe(this, count -> {
            if (count != null) {
                textViewRequestAbsenceValue.setText(String.valueOf(count));
            }
        });
        
        dashboardViewModel.getPresentTodayCount().observe(this, count -> {
            if (count != null) {
                textViewPresentTodayValue.setText(String.valueOf(count));
            }
        });
        
        dashboardViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh dashboard stats when activity resumes
        dashboardViewModel.loadDashboardStats();
    }

    private void setupClickListeners() {
        cardOfficerList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OfficerListManagementActivity.class);
            startActivity(intent);
        });

        cardDutyAssignment.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DutyAssignmentActivity.class);
            startActivity(intent);
        });

        cardPendingActivities.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PendingActivitiesActivity.class);
            startActivity(intent);
        });

        cardAttendanceTracking.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AttendanceTrackingActivity.class);
            startActivity(intent);
        });

        cardNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminNotificationsActivity.class);
            startActivity(intent);
        });

        cardClockSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ClockSettingsActivity.class);
            startActivity(intent);
        });

        cardAbsenceRequests.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AbsenceRequestsActivity.class);
            startActivity(intent);
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
                // Already on home, just close drawer
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
                startActivity(new Intent(this, AttendanceTrackingActivity.class));
                finish();
            } else if (itemId == R.id.nav_notifications) {
                startActivity(new Intent(this, AdminNotificationsActivity.class));
                finish();
            } else if (itemId == R.id.nav_absence_requests) {
                startActivity(new Intent(this, AbsenceRequestsActivity.class));
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
}
