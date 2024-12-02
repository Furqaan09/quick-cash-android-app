package com.example.quickcash.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.firebase.FirebaseNotificationSubscriptionManager;
import com.example.quickcash.firebase.FirebasePreferredEmployers;
import com.example.quickcash.util.employeeView.EmployeeApplicationsActivity;
import com.example.quickcash.util.employeeView.PreferredEmployersActivity;
import com.example.quickcash.util.jobPost.JobPost;
import com.example.quickcash.util.jobPost.JobPostAdapter;
import com.example.quickcash.util.jobFilter.JobPostFilter;
import com.example.quickcash.util.jobFilter.JobTitleFilter;
import com.example.quickcash.util.jobFilter.JobTypeFilter;
import com.example.quickcash.maps.MapsActivity;
import com.example.quickcash.R;
import com.example.quickcash.firebase.FirebaseCRUD;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.animation.RotateAnimation;

import java.util.ArrayList;

public class EmployeeActivity extends AppCompatActivity implements JobPostAdapter.OnItemClickListener {
    FirebaseCRUD firebaseCRUD;
    String jobSeekerID;

    RecyclerView recyclerView;
    JobPostAdapter jobPostAdapter;
    ArrayList<JobPost> jobPostList;
    JobPostFilter jobPostFilter;

    private FirebaseAuth mAuth;
    private FirebaseNotificationSubscriptionManager subscriptionManager;
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

        // Extract employerID.
        jobSeekerID = getIntent().getStringExtra("jobSeekerID");

        // initialize the firebase authorization
        mAuth = FirebaseAuth.getInstance();
        subscriptionManager = new FirebaseNotificationSubscriptionManager();

        Button applicationStatusButton = findViewById(R.id.applicationStatusButton);
        EditText jobTitleEditText = findViewById(R.id.searchEditText);
        dropdownContent = findViewById(R.id.dropdownContent);
        triangleIcon = findViewById(R.id.triangleIcon);

        // Job type filter
        CheckBox jobtypeCheckBox = findViewById(R.id.jobtypeCheckBox);
        Spinner jobtypeSpinner = findViewById(R.id.jobtypeSpinner);
        String[] jobTypes = getResources().getStringArray(R.array.job_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobtypeSpinner.setAdapter(adapter);

        // Apply and reset filters
        Button applyButton = findViewById(R.id.applyButton);
        Button resetButton = findViewById(R.id.resetButton);

        // Toggle dropdown
        LinearLayout dropdownHeader = findViewById(R.id.dropdownHeader);
        dropdownHeader.setOnClickListener(view -> toggleDropdown());

        // Show or hide job type spinner based on checkbox state
        jobtypeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                jobtypeSpinner.setVisibility(View.VISIBLE);
            } else {
                jobtypeSpinner.setVisibility(View.GONE);
            }
        });

        // Apply filters
        applyButton.setOnClickListener(view -> {
            jobPostFilter.clear();
            if (jobtypeCheckBox.isChecked()) {
                String selectedJobType = jobtypeSpinner.getSelectedItem().toString().trim();
                jobPostFilter.add(new JobTypeFilter(selectedJobType));
            }
            if (!jobTitleEditText.getText().toString().trim().isEmpty()) {
                jobPostFilter.add(new JobTitleFilter(jobTitleEditText.getText().toString().trim()));
            }
            filterJobPostList();
            jobPostAdapter.notifyDataSetChanged();
        });

        // Reset filters
        resetButton.setOnClickListener(v -> {
            jobtypeCheckBox.setChecked(false);
            jobtypeSpinner.setSelection(0);
            jobTitleEditText.setText("");
            jobPostFilter.clear();
            filterJobPostList();
            jobPostAdapter.notifyDataSetChanged();
        });

        // Application status button
        applicationStatusButton.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeActivity.this, EmployeeApplicationsActivity.class);
            startActivity(intent);
        });

        jobPostFilter = new JobPostFilter();
        initializeFirebaseCRUD();
        setupRecyclerView();
        fetchJobPosts();
        setupLogoutButton();
        setUpGoogleMapButton();
        setUpPreferredEmployersButton();
    }

    private void setupLogoutButton() {
        Button logoutButton = findViewById(R.id.logoutButton);
        // Logout button
        logoutButton.setOnClickListener(v -> {
            // Unsubscribe from all topics to stop receiving notifications
            subscriptionManager.unsubscribeFromPreferredJobs();
            subscriptionManager.unsubscribeFromEmployerTopic();

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
        });
    }

    private void toggleDropdown() {
        if (isDropdownExpanded) {
            dropdownContent.setVisibility(View.GONE);
            rotateTriangle(90, 0);
        } else {
            dropdownContent.setVisibility(View.VISIBLE);
            rotateTriangle(0, 90);
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
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseCRUD = new FirebaseCRUD(firebaseDatabase);
    }

    private void filterJobPostList() {
        jobPostList.clear();
        jobPostList.addAll(firebaseCRUD.readAllJobPosts());

        ArrayList<JobPost> filteredJobPostList = new ArrayList<>();
        for (JobPost jobPost : jobPostList) {
            if (jobPostFilter.satisfy(jobPost)) {
                filteredJobPostList.add(jobPost);
            }
        }
        jobPostList.clear();
        jobPostList.addAll(filteredJobPostList);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.employeeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        jobPostList = new ArrayList<>();
        jobPostAdapter = new JobPostAdapter(jobPostList, this);
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

    // Implement the interface method.
    @Override
    public void onViewDetailsClick(JobPost jobPost) {

        // Start JobDetailsActivity and pass the jobPost data.
        Intent intent = new Intent(this, JobDetailsActivity.class);
        intent.putExtra("jobPost", jobPost);
        intent.putExtra("role", "employee");
        startActivity(intent);
    }

    private void setUpGoogleMapButton() {
        Button mapsButton = findViewById(R.id.viewOnMapsButton);
        mapsButton.setOnClickListener(view -> onViewOnMapsClick());
    }

    private void onViewOnMapsClick() {
        Intent mapsIntent = new Intent(EmployeeActivity.this, MapsActivity.class);
        startActivity(mapsIntent);
    }

    private void setUpPreferredEmployersButton() {
        Button prefEmployersButton = findViewById(R.id.preferredEmployersButton);
        prefEmployersButton.setOnClickListener(view -> {
            Intent intent = new Intent(EmployeeActivity.this, PreferredEmployersActivity.class);
            startActivity(intent);
        });
    }
}
