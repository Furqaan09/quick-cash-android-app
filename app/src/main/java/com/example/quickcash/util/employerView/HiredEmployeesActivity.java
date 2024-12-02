package com.example.quickcash.util.employerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.firebase.FirebaseApplicationSubmission;
import com.example.quickcash.firebase.FirebaseCRUD;
import com.example.quickcash.firebase.FirebaseCompleteJob;
import com.example.quickcash.firebase.FirebaseHiredEmployees;
import com.example.quickcash.ui.EmployeeActivity;
import com.example.quickcash.util.employeeView.ApplicationAdapter;
import com.example.quickcash.util.employeeView.EmployeeApplicationsActivity;
import com.example.quickcash.util.ratingSystem.RatingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HiredEmployeesActivity extends AppCompatActivity implements HiredEmployeeAdapter.OnItemClickListener {

    String employerId;
    FirebaseHiredEmployees firebaseHiredEmployees;
    FirebaseCompleteJob completeJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hired_employees);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initialize();
        setUpRecyclerView();
        setUpDashboardButton();
    }

    private void initialize() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            employerId = currentUser.getEmail();
        }
        firebaseHiredEmployees = new FirebaseHiredEmployees();
        completeJob = new FirebaseCompleteJob();
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.hiredEmployeesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch data asynchronously and set up RecyclerView once data is available
        firebaseHiredEmployees.returnHiredEmployees(employerId, employees -> {
            HiredEmployeeAdapter adapter = new HiredEmployeeAdapter(employees, this);
            recyclerView.setAdapter(adapter);
        });
    }

    private void setUpDashboardButton() {
        Button dashboardButton = findViewById(R.id.dashboardButton);
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(HiredEmployeesActivity.this, EmployeeActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onMarkCompleteClick(HiredEmployee employee) {
        // Get the application node reference
        DatabaseReference applicationRef = FirebaseDatabase.getInstance().getReference("applications").child(employee.getApplicationID());

        // Update the status to "Completed"
        applicationRef.child("applicantStatus").setValue("Completed")
        .addOnSuccessListener(aVoid -> {
            // Notify the user
            Toast.makeText(this, "Application marked as Completed", Toast.LENGTH_LONG).show();

            // Refresh the RecyclerView
           setUpRecyclerView();
        })
        .addOnFailureListener(e -> {
            // Handle the error
            Toast.makeText(this, "Failed to update status", Toast.LENGTH_LONG).show();
        });

    }

    @Override
    public void onRateEmployeeClick(HiredEmployee employee){
        Intent intent = new Intent(HiredEmployeesActivity.this, RatingActivity.class);
        intent.putExtra("employeeID", employee.getEmployeeEmail());
        intent.putExtra("applicationID", employee.getApplicationID());
        startActivity(intent);
    }

    @Override
    public void onPayNowClick(HiredEmployee employee) {
        String jobId = employee.getJobTitleAndId().split("#")[1];

        // Navigate to the payment page
        Intent intent = new Intent(HiredEmployeesActivity.this, ProcessPaymentActivity.class);
        intent.putExtra("jobId", jobId);
        intent.putExtra("applicationId", employee.getApplicationID());

        // Get the salary for the completed job and move to payment page
        completeJob.getSalaryForCompletedJob(employee.getApplicationID(), salary -> {
            // Handle the retrieved salary (e.g., display it on the UI)
            intent.putExtra("salary", salary);
            startActivity(intent);
        });
    }
}