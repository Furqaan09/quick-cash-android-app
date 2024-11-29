package com.example.quickcash.util.employeeView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcash.R;
import com.example.quickcash.firebase.FirebaseEmployerProfile;
import com.example.quickcash.util.employeeView.EmployerProfile;

public class EmployerProfileActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView ratingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_profile);

        // Initialize views
        nameTextView = findViewById(R.id.employerNameTextView);
        emailTextView = findViewById(R.id.employerEmailTextView);
        phoneTextView = findViewById(R.id.employerPhoneTextView);
        ratingTextView = findViewById(R.id.employerRatingTextView);

        // Get jobId from the intent
        String jobId = getIntent().getStringExtra("JobID");

        if (jobId != null) {
            fetchEmployerProfile(jobId);
        } else {
            Toast.makeText(this, "Job ID not found.", Toast.LENGTH_SHORT).show();
        }
    }

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
}