package com.example.officerdutymanagement;

import android.app.AlertDialog;
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
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officerdutymanagement.adapter.AbsenceRequestAdapter;
import com.example.officerdutymanagement.model.AbsenceRequest;
import com.example.officerdutymanagement.repository.AbsenceRequestRepository;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class AbsenceRequestsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_ADMIN_NAME = "admin_name";

    private RecyclerView recyclerViewAbsenceRequests;
    private AbsenceRequestAdapter absenceRequestAdapter;
    private AbsenceRequestRepository absenceRequestRepository;
    private TextView textViewPlaceholder;
    private AutoCompleteTextView autoCompleteStatus;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageViewMenu;
    private ImageView imageViewBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_absence_requests);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupDrawer();
        setupStatusFilter();
        setupRecyclerView();
        initializeRepository();
        loadAbsenceRequests(null);
    }

    private void initializeViews() {
        recyclerViewAbsenceRequests = findViewById(R.id.recyclerViewAbsenceRequests);
        textViewPlaceholder = findViewById(R.id.textViewPlaceholder);
        autoCompleteStatus = findViewById(R.id.autoCompleteStatus);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        imageViewMenu = findViewById(R.id.imageViewMenu);
        imageViewBack = findViewById(R.id.imageViewBack);

        if (imageViewBack != null) {
            imageViewBack.setOnClickListener(v -> finish());
        }
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
                startActivity(new Intent(this, PendingActivitiesActivity.class));
                finish();
            } else if (itemId == R.id.nav_attendance_tracking) {
                startActivity(new Intent(this, AttendanceTrackingActivity.class));
                finish();
            } else if (itemId == R.id.nav_notifications) {
                startActivity(new Intent(this, AdminNotificationsActivity.class));
                finish();
            } else if (itemId == R.id.nav_absence_requests) {
                // Already on this screen, just close drawer
            } else if (itemId == R.id.nav_logout) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                prefs.edit().clear().apply();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            return true;
        });
    }

    private void setupStatusFilter() {
        String[] statuses = {"All", "Pending", "Approved", "Rejected"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            statuses
        );

        autoCompleteStatus.setAdapter(adapter);
        autoCompleteStatus.setText(statuses[1], false); // Default to "Pending"
        autoCompleteStatus.setOnItemClickListener((parent, view, position, id) -> {
            String selectedStatus = statuses[position];
            String filterStatus = "All".equals(selectedStatus) ? null : selectedStatus.toLowerCase();
            loadAbsenceRequests(filterStatus);
        });
    }

    private void setupRecyclerView() {
        absenceRequestAdapter = new AbsenceRequestAdapter(new ArrayList<>());
        absenceRequestAdapter.setOnActionClickListener(new AbsenceRequestAdapter.OnActionClickListener() {
            @Override
            public void onApprove(int position, AbsenceRequest absenceRequest) {
                showConfirmDialog(absenceRequest, true);
            }

            @Override
            public void onReject(int position, AbsenceRequest absenceRequest) {
                showConfirmDialog(absenceRequest, false);
            }
        });
        recyclerViewAbsenceRequests.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAbsenceRequests.setAdapter(absenceRequestAdapter);
    }

    private void initializeRepository() {
        absenceRequestRepository = AbsenceRequestRepository.getInstance();

        absenceRequestRepository.getAbsenceRequestList().observe(this, absenceRequestList -> {
            if (absenceRequestList != null && !absenceRequestList.isEmpty()) {
                absenceRequestAdapter.updateAbsenceRequestList(absenceRequestList);
                if (textViewPlaceholder != null) {
                    textViewPlaceholder.setVisibility(View.GONE);
                }
                recyclerViewAbsenceRequests.setVisibility(View.VISIBLE);
            } else {
                if (textViewPlaceholder != null) {
                    textViewPlaceholder.setVisibility(View.VISIBLE);
                }
                recyclerViewAbsenceRequests.setVisibility(View.GONE);
            }
        });

        absenceRequestRepository.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        absenceRequestRepository.getCurrentAbsenceRequest().observe(this, absenceRequest -> {
            if (absenceRequest != null) {
                // Refresh the list after status update
                String selectedStatus = autoCompleteStatus.getText().toString();
                String filterStatus = "All".equals(selectedStatus) ? null : selectedStatus.toLowerCase();
                loadAbsenceRequests(filterStatus);
            }
        });
    }

    private void loadAbsenceRequests(String status) {
        absenceRequestRepository.getAllAbsenceRequests(status);
    }

    private void showConfirmDialog(AbsenceRequest absenceRequest, boolean isApprove) {
        String action = isApprove ? "approve" : "reject";
        String message = "Are you sure you want to " + action + " this absence request?";

        new AlertDialog.Builder(this)
            .setTitle("Confirm Action")
            .setMessage(message)
            .setPositiveButton(isApprove ? "Approve" : "Reject", (dialog, which) -> {
                String status = isApprove ? "approved" : "rejected";
                if (absenceRequest.getId() != null) {
                    absenceRequestRepository.updateAbsenceRequestStatus(absenceRequest.getId(), status);
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String selectedStatus = autoCompleteStatus.getText().toString();
        String filterStatus = "All".equals(selectedStatus) ? null : selectedStatus.toLowerCase();
        loadAbsenceRequests(filterStatus);
    }
}

