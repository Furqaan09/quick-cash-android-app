package com.example.quickcash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import kotlinx.coroutines.Job;

public class EmployeeActivity extends AppCompatActivity implements JobPostAdapter.OnItemClickListener {
    FirebaseCRUD firebaseCRUD;
    RecyclerView recyclerView;
    JobPostAdapter jobPostAdapter;
    ArrayList<JobPost> jobPostList;
    JobPostFilter jobPostFilter;

    private FirebaseAuth mAuth;

    private boolean isDropdownExpanded = false;
    private LinearLayout dropdownContent;
    private ImageView triangleIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // initialize the firebase authorization
        // initialize ui features and find views
        mAuth = FirebaseAuth.getInstance();
        Button logoutButton = findViewById(R.id.logoutButton);
        Button resetButton = findViewById(R.id.resetButton);
        CheckBox salaryCheckBox = findViewById(R.id.salaryCheckBox);
        CheckBox locationCheckBox = findViewById(R.id.locationCheckBox);
        CheckBox jobtypeCheckBox = findViewById(R.id.jobtypeCheckBox);
        Spinner jobtypeSpinner = findViewById(R.id.jobtypeSpinner);
        String[] jobTypes = getResources().getStringArray(R.array.job_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobtypeSpinner.setAdapter(adapter);
        LinearLayout dropdownHeader = findViewById(R.id.dropdownHeader);
        dropdownContent = findViewById(R.id.dropdownContent);
        triangleIcon = findViewById(R.id.triangleIcon);
        Button applyButton = findViewById(R.id.applyButton);
        LinearLayout salaryInputLayout = findViewById(R.id.salaryInputLayout);
        EditText highEditText = findViewById(R.id.highEditText);
        EditText lowEditText = findViewById(R.id.lowEditText);
        TextView radiusTextView = findViewById(R.id.radiusTextView);
        EditText locationEditText = findViewById(R.id.locationEditText);
        EditText radiusEditText = findViewById(R.id.radiusEditText);

        // Handle dropdown click
        dropdownHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleDropdown();
            }
        });

        salaryCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Show the High and Low inputs if checked, hide them otherwise
                if (isChecked) {
                    salaryInputLayout.setVisibility(View.VISIBLE);
                } else {
                    salaryInputLayout.setVisibility(View.GONE);
                }
            }
        });

        locationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Show the High and Low inputs if checked, hide them otherwise
                if (isChecked) {
                    radiusTextView.setVisibility(View.VISIBLE);
                    locationEditText.setVisibility(View.VISIBLE);
                    radiusEditText.setVisibility(View.VISIBLE);
                } else {
                    radiusTextView.setVisibility(View.GONE);
                    locationEditText.setVisibility(View.GONE);
                    radiusEditText.setVisibility(View.GONE);
                }
            }
        });

        jobtypeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    jobtypeSpinner.setVisibility(View.VISIBLE);
                } else {
                    jobtypeSpinner.setVisibility(View.GONE);
                }
            }
        });

        // Handle apply button click
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO:
                // 1. Get the selected filters from the UI
                // 2. Create a new JobPostFilter with the selected filters
                // 3. Apply the filter to the jobPostList.
                // 4. Update the jobPostAdapter with the filtered jobPostList.
            }
        });

        // Handle reset button click
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Reset all filter input fields.

                salaryCheckBox.setChecked(false);
                locationCheckBox.setChecked(false);
                jobtypeCheckBox.setChecked(false);
                highEditText.setText("");
                lowEditText.setText("");
                jobtypeSpinner.setSelection(0);

                // TODO:
                // 1. Get the selected filters from the UI
                // 2. Create a new JobPostFilter with the selected filters
                // 3. Apply the filter to the jobPostList.
                // 4. Update the jobPostAdapter with the filtered jobPostList.
            }
        });

        // set onClick listener for the logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // log out
                mAuth.signOut();
                Toast.makeText(EmployeeActivity.this, "You have been logged out.", Toast.LENGTH_SHORT).show();

                // clear session data
                clearSessionData();

                // redirect to LoginActivity
                Intent intent = new Intent(EmployeeActivity.this, LoginActivity.class);
                startActivity(intent);

                // close the current activity
                finish();
            }
        });

        jobPostFilter = new JobPostFilter();

        initializeFirebaseCRUD();
        setupRecyclerView();
        fetchJobPosts();
    }

    private void toggleDropdown() {
        if (isDropdownExpanded) {
            // Collapse dropdown and rotate the triangle back
            dropdownContent.setVisibility(View.GONE);
            rotateTriangle(90, 0); // rotate from 90 to 0
        } else {
            // Expand dropdown and rotate the triangle
            dropdownContent.setVisibility(View.VISIBLE);
            rotateTriangle(0, 90); // rotate from 0 to 90
        }
        isDropdownExpanded = !isDropdownExpanded;
    }

    private void rotateTriangle(float fromDegrees, float toDegrees) {
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(300);
        rotateAnimation.setFillAfter(true);
        triangleIcon.startAnimation(rotateAnimation);
    }

    // method to clear session data
    private void clearSessionData() {
        // clear Firebase session
        FirebaseAuth.getInstance().signOut();

        // clear SharedPreferences session ( the locally stored data)
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();  // clear all data in the user_session file
        editor.apply();
    }

    private void initializeFirebaseCRUD() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.FIREBASE_DATABASE_URL);
        firebaseCRUD = new FirebaseCRUD(firebaseDatabase);
    }

    private void filterJobPostList() {
        ArrayList<JobPost> filteredJobPostList = new ArrayList<>();
        for (JobPost jobPost : jobPostList) {
            if (jobPostFilter.satisfy(jobPost)) {
                filteredJobPostList.add(jobPost);
            }
        }
        jobPostList = filteredJobPostList;
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.employeeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        jobPostList = new ArrayList<>();
        jobPostAdapter = new JobPostAdapter(jobPostList, this); // Pass 'this' as the listener.
        recyclerView.setAdapter(jobPostAdapter);
    }

    private void fetchJobPosts() {
        firebaseCRUD.getDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobPostList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    JobPost jobPost = postSnapshot.getValue(JobPost.class);
                    jobPostList.add(jobPost);
                }

                filterJobPostList();
                jobPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error fetching job posts: " + error.getMessage());
            }
        });
    }

    @Override
    public void onViewDetailsClick(JobPost jobPost) {

        // Start JobDetailsActivity and pass the jobPost data.
        Intent intent = new Intent(this, JobDetailsActivity.class);
        intent.putExtra("jobPost", jobPost);
        startActivity(intent);
    }
}
