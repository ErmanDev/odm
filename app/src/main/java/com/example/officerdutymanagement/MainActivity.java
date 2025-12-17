package com.example.officerdutymanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
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
    private View cardEditProfile;
    private View cardDutyAssignment;
    private View cardPendingActivities;
    private View cardAttendanceTracking;
    private View cardNotifications;
    private Button buttonAllDepartment;

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
        setupClickListeners();
    }

    private void initializeViews() {
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
        cardEditProfile = findViewById(R.id.cardEditProfile);
        cardDutyAssignment = findViewById(R.id.cardDutyAssignment);
        cardPendingActivities = findViewById(R.id.cardPendingActivities);
        cardAttendanceTracking = findViewById(R.id.cardAttendanceTracking);
        cardNotifications = findViewById(R.id.cardNotifications);
        buttonAllDepartment = findViewById(R.id.buttonAllDepartment);
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
        
        // Set metrics
        textViewLateCheckInValue.setText(String.valueOf(dashboardViewModel.getLateCheckInCount()));
        textViewLateCheckInLabel.setText(R.string.late_check_in);
        
        textViewRequestAbsenceValue.setText(String.valueOf(dashboardViewModel.getRequestForAbsenceCount()));
        textViewRequestAbsenceLabel.setText(R.string.request_for_absence);
        textViewRequestAbsenceLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        
        textViewPresentTodayValue.setText(String.valueOf(dashboardViewModel.getPresentTodayCount()));
        textViewPresentTodayLabel.setText(R.string.present_today);
        
        // Setup cards with icons and titles
        setupCard(cardOfficerList, R.drawable.ic_officer_list, R.string.officer_list_management);
        setupCard(cardEditProfile, R.drawable.ic_edit_profile, R.string.edit_employee_profile);
        setupCard(cardDutyAssignment, R.drawable.ic_duty_assignment, R.string.duty_assignment);
        setupCard(cardPendingActivities, R.drawable.ic_pending_activities, R.string.pending_activities);
        setupCard(cardAttendanceTracking, R.drawable.ic_attendance_tracking, R.string.attendance_performance_tracking);
        setupCard(cardNotifications, R.drawable.ic_notifications, R.string.notifications_alert);
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

    private void setupClickListeners() {
        cardOfficerList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OfficerListManagementActivity.class);
            startActivity(intent);
        });

        cardEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditEmployeeProfileActivity.class);
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

        buttonAllDepartment.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DepartmentsActivity.class);
            startActivity(intent);
        });
    }
}
