package com.example.officerdutymanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import com.example.officerdutymanagement.adapter.PendingActivityAdapter;
import com.example.officerdutymanagement.model.DutyAssignment;
import com.example.officerdutymanagement.model.PendingActivity;
import com.example.officerdutymanagement.model.User;
import com.example.officerdutymanagement.repository.DutyAssignmentRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PendingActivitiesActivity extends AppCompatActivity {

    private AutoCompleteTextView departmentFilter;
    private RecyclerView recyclerViewActivities;
    private PendingActivityAdapter activityAdapter;
    private List<PendingActivity> activityList;
    private List<PendingActivity> filteredActivityList;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageViewMenu;
    private ImageView imageViewBack;
    
    private DutyAssignmentRepository dutyAssignmentRepository;
    private List<DutyAssignment> allDutyAssignments;
    
    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_ADMIN_NAME = "admin_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pending_activities);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        initializeRepository();
        setupDrawer();
        setupRecyclerView();
        loadActivityData();
    }

    private void initializeViews() {
        departmentFilter = findViewById(R.id.autoCompleteDepartment);
        recyclerViewActivities = findViewById(R.id.recyclerViewActivities);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        imageViewMenu = findViewById(R.id.imageViewMenu);
        imageViewBack = findViewById(R.id.imageViewBack);
        
        if (imageViewBack != null) {
            imageViewBack.setOnClickListener(v -> finish());
        }
    }

    private void initializeRepository() {
        dutyAssignmentRepository = DutyAssignmentRepository.getInstance();
        allDutyAssignments = new ArrayList<>();
        
        dutyAssignmentRepository.getDutyAssignmentList().observe(this, assignments -> {
            if (assignments != null) {
                allDutyAssignments.clear();
                allDutyAssignments.addAll(assignments);
                convertDutyAssignmentsToPendingActivities();
                setupDepartmentFilter();
            }
        });
        
        dutyAssignmentRepository.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void convertDutyAssignmentsToPendingActivities() {
        activityList.clear();
        
        for (DutyAssignment assignment : allDutyAssignments) {
            // Only include assignments with "pending" status
            if (assignment.getStatus() != null && assignment.getStatus().toLowerCase().equals("pending")) {
                String officerName = assignment.getOfficerName();
                if (officerName == null || officerName.isEmpty()) {
                    // Try to get from user object
                    if (assignment.getUser() != null) {
                        officerName = assignment.getUser().getFullName() != null ? 
                            assignment.getUser().getFullName() : 
                            assignment.getUser().getUsername();
                    }
                }
                
                String department = assignment.getDepartment() != null ? assignment.getDepartment() : "Unknown";
                String activity = assignment.getTaskLocation() != null ? assignment.getTaskLocation() : "No activity";
                String status = assignment.getStatus() != null ? assignment.getStatus() : "Pending";
                
                activityList.add(new PendingActivity(officerName, department, activity, status));
            }
        }
        
        filteredActivityList.clear();
        filteredActivityList.addAll(activityList);
        activityAdapter.notifyDataSetChanged();
    }
    
    private void setupDepartmentFilter() {
        // Collect unique departments from the data
        Set<String> departmentSet = new HashSet<>();
        departmentSet.add(getString(R.string.all_department));
        
        for (PendingActivity activity : activityList) {
            if (activity.getDepartment() != null && !activity.getDepartment().isEmpty()) {
                departmentSet.add(activity.getDepartment());
            }
        }
        
        List<String> departments = new ArrayList<>(departmentSet);
        java.util.Collections.sort(departments);
        // Move "All Departments" to the top
        if (departments.contains(getString(R.string.all_department))) {
            departments.remove(getString(R.string.all_department));
            departments.add(0, getString(R.string.all_department));
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            departments
        );

        departmentFilter.setAdapter(adapter);
        departmentFilter.setText(departments.get(0), false);
        departmentFilter.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDepartment = departments.get(position);
            filterActivities(selectedDepartment);
        });
    }

    private void setupRecyclerView() {
        filteredActivityList = new ArrayList<>();
        activityAdapter = new PendingActivityAdapter(filteredActivityList);
        recyclerViewActivities.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewActivities.setAdapter(activityAdapter);
    }

    private void loadActivityData() {
        // Fetch all duty assignments from API
        // Backend will filter by department for supervisors, show all for admins
        dutyAssignmentRepository.getAllDutyAssignments();
    }

    private void filterActivities(String department) {
        filteredActivityList.clear();
        if (department.equals(getString(R.string.all_department))) {
            filteredActivityList.addAll(activityList);
        } else {
            for (PendingActivity activity : activityList) {
                if (activity.getDepartment() != null && activity.getDepartment().equals(department)) {
                    filteredActivityList.add(activity);
                }
            }
        }
        activityAdapter.notifyDataSetChanged();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when activity resumes
        loadActivityData();
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
            } else if (itemId == R.id.nav_duty_assignment) {
                startActivity(new Intent(this, DutyAssignmentActivity.class));
                finish();
            } else if (itemId == R.id.nav_pending_activities) {
                // Already on this screen, just close drawer
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

