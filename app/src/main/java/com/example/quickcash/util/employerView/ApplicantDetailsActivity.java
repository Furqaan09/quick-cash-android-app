package com.example.quickcash.util.employerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quickcash.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class ApplicantDetailsActivity extends AppCompatActivity {

    // Application Status Strings
    private static final String STATUS_SHORTLISTED = "Shortlisted";
    private static final String STATUS_REJECTED = "Rejected";
    private static final String STATUS_PENDING = "Pending";
    private static final String STATUS_SUBMITTED = "Submitted";
    private static final String STATUS_HIRED = "Hired";

    // Declaring UI elements and instance variables
    private TextView shortlistedTextView;
    private TextView rejectedTextView;
    private TextView pendingTextView;
    private Button offerJobButton;
    private Button submitButton;
    private LinearLayout inputLayout;
    private LinearLayout buttonLayout;
    private EditText salaryEditText;
    private EditText startDateEditText;
    private DatabaseReference applicationRef;
    private Applicant applicant;
    private String applicationId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_applicant_details_view);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setUpButtons();

        // Get the applicant from the intent
        applicant = (Applicant) getIntent().getSerializableExtra("applicant");
        // From the email, get the associated application node
        getApplicationNode(applicant.getApplicantEmail());
    }

    // This method initializes the declared Ui elements
    private void initializeViews() {
        shortlistedTextView = findViewById(R.id.shortlistedTextView);
        rejectedTextView = findViewById(R.id.rejectedTextView);
        pendingTextView = findViewById(R.id.pendingTextView);
        offerJobButton = findViewById(R.id.offerJobButton);
        inputLayout = findViewById(R.id.inputLayout);
        salaryEditText = findViewById(R.id.salaryEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        submitButton = findViewById(R.id.submitButton);
        buttonLayout = findViewById(R.id.buttonLayout);
    }

    // This method sets up on click listener for each buttons
    private void setUpButtons() {
        Button shortlistButton = findViewById(R.id.shortlistButton);
        shortlistButton.setOnClickListener(view -> onShortlistClicked());

        Button rejectButton = findViewById(R.id.rejectButton);
        rejectButton.setOnClickListener(view -> onRejectClicked());

        Button viewResume = findViewById(R.id.viewResumeButton);
        viewResume.setOnClickListener(view -> onViewResume());

        offerJobButton.setOnClickListener(view -> onOfferJobClicked());
        startDateEditText.setOnClickListener(view -> showDatePicker());
        submitButton.setOnClickListener(view -> onSubmitClicked());
    }

    /*
     * This method is used to fetch the application node which contains all the application data from
     * the Firebase by matching the applicant email
     */
    private void getApplicationNode(String applicantEmail) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("applications");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot applicationSnapshot : snapshot.getChildren()) {
                    // Retrieve the applicantEmail field within each application node
                    String email = applicationSnapshot.child("applicantEmail").getValue(String.class);
                    String jobId = applicationSnapshot.child("jobId").getValue(String.class);

                    // Check if this email matches the input email
                    if (applicantEmail.equals(email) && applicant.getJobId().equals(jobId)) {
                        applicationId = applicationSnapshot.getKey();
                        // Initialize applicationRef to point this node to make for future references
                        applicationRef = FirebaseDatabase.getInstance().getReference("applications").child(applicationId);
                        setupApplicantDetails();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error retrieving application: " + error.getMessage());
            }
        });
    }

    // This method sets up applicant details in the UI and manages the Ui based on the applicant status
    private void setupApplicantDetails() {
        if (applicant != null) {
            // Set up applicant details
            TextView applicantNameTextView = findViewById(R.id.applicantNameTextView);
            TextView applicantEmailTextView = findViewById(R.id.applicantEmailTextView);
            TextView applicantPhoneTextView = findViewById(R.id.applicantPhoneTextView);
            TextView jobIdTextView = findViewById(R.id.applicantJobIDTextView);
            TextView applicantRatingtextView = findViewById(R.id.applicantRatingTextView);

            applicantNameTextView.setText("Name: "+applicant.getApplicantName());
            applicantEmailTextView.setText("Email: "+applicant.getApplicantEmail());
            applicantPhoneTextView.setText("Phone: "+applicant.getApplicantPhone());
            getEmployeeRating(rating -> applicantRatingtextView.setText("Rating: "+rating));
            jobIdTextView.setText("Job ID: "+applicant.getJobId());

            // Retrieve the application status and set the Ui accordingly
            applicationRef.child("applicantStatus").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String status = snapshot.getValue(String.class);
                    manageStatusView(status);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Status fetch failed", "Error fetching Status!", error.toException());
                }
            });
        }
    }

    public void getEmployeeRating(RatingCallback callback) {
        DatabaseReference employeeRef = FirebaseDatabase.getInstance().getReference("users/employee");

        employeeRef.orderByChild("email").equalTo(applicant.getApplicantEmail()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot employeeSnapshot : snapshot.getChildren()) {
                    String ratingValue = employeeSnapshot.child("ratingValue").getValue(String.class);
                    String ratingCount = employeeSnapshot.child("ratingCount").getValue(String.class);

                    if (ratingCount == null && ratingValue == null) {
                        callback.onRatingCalculated("No ratings yet!");
                    } else {
                        double value = Double.parseDouble(ratingValue);
                        int count = Integer.parseInt(ratingCount);

                        double rating = value / count;

                        // Pass the calculated rating to the callback
                        callback.onRatingCalculated(String.format("%.1f", rating));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error fetching employee details: " + error);
            }
        });
    }

    public interface RatingCallback {
        void onRatingCalculated(String rating);
    }

    // This method retrieves the resume download Uri from the database and opens it in browser window
    private void onViewResume() {
        applicationRef.child("resumeUri").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String resumeUri = snapshot.getValue(String.class);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resumeUri));
                startActivity(browserIntent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Resume load fail", "Error downloading resume!", error.toException());
            }
        });
    }

    // This method clears all the previous Ui elements
    private void clearOldDetails() {
        shortlistedTextView.setVisibility(View.GONE);
        rejectedTextView.setVisibility(View.GONE);
        pendingTextView.setVisibility(View.GONE);
        offerJobButton.setVisibility(View.GONE);
        buttonLayout.setVisibility(View.GONE);
        inputLayout.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
    }

    // This method sets up the Ui elements based on the retrieved status
    private void manageStatusView(String status) {
        clearOldDetails();
        if (status.equals(STATUS_SHORTLISTED)) {
            buttonLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.shortlistButton).setVisibility(View.GONE);
            shortlistedTextView.setVisibility(View.VISIBLE);
            offerJobButton.setVisibility(View.VISIBLE);
        } else if (status.equals(STATUS_REJECTED)) {
            rejectedTextView.setVisibility(View.VISIBLE);
        } else if (status.equals(STATUS_PENDING)) {
            buttonLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.shortlistButton).setVisibility(View.GONE);
            pendingTextView.setVisibility(View.VISIBLE);
        } else if (status.equals(STATUS_SUBMITTED)) {
            buttonLayout.setVisibility(View.VISIBLE);
        } else if (status.equals(STATUS_HIRED)) {
            pendingTextView.setText("Hired!");
            pendingTextView.setVisibility(View.VISIBLE);
        }
    }

    // This method sets the applicant Status as "Rejected" in the firebase and updates the Ui
    private void onRejectClicked() {
        // Update application status to 'Rejected' in Firebase
        applicationRef.child("applicantStatus").setValue(STATUS_REJECTED)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Application marked as Rejected", Toast.LENGTH_SHORT).show();
                    manageStatusView(STATUS_REJECTED);
                });
    }

    // This method sets the applicant Status as "Shortlisted" in the firebase and updates the Ui
    private void onShortlistClicked() {
        applicationRef.child("applicantStatus").setValue(STATUS_SHORTLISTED)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Application marked as Shortlisted", Toast.LENGTH_SHORT).show();
                    manageStatusView(STATUS_SHORTLISTED);
                });
    }

    // This method displays the additional information tab required to send a job offer
    private void onOfferJobClicked() {
        buttonLayout.setVisibility(View.GONE);
        inputLayout.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
    }

    // This method allows the employer to choose a date from an in app calender
    private void showDatePicker() {
        // Show a DatePickerDialog and set the selected date to startDateEditText
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(ApplicantDetailsActivity.this,
                (view, year, month, day) -> {
                    month = month + 1;
                    String date = day + "/" + month + "/" + year;
                    startDateEditText.setText(date);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    /*
     * This method pushes the new updates to the firebase if an employer chooses to offer the job to
     * an employee and marks the status as "Pending"
     */
    private void onSubmitClicked() {
        String salary = salaryEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();

        // Check if both the fields are filled correctly
        if (isValidSalary(salary) && isValidDate(startDate)) {
            HashMap<String, Object> updates = new HashMap<>();
            updates.put("salary", salary);
            updates.put("startDate", startDate);
            updates.put("applicantStatus", STATUS_PENDING);

            // Push the updates to Firebase under the applicant node
            applicationRef.updateChildren(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Job offer sent. Application marked as Pending", Toast.LENGTH_SHORT).show();
                        manageStatusView("Pending");
                    });
        }
    }

    // This method validates the Salary field
    private boolean isValidSalary(String salary) {
        if (salary.isEmpty()) {
            salaryEditText.setError("Salary required");
            salaryEditText.requestFocus();
            return false;
        }
        return true;
    }

    // This method validates the Date field
    private boolean isValidDate(String startDate) {
        if (startDate.isEmpty()) {
            startDateEditText.setError("Start date required");
            startDateEditText.requestFocus();
            return false;
        }
        return true;
    }
}
