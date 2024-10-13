package com.example.quickcash;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EmployeeActivity extends AppCompatActivity {

    TextView employeeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        employeeText = findViewById(R.id.employeeText);
        setViewDetailsButton();
    }

    public void setViewDetailsButton() {
        Button viewDetails = findViewById(R.id.viewDetailsButton);
        viewDetails.setOnClickListener(view -> viewJobDetails());
    }

    public void viewJobDetails() {
        Intent details = new Intent(EmployeeActivity.this, JobDetailsActivity.class);
        EmployeeActivity.this.startActivity(details);
    }
}