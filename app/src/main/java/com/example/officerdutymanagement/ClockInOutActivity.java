package com.example.officerdutymanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ClockInOutActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_ADMIN_NAME = "admin_name";
    private static final String KEY_CLOCK_STATE = "clock_state";
    private static final String KEY_LAST_CLOCK_IN_TIME = "last_clock_in_time";
    private static final String CLOCKED_IN = "CLOCKED_IN";
    private static final String CLOCKED_OUT = "CLOCKED_OUT";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageViewMenu;
    private TextView textViewCurrentTime;
    private TextView textViewCurrentDate;
    private TextView textViewStatus;
    private Button buttonClockAction;
    private Button buttonBack;

    private boolean isClockedIn = false;
    private android.os.Handler timeUpdateHandler;
    private Runnable timeUpdateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clock_in_out);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        loadClockState();
        updateUI();
        setupDrawer();
        setupClickListeners();
        startTimeUpdate();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        imageViewMenu = findViewById(R.id.imageViewMenu);
        textViewCurrentTime = findViewById(R.id.textViewCurrentTime);
        textViewCurrentDate = findViewById(R.id.textViewCurrentDate);
        textViewStatus = findViewById(R.id.textViewStatus);
        buttonClockAction = findViewById(R.id.buttonClockAction);
        buttonBack = findViewById(R.id.buttonBack);
    }

    private void loadClockState() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String state = prefs.getString(KEY_CLOCK_STATE, CLOCKED_OUT);
        isClockedIn = CLOCKED_IN.equals(state);
    }

    private void saveClockState(boolean clockedIn) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_CLOCK_STATE, clockedIn ? CLOCKED_IN : CLOCKED_OUT);
        
        if (clockedIn) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            editor.putString(KEY_LAST_CLOCK_IN_TIME, timeFormat.format(new Date()));
        }
        
        editor.apply();
        isClockedIn = clockedIn;
    }

    private void updateUI() {
        if (isClockedIn) {
            buttonClockAction.setText("Clock Out");
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String lastClockInTime = prefs.getString(KEY_LAST_CLOCK_IN_TIME, "");
            if (!lastClockInTime.isEmpty()) {
                textViewStatus.setText("On duty since " + lastClockInTime);
            } else {
                textViewStatus.setText("Currently on duty");
            }
        } else {
            buttonClockAction.setText("Clock In");
            textViewStatus.setText("Not started");
        }
        
        updateTimeAndDate();
    }

    private void updateTimeAndDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        
        textViewCurrentTime.setText(timeFormat.format(calendar.getTime()));
        textViewCurrentDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void startTimeUpdate() {
        timeUpdateHandler = new android.os.Handler(android.os.Looper.getMainLooper());
        timeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateTimeAndDate();
                timeUpdateHandler.postDelayed(this, 1000);
            }
        };
        timeUpdateHandler.post(timeUpdateRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeUpdateHandler != null && timeUpdateRunnable != null) {
            timeUpdateHandler.removeCallbacks(timeUpdateRunnable);
        }
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
                Intent intent = new Intent(ClockInOutActivity.this, OfficerActivity.class);
                startActivity(intent);
                finish();
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_edit_profile) {
                Intent intent = new Intent(ClockInOutActivity.this, EditEmployeeProfileActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_clock_in_out) {
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_duty_schedule) {
                Intent intent = new Intent(ClockInOutActivity.this, DutyScheduleActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_notifications) {
                Intent intent = new Intent(ClockInOutActivity.this, NotificationsActivity.class);
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

        Intent intent = new Intent(ClockInOutActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupClickListeners() {
        imageViewMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(navigationView);
        });

        buttonClockAction.setOnClickListener(v -> {
            boolean newState = !isClockedIn;
            saveClockState(newState);
            updateUI();
            
            String message = newState ? "You clocked in" : "You clocked out";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        buttonBack.setOnClickListener(v -> {
            finish();
        });
    }
}

