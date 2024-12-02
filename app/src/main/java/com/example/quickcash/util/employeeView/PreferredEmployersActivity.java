package com.example.quickcash.util.employeeView;

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
import com.example.quickcash.firebase.FirebasePreferredEmployers;
import com.example.quickcash.ui.EmployeeActivity;

public class PreferredEmployersActivity extends AppCompatActivity implements PreferredEmployerAdapter.OnItemClickListener {

    FirebasePreferredEmployers firebasePreferredEmployers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preferred_employers);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebasePreferredEmployers = new FirebasePreferredEmployers();
        setUpRecyclerView();
        setUpDashboardButton();
    }

    // This method sets up the recycler view by getting information from the firebase
    private void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.preferredEmployersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch data and set up RecyclerView once data is available
        firebasePreferredEmployers.returnAllPreferredEmployers(employers -> {
            PreferredEmployerAdapter adapter = new PreferredEmployerAdapter(employers, this);
            recyclerView.setAdapter(adapter);
        });
    }

    // This method sets up remove button to remove an employer from the preferred list
    @Override
    public void onClickRemoveButton(PreferredEmployer preferredEmployer) {
        String employerUID = preferredEmployer.getEmployerUID();
        firebasePreferredEmployers.removePreferredEmployer(employerUID, () -> {
            String employerName = preferredEmployer.getName();
            Toast.makeText(PreferredEmployersActivity.this, employerName+" was successfully removed from preferred employers", Toast.LENGTH_LONG).show();

            // Reset the recycler view after removing
            setUpRecyclerView();
        });
    }

    // This method sets up return to dashboard button to take employee back to dashboard
    private void setUpDashboardButton() {
        Button dashboardButton = findViewById(R.id.dashboardButton);
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(PreferredEmployersActivity.this, EmployeeActivity.class);
            startActivity(intent);
        });
    }
}