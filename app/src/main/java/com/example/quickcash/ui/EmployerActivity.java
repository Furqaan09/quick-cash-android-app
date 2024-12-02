package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.util.employerView.HiredEmployeesActivity;
import com.example.quickcash.util.jobPost.JobPost;
import com.example.quickcash.util.jobPost.JobPostAdapter;
import com.example.quickcash.util.jobPost.JobPostingActivity;
import com.example.quickcash.R;
import com.example.quickcash.firebase.FirebaseCRUD;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class EmployerActivity extends AppCompatActivity implements JobPostAdapter.OnItemClickListener {

    FirebaseCRUD firebaseCRUD;
    String jobPosterID;
    ImageButton createPostButton;

    RecyclerView recyclerView;
    JobPostAdapter jobPostAdapter;
    ArrayList<JobPost> jobPostList;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Extract employerID.
        jobPosterID = getIntent().getStringExtra("jobPosterID");

        // Initialize the UI components.
        createPostButton = findViewById(R.id.createPostButton);
        createPostButton.setOnClickListener(view -> changeToJobPostingActivity());

        initializeFirebaseCRUD();
        setupRecyclerView();
        fetchJobPosts();
        setUpHiredEmployeesButton();

        // initialize the firebase authorization
        mAuth = FirebaseAuth.getInstance();
        setupLogoutButton();
    }

    private void setUpHiredEmployeesButton() {
        Button hiredEmployeesButton = findViewById(R.id.hiredEmployeesButton);
        hiredEmployeesButton.setOnClickListener(view -> {
            Intent intent = new Intent(EmployerActivity.this, HiredEmployeesActivity.class);
            startActivity(intent);
        });
    }

    private void setupLogoutButton() {
        Button logoutButton = findViewById(R.id.logoutButton);
        // set onClick listener for the logout button
        logoutButton.setOnClickListener(v -> {
            // log out
            mAuth.signOut();
            Toast.makeText(EmployerActivity.this, "You have been logged out.", Toast.LENGTH_SHORT).show();

            // clear session data
            clearSessionData();

            // redirect to LoginActivity
            Intent intent = new Intent(EmployerActivity.this, LoginActivity.class);
            startActivity(intent);

            // close the current activity
            finish();
        });
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
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseCRUD = new FirebaseCRUD(firebaseDatabase);
    }

    private void changeToJobPostingActivity() {
        Intent intent = new Intent(this, JobPostingActivity.class);
        intent.putExtra("jobPosterID", jobPosterID);
        startActivity(intent);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.employerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        jobPostList = new ArrayList<>();
        jobPostAdapter = new JobPostAdapter(jobPostList, this); // Pass 'this' as the listener
        recyclerView.setAdapter(jobPostAdapter);
    }

    private void fetchJobPosts() {
        firebaseCRUD.getDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobPostList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    JobPost jobPost = postSnapshot.getValue(JobPost.class);
                    if (jobPost != null && jobPosterID.equals(jobPost.getJobPosterID())) {
                        jobPostList.add(jobPost);
                    }
                }
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
        intent.putExtra("role", "employer");
        startActivity(intent);
    }
}