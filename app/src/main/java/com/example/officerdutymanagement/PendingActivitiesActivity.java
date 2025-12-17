package com.example.officerdutymanagement;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officerdutymanagement.adapter.PendingActivityAdapter;
import com.example.officerdutymanagement.model.PendingActivity;

import java.util.ArrayList;
import java.util.List;

public class PendingActivitiesActivity extends AppCompatActivity {

    private AutoCompleteTextView departmentFilter;
    private RecyclerView recyclerViewActivities;
    private PendingActivityAdapter activityAdapter;
    private List<PendingActivity> activityList;
    private List<PendingActivity> filteredActivityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pending_activities);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupDepartmentFilter();
        setupRecyclerView();
        loadActivityData();
    }

    private void initializeViews() {
        departmentFilter = findViewById(R.id.autoCompleteDepartment);
        recyclerViewActivities = findViewById(R.id.recyclerViewActivities);
    }

    private void setupDepartmentFilter() {
        String[] departments = {
            getString(R.string.all_department),
            "Sanitation Department",
            "Maintenance Department",
            "Administrative Department",
            "Logistics Department",
            "Health and Safety Department"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            departments
        );

        departmentFilter.setAdapter(adapter);
        departmentFilter.setText(departments[0], false);
        departmentFilter.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDepartment = departments[position];
            filterActivities(selectedDepartment);
        });
    }

    private void setupRecyclerView() {
        filteredActivityList = new ArrayList<>();
        activityAdapter = new PendingActivityAdapter(filteredActivityList);
        recyclerViewActivities.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewActivities.setAdapter(activityAdapter);
    }

    private void loadActivityData() {
        activityList = new ArrayList<>();
        // Sample data based on the image
        activityList.add(new PendingActivity("Juan Dela Cruz", "Sanitation", "Barangay Clean-up", "Pending"));
        activityList.add(new PendingActivity("Joanne Reyes", "Sanitation", "Barangay Clean-up", "Pending"));
        activityList.add(new PendingActivity("Maria Lopez", "Sanitation", "Barangay Clean-up", "On going"));
        activityList.add(new PendingActivity("Leo Garcia", "Sanitation", "Barangay Clean-up", "Pending"));
        activityList.add(new PendingActivity("Angela Rivera", "Sanitation", "Barangay Clean-up", "Pending"));

        filteredActivityList.clear();
        filteredActivityList.addAll(activityList);
        activityAdapter.notifyDataSetChanged();
    }

    private void filterActivities(String department) {
        filteredActivityList.clear();
        if (department.equals(getString(R.string.all_department))) {
            filteredActivityList.addAll(activityList);
        } else {
            String departmentName = department.replace(" Department", "");
            for (PendingActivity activity : activityList) {
                if (activity.getDepartment().equals(departmentName)) {
                    filteredActivityList.add(activity);
                }
            }
        }
        activityAdapter.notifyDataSetChanged();
    }
}

