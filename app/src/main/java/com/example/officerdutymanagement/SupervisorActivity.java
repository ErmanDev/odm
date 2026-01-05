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

public class SupervisorActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_SUPERVISOR_NAME = "supervisor_name";
    private static final String KEY_USER_DEPARTMENT = "user_department";

    private TextView textViewGreeting;
    private TextView textViewDate;
    private TextView textViewPendingLeaveValue;
    private TextView textViewPendingLeaveLabel;
    private TextView textViewActiveDutyValue;
    private TextView textViewActiveDutyLabel;
    private TextView textViewAttendanceTodayValue;
    private TextView textViewAttendanceTodayLabel;
    private TextView textViewTotalOfficersValue;
    private TextView textViewTotalOfficersLabel;
    
    private View cardDutyScheduleManagement;
    private View cardOfficerAssignment;
    private View cardAttendanceMonitoring;
    private View cardLeaveRequests;
    private View cardOfficerManagement;
    
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageViewMenu;

    private DashboardViewModel dashboardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supervisor);
        
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
        View metricPendingLeave = findViewById(R.id.metricPendingLeave);
        textViewPendingLeaveValue = metricPendingLeave.findViewById(R.id.textViewMetricValue);
        textViewPendingLeaveLabel = metricPendingLeave.findViewById(R.id.textViewMetricLabel);
        
        View metricActiveDuty = findViewById(R.id.metricActiveDuty);
        textViewActiveDutyValue = metricActiveDuty.findViewById(R.id.textViewMetricValue);
        textViewActiveDutyLabel = metricActiveDuty.findViewById(R.id.textViewMetricLabel);
        
        View metricAttendanceToday = findViewById(R.id.metricAttendanceToday);
        textViewAttendanceTodayValue = metricAttendanceToday.findViewById(R.id.textViewMetricValue);
        textViewAttendanceTodayLabel = metricAttendanceToday.findViewById(R.id.textViewMetricLabel);
        
        View metricTotalOfficers = findViewById(R.id.metricTotalOfficers);
        textViewTotalOfficersValue = metricTotalOfficers.findViewById(R.id.textViewMetricValue);
        textViewTotalOfficersLabel = metricTotalOfficers.findViewById(R.id.textViewMetricLabel);
        
        // Cards
        cardDutyScheduleManagement = findViewById(R.id.cardDutyScheduleManagement);
        cardOfficerAssignment = findViewById(R.id.cardOfficerAssignment);
        cardAttendanceMonitoring = findViewById(R.id.cardAttendanceMonitoring);
        cardLeaveRequests = findViewById(R.id.cardLeaveRequests);
        cardOfficerManagement = findViewById(R.id.cardOfficerManagement);
    }

    private void initializeViewModel() {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
    }

    private void setupDashboard() {
        // Load supervisor name from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String supervisorName = prefs.getString(KEY_SUPERVISOR_NAME, "Supervisor");
        
        // Set greeting
        textViewGreeting.setText("Hello, " + supervisorName);
        
        // Set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        textViewDate.setText(currentDate);
        
        // Set metrics labels
        textViewPendingLeaveLabel.setText("Pending Leave");
        textViewPendingLeaveLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        textViewActiveDutyLabel.setText("Active Duty");
        textViewActiveDutyLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        textViewAttendanceTodayLabel.setText("Present Today");
        textViewTotalOfficersLabel.setText("Total Officers");
        textViewTotalOfficersLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        
        // Observe LiveData from ViewModel
        observeViewModel();
        
        // Load supervisor dashboard stats
        dashboardViewModel.loadSupervisorDashboardStats();
        
        // Setup cards with icons and titles
        setupCard(cardDutyScheduleManagement, R.drawable.ic_schedule, "Duty Schedule");
        setupCard(cardOfficerAssignment, R.drawable.ic_duty_assignment, "Officer Assignment");
        setupCard(cardAttendanceMonitoring, R.drawable.ic_attendance_tracking, "Attendance");
        setupCard(cardLeaveRequests, R.drawable.ic_edit_profile, "Leave Requests");
        setupCard(cardOfficerManagement, R.drawable.ic_officer_list, "Officer Management");
    }

    private void setupCard(View cardView, int iconResId, String title) {
        ImageView iconView = cardView.findViewById(R.id.imageViewCardIcon);
        TextView titleView = cardView.findViewById(R.id.textViewCardTitle);
        
        if (iconView != null) {
            iconView.setImageResource(iconResId);
        }
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    private void observeViewModel() {
        dashboardViewModel.getRequestForAbsenceCount().observe(this, count -> {
            if (count != null) {
                textViewPendingLeaveValue.setText(String.valueOf(count));
            }
        });
        
        dashboardViewModel.getActiveDutyAssignmentsCount().observe(this, count -> {
            if (count != null) {
                textViewActiveDutyValue.setText(String.valueOf(count));
            }
        });
        
        dashboardViewModel.getPresentTodayCount().observe(this, count -> {
            if (count != null) {
                textViewAttendanceTodayValue.setText(String.valueOf(count));
            }
        });
        
        dashboardViewModel.getTotalOfficersCount().observe(this, count -> {
            if (count != null) {
                textViewTotalOfficersValue.setText(String.valueOf(count));
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
        dashboardViewModel.loadSupervisorDashboardStats();
    }

    private void setupClickListeners() {
        cardDutyScheduleManagement.setOnClickListener(v -> {
            // TODO: Navigate to DutyScheduleManagementActivity when created
            Toast.makeText(this, "Duty Schedule Management - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        cardOfficerAssignment.setOnClickListener(v -> {
            Intent intent = new Intent(SupervisorActivity.this, DutyAssignmentActivity.class);
            startActivity(intent);
        });

        cardAttendanceMonitoring.setOnClickListener(v -> {
            Intent intent = new Intent(SupervisorActivity.this, AttendanceTrackingActivity.class);
            startActivity(intent);
        });

        cardLeaveRequests.setOnClickListener(v -> {
            Intent intent = new Intent(SupervisorActivity.this, AbsenceRequestsActivity.class);
            startActivity(intent);
        });

        cardOfficerManagement.setOnClickListener(v -> {
            Intent intent = new Intent(SupervisorActivity.this, OfficerListManagementActivity.class);
            startActivity(intent);
        });
    }

    private void setupDrawer() {
        // Setup drawer header with user info
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            TextView navHeaderGreeting = headerView.findViewById(R.id.navHeaderGreeting);
            TextView navHeaderName = headerView.findViewById(R.id.navHeaderName);
            TextView navHeaderDepartment = headerView.findViewById(R.id.navHeaderDepartment);
            
            if (navHeaderGreeting != null) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
                String greeting;
                if (hour < 12) {
                    greeting = "Good Morning";
                } else if (hour < 17) {
                    greeting = "Good Afternoon";
                } else {
                    greeting = "Good Evening";
                }
                navHeaderGreeting.setText(greeting);
            }
            
            if (navHeaderName != null) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                String userName = prefs.getString(KEY_SUPERVISOR_NAME, "Supervisor");
                navHeaderName.setText(userName);
            }
            
            if (navHeaderDepartment != null) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                String department = prefs.getString(KEY_USER_DEPARTMENT, "Department");
                navHeaderDepartment.setText(department);
            }
        }

        imageViewMenu.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            drawerLayout.closeDrawer(navigationView);

            if (itemId == R.id.nav_home) {
                // Already on home, just close drawer
            } else if (itemId == R.id.nav_duty_schedule_management) {
                // TODO: Navigate to DutyScheduleManagementActivity when created
                Toast.makeText(this, "Duty Schedule Management - Coming Soon", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_officer_assignment) {
                startActivity(new Intent(this, DutyAssignmentActivity.class));
                finish();
            } else if (itemId == R.id.nav_attendance_monitoring) {
                startActivity(new Intent(this, AttendanceTrackingActivity.class));
                finish();
            } else if (itemId == R.id.nav_leave_requests) {
                startActivity(new Intent(this, AbsenceRequestsActivity.class));
                finish();
            } else if (itemId == R.id.nav_officer_management) {
                startActivity(new Intent(this, OfficerListManagementActivity.class));
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

