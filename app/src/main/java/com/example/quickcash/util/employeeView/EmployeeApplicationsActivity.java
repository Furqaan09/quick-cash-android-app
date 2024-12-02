package com.example.quickcash.util.employeeView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.firebase.FirebaseEmployeeApplicationInfo;
import com.example.quickcash.ui.EmployeeActivity;
import com.example.quickcash.util.employerView.HiredEmployee;
import com.example.quickcash.util.employerView.HiredEmployeesActivity;
import com.example.quickcash.util.ratingSystem.RatingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmployeeApplicationsActivity extends AppCompatActivity implements ApplicationAdapter.OnItemClickListener {

    String employeeEmail;
    FirebaseEmployeeApplicationInfo firebaseEmployeeApplicationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_applications);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeFirebase();
        setUpRecyclerView();
        setUpDashboardButton();
    }

    private void initializeFirebase() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            employeeEmail = currentUser.getEmail();
        }
        firebaseEmployeeApplicationInfo = new FirebaseEmployeeApplicationInfo();
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.employeeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch data asynchronously and set up RecyclerView once data is available
        firebaseEmployeeApplicationInfo.returnEmployeeApplications(employeeEmail, applications -> {
            ApplicationAdapter applicationAdapter = new ApplicationAdapter(applications, this);
            recyclerView.setAdapter(applicationAdapter);
        });
    }

    private void setUpDashboardButton() {
        Button dashboardButton = findViewById(R.id.dashboardButton);
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeApplicationsActivity.this, EmployeeActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onViewOfferClick(ApplicationData applicationData) {
        OfferDialog offerDialog = new OfferDialog(this, applicationData);
        offerDialog.show();
    }

    @Override
    public void onRateEmployerClick(ApplicationData applicationData){
        Intent intent = new Intent(EmployeeApplicationsActivity.this, RatingActivity.class);
        intent.putExtra("employerID", applicationData.getEmployerEmail());
        intent.putExtra("applicationID", applicationData.getApplicationId());
        startActivity(intent);
    }
}
