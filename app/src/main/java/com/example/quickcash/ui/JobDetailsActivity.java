package com.example.quickcash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quickcash.R;
import com.example.quickcash.firebase.FirebasePreferredJobs;
import com.example.quickcash.firebase.FirebaseCompleteJob;
import com.example.quickcash.util.employeeView.EmployerProfileActivity;
import com.example.quickcash.util.employeeView.JobApplicationActivity;
import com.example.quickcash.util.employerView.JobApplicantsActivity;
import com.example.quickcash.util.jobPost.JobPost;

public class JobDetailsActivity extends AppCompatActivity {

    JobPost jobPost;
    FirebaseCompleteJob completeJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_job_details_view);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the jobPost from the intent and initialize views
        jobPost = getIntent().getSerializableExtra("jobPost", JobPost.class);
        // Initialize FirebaseCompleteJob
        completeJob = new FirebaseCompleteJob();
        initializeViews();

        // Manage buttons based on the role of user
        String role = getIntent().getStringExtra("role");
        manageButtons(role);
    }

    protected void initializeViews() {
        TextView jobID = findViewById(R.id.jobIDDetails);
        jobID.setText(jobPost.getJobID());

        TextView jobDetailsTitle = findViewById(R.id.jobDetailsTitle);
        jobDetailsTitle.setText(jobPost.getJobTitle());

        TextView companyName = findViewById(R.id.companyNameDetails);
        companyName.setText(jobPost.getCompanyName());

        TextView jobDescription = findViewById(R.id.jobDescriptionDetails);
        jobDescription.setText(jobPost.getJobDescription());

        TextView jobType = findViewById(R.id.jobTypeDetails);
        jobType.setText(jobPost.getJobType());

        TextView experienceLevel = findViewById(R.id.experienceLevelDetails);
        experienceLevel.setText(jobPost.getExperienceLevel());

        TextView industry = findViewById(R.id.industryDetails);
        industry.setText(jobPost.getIndustry());

        TextView location = findViewById(R.id.locationDetails);
        location.setText(jobPost.getLocation());

        TextView postedDate = findViewById(R.id.postedDateDetails);
        postedDate.setText(jobPost.getPostedDate());
    }

    protected void manageButtons(String role) {
        if (role != null && role.equals("employee")) {
            Button applyButton = findViewById(R.id.applyButton);
            applyButton.setVisibility(View.VISIBLE);
            applyButton.setOnClickListener(view -> handleApply());

            Button employerProfileButton = findViewById(R.id.employerProfileButton);
            employerProfileButton.setVisibility(View.VISIBLE);
            employerProfileButton.setOnClickListener(v -> {
                Intent intent = new Intent(JobDetailsActivity.this, EmployerProfileActivity.class);
                intent.putExtra("JobID", jobPost.getJobID());
                startActivity(intent);
            });

            // add to preferred jobs button logic
            Button addToPreferredJobsButton = findViewById(R.id.addToPreferredJobsButton);
            addToPreferredJobsButton.setVisibility(View.VISIBLE);
            addToPreferredJobsButton.setOnClickListener(v -> handleAddToPreferredJobs());
        } else {
            Button viewApplicantsButton = findViewById(R.id.viewApplicantsButton);
            viewApplicantsButton.setVisibility(View.VISIBLE);
            viewApplicantsButton.setOnClickListener(view -> handleViewApplicants());
        }
    }

    protected void handleApply() {
        Intent apply = new Intent(JobDetailsActivity.this, JobApplicationActivity.class);
        apply.putExtra("jobId", jobPost.getJobID());
        apply.putExtra("jobTitle", jobPost.getJobTitle());
        startActivity(apply);
    }

    protected void handleViewApplicants() {
        String jobIDStr = jobPost.getJobID();

        if (jobIDStr != null) {
            Intent intent = new Intent(JobDetailsActivity.this, JobApplicantsActivity.class);
            intent.putExtra("jobID", jobIDStr);  // pass jobID to the next activity
            startActivity(intent);
        } else {
            Toast.makeText(JobDetailsActivity.this, "No Job ID available", Toast.LENGTH_SHORT).show();
        }
    }

    protected void handleAddToPreferredJobs() {
        FirebasePreferredJobs firebasePreferredJobs = new FirebasePreferredJobs();
        firebasePreferredJobs.addPreferredJob(jobPost.getJobTitle());
        Toast.makeText(this, "Job added to preferred list.", Toast.LENGTH_SHORT).show();
    }
}