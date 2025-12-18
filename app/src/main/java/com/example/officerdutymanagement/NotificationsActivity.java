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

import com.example.officerdutymanagement.adapter.NotificationAdapter;
import com.example.officerdutymanagement.model.Notification;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_ADMIN_NAME = "admin_name";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageViewMenu;
    private ImageView imageViewProfile;
    private TextView textViewGreeting;
    private TextView textViewUserName;
    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupUserInfo();
        setupRecyclerView();
        loadNotificationData();
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
        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
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
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationList);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotifications.setAdapter(notificationAdapter);
    }

    private void loadNotificationData() {
        notificationList.clear();
        // Load sample notification data (expanded list)
        notificationList.add(new Notification("New Assignment", "New duty procedures effective next week."));
        notificationList.add(new Notification("Duty Time Over", "Your shift has ended. Don't forget to check out."));
        notificationList.add(new Notification("Upcoming Duty Reminder", "Your next duty starts in 1 hour."));
        notificationList.add(new Notification("General Announcement", "New duty procedures effective next week."));
        notificationList.add(new Notification("Schedule Update", "Your duty schedule for next week has been updated."));
        notificationList.add(new Notification("Meeting Reminder", "Team meeting scheduled for tomorrow at 2:00 PM."));
        notificationList.add(new Notification("System Maintenance", "System will be under maintenance this weekend."));
        notificationList.add(new Notification("Holiday Notice", "Office will be closed on the upcoming holiday."));
        notificationList.add(new Notification("Training Session", "Mandatory training session next Monday."));
        notificationList.add(new Notification("Performance Review", "Your quarterly performance review is due."));
        
        notificationAdapter.notifyDataSetChanged();
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
                Intent intent = new Intent(NotificationsActivity.this, OfficerActivity.class);
                startActivity(intent);
                finish();
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_clock_in_out) {
                Intent intent = new Intent(NotificationsActivity.this, ClockInOutActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_duty_schedule) {
                Intent intent = new Intent(NotificationsActivity.this, DutyScheduleActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_notifications) {
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

        Intent intent = new Intent(NotificationsActivity.this, LoginActivity.class);
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

