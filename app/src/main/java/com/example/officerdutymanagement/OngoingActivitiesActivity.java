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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import com.example.officerdutymanagement.adapter.OngoingActivityAdapter;
import com.example.officerdutymanagement.model.DutyAssignment;
import com.example.officerdutymanagement.model.OngoingActivity;
import com.example.officerdutymanagement.repository.DutyAssignmentRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OngoingActivitiesActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_ADMIN_NAME = "admin_name";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageViewMenu;
    private ImageView imageViewProfile;
    private TextView textViewGreeting;
    private TextView textViewUserName;
    private RecyclerView recyclerViewActivities;
    private OngoingActivityAdapter ongoingActivityAdapter;
    private List<OngoingActivity> ongoingActivityList;
    
    private DutyAssignmentRepository dutyAssignmentRepository;
    private List<DutyAssignment> allDutyAssignments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ongoing_activities);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupUserInfo();
        setupRecyclerView();
        initializeRepository();
        loadActivityData();
        setupDrawer();
        setupClickListeners();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        imageViewMenu = findViewById(R.id.imageViewMenu);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        textViewGreeting = findViewById(R.id.textViewGreeting);
        textViewUserName = findViewById(R.id.textViewUserName);
        recyclerViewActivities = findViewById(R.id.recyclerViewActivities);
    }

    private void setupUserInfo() {
        // Load user name from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userName = prefs.getString(KEY_ADMIN_NAME, "Officer");
        
        // Set greeting based on time of day
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
        
        textViewGreeting.setText(greeting);
        textViewUserName.setText(userName);
    }

    private void setupRecyclerView() {
        ongoingActivityList = new ArrayList<>();
        ongoingActivityAdapter = new OngoingActivityAdapter(ongoingActivityList);
        recyclerViewActivities.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewActivities.setAdapter(ongoingActivityAdapter);
    }

    private void initializeRepository() {
        dutyAssignmentRepository = DutyAssignmentRepository.getInstance();
        allDutyAssignments = new ArrayList<>();
        
        dutyAssignmentRepository.getDutyAssignmentList().observe(this, assignments -> {
            if (assignments != null) {
                allDutyAssignments.clear();
                allDutyAssignments.addAll(assignments);
                convertDutyAssignmentsToOngoingActivities();
            }
        });
        
        dutyAssignmentRepository.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void convertDutyAssignmentsToOngoingActivities() {
        ongoingActivityList.clear();
        
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        
        for (DutyAssignment assignment : allDutyAssignments) {
            // Only include assignments with "ongoing" or "in-progress" status
            String status = assignment.getStatus() != null ? assignment.getStatus().toLowerCase() : "";
            if (status.equals("ongoing") || status.equals("in-progress") || status.equals("in_progress")) {
                // Format date
                String dateStr = "--";
                if (assignment.getDate() != null) {
                    try {
                        java.util.Date date = inputFormat.parse(assignment.getDate());
                        if (date != null) {
                            dateStr = outputFormat.format(date);
                        }
                    } catch (Exception e) {
                        try {
                            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            java.util.Date date = simpleFormat.parse(assignment.getDate());
                            if (date != null) {
                                dateStr = outputFormat.format(date);
                            }
                        } catch (Exception ex) {
                            // Keep default "--"
                        }
                    }
                }
                
                String task = assignment.getTaskLocation() != null ? assignment.getTaskLocation() : "No task";
                String location = assignment.getDepartment() != null ? assignment.getDepartment() : "Unknown";
                String displayStatus = assignment.getStatus() != null ? assignment.getStatus() : "In Progress";
                String action = "View Details";
                
                ongoingActivityList.add(new OngoingActivity(dateStr, task, location, displayStatus, action));
            }
        }
        
        ongoingActivityAdapter.notifyDataSetChanged();
    }
    
    private void loadActivityData() {
        // Fetch all duty assignments from API
        // Backend will filter by department for supervisors, show all for admins
        dutyAssignmentRepository.getAllDutyAssignments();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when activity resumes
        loadActivityData();
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
                Intent intent = new Intent(OngoingActivitiesActivity.this, OfficerActivity.class);
                startActivity(intent);
                finish();
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_clock_in_out) {
                Intent intent = new Intent(OngoingActivitiesActivity.this, ClockInOutActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_duty_schedule) {
                Intent intent = new Intent(OngoingActivitiesActivity.this, DutyScheduleActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_notifications) {
                Intent intent = new Intent(OngoingActivitiesActivity.this, NotificationsActivity.class);
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

        Intent intent = new Intent(OngoingActivitiesActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupClickListeners() {
        imageViewMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(navigationView);
        });
    }
}

