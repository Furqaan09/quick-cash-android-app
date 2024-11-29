package com.example.quickcash.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseRating {

    // Instance Variables
    private DatabaseReference employeeRef;
    private DatabaseReference applicationRef;

    // Constructor
    public FirebaseRating() {
        employeeRef = FirebaseDatabase.getInstance().getReference("users/employee");
        applicationRef = FirebaseDatabase.getInstance().getReference("applications");
    }

    // This method is used to rate an employee and update the ratings in the firebase
    public void rateEmployee(String employeeID, String applicationID, String rating) {
        // Get the employee node using the employeeID (employee Email)
        employeeRef.orderByChild("email").equalTo(employeeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot employeeSnapshot : snapshot.getChildren()) {
                    String employeeNode = employeeSnapshot.getKey();
                    String ratingValue = employeeSnapshot.child("ratingValue").getValue(String.class);
                    String ratingCount = employeeSnapshot.child("ratingCount").getValue(String.class);

                    // If the rating is null then simply add the first rating
                    if (ratingValue == null && ratingCount == null) {
                        employeeRef.child(employeeNode).child("ratingValue").setValue(rating);
                        employeeRef.child(employeeNode).child("ratingCount").setValue("1");
                    } else {
                        // Convert the values in numerical form
                        double prevValue = Double.parseDouble(ratingValue);
                        int prevCount = Integer.parseInt(ratingCount);
                        double givenRating = Double.parseDouble(rating);

                        // Update the rating value and rating count
                        prevValue+=givenRating;
                        prevCount++;

                        // Convert the new rating back to string
                        String newValue = String.valueOf(prevValue);
                        String newCount = String.valueOf(prevCount);

                        // Submit new values to firebase
                        employeeRef.child(employeeNode).child("ratingValue").setValue(newValue);
                        employeeRef.child(employeeNode).child("ratingCount").setValue(newCount);
                    }

                    // Update the employee status as Reviewed
                    applicationRef.child(applicationID).child("employeeReview").setValue("Reviewed");
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Employee", "Error loading employee info: "+error);
            }
        });
    }

}
