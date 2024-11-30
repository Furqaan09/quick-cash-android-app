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

    // Instance variables
    private DatabaseReference preferredJobsRef;
    private DatabaseReference prefEmployerRef;
    private FirebaseMessaging firebaseMessaging;

    // Constructor
    public FirebaseNotificationSubscriptionManager() {
        this.prefEmployerRef = FirebaseDatabase.getInstance().getReference("preferred_employers");
        this.preferredJobsRef = FirebaseDatabase.getInstance().getReference("preferred_jobs");
        this.firebaseMessaging = FirebaseMessaging.getInstance();
    }

    // Method to get the current User's UID
    public String getCurrentUserUID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // This method will subscribe to all preferred jobs topic marked by this employee using firebase
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

    // This method will unsubscribe from all preferred jobs topic marked by this employee using firebase
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

    // This method will subscribe to all preferred employers topic marked by this employee using firebase
    public void subscribeToEmployerTopic() {
        String currentUserUID = getCurrentUserUID();

        prefEmployerRef.child(currentUserUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot employerSnapshot : snapshot.getChildren()) {
                    String employerUID = employerSnapshot.getKey();
                    FirebaseMessaging.getInstance().subscribeToTopic("preferred_employer_" + employerUID)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("PreferredEmployer", "Subscribed to topic: preferred_employer_" + employerUID);
                                } else {
                                    Log.e("PreferredEmployer", "Failed to subscribe to topic: preferred_employer_" + employerUID);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PreferredEmployer", "Error loading preferred employers: " + error.getMessage());
            }
        });
    }

    // This method will unsubscribe from all preferred employers topic marked by this employee using firebase
    public void unsubscribeFromEmployerTopic() {
        String currentUserUID = getCurrentUserUID();

        prefEmployerRef.child(currentUserUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot employerSnapshot : snapshot.getChildren()) {
                    String employerUID = employerSnapshot.getKey();
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("preferred_employer_" + employerUID)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("PreferredEmployer", "Unsubscribed from topic: preferred_employer_" + employerUID);
                                } else {
                                    Log.e("PreferredEmployer", "Failed to unsubscribe from topic: preferred_employer_" + employerUID);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PreferredEmployer", "Error loading preferred employers: " + error.getMessage());
            }
        });
    }
}
