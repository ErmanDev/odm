package com.example.officerdutymanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class EditEmployeeProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_ADMIN_NAME = "admin_name";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageViewMenu;
    private ImageView imageViewProfile;
    private TextView textViewEmployeeName;
    private TextView textViewEmployeeId;
    private EditText editTextName;
    private EditText editTextAge;
    private EditText editTextEmployeeId;
    private EditText editTextPosition;
    private EditText editTextDepartment;
    private EditText editTextEmail;
    private EditText editTextAddress;
    private EditText editTextPhone;
    private Button buttonSaveChanges;
    private boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_employee_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        loadEmployeeData();
        setupDrawer();
        setupClickListeners();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        imageViewMenu = findViewById(R.id.imageViewMenu);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        textViewEmployeeName = findViewById(R.id.textViewEmployeeName);
        textViewEmployeeId = findViewById(R.id.textViewEmployeeId);
        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        editTextEmployeeId = findViewById(R.id.editTextEmployeeId);
        editTextPosition = findViewById(R.id.editTextPosition);
        editTextDepartment = findViewById(R.id.editTextDepartment);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
    }

    private void loadEmployeeData() {
        // Sample data - in real app, this would come from intent or database
        textViewEmployeeName.setText("Juan Dela Cruz");
        textViewEmployeeId.setText("ID-ACC-0098");
        
        editTextName.setText("Juan Dela Cruz");
        editTextAge.setText("22");
        editTextEmployeeId.setText("ACC-0098");
        editTextPosition.setText("Garbage Collector");
        editTextDepartment.setText("Sanitation");
        editTextEmail.setText("mahasau@gmail.com");
        editTextAddress.setText("P- 13 Hagkol Valencia");
        editTextPhone.setText("09542518340");
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
                Intent intent = new Intent(EditEmployeeProfileActivity.this, OfficerActivity.class);
                startActivity(intent);
                finish();
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_edit_profile) {
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_clock_in_out) {
                Intent intent = new Intent(EditEmployeeProfileActivity.this, ClockInOutActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_duty_schedule) {
                Intent intent = new Intent(EditEmployeeProfileActivity.this, DutyScheduleActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (itemId == R.id.nav_notifications) {
                Intent intent = new Intent(EditEmployeeProfileActivity.this, NotificationsActivity.class);
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

        Intent intent = new Intent(EditEmployeeProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupClickListeners() {
        imageViewMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(navigationView);
        });

        buttonSaveChanges.setOnClickListener(v -> {
            if (!isSaved) {
                // Save changes - disable all inputs and change button text
                disableAllInputs();
                buttonSaveChanges.setText("Done");
                isSaved = true;
                // TODO: Save changes to database/API
            } else {
                // Done button clicked - go back
                finish();
            }
        });
    }

    private void disableAllInputs() {
        editTextName.setEnabled(false);
        editTextAge.setEnabled(false);
        editTextEmployeeId.setEnabled(false);
        editTextPosition.setEnabled(false);
        editTextDepartment.setEnabled(false);
        editTextEmail.setEnabled(false);
        editTextAddress.setEnabled(false);
        editTextPhone.setEnabled(false);
    }
}

