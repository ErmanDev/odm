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
import androidx.lifecycle.Observer;

import com.example.officerdutymanagement.model.Officer;
import com.example.officerdutymanagement.model.User;
import com.example.officerdutymanagement.repository.AuthRepository;
import com.example.officerdutymanagement.repository.OfficerRepository;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.List;

public class EditEmployeeProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_ADMIN_NAME = "admin_name";
    public static final String EXTRA_OFFICER_ID = "officer_id";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageViewMenu;
    private ImageView imageViewProfile;
    private TextView textViewEmployeeName;
    private TextView textViewEmployeeId;
    private EditText editTextName;
    private EditText editTextDepartment;
    private Button buttonSaveChanges;
    
    private OfficerRepository officerRepository;
    private AuthRepository authRepository;
    private Officer currentOfficer;
    private Integer officerId;
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
        initializeRepository();
        checkOfficerIdFromIntent();
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
        editTextDepartment = findViewById(R.id.editTextDepartment);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
    }

    private void initializeRepository() {
        officerRepository = OfficerRepository.getInstance();
        authRepository = new AuthRepository(this);
        
        // Observe current officer
        officerRepository.getCurrentOfficer().observe(this, officer -> {
            if (officer != null) {
                currentOfficer = officer;
                if (officerId == null) {
                    // Initial load - set officerId
                    officerId = officer.getId();
                }
                loadOfficerDataIntoUI(officer);
                
                // If we just saved and this is the updated officer, disable inputs
                if (isSaved && officerId != null && officer.getId() != null && 
                    officer.getId().equals(officerId)) {
                    disableAllInputs();
                    buttonSaveChanges.setText("Done");
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        // Observe error messages
        officerRepository.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                // If save failed, reset isSaved flag
                if (isSaved) {
                    isSaved = false;
                    buttonSaveChanges.setText("Save Changes");
                }
            }
        });
        
        // Observe loading state
        officerRepository.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                buttonSaveChanges.setEnabled(!isLoading);
                if (isLoading) {
                    buttonSaveChanges.setText("Saving...");
                } else {
                    // Loading finished
                    if (!isSaved) {
                        buttonSaveChanges.setText("Save Changes");
                    } else {
                        buttonSaveChanges.setText("Done");
                    }
                }
            }
        });
    }

    private void checkOfficerIdFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_OFFICER_ID)) {
            // Officer ID provided - load that officer
            officerId = intent.getIntExtra(EXTRA_OFFICER_ID, -1);
            if (officerId > 0) {
                officerRepository.getOfficerById(officerId);
            }
        } else {
            // No officer ID - load logged-in user's officer profile
            loadMyOfficerProfile();
        }
    }

    private void loadMyOfficerProfile() {
        // Get logged-in user first
        authRepository.getMe(new AuthRepository.GetMeCallback() {
            @Override
            public void onSuccess(User user) {
                if (user != null) {
                    // Get all officers and find the one with matching userId
                    officerRepository.getOfficers();
                    officerRepository.getOfficerList().observe(EditEmployeeProfileActivity.this, new Observer<List<Officer>>() {
                        @Override
                        public void onChanged(List<Officer> officers) {
                            if (officers != null) {
                                // Find officer with matching userId
                                for (Officer officer : officers) {
                                    if (officer.getUserId() != null && officer.getUserId().equals(getUserIdFromUser(user))) {
                                        officerId = officer.getId();
                                        currentOfficer = officer;
                                        loadOfficerDataIntoUI(officer);
                                        // Remove observer to avoid multiple calls
                                        officerRepository.getOfficerList().removeObserver(this);
                                        break;
                                    }
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(EditEmployeeProfileActivity.this, "Failed to load user: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Integer getUserIdFromUser(User user) {
        return user != null ? user.getId() : null;
    }

    private void loadOfficerDataIntoUI(Officer officer) {
        if (officer == null) {
            return;
        }
        
        textViewEmployeeName.setText(officer.getName() != null ? officer.getName() : "");
        if (officer.getId() != null) {
            textViewEmployeeId.setText("ID: " + officer.getId());
        } else {
            textViewEmployeeId.setText("");
        }
        
        editTextName.setText(officer.getName() != null ? officer.getName() : "");
        editTextDepartment.setText(officer.getDepartment() != null ? officer.getDepartment() : "");
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
                // Save changes to API
                saveOfficerProfile();
            } else {
                // Done button clicked - go back
                finish();
            }
        });
    }

    private void saveOfficerProfile() {
        if (officerId == null || officerId <= 0) {
            Toast.makeText(this, "Invalid officer ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = editTextName.getText().toString().trim();
        String department = editTextDepartment.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Name is required");
            return;
        }

        if (department.isEmpty()) {
            editTextDepartment.setError("Department is required");
            return;
        }

        Officer updatedOfficer = new Officer(name, department);
        updatedOfficer.setId(officerId);
        if (currentOfficer != null && currentOfficer.getUserId() != null) {
            updatedOfficer.setUserId(currentOfficer.getUserId());
        }

        // Mark as saved before API call - observer will handle UI update on success
        isSaved = true;
        officerRepository.updateOfficer(officerId, updatedOfficer);
    }

    private void disableAllInputs() {
        editTextName.setEnabled(false);
        editTextDepartment.setEnabled(false);
    }
}

