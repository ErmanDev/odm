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

import com.example.officerdutymanagement.adapter.DutyScheduleAdapter;
import com.example.officerdutymanagement.model.DutySchedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DutyScheduleActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_ADMIN_NAME = "admin_name";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageViewMenu;
    private ImageView imageViewProfile;
    private TextView textViewGreeting;
    private TextView textViewUserName;
    private RecyclerView recyclerViewDutySchedule;
    private DutyScheduleAdapter dutyScheduleAdapter;
    private List<DutySchedule> dutyScheduleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_duty_schedule);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupUserInfo();
        setupRecyclerView();
        loadScheduleData();
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
        recyclerViewDutySchedule = findViewById(R.id.recyclerViewDutySchedule);
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
        dutyScheduleList = new ArrayList<>();
        dutyScheduleAdapter = new DutyScheduleAdapter(dutyScheduleList);
        recyclerViewDutySchedule.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDutySchedule.setAdapter(dutyScheduleAdapter);
    }

    private void loadScheduleData() {
        dutyScheduleList.clear();
        // Load sample duty schedule data (same as in OfficerActivity)
        dutyScheduleList.add(new DutySchedule("Aug 21", "12:00 pm - 2:00 pm"));
        dutyScheduleList.add(new DutySchedule("Aug 22", "12:00 pm - 2:00 pm"));
        dutyScheduleList.add(new DutySchedule("Aug 23", "12:00 am - 9:00 pm"));
        dutyScheduleList.add(new DutySchedule("Aug 24", "12:00 am - 8:00 pm"));
        dutyScheduleList.add(new DutySchedule("Aug 25", "10:00 pm - 2:00 pm"));
        dutyScheduleList.add(new DutySchedule("Aug 26", "05:00 pm - 2:00 pm"));
        dutyScheduleList.add(new DutySchedule("Aug 27", "08:00 am - 4:00 pm"));
        dutyScheduleList.add(new DutySchedule("Aug 28", "09:00 am - 5:00 pm"));
        dutyScheduleList.add(new DutySchedule("Aug 29", "10:00 am - 6:00 pm"));
        dutyScheduleList.add(new DutySchedule("Aug 30", "11:00 am - 7:00 pm"));
        
        dutyScheduleAdapter.notifyDataSetChanged();
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
                Intent intent = new Intent(DutyScheduleActivity.this, OfficerActivity.class);
                startActivity(intent);
                finish();
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_edit_profile) {
                Intent intent = new Intent(DutyScheduleActivity.this, EditEmployeeProfileActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_clock_in_out) {
                Intent intent = new Intent(DutyScheduleActivity.this, ClockInOutActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_duty_schedule) {
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_notifications) {
                Intent intent = new Intent(DutyScheduleActivity.this, NotificationsActivity.class);
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

        Intent intent = new Intent(DutyScheduleActivity.this, LoginActivity.class);
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

