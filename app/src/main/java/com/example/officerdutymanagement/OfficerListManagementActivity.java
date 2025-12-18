package com.example.officerdutymanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import com.example.officerdutymanagement.adapter.OfficerAdapter;
import com.example.officerdutymanagement.adapter.OfficerNameAdapter;
import com.example.officerdutymanagement.model.Officer;
import com.example.officerdutymanagement.repository.OfficerRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OfficerListManagementActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_USER_ROLE = "user_role";

    // Admin views
    private AutoCompleteTextView departmentFilter;
    
    // Officer views
    private EditText editTextSearch;
    private Button buttonAddName;
    private TextView textViewCategoryTitle;
    
    // Common views
    private RecyclerView recyclerViewOfficers;
    private OfficerAdapter officerAdapter;
    private OfficerNameAdapter officerNameAdapter;
    private List<Officer> officerList;
    private List<Officer> filteredOfficerList;
    private List<String> officerNameList;
    private List<String> filteredOfficerNameList;
    
    private OfficerRepository officerRepository;
    private boolean isOfficerView = false;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageViewMenu;
    private ImageView imageViewBack;
    
    private static final String PREFS_NAME_ADMIN = "OfficerDutyPrefs";
    private static final String KEY_ADMIN_NAME = "admin_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        // Check user role to determine which layout to use
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userRole = prefs.getString(KEY_USER_ROLE, "ADMIN");
        isOfficerView = "OFFICER".equals(userRole);
        
        setContentView(isOfficerView ? 
            R.layout.activity_officer_list_management_officer : 
            R.layout.activity_officer_list_management);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        initializeRepository();
        if (isOfficerView) {
            setupOfficerView();
        } else {
            setupAdminView();
            setupDrawer();
        }
    }

    private void initializeViews() {
        recyclerViewOfficers = findViewById(R.id.recyclerViewOfficers);
        
        if (isOfficerView) {
            editTextSearch = findViewById(R.id.editTextSearch);
            buttonAddName = findViewById(R.id.buttonAddName);
            textViewCategoryTitle = findViewById(R.id.textViewCategoryTitle);
        } else {
            departmentFilter = findViewById(R.id.autoCompleteDepartment);
            drawerLayout = findViewById(R.id.drawerLayout);
            navigationView = findViewById(R.id.navigationView);
            imageViewMenu = findViewById(R.id.imageViewMenu);
            imageViewBack = findViewById(R.id.imageViewBack);
            
            if (imageViewBack != null) {
                imageViewBack.setOnClickListener(v -> finish());
            }
        }
    }

    private void setupOfficerView() {
        setupOfficerRecyclerView();
        setupSearchFunctionality();
        setupAddNameButton();
        loadOfficerNameData();
    }

    private void initializeRepository() {
        officerRepository = OfficerRepository.getInstance();
        
        // Observe officer list changes
        officerRepository.getOfficerList().observe(this, officers -> {
            if (officers != null) {
                officerList = officers;
                if (isOfficerView) {
                    updateOfficerNameList();
                } else {
                    updateFilteredOfficerList();
                    updateDepartmentFilter();
                }
            }
        });
        
        // Observe error messages
        officerRepository.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                android.widget.Toast.makeText(this, errorMessage, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdminView() {
        setupRecyclerView();
        // Initialize department filter with empty list, will be updated when data loads
        setupDepartmentFilterInitial();
        loadOfficerData();
    }
    
    private void setupDepartmentFilterInitial() {
        // Set up with just "All Department" initially
        String[] departments = { getString(R.string.all_department) };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            departments
        );
        departmentFilter.setAdapter(adapter);
        departmentFilter.setText(departments[0], false);
    }

    private void setupSearchFunctionality() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOfficerNames(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupAddNameButton() {
        buttonAddName.setOnClickListener(v -> {
            // TODO: Implement add name functionality
            android.widget.Toast.makeText(this, "Add Name functionality will be implemented", 
                android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    private void setupOfficerRecyclerView() {
        filteredOfficerNameList = new ArrayList<>();
        officerNameAdapter = new OfficerNameAdapter(filteredOfficerNameList);
        recyclerViewOfficers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOfficers.setAdapter(officerNameAdapter);
    }

    private void loadOfficerNameData() {
        officerRepository.getOfficers();
    }
    
    private void updateOfficerNameList() {
        if (officerList == null) {
            return;
        }
        
        officerNameList = new ArrayList<>();
        for (Officer officer : officerList) {
            if (officer.getName() != null && !officer.getName().isEmpty()) {
                officerNameList.add(officer.getName());
            }
        }
        
        filteredOfficerNameList.clear();
        filteredOfficerNameList.addAll(officerNameList);
        officerNameAdapter.notifyDataSetChanged();
    }

    private void filterOfficerNames(String searchQuery) {
        filteredOfficerNameList.clear();
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            filteredOfficerNameList.addAll(officerNameList);
        } else {
            String query = searchQuery.toLowerCase().trim();
            for (String name : officerNameList) {
                if (name.toLowerCase().contains(query)) {
                    filteredOfficerNameList.add(name);
                }
            }
        }
        officerNameAdapter.notifyDataSetChanged();
    }

    private void updateDepartmentFilter() {
        if (officerList == null || officerList.isEmpty()) {
            return;
        }
        
        // Extract unique departments from officer list
        Set<String> departmentSet = new HashSet<>();
        for (Officer officer : officerList) {
            if (officer.getDepartment() != null && !officer.getDepartment().isEmpty()) {
                departmentSet.add(officer.getDepartment());
            }
        }
        
        // Create departments array with "All Department" first
        List<String> departmentsList = new ArrayList<>();
        departmentsList.add(getString(R.string.all_department));
        departmentsList.addAll(new ArrayList<>(departmentSet));
        
        String[] departments = departmentsList.toArray(new String[0]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            departments
        );

        departmentFilter.setAdapter(adapter);
        departmentFilter.setText(departments[0], false);
        departmentFilter.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDepartment = departments[position];
            filterOfficers(selectedDepartment);
        });
    }

    private void setupRecyclerView() {
        filteredOfficerList = new ArrayList<>();
        officerAdapter = new OfficerAdapter(filteredOfficerList);
        recyclerViewOfficers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOfficers.setAdapter(officerAdapter);
    }

    private void loadOfficerData() {
        officerRepository.getOfficers();
    }
    
    private void updateFilteredOfficerList() {
        if (officerList == null) {
            return;
        }
        
        filteredOfficerList.clear();
        filteredOfficerList.addAll(officerList);
        officerAdapter.updateOfficerList(filteredOfficerList);
    }

    private void filterOfficers(String department) {
        if (officerList == null) {
            return;
        }
        
        filteredOfficerList.clear();
        if (department.equals(getString(R.string.all_department))) {
            filteredOfficerList.addAll(officerList);
        } else {
            for (Officer officer : officerList) {
                if (officer.getDepartment() != null && officer.getDepartment().equals(department)) {
                    filteredOfficerList.add(officer);
                }
            }
        }
        officerAdapter.updateOfficerList(filteredOfficerList);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh officer data when activity resumes
        officerRepository.getOfficers();
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
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME_ADMIN, MODE_PRIVATE);
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
                // Already on this screen, just close drawer
            } else if (itemId == R.id.nav_duty_assignment) {
                startActivity(new Intent(this, DutyAssignmentActivity.class));
                finish();
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
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME_ADMIN, MODE_PRIVATE);
                prefs.edit().clear().apply();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            return true;
        });
    }
}

