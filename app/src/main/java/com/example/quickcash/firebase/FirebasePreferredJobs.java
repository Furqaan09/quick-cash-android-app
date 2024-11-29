package com.example.quickcash.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebasePreferredJobs {

    private final DatabaseReference preferredJobsRef;

    public FirebasePreferredJobs() {
        // reference to preferred jobs node in firebase
        preferredJobsRef = FirebaseDatabase.getInstance().getReference("preferred_jobs");
    }

    // add job to users preferred jobs
    public void addPreferredJob(String jobTitle) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (userId != null && jobTitle != null) {
            preferredJobsRef.child(userId).child(jobTitle).setValue(true)
                    .addOnSuccessListener(aVoid -> {
                        // successfully added to preferred jobs list
                        System.out.println("Job added to preferred jobs list.");
                    })
                    .addOnFailureListener(e -> {
                        // error
                        System.err.println("Failed to add job to preferred jobs list: " + e.getMessage());
                    });
        } else {
            System.err.println("Error: User ID or Job Title is null.");
        }
    }

    // remove a job from users preferred jobs list
    public void removePreferredJob(String jobTitle) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (userId != null && jobTitle != null) {
            preferredJobsRef.child(userId).child(jobTitle).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // successfully removed from preferred jobs list
                        System.out.println("Job removed from preferred jobs list.");
                    })
                    .addOnFailureListener(e -> {
                        // error
                        System.err.println("Failed to remove job from preferred jobs list: " + e.getMessage());
                    });
        } else {
            System.err.println("Error: User ID or Job Title is null.");
        }
    }

    // fetches preferred jobs for current user
    public DatabaseReference getPreferredJobsForCurrentUser() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId != null) {
            return preferredJobsRef.child(userId);
        }
        return null;
    }
}
