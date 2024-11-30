package com.example.quickcash.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseNotificationSubscriptionManager {
    DatabaseReference preferredJobsRef = FirebaseDatabase.getInstance().getReference("preferred_jobs");
    String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public void subscribeToPreferredJobs() {
        preferredJobsRef.child(currentUserUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    String jobTitle = jobSnapshot.getKey();
                    if (jobTitle != null) {
                        FirebaseMessaging.getInstance().subscribeToTopic("preferred_job_" + jobTitle)
                                .addOnSuccessListener(aVoid -> Log.d("PreferredJobs", "Subscribed to topic: " + jobTitle))
                                .addOnFailureListener(e -> Log.e("PreferredJobs", "Failed to subscribe: " + e.getMessage()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PreferredJobs", "Error loading preferred jobs: " + error.getMessage());
            }
        });
    }

    public void unsubscribeFromPreferredJobs() {
        preferredJobsRef.child(currentUserUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    String jobTitle = jobSnapshot.getKey();
                    if (jobTitle != null) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("preferred_job_" + jobTitle)
                                .addOnSuccessListener(aVoid -> Log.d("PreferredJobs", "Unsubscribed from topic: " + jobTitle))
                                .addOnFailureListener(e -> Log.e("PreferredJobs", "Failed to unsubscribe: " + e.getMessage()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PreferredJobs", "Error loading preferred jobs for unsubscribe: " + error.getMessage());
            }
        });
    }

}
