package com.example.quickcash.util.ratingSystem;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quickcash.R;
import com.example.quickcash.firebase.FirebaseRating;
import com.example.quickcash.util.employeeView.EmployeeApplicationsActivity;
import com.example.quickcash.util.employerView.HiredEmployeesActivity;

public class RatingActivity extends AppCompatActivity {

    private int rating;
    FirebaseRating firebaseRating;
    ImageButton star1;
    ImageButton star2;
    ImageButton star3;
    ImageButton star4;
    ImageButton star5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rating);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        firebaseRating = new FirebaseRating();
    }

    // Initialize button and views
    private void initializeViews() {
        star1 = findViewById(R.id.star1);
        star1.setOnClickListener(v -> handleStar1());

        star2 = findViewById(R.id.star2);
        star2.setOnClickListener(v -> handleStar2());

        star3 = findViewById(R.id.star3);
        star3.setOnClickListener(v -> handleStar3());

        star4 = findViewById(R.id.star4);
        star4.setOnClickListener(v -> handleStar4());

        star5 = findViewById(R.id.star5);
        star5.setOnClickListener(v -> handleStar5());

        EditText feedback = findViewById(R.id.feedbackTextField);

        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> handleSubmit());
    }


    // Manipulate the star behaviours
    private void handleStar1(){
        resetStars();
        rating = 1;
        star1.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN);
    }

    private void handleStar2(){
        resetStars();
        rating = 2;
        star1.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN);
        star2.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN);
    }

    private void handleStar3(){
        resetStars();
        rating = 3;
        star1.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN);
        star2.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN);
        star3.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN);
    }

    private void handleStar4(){
        resetStars();
        rating = 4;
        star1.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN);
        star2.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN);
        star3.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN);
        star4.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN);
    }

    private void handleStar5(){
        resetStars();
        rating = 5;
        star1.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN);
        star2.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN);
        star3.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN);
        star4.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN);
        star5.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN);
    }

    private void resetStars(){
        rating = 0;
        star1.setColorFilter(Color.parseColor("#E0DADA"), PorterDuff.Mode.SRC_IN);
        star2.setColorFilter(Color.parseColor("#E0DADA"), PorterDuff.Mode.SRC_IN);
        star3.setColorFilter(Color.parseColor("#E0DADA"), PorterDuff.Mode.SRC_IN);
        star4.setColorFilter(Color.parseColor("#E0DADA"), PorterDuff.Mode.SRC_IN);
        star5.setColorFilter(Color.parseColor("#E0DADA"), PorterDuff.Mode.SRC_IN);
    }

    // Handle the submit behaviour
    private void handleSubmit() {

        // Retrieve the ID of the user
        String employeeID = getIntent().getStringExtra("employeeID");
        String employerID = getIntent().getStringExtra("employerID");

        String applicationID = getIntent().getStringExtra("applicationID");
        String ratingStr = String.valueOf(rating);

        // Depending on the role of the user, rate appropriately
        if (employeeID != null && employerID == null) {
            String role = "employee";
            firebaseRating.rateUser(role, employeeID, applicationID, ratingStr);
            Intent intent = new Intent(RatingActivity.this, HiredEmployeesActivity.class);
            startActivity(intent);
        } else if (employerID != null && employeeID == null) {
            String role = "employer";
            firebaseRating.rateUser(role, employerID, applicationID, ratingStr);
            Intent intent = new Intent(RatingActivity.this, EmployeeApplicationsActivity.class);
            startActivity(intent);
        }
    }
}