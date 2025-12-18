package com.example.officerdutymanagement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officerdutymanagement.adapter.AttendanceAdapter;
import com.example.officerdutymanagement.model.Attendance;
import com.example.officerdutymanagement.repository.AttendanceRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AttendanceTrackingActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "OfficerDutyPrefs";
    private static final String KEY_USER_ROLE = "user_role";

    private RecyclerView recyclerViewAttendance;
    private AttendanceAdapter attendanceAdapter;
    private AttendanceRepository attendanceRepository;
    private TextView textViewPlaceholder;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendance_tracking);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        checkUserRole();
        setupRecyclerView();
        initializeRepository();
        loadAttendance();
    }

    private void initializeViews() {
        recyclerViewAttendance = findViewById(R.id.recyclerViewAttendance);
        textViewPlaceholder = findViewById(R.id.textViewPlaceholder);
    }

    private void checkUserRole() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userRole = prefs.getString(KEY_USER_ROLE, "admin");
        isAdmin = "admin".equalsIgnoreCase(userRole);
    }

    private void setupRecyclerView() {
        attendanceAdapter = new AttendanceAdapter(new ArrayList<>());
        recyclerViewAttendance.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAttendance.setAdapter(attendanceAdapter);
    }

    private void initializeRepository() {
        attendanceRepository = AttendanceRepository.getInstance();
        
        attendanceRepository.getAttendanceList().observe(this, attendanceList -> {
            if (attendanceList != null && !attendanceList.isEmpty()) {
                attendanceAdapter.updateAttendanceList(attendanceList);
                if (textViewPlaceholder != null) {
                    textViewPlaceholder.setVisibility(android.view.View.GONE);
                }
                recyclerViewAttendance.setVisibility(android.view.View.VISIBLE);
            } else {
                if (textViewPlaceholder != null) {
                    textViewPlaceholder.setVisibility(android.view.View.VISIBLE);
                }
                recyclerViewAttendance.setVisibility(android.view.View.GONE);
            }
        });

        attendanceRepository.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAttendance() {
        // Load attendance for the current month by default
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        // Start of month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = dateFormat.format(calendar.getTime());
        
        // End of month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = dateFormat.format(calendar.getTime());

        if (isAdmin) {
            attendanceRepository.getAllAttendance(startDate, endDate, null, null);
        } else {
            attendanceRepository.getMyAttendance(startDate, endDate);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAttendance();
    }
}


