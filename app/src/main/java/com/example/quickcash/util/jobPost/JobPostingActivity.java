package com.example.quickcash.util.jobPost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quickcash.R;
import com.example.quickcash.firebase.FirebaseCRUD;
import com.example.quickcash.firebase.FirebasePreferredEmployerJobPostNotifier;
import com.example.quickcash.ui.EmployerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class JobPostingActivity extends AppCompatActivity {

    private EditText jobCompanyBox;
    private EditText jobTitleBox;
    private EditText jobDescriptionBox;
    private Spinner jobTypeSpinner;
    private Spinner experienceSpinner;
    private EditText jobIndustryBox;
    private EditText jobLocationBox;
    private TextView statusLabel;

    private FirebaseCRUD firebaseCRUD;
    private FirebasePreferredEmployerJobPostNotifier firebasePreferredEmployerJobPostNotifier;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_job_posting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components.
        initializeUIComponents();

        // Initialize firebase database
        initializeFirebase();
    }

    private void initializeUIComponents() {
        jobCompanyBox = findViewById(R.id.jobCompanyBox);
        jobTitleBox = findViewById(R.id.jobTitleBox);
        jobDescriptionBox = findViewById(R.id.jobDescriptionBox);
        jobTypeSpinner = findViewById(R.id.jobTypeSpinner);
        experienceSpinner = findViewById(R.id.experienceSpinner);
        jobIndustryBox = findViewById(R.id.jobIndustryBox);
        jobLocationBox = findViewById(R.id.jobLocationBox);
        statusLabel = findViewById(R.id.statusLabel);
        Button submitButton = findViewById(R.id.submitButton);

        // Set onClickListener for the button.
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createJobPost();
            }
        });
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseCRUD = new FirebaseCRUD(firebaseDatabase);
        firebasePreferredEmployerJobPostNotifier = new FirebasePreferredEmployerJobPostNotifier(this, getEmployerUid());
    }

    /**
     * Function to get the current UTC date formatted as "July 11, 2024".
     */
    private String getCurrentUTCDate() {
        // Get the current UTC date.
        LocalDate utcDate = LocalDate.now(ZoneOffset.UTC);

        // Format the date as "October 12, 2024".
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        return utcDate.format(dateFormatter);
    }

    /**
     * Collects data from the form, encapsulates them into a JobPost object
     * and writes it to the firebase database.
     */
    private void createJobPost() {

        // Collect data form the form.
        String companyName = jobCompanyBox.getText().toString().trim();
        String jobTitle = jobTitleBox.getText().toString().trim();
        String jobDescription = jobDescriptionBox.getText().toString().trim();
        String jobType = jobTypeSpinner.getSelectedItem().toString().trim();
        String experienceLevel = experienceSpinner.getSelectedItem().toString().trim();
        String industry = jobIndustryBox.getText().toString().trim();
        String location = jobLocationBox.getText().toString().trim();

        String jobId = JobPost.generateJobID();
        // Encapsulate the data collected into a JobPost object.
        JobPost jobPost = new JobPost(
                jobId,
                getIntent().getStringExtra("jobPosterID"),
                jobTitle,
                location,
                jobType,
                getCurrentUTCDate(),
                companyName,
                jobDescription,
                experienceLevel,
                industry
        );

        // Validate user input.
        boolean isValidJobPost = JobPostValidator.validateJobPost(jobPost);
        if (isValidJobPost) {

            // Write the JobPost object to the Firebase database.
            firebaseCRUD.createJobPost(jobPost);

            // Send a notification to the subscribed employees about the newly posted job
            firebasePreferredEmployerJobPostNotifier.sendJobNotification(jobTitle, jobId, jobType, location);

            // Display success message.
            statusLabel.setText(R.string.SUCCESSFUL_JOB_POST_FEEDBACK);

            // Go back to the Employer activity.
            Intent intent = new Intent(this, EmployerActivity.class);
            intent.putExtra("jobPosterID", getIntent().getStringExtra("jobPosterID"));
            startActivity(intent);
        } else {

            // Display error message.
            statusLabel.setText(R.string.UNSUCCESSFUL_JOB_POST_FEEDBACK);
        }
    }

    // This method is used to retrieve current user's (employer) UID
    private String getEmployerUid() {
        return mAuth.getCurrentUser().getUid();
    }
}
