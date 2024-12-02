package com.example.quickcash.firebase;

import androidx.annotation.NonNull;

import com.example.quickcash.util.employeeView.EmployerProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseEmployerProfile {

    private final DatabaseReference jobPostsRef;
    private final DatabaseReference employerRef;

    public FirebaseEmployerProfile() {

        this.jobPostsRef = FirebaseDatabase.getInstance().getReference("job_posts");
        this.employerRef = FirebaseDatabase.getInstance().getReference("users/employer");
    }

    public void fetchEmployerProfile(String jobId, OnEmployerProfileFetchedListener listener) {

        jobPostsRef.child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot jobSnapshot) {

                String jobPosterId = jobSnapshot.child("jobPosterID").getValue(String.class);

                employerRef.orderByChild("email").equalTo(jobPosterId).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot employerSnapshot) {

                        if (employerSnapshot.exists()) {
                            for (DataSnapshot employerNode : employerSnapshot.getChildren()) {
                                String name = employerNode.child("name").getValue(String.class);
                                String email = employerNode.child("email").getValue(String.class);
                                String phone = employerNode.child("phone").getValue(String.class);
                                String ratingValue = employerNode.child("ratingValue").getValue(String.class);
                                String ratingCount = employerNode.child("ratingCount").getValue(String.class);
                                EmployerProfile employerProfile = new EmployerProfile(name, email, phone, ratingValue, ratingCount);
                                listener.onProfileFetched(employerProfile);
                            }
                        } else {
                            listener.onError("Employer not found.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onError("Error fetching employer details: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError("Error fetching job details: " + error.getMessage());
            }
        });
    }

    public interface OnEmployerProfileFetchedListener {

        void onProfileFetched(EmployerProfile employerProfile);
        void onError(String errorMessage);

    }

}

