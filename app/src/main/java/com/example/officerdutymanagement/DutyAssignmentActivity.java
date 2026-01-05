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

import android.app.DatePickerDialog;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.officerdutymanagement.adapter.DutyAssignmentAdapter;
import com.example.officerdutymanagement.model.DutyAssignment;
import com.example.officerdutymanagement.model.Officer;
import com.example.officerdutymanagement.model.User;
import com.example.officerdutymanagement.repository.DutyAssignmentRepository;
import com.example.officerdutymanagement.repository.OfficerRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DutyAssignmentActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDutyAssignments;
    private DutyAssignmentAdapter dutyAssignmentAdapter;
    private List<DutyAssignment> dutyAssignmentList;
    private MaterialButton buttonAssignDuty;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageViewMenu;
    private ImageView imageViewBack;
    private TextView textViewPlaceholder;
    
    private DutyAssignmentRepository dutyAssignmentRepository;
    private OfficerRepository officerRepository;
    private List<Officer> officerList;
    
    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_ADMIN_NAME = "admin_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_duty_assignment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupRecyclerView();
        initializeRepository();
        setupDrawer();
        loadDutyAssignments();
        loadOfficers();
        setupClickListeners();
    }

    private void initializeViews() {
        recyclerViewDutyAssignments = findViewById(R.id.recyclerViewDutyAssignments);
        buttonAssignDuty = findViewById(R.id.buttonAssignDuty);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        imageViewMenu = findViewById(R.id.imageViewMenu);
        imageViewBack = findViewById(R.id.imageViewBack);
        textViewPlaceholder = findViewById(R.id.textViewPlaceholder);
        
        if (imageViewBack != null) {
            imageViewBack.setOnClickListener(v -> finish());
        }
    }

    private void setupRecyclerView() {
        dutyAssignmentList = new ArrayList<>();
        dutyAssignmentAdapter = new DutyAssignmentAdapter(dutyAssignmentList);
        dutyAssignmentAdapter.setOnActionClickListener(new DutyAssignmentAdapter.OnActionClickListener() {
            @Override
            public void onEdit(int position, DutyAssignment assignment) {
                showCreateDutyAssignmentDialog(assignment);
            }

            @Override
            public void onDelete(int position, DutyAssignment assignment) {
                showDeleteConfirmationDialog(assignment);
            }
        });
        recyclerViewDutyAssignments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDutyAssignments.setAdapter(dutyAssignmentAdapter);
    }

    private void initializeRepository() {
        dutyAssignmentRepository = DutyAssignmentRepository.getInstance();
        officerRepository = OfficerRepository.getInstance();
        officerList = new ArrayList<>();
        
        // Load officers list
        officerRepository.getOfficerList().observe(this, officers -> {
            if (officers != null) {
                officerList.clear();
                officerList.addAll(officers);
            }
        });
        
        officerRepository.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        
        dutyAssignmentRepository.getDutyAssignmentList().observe(this, assignments -> {
            if (assignments != null && !assignments.isEmpty()) {
                dutyAssignmentList.clear();
                dutyAssignmentList.addAll(assignments);
                dutyAssignmentAdapter.notifyDataSetChanged();
                if (textViewPlaceholder != null) {
                    textViewPlaceholder.setVisibility(View.GONE);
                }
                recyclerViewDutyAssignments.setVisibility(View.VISIBLE);
            } else {
                dutyAssignmentList.clear();
                dutyAssignmentAdapter.notifyDataSetChanged();
                if (textViewPlaceholder != null) {
                    textViewPlaceholder.setVisibility(View.VISIBLE);
                }
                recyclerViewDutyAssignments.setVisibility(View.GONE);
            }
        });
        
        dutyAssignmentRepository.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        
        dutyAssignmentRepository.getCurrentDutyAssignment().observe(this, assignment -> {
            if (assignment != null) {
                // Refresh list after create/update
                loadDutyAssignments();
            }
        });
    }
    
    private void loadDutyAssignments() {
        dutyAssignmentRepository.getAllDutyAssignments();
    }
    
    private void loadOfficers() {
        officerRepository.getOfficers();
    }

    private void setupClickListeners() {
        buttonAssignDuty.setOnClickListener(v -> {
            showCreateDutyAssignmentDialog(null);
        });
    }
    
    private void showDeleteConfirmationDialog(DutyAssignment assignment) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Duty Assignment")
                .setMessage("Are you sure you want to delete this duty assignment?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dutyAssignmentRepository.deleteDutyAssignment(assignment.getId());
                    loadDutyAssignments();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void showCreateDutyAssignmentDialog(DutyAssignment assignment) {
        // Load officers if not already loaded
        if (officerList.isEmpty()) {
            officerRepository.getOfficers();
        }
        
        // Inflate dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_duty_assignment, null);
        
        // Get dialog views
        TextView textViewDialogTitle = dialogView.findViewById(R.id.textViewDialogTitle);
        TextInputEditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        AutoCompleteTextView autoCompleteOfficer = dialogView.findViewById(R.id.autoCompleteOfficer);
        AutoCompleteTextView autoCompleteDepartment = dialogView.findViewById(R.id.autoCompleteDepartment);
        TextInputEditText editTextTaskLocation = dialogView.findViewById(R.id.editTextTaskLocation);
        AutoCompleteTextView autoCompleteStatus = dialogView.findViewById(R.id.autoCompleteStatus);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        
        // Set dialog title
        if (assignment != null) {
            textViewDialogTitle.setText("Edit Duty Assignment");
        } else {
            textViewDialogTitle.setText("Assign New Duty");
        }
        
        // Setup date picker
        Calendar dateCalendar = Calendar.getInstance();
        if (assignment != null && assignment.getDate() != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                dateCalendar.setTime(inputFormat.parse(assignment.getDate()));
            } catch (Exception e) {
                try {
                    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    dateCalendar.setTime(simpleFormat.parse(assignment.getDate()));
                } catch (Exception ex) {
                    // Keep current date
                }
            }
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        editTextDate.setText(dateFormat.format(dateCalendar.getTime()));
        
        editTextDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        dateCalendar.set(Calendar.YEAR, year);
                        dateCalendar.set(Calendar.MONTH, month);
                        dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        editTextDate.setText(dateFormat.format(dateCalendar.getTime()));
                    },
                    dateCalendar.get(Calendar.YEAR),
                    dateCalendar.get(Calendar.MONTH),
                    dateCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
        
        // Setup officer dropdown
        List<String> officerNames = new ArrayList<>();
        for (Officer officer : officerList) {
            officerNames.add(officer.getDisplayName());
        }
        
        ArrayAdapter<String> officerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                officerNames
        );
        autoCompleteOfficer.setAdapter(officerAdapter);
        
        // Pre-select officer if editing
        if (assignment != null && assignment.getOfficerName() != null) {
            autoCompleteOfficer.setText(assignment.getOfficerName(), false);
        }
        
        // Setup department dropdown
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userRole = prefs.getString("user_role", "ADMIN");
        String userDepartment = prefs.getString("user_department", null);
        
        // Collect unique departments
        java.util.Set<String> departmentSet = new java.util.HashSet<>();
        for (Officer officer : officerList) {
            if (officer.getDepartment() != null && !officer.getDepartment().isEmpty()) {
                // For supervisors, only show their own department
                if ("supervisor".equalsIgnoreCase(userRole) && userDepartment != null) {
                    if (userDepartment.equals(officer.getDepartment())) {
                        departmentSet.add(officer.getDepartment());
                    }
                } else {
                    // For admins, show all departments
                    departmentSet.add(officer.getDepartment());
                }
            }
        }
        
        List<String> departments = new ArrayList<>(departmentSet);
        java.util.Collections.sort(departments);
        
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                departments
        );
        autoCompleteDepartment.setAdapter(departmentAdapter);
        
        // Auto-fill department when officer is selected
        autoCompleteOfficer.setOnItemClickListener((parent, view, position, id) -> {
            String selectedOfficerName = officerNames.get(position);
            for (Officer officer : officerList) {
                if (selectedOfficerName.equals(officer.getDisplayName())) {
                    if (officer.getDepartment() != null) {
                        autoCompleteDepartment.setText(officer.getDepartment(), false);
                    }
                    break;
                }
            }
        });
        
        // Pre-fill department if editing
        if (assignment != null && assignment.getDepartment() != null) {
            autoCompleteDepartment.setText(assignment.getDepartment(), false);
        } else if (departments.size() == 1) {
            // If only one department available, pre-select it
            autoCompleteDepartment.setText(departments.get(0), false);
        }
        
        // Pre-fill task/location if editing
        if (assignment != null && assignment.getTaskLocation() != null) {
            editTextTaskLocation.setText(assignment.getTaskLocation());
        }
        
        // Setup status dropdown
        String[] statuses = {"pending", "ongoing", "completed", "cancelled"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                statuses
        );
        autoCompleteStatus.setAdapter(statusAdapter);
        
        // Pre-select status if editing, otherwise default to "pending"
        if (assignment != null && assignment.getStatus() != null) {
            autoCompleteStatus.setText(assignment.getStatus().toLowerCase(), false);
        } else {
            autoCompleteStatus.setText("pending", false);
        }
        
        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();
        
        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        
        buttonSave.setOnClickListener(v -> {
            // Validate form
            if (editTextDate.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (autoCompleteOfficer.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please select an officer", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (autoCompleteDepartment.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please select a department", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (editTextTaskLocation.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter task/location", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (autoCompleteStatus.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Find selected officer
            Officer selectedOfficer = null;
            String selectedOfficerName = autoCompleteOfficer.getText().toString().trim();
            for (Officer officer : officerList) {
                if (selectedOfficerName.equals(officer.getDisplayName())) {
                    selectedOfficer = officer;
                    break;
                }
            }
            
            if (selectedOfficer == null) {
                Toast.makeText(this, "Invalid officer selected", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get userId from officer (prefer userId, fallback to id)
            Integer userId = selectedOfficer.getUserId();
            if (userId == null && selectedOfficer.getId() != null) {
                userId = selectedOfficer.getId();
            }
            
            if (userId == null) {
                Toast.makeText(this, "Officer ID not found", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Create or update assignment
            DutyAssignment dutyAssignment = assignment != null ? assignment : new DutyAssignment();
            dutyAssignment.setUserId(userId);
            dutyAssignment.setDate(formatDateForBackend(dateCalendar.getTime()));
            dutyAssignment.setDepartment(autoCompleteDepartment.getText().toString().trim());
            dutyAssignment.setTaskLocation(editTextTaskLocation.getText().toString().trim());
            dutyAssignment.setStatus(autoCompleteStatus.getText().toString().trim().toLowerCase());
            
            if (assignment != null) {
                // Update existing assignment
                dutyAssignmentRepository.updateDutyAssignment(assignment.getId(), dutyAssignment);
            } else {
                // Create new assignment
                dutyAssignmentRepository.createDutyAssignment(dutyAssignment);
            }
            
            dialog.dismiss();
        });
        
        dialog.show();
    }
    
    private String formatDateForBackend(java.util.Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        return format.format(date);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadDutyAssignments();
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
                // Already on this screen, just close drawer
            } else if (itemId == R.id.nav_pending_activities) {
                startActivity(new Intent(this, PendingActivitiesActivity.class));
                finish();
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

