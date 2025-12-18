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
import com.example.officerdutymanagement.adapter.OfficerAbsenceRequestAdapter;
import com.example.officerdutymanagement.adapter.OfficerDutyAssignmentAdapter;
import com.example.officerdutymanagement.model.AbsenceRequest;
import com.example.officerdutymanagement.model.DutyAssignment;
import com.example.officerdutymanagement.model.DutySchedule;
import com.example.officerdutymanagement.model.Notification;
import com.example.officerdutymanagement.model.OngoingActivity;
import com.example.officerdutymanagement.repository.AbsenceRequestRepository;
import com.example.officerdutymanagement.repository.DutyAssignmentRepository;

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

    // Duty Schedule
    private RecyclerView recyclerViewDutySchedule;
    private DutyScheduleAdapter dutyScheduleAdapter;
    private List<DutySchedule> dutyScheduleList;
    private TextView textViewViewFullSchedule;

    // Request Absence
    private EditText editTextAbsenceStartDate;
    private EditText editTextAbsenceEndDate;
    private EditText editTextAbsenceReason;
    private Button buttonSubmitAbsenceRequest;
    private AbsenceRequestRepository absenceRequestRepository;
    private Calendar startDateCalendar;
    private Calendar endDateCalendar;

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

    // My Absence Requests
    private RecyclerView recyclerViewAbsenceRequests;
    private OfficerAbsenceRequestAdapter officerAbsenceRequestAdapter;
    private List<AbsenceRequest> absenceRequestList;
    private TextView textViewNoAbsenceRequests;

    // My Assignments
    private RecyclerView recyclerViewMyAssignments;
    private OfficerDutyAssignmentAdapter officerDutyAssignmentAdapter;
    private List<DutyAssignment> myAssignmentList;
    private TextView textViewNoAssignments;
    private DutyAssignmentRepository dutyAssignmentRepository;

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
        initializeRepository();
        setupUserInfo();
        setupRecyclerViews();
        initializeDutyAssignmentRepository();
        loadData();
        loadAbsenceRequests();
        loadMyAssignments();
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

        // Duty Schedule
        recyclerViewDutySchedule = findViewById(R.id.recyclerViewDutySchedule);
        textViewViewFullSchedule = findViewById(R.id.textViewViewFullSchedule);

        // Request Absence
        editTextAbsenceStartDate = findViewById(R.id.editTextAbsenceStartDate);
        editTextAbsenceEndDate = findViewById(R.id.editTextAbsenceEndDate);
        editTextAbsenceReason = findViewById(R.id.editTextAbsenceReason);
        buttonSubmitAbsenceRequest = findViewById(R.id.buttonSubmitAbsenceRequest);
        
        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();

        // Notifications
        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        buttonSeeMoreNotifications = findViewById(R.id.buttonSeeMoreNotifications);

        // Ongoing Activities
        recyclerViewActivities = findViewById(R.id.recyclerViewActivities);
        buttonSeeMoreActivities = findViewById(R.id.buttonSeeMoreActivities);

        // My Absence Requests
        recyclerViewAbsenceRequests = findViewById(R.id.recyclerViewAbsenceRequests);
        textViewNoAbsenceRequests = findViewById(R.id.textViewNoAbsenceRequests);

        // My Assignments
        recyclerViewMyAssignments = findViewById(R.id.recyclerViewMyAssignments);
        textViewNoAssignments = findViewById(R.id.textViewNoAssignments);
    }

    private void initializeRepository() {
        absenceRequestRepository = AbsenceRequestRepository.getInstance();
        
        // Observe absence request creation
        absenceRequestRepository.getCurrentAbsenceRequest().observe(this, absenceRequest -> {
            if (absenceRequest != null) {
                Toast.makeText(this, "Absence request submitted successfully", Toast.LENGTH_SHORT).show();
                // Clear form
                editTextAbsenceStartDate.setText("");
                editTextAbsenceEndDate.setText("");
                editTextAbsenceReason.setText("");
                startDateCalendar = Calendar.getInstance();
                endDateCalendar = Calendar.getInstance();
                // Refresh absence requests list
                loadAbsenceRequests();
            }
        });
        
        // Observe absence requests list
        absenceRequestRepository.getAbsenceRequestList().observe(this, absenceRequests -> {
            if (absenceRequests != null && !absenceRequests.isEmpty()) {
                // Limit to 5 most recent requests for dashboard view
                List<AbsenceRequest> recentRequests = absenceRequests.size() > 5 
                    ? absenceRequests.subList(0, 5) 
                    : absenceRequests;
                absenceRequestList = recentRequests;
                officerAbsenceRequestAdapter.updateAbsenceRequestList(absenceRequestList);
                if (textViewNoAbsenceRequests != null) {
                    textViewNoAbsenceRequests.setVisibility(View.GONE);
                }
                if (recyclerViewAbsenceRequests != null) {
                    recyclerViewAbsenceRequests.setVisibility(View.VISIBLE);
                }
            } else {
                absenceRequestList = new ArrayList<>();
                officerAbsenceRequestAdapter.updateAbsenceRequestList(absenceRequestList);
                if (textViewNoAbsenceRequests != null) {
                    textViewNoAbsenceRequests.setVisibility(View.VISIBLE);
                }
                if (recyclerViewAbsenceRequests != null) {
                    recyclerViewAbsenceRequests.setVisibility(View.GONE);
                }
            }
        });
        
        // Observe error messages
        absenceRequestRepository.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
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

        // My Absence Requests RecyclerView
        absenceRequestList = new ArrayList<>();
        officerAbsenceRequestAdapter = new OfficerAbsenceRequestAdapter(absenceRequestList);
        recyclerViewAbsenceRequests.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAbsenceRequests.setAdapter(officerAbsenceRequestAdapter);

        // My Assignments RecyclerView
        myAssignmentList = new ArrayList<>();
        officerDutyAssignmentAdapter = new OfficerDutyAssignmentAdapter(myAssignmentList);
        recyclerViewMyAssignments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMyAssignments.setAdapter(officerDutyAssignmentAdapter);
    }

    private void initializeDutyAssignmentRepository() {
        dutyAssignmentRepository = DutyAssignmentRepository.getInstance();
        
        dutyAssignmentRepository.getDutyAssignmentList().observe(this, assignments -> {
            if (assignments != null && !assignments.isEmpty()) {
                // Limit to 5 most recent assignments for dashboard view
                List<DutyAssignment> recentAssignments = assignments.size() > 5 
                    ? assignments.subList(0, 5) 
                    : assignments;
                myAssignmentList = recentAssignments;
                officerDutyAssignmentAdapter.updateDutyAssignmentList(myAssignmentList);
                if (textViewNoAssignments != null) {
                    textViewNoAssignments.setVisibility(View.GONE);
                }
                if (recyclerViewMyAssignments != null) {
                    recyclerViewMyAssignments.setVisibility(View.VISIBLE);
                }
            } else {
                myAssignmentList = new ArrayList<>();
                officerDutyAssignmentAdapter.updateDutyAssignmentList(myAssignmentList);
                if (textViewNoAssignments != null) {
                    textViewNoAssignments.setVisibility(View.VISIBLE);
                }
                if (recyclerViewMyAssignments != null) {
                    recyclerViewMyAssignments.setVisibility(View.GONE);
                }
            }
        });
        
        dutyAssignmentRepository.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMyAssignments() {
        dutyAssignmentRepository.getMyDutyAssignments();
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

        textViewViewFullSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(OfficerActivity.this, DutyScheduleActivity.class);
            startActivity(intent);
        });

        editTextAbsenceStartDate.setOnClickListener(v -> showStartDatePicker());
        editTextAbsenceEndDate.setOnClickListener(v -> showEndDatePicker());
        
        buttonSubmitAbsenceRequest.setOnClickListener(v -> submitAbsenceRequest());

        buttonSeeMoreNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(OfficerActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        buttonSeeMoreActivities.setOnClickListener(v -> {
            Intent intent = new Intent(OfficerActivity.this, OngoingActivitiesActivity.class);
            startActivity(intent);
        });
    }

    private void showStartDatePicker() {
        android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                startDateCalendar.set(Calendar.YEAR, year);
                startDateCalendar.set(Calendar.MONTH, month);
                startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                editTextAbsenceStartDate.setText(dateFormat.format(startDateCalendar.getTime()));
            },
            startDateCalendar.get(Calendar.YEAR),
            startDateCalendar.get(Calendar.MONTH),
            startDateCalendar.get(Calendar.DAY_OF_MONTH)
        );
        // Prevent selecting past dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showEndDatePicker() {
        android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                endDateCalendar.set(Calendar.YEAR, year);
                endDateCalendar.set(Calendar.MONTH, month);
                endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                editTextAbsenceEndDate.setText(dateFormat.format(endDateCalendar.getTime()));
            },
            endDateCalendar.get(Calendar.YEAR),
            endDateCalendar.get(Calendar.MONTH),
            endDateCalendar.get(Calendar.DAY_OF_MONTH)
        );
        // Set minimum date to start date if it's already selected
        if (editTextAbsenceStartDate.getText().toString().isEmpty()) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        } else {
            datePickerDialog.getDatePicker().setMinDate(startDateCalendar.getTimeInMillis());
        }
        datePickerDialog.show();
    }

    private void submitAbsenceRequest() {
        // Validate start date
        if (editTextAbsenceStartDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select a start date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate end date
        if (editTextAbsenceEndDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select an end date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate dates (start <= end)
        if (startDateCalendar.getTimeInMillis() > endDateCalendar.getTimeInMillis()) {
            Toast.makeText(this, "Start date must be before or equal to end date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate that start date is not in the past
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        Calendar startDate = (Calendar) startDateCalendar.clone();
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);
        
        if (startDate.getTimeInMillis() < today.getTimeInMillis()) {
            Toast.makeText(this, "Start date cannot be in the past", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate reason
        String reason = editTextAbsenceReason.getText().toString().trim();
        if (reason.isEmpty()) {
            Toast.makeText(this, "Please provide a reason", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create absence request
        AbsenceRequest absenceRequest = new AbsenceRequest();
        absenceRequest.setStartDate(startDateCalendar.getTime());
        absenceRequest.setEndDate(endDateCalendar.getTime());
        absenceRequest.setReason(reason);

        // Submit request
        absenceRequestRepository.createAbsenceRequest(absenceRequest);
    }

    private void loadAbsenceRequests() {
        absenceRequestRepository.getMyAbsenceRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh absence requests and assignments when activity resumes
        loadAbsenceRequests();
        loadMyAssignments();
    }
}
