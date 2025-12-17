package com.example.officerdutymanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.officerdutymanagement.adapter.NotificationAdapter;
import com.example.officerdutymanagement.adapter.OngoingActivityAdapter;
import com.example.officerdutymanagement.model.DutySchedule;
import com.example.officerdutymanagement.model.Notification;
import com.example.officerdutymanagement.model.OngoingActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OfficerActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_ADMIN_NAME = "admin_name";

    // Drawer
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // Header views
    private ImageView imageViewMenu;
    private ImageView imageViewProfile;
    private TextView textViewGreeting;
    private TextView textViewUserName;

    // Check In/Out
    private Button buttonCheckIn;
    private Button buttonCheckOut;

    // Duty Schedule
    private RecyclerView recyclerViewDutySchedule;
    private DutyScheduleAdapter dutyScheduleAdapter;
    private List<DutySchedule> dutyScheduleList;
    private TextView textViewViewFullSchedule;

    // Request Absence
    private EditText editTextAbsenceDate;
    private EditText editTextAbsenceReason;
    private Button buttonChooseFile;

    // Notifications
    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private Button buttonSeeMoreNotifications;

    // Ongoing Activities
    private RecyclerView recyclerViewActivities;
    private OngoingActivityAdapter ongoingActivityAdapter;
    private List<OngoingActivity> ongoingActivityList;
    private Button buttonSeeMoreActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_officer);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupUserInfo();
        setupRecyclerViews();
        loadData();
        setupDrawer();
        setupClickListeners();
    }

    private void initializeViews() {
        // Drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Header
        imageViewMenu = findViewById(R.id.imageViewMenu);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        textViewGreeting = findViewById(R.id.textViewGreeting);
        textViewUserName = findViewById(R.id.textViewUserName);

        // Check In/Out
        buttonCheckIn = findViewById(R.id.buttonCheckIn);
        buttonCheckOut = findViewById(R.id.buttonCheckOut);

        // Duty Schedule
        recyclerViewDutySchedule = findViewById(R.id.recyclerViewDutySchedule);
        textViewViewFullSchedule = findViewById(R.id.textViewViewFullSchedule);

        // Request Absence
        editTextAbsenceDate = findViewById(R.id.editTextAbsenceDate);
        editTextAbsenceReason = findViewById(R.id.editTextAbsenceReason);
        buttonChooseFile = findViewById(R.id.buttonChooseFile);

        // Notifications
        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        buttonSeeMoreNotifications = findViewById(R.id.buttonSeeMoreNotifications);

        // Ongoing Activities
        recyclerViewActivities = findViewById(R.id.recyclerViewActivities);
        buttonSeeMoreActivities = findViewById(R.id.buttonSeeMoreActivities);
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
            greeting = "Good Morning";
        } else if (hour < 17) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }
        
        textViewGreeting.setText(greeting);
        textViewUserName.setText(userName);
    }

    private void setupRecyclerViews() {
        // Duty Schedule RecyclerView
        dutyScheduleList = new ArrayList<>();
        dutyScheduleAdapter = new DutyScheduleAdapter(dutyScheduleList);
        recyclerViewDutySchedule.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDutySchedule.setAdapter(dutyScheduleAdapter);

        // Notifications RecyclerView
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationList);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotifications.setAdapter(notificationAdapter);

        // Ongoing Activities RecyclerView
        ongoingActivityList = new ArrayList<>();
        ongoingActivityAdapter = new OngoingActivityAdapter(ongoingActivityList);
        recyclerViewActivities.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewActivities.setAdapter(ongoingActivityAdapter);
    }

    private void loadData() {
        // Load duty schedule data
        dutyScheduleList.clear();
        dutyScheduleList.add(new DutySchedule("Aug 21", "12:00 pm - 2:00 pm"));
        dutyScheduleList.add(new DutySchedule("Aug 22", "12:00 pm - 2:00 pm"));
        dutyScheduleList.add(new DutySchedule("Aug 23", "12:00 am - 9:00 pm"));
        dutyScheduleList.add(new DutySchedule("Aug 24", "12:00 am - 8:00 pm"));
        dutyScheduleList.add(new DutySchedule("Aug 25", "10:00 pm - 2:00 pm"));
        dutyScheduleList.add(new DutySchedule("Aug 26", "05:00 pm - 2:00 pm"));
        dutyScheduleAdapter.notifyDataSetChanged();

        // Load notifications data
        notificationList.clear();
        notificationList.add(new Notification("New Assignment", "New duty procedures effective next week."));
        notificationList.add(new Notification("Duty Time Over", "Your shift has ended. Don't forget to check out."));
        notificationList.add(new Notification("Upcoming Duty Reminder", "Your next duty starts in 1 hour."));
        notificationList.add(new Notification("General Announcement", "New duty procedures effective next week."));
        notificationAdapter.notifyDataSetChanged();

        // Load ongoing activities data
        ongoingActivityList.clear();
        ongoingActivityList.add(new OngoingActivity("Aug 21", "Barangay Patrol", "Zone 3", "Not Started", "Start"));
        ongoingActivityList.add(new OngoingActivity("Aug 22", "Traffic Assistance", "Main Road", "Scheduled", "Ongoing"));
        ongoingActivityAdapter.notifyDataSetChanged();
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
                String userName = prefs.getString(KEY_ADMIN_NAME, "Officer");
                navHeaderName.setText(userName);
            }
        }

        // Setup navigation item selection
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_dashboard) {
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_edit_profile) {
                Intent intent = new Intent(OfficerActivity.this, EditEmployeeProfileActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_clock_in_out) {
                Intent intent = new Intent(OfficerActivity.this, ClockInOutActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_duty_schedule) {
                Intent intent = new Intent(OfficerActivity.this, DutyScheduleActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_notifications) {
                Intent intent = new Intent(OfficerActivity.this, NotificationsActivity.class);
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
        // Clear SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Navigate to LoginActivity
        Intent intent = new Intent(OfficerActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupClickListeners() {
        imageViewMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(navigationView);
        });

        buttonCheckIn.setOnClickListener(v -> {
            // TODO: Implement check in functionality
            Toast.makeText(this, "Check In", Toast.LENGTH_SHORT).show();
        });

        buttonCheckOut.setOnClickListener(v -> {
            // TODO: Implement check out functionality
            Toast.makeText(this, "Check Out", Toast.LENGTH_SHORT).show();
        });

        textViewViewFullSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(OfficerActivity.this, DutyScheduleActivity.class);
            startActivity(intent);
        });

        editTextAbsenceDate.setOnClickListener(v -> {
            // TODO: Show date picker
            Toast.makeText(this, "Select Date", Toast.LENGTH_SHORT).show();
        });

        buttonChooseFile.setOnClickListener(v -> {
            // TODO: Open file picker
            Toast.makeText(this, "Choose File", Toast.LENGTH_SHORT).show();
        });

        buttonSeeMoreNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(OfficerActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        buttonSeeMoreActivities.setOnClickListener(v -> {
            Intent intent = new Intent(OfficerActivity.this, OngoingActivitiesActivity.class);
            startActivity(intent);
        });
    }
}
