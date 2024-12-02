package com.example.quickcash.util.employeeView;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.quickcash.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OfferDialog extends Dialog {
    private final ApplicationData applicationData;
    private DatabaseReference applicationsRef = FirebaseDatabase.getInstance().getReference("applications");
    private String jobId;
    private String applicantEmail;
    private String startDate;
    private String salary;
    private String applicationNode;

    public OfferDialog(@NonNull Context context, ApplicationData applicationData) {
        super(context);
        this.applicationData = applicationData;
        this.jobId = getJobId(applicationData.getJobIdAndTitle());
        this.applicantEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        setContentView(R.layout.offer_item_view);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDetailsFromFirebase();
    }

    private void initializeDialog() {
        // Initialize views
        TextView jobTitleAndId = findViewById(R.id.jobTitleAndID);
        TextView companyName = findViewById(R.id.jobCompany);
        TextView location = findViewById(R.id.jobLocation);
        TextView startDate = findViewById(R.id.startDateTextView);
        TextView salary = findViewById(R.id.salaryTextView);
        Button acceptButton = findViewById(R.id.acceptOfferButton);
        Button rejectButton = findViewById(R.id.rejectOfferButton);

        // Set basic application data
        jobTitleAndId.setText(applicationData.getJobIdAndTitle());
        companyName.setText(applicationData.getCompanyName());
        location.setText(applicationData.getJobLocation());
        startDate.setText("Salary: "+this.startDate);
        salary.setText("Start Date: "+this.salary);

        // Set up click listeners
        acceptButton.setOnClickListener(view -> handleAcceptJob());
        rejectButton.setOnClickListener(view -> handleRejectJob());
    }

    private void getDetailsFromFirebase() {
        applicationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot applicationSnapshot : snapshot.getChildren()) {
                    String email = applicationSnapshot.child("applicantEmail").getValue(String.class);
                    String job = applicationSnapshot.child("jobId").getValue(String.class);

                    if (email.equals(applicantEmail) && job.equals(jobId)) {
                        applicationNode = applicationSnapshot.getKey();
                        salary = applicationSnapshot.child("startDate").getValue(String.class);
                        startDate = applicationSnapshot.child("salary").getValue(String.class);

                        initializeDialog();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error loading applications: " + error.getMessage());
            }
        });
    }

    private void handleAcceptJob() {
        applicationsRef.child(applicationNode).child("applicantStatus").setValue("Hired");
        applicationsRef.child(applicationNode).child("paymentStatus").setValue("Not Paid");
        applicationsRef.child(applicationNode).child("employeeReview").setValue("Not Reviewed");
        applicationsRef.child(applicationNode).child("employerReview").setValue("Not Reviewed");
        dismiss();
    }

    private void handleRejectJob() {
        applicationsRef.child(applicationNode).child("applicantStatus").setValue("Rejected");
        dismiss();
    }

    private String getJobId(String jobTitleAndId) {
        return jobTitleAndId.split("#")[1];
    }
}
