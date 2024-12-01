package com.example.quickcash.util.employeeView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcash.R;
import com.example.quickcash.firebase.FirebaseEmployerProfile;
import com.example.quickcash.firebase.FirebasePreferredEmployers;
import com.example.quickcash.ui.JobDetailsActivity;

public class EmployerProfileActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView ratingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_profile);

        initializeViews();

        // Get jobId from the intent
        String jobId = getIntent().getStringExtra("JobID");
        fetchEmployerProfile(jobId);
    }

    // Initialize views
    private void initializeViews() {
        Button addToPreferredButton;
        nameTextView = findViewById(R.id.employerNameTextView);
        emailTextView = findViewById(R.id.employerEmailTextView);
        phoneTextView = findViewById(R.id.employerPhoneTextView);
        ratingTextView = findViewById(R.id.employerRatingTextView);
        addToPreferredButton = findViewById(R.id.addToPreferredListButton);
        addToPreferredButton.setOnClickListener(v -> handleAddToPreferred());
    }

    // Fetch and display the Employer profile
    private void fetchEmployerProfile(String jobId) {
        FirebaseEmployerProfile firebaseEmployerProfile = new FirebaseEmployerProfile();
        firebaseEmployerProfile.fetchEmployerProfile(jobId, new FirebaseEmployerProfile.OnEmployerProfileFetchedListener() {
            @Override
            public void onProfileFetched(EmployerProfile employerProfile) {
                // Set the profile data to the TextViews
                nameTextView.setText("Name: "+employerProfile.getName());
                emailTextView.setText("Email: "+employerProfile.getEmail());
                phoneTextView.setText("Phone: "+employerProfile.getPhone());

                if (employerProfile.getRatingValue() != null && employerProfile.getRatingCount() != null) {
                    double ratingValue = Integer.parseInt(employerProfile.getRatingValue());
                    int ratingCount = Integer.parseInt(employerProfile.getRatingCount());
                    double rating = ratingValue / ratingCount;
                    ratingTextView.setText("Rating: "+ String.format("%.1f", rating));
                } else {
                    ratingTextView.setText("No reviews yet!");
                }

            }

            @Override
            public void onError(String errorMessage) {
                // Handle the error
                Toast.makeText(EmployerProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("EmployerProfile", errorMessage);
            }
        });
    }

    // Handle on click Add to Preferred Button
    private void handleAddToPreferred() {
        String employerEmail = emailTextView.getText().toString().replace("Email: ", "").trim();

        FirebasePreferredEmployers firebasePreferredEmployers = new FirebasePreferredEmployers();
        firebasePreferredEmployers.addPreferredEmployer(employerEmail);

        Toast.makeText(this, "Employer Added to Preferred List", Toast.LENGTH_SHORT).show();
    }
}