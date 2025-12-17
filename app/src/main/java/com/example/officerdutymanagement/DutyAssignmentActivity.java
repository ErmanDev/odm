package com.example.officerdutymanagement;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officerdutymanagement.adapter.DutyAssignmentAdapter;
import com.example.officerdutymanagement.model.DutyAssignment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class DutyAssignmentActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDutyAssignments;
    private DutyAssignmentAdapter dutyAssignmentAdapter;
    private List<DutyAssignment> dutyAssignmentList;
    private MaterialButton buttonAssignDuty;

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
        loadSampleData();
        setupClickListeners();
    }

    private void initializeViews() {
        recyclerViewDutyAssignments = findViewById(R.id.recyclerViewDutyAssignments);
        buttonAssignDuty = findViewById(R.id.buttonAssignDuty);
    }

    private void setupRecyclerView() {
        dutyAssignmentList = new ArrayList<>();
        dutyAssignmentAdapter = new DutyAssignmentAdapter(dutyAssignmentList);
        recyclerViewDutyAssignments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDutyAssignments.setAdapter(dutyAssignmentAdapter);
    }

    private void loadSampleData() {
        dutyAssignmentList.clear();
        dutyAssignmentList.add(new DutyAssignment("Jan 20", "John Doe", "Security", "Gate Patrol", "Scheduled"));
        dutyAssignmentList.add(new DutyAssignment("Jan 21", "Mae Monterola", "Logistics", "Warehouse Check", "Pending"));
        dutyAssignmentList.add(new DutyAssignment("Jan 22", "Arthur Lim", "Operations", "Night Shift", "Assigned"));
        dutyAssignmentList.add(new DutyAssignment("Jan 23", "Karen Dela Cruz", "Maintenance", "Facility Sweep", "Scheduled"));
        dutyAssignmentList.add(new DutyAssignment("Jan 24", "Leo Santos", "Security", "Lobby Oversight", "Pending"));
        dutyAssignmentAdapter.notifyDataSetChanged();
    }

    private void setupClickListeners() {
        buttonAssignDuty.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.assign_new_duty_placeholder), Toast.LENGTH_SHORT).show();
        });
    }
}

