package com.example.quickcash.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseNotificationSubscriptionManager {

    private DatabaseReference preferredJobsRef = FirebaseDatabase.getInstance().getReference("preferred_jobs");
    private FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();

    public String getCurrentUserUID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void subscribeToPreferredJobs() {
        String currentUserUID = getCurrentUserUID();
        preferredJobsRef.child(currentUserUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    String jobTitle = jobSnapshot.getKey();
                    firebaseMessaging.subscribeToTopic("preferred_job_" + jobTitle)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("PreferredJobs", "Subscribed to topic: " + jobTitle);
                                } else {
                                    Log.e("PreferredJobs", "Failed to subscribe: " + jobTitle);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PreferredJobs", "Error loading preferred jobs: " + error.getMessage());
            }
        });
    }

    public void unsubscribeFromPreferredJobs() {
        String currentUserUID = getCurrentUserUID();
        preferredJobsRef.child(currentUserUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    String jobTitle = jobSnapshot.getKey();
                    firebaseMessaging.unsubscribeFromTopic("preferred_job_" + jobTitle)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("PreferredJobs", "Unsubscribed to topic: " + jobTitle);
                                } else {
                                    Log.e("PreferredJobs", "Failed to unsubscribe: " + jobTitle);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PreferredJobs", "Error loading preferred jobs for unsubscribe: " + error.getMessage());
            }
        });
    }

}
