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
    private DatabaseReference userRef;
    private DatabaseReference applicationRef;
    private static final String RATING_VALUE = "ratingValue";
    private static final String RATING_COUNT = "ratingCount";

    // Constructor
    public FirebaseRating() {
        applicationRef = FirebaseDatabase.getInstance().getReference("applications");
    }

    // This method is used to rate an user and update the ratings in the firebase
    public void rateUser(String role, String userID, String applicationID, String rating) {

        if (role.equals("employee")) {
            userRef = FirebaseDatabase.getInstance().getReference("users/employee");
        } else {
            userRef = FirebaseDatabase.getInstance().getReference("users/employer");
        }

        // Get the user node using the userID (user Email)
        userRef.orderByChild("email").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userNode = userSnapshot.getKey();
                    String ratingValue = userSnapshot.child(RATING_VALUE).getValue(String.class);
                    String ratingCount = userSnapshot.child(RATING_COUNT).getValue(String.class);

                    // If the rating is null then simply add the first rating
                    if (ratingValue == null && ratingCount == null) {
                        userRef.child(userNode).child(RATING_VALUE).setValue(rating);
                        userRef.child(userNode).child(RATING_COUNT).setValue("1");
                    } else {
                        // Convert the values in numerical form
                        assert ratingValue != null;
                        double prevValue = Double.parseDouble(ratingValue);
                        assert ratingCount != null;
                        int prevCount = Integer.parseInt(ratingCount);
                        double givenRating = Double.parseDouble(rating);

                        // Update the rating value and rating count
                        prevValue+=givenRating;
                        prevCount++;

                        // Convert the new rating back to string
                        String newValue = String.valueOf(prevValue);
                        String newCount = String.valueOf(prevCount);

                        // Submit new values to firebase
                        userRef.child(userNode).child(RATING_VALUE).setValue(newValue);
                        userRef.child(userNode).child(RATING_COUNT).setValue(newCount);
                    }

                    // Update the user status as Reviewed
                    if (role.equals("employee")) {
                        applicationRef.child(applicationID).child("employeeReview").setValue("Reviewed");
                    } else {
                        applicationRef.child(applicationID).child("employerReview").setValue("Reviewed");
                    }

                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("User", "Error loading user info: "+error);
            }
        });
    }

}
