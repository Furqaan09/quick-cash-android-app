package com.example.quickcash.firebase;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebasePreferredJobs {

    private DatabaseReference preferredJobsRef;
    private FirebaseNotificationSubscriptionManager subscriptionManager;
    private String currentUserUID;

    // Constructor
    public FirebasePreferredJobs() {
        this.preferredJobsRef = FirebaseDatabase.getInstance().getReference("preferred_jobs");
        this.currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.subscriptionManager = new FirebaseNotificationSubscriptionManager();
    }

    // Add a job title to the preferred list
    public void addPreferredJob(String jobTitle) {
        String formattedJobTitle = jobTitle.replace(" ", "_"); // Replace spaces with underscores
        preferredJobsRef.child(currentUserUID).child(formattedJobTitle).setValue(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("PreferredJobs", "Added preferred job: " + jobTitle);
                        subscriptionManager.subscribeToPreferredJobs();
                    } else {
                        Log.e("PreferredJobs", "Failed to add preferred job: " + jobTitle);
                    }
                });
    }

    // Remove a job title from the preferred list
    public void removePreferredJob(String jobTitle) {
        String formattedJobTitle = jobTitle.replace(" ", "_"); // Replace spaces with underscores
        preferredJobsRef.child(currentUserUID).child(formattedJobTitle).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("PreferredJobs", "Removed preferred job: " + jobTitle);
                        subscriptionManager.unsubscribeFromPreferredJobs();
                    } else {
                        Log.e("PreferredJobs", "Failed to remove preferred job: " + jobTitle);
                    }
                });
    }
}
