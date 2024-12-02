package com.example.quickcash.util.employerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JobApplicantsActivity extends AppCompatActivity implements ApplicantAdapter.OnItemClickListener {

    // UI Components
    RecyclerView applicantsRecyclerView;  // RecyclerView to display the list of applicants
    ApplicantAdapter applicantAdapter;    // Adapter to bind applicants to the RecyclerView
    ArrayList<Applicant> applicantList;   // List to hold applicant data

    // Firebase Database reference to "applications" node
    DatabaseReference applicationsRef;  // Direct reference to "applications" node
    String jobID;                       // ID of the job for which applicants are being viewed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Optional: Makes the activity UI edge-to-edge
        setContentView(R.layout.activity_job_applicants);  // Set the layout for the activity

        // Ensure the layout fits correctly with system insets (optional for edge-to-edge UI)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the jobID passed from JobDetailsActivity
        jobID = getIntent().getStringExtra("jobID");
        Log.d("JobApplicantsActivity", "Received Job ID: " + jobID);

        if (jobID == null) {
            // Handle the case where no jobID is passed; show a message and finish the activity
            Toast.makeText(this, "No job ID provided", Toast.LENGTH_SHORT).show();
            finish();  // End the activity if no jobID is available to avoid errors
            return;
        }

        // Display a confirmation message that jobID is received
        Toast.makeText(this, "Viewing applicants for Job ID: " + jobID, Toast.LENGTH_SHORT).show();

        // Initialize the DatabaseReference to the "applications" node
        applicationsRef = FirebaseDatabase.getInstance().getReference("applications");

        setupRecyclerView();  // Initialize and set up the RecyclerView for displaying applicants

        fetchApplicants();  // Fetch applicants from Firebase for the given jobID
    }

    // Method to set up the RecyclerView which will display the list of applicants
    private void setupRecyclerView() {
        // Find the RecyclerView in the layout and set its layout manager to a LinearLayout
        applicantsRecyclerView = findViewById(R.id.applicantsRecyclerView);
        applicantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list of applicants and set up the adapter
        applicantList = new ArrayList<>();
        applicantAdapter = new ApplicantAdapter(applicantList, this);  // Pass 'this' as the click listener
        applicantsRecyclerView.setAdapter(applicantAdapter);  // Set the adapter to the RecyclerView
    }

    // Method to fetch applicants from Firebase who applied for the specific jobID
    private void fetchApplicants() {
        // Access the "applications" node directly
        applicationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                applicantList.clear();  // Clear the current list to avoid duplicates

                // Iterate through each child in "applications" (each applicant node)
                for (DataSnapshot applicantSnapshot : snapshot.getChildren()) {
                    // Convert each child into an Applicant object
                    Applicant applicant = applicantSnapshot.getValue(Applicant.class);

                    // Check if the applicant is not null and has the correct jobID
                    if (applicant != null && jobID.equals(applicant.getJobId())) {
                        applicantList.add(applicant);  // Add to the list if jobID matches
                        Log.d("JobApplicantsActivity", "Added applicant: " + applicant.getApplicantName() + " for Job ID: " + applicant.getJobId());
                    }
                }

                // Notify the adapter to update the RecyclerView
                applicantAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error fetching applications: "+error.getMessage());
            }
        });
    }

    // This method is part of the OnItemClickListener interface, handling click events for applicants
    @Override
    public void onViewApplicantClick(Applicant applicant) {
        // Start ApplicantDetailsActivity when an applicant is clicked
        Intent intent = new Intent(this, ApplicantDetailsActivity.class);

        intent.putExtra("applicant", applicant);  // Pass the entire applicant object
        startActivity(intent);  // Launch the ApplicantDetailsActivity
    }
}