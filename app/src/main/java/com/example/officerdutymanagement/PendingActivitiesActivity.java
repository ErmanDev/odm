package com.example.officerdutymanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.example.officerdutymanagement.model.PendingActivity;

import java.util.ArrayList;
import java.util.List;

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
        setupDrawer();
        setupDepartmentFilter();
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

    private void setupDepartmentFilter() {
        String[] departments = {
            getString(R.string.all_department),
            "Sanitation Department",
            "Maintenance Department",
            "Administrative Department",
            "Logistics Department",
            "Health and Safety Department"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            departments
        );

        departmentFilter.setAdapter(adapter);
        departmentFilter.setText(departments[0], false);
        departmentFilter.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDepartment = departments[position];
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
        activityList = new ArrayList<>();
        // Sample data based on the image
        activityList.add(new PendingActivity("Juan Dela Cruz", "Sanitation", "Barangay Clean-up", "Pending"));
        activityList.add(new PendingActivity("Joanne Reyes", "Sanitation", "Barangay Clean-up", "Pending"));
        activityList.add(new PendingActivity("Maria Lopez", "Sanitation", "Barangay Clean-up", "On going"));
        activityList.add(new PendingActivity("Leo Garcia", "Sanitation", "Barangay Clean-up", "Pending"));
        activityList.add(new PendingActivity("Angela Rivera", "Sanitation", "Barangay Clean-up", "Pending"));

        filteredActivityList.clear();
        filteredActivityList.addAll(activityList);
        activityAdapter.notifyDataSetChanged();
    }

    private void filterActivities(String department) {
        filteredActivityList.clear();
        if (department.equals(getString(R.string.all_department))) {
            filteredActivityList.addAll(activityList);
        } else {
            String departmentName = department.replace(" Department", "");
            for (PendingActivity activity : activityList) {
                if (activity.getDepartment().equals(departmentName)) {
                    filteredActivityList.add(activity);
                }
            }
        }
        activityAdapter.notifyDataSetChanged();
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

