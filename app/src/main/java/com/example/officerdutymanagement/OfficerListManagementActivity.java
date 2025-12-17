package com.example.officerdutymanagement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officerdutymanagement.adapter.OfficerAdapter;
import com.example.officerdutymanagement.adapter.OfficerNameAdapter;
import com.example.officerdutymanagement.model.Officer;

import java.util.ArrayList;
import java.util.List;

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
    
    private boolean isOfficerView = false;

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
        if (isOfficerView) {
            setupOfficerView();
        } else {
            setupAdminView();
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
        }
    }

    private void setupOfficerView() {
        setupOfficerRecyclerView();
        setupSearchFunctionality();
        setupAddNameButton();
        loadOfficerNameData();
    }

    private void setupAdminView() {
        setupDepartmentFilter();
        setupRecyclerView();
        loadOfficerData();
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
        // Sample data based on the image - Sanitation Office officers
        officerNameList = new ArrayList<>();
        officerNameList.add("James Villanueva");
        officerNameList.add("Juan Dela Cruz");
        officerNameList.add("Juan Dela Cruz");
        officerNameList.add("Maria Lopez");
        officerNameList.add("Paul Ramirez");
        officerNameList.add("Ella Cruz");
        officerNameList.add("James Villanueva");
        officerNameList.add("Carla Domingo");
        officerNameList.add("Rico Santos");
        officerNameList.add("Leo Garcia");
        officerNameList.add("Joanne Reyes");
        officerNameList.add("Mark Castillo");
        officerNameList.add("Angela Rivera");

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
        officerList = new ArrayList<>();
        // Sample data based on the image
        officerList.add(new Officer("Juan Dela Cruz", "Maintenance Department"));
        officerList.add(new Officer("Maria Lopez", "Administrative Department"));
        officerList.add(new Officer("Leo Garcia", "Maintenance Department"));
        officerList.add(new Officer("Angela Rivera", "Maintenance Department"));
        officerList.add(new Officer("Rico Santos", "Logistics Department"));
        officerList.add(new Officer("Rico Santos", "Maintenance Department"));
        officerList.add(new Officer("Joanne Reyes", "Sanitation Department"));

        filteredOfficerList.clear();
        filteredOfficerList.addAll(officerList);
        officerAdapter.notifyDataSetChanged();
    }

    private void filterOfficers(String department) {
        filteredOfficerList.clear();
        if (department.equals(getString(R.string.all_department))) {
            filteredOfficerList.addAll(officerList);
        } else {
            for (Officer officer : officerList) {
                if (officer.getDepartment().equals(department)) {
                    filteredOfficerList.add(officer);
                }
            }
        }
        officerAdapter.notifyDataSetChanged();
    }
}

