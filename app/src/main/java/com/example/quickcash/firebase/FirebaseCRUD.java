package com.example.quickcash.firebase;

import androidx.annotation.NonNull;

import com.example.quickcash.util.jobPost.JobPost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * FirebaseCRUD class abstracts Firebase Realtime Database operations for JobPost objects.
 */
public class FirebaseCRUD {

    private final DatabaseReference databaseReference;
    private final Map<String, JobPost> cachedJobPosts;

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    /**
     * Constructor initializes the database reference and the cache.
     * It sets up a listener to keep the cache updated with any changes in the database.
     *
     * @param firebaseDatabase the FirebaseDatabase instance.
     */
    public FirebaseCRUD(FirebaseDatabase firebaseDatabase) {
        this.databaseReference = firebaseDatabase.getReference("job_posts");
        this.cachedJobPosts = new HashMap<>();
        initializeCache();
    }

    /**
     * Initializes the cache by setting up a listener on the database reference.
     * The cache is updated whenever data changes in the "job_posts" node.
     */
    private void initializeCache() {

        // Set up a listener to update cachedJobPosts whenever data changes.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cachedJobPosts.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    JobPost jobPost = postSnapshot.getValue(JobPost.class);
                    if (jobPost != null) {
                        cachedJobPosts.put(jobPost.getJobID(), jobPost);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // Handle possible errors, e.g., log the error.
                System.err.println("Error updating cache: " + error.getMessage());
            }
        });
    }

    /**
     * Creates a new job post in the Firebase database.
     * Assumes the jobPost argument has a unique jobID.
     *
     * @param jobPost the JobPost object to create.
     */
    public void createJobPost(JobPost jobPost) {
        String jobID = jobPost.getJobID();
        databaseReference.child(jobID).setValue(jobPost);
    }

    /**
     * Retrieves the JobPost from the cache with the given jobID, if found.
     * Otherwise, returns null.
     *
     * @param jobID the job ID to search for.
     * @return the JobPost object if found, or null if not found.
     */
    public JobPost readJobPost(String jobID) {
        return cachedJobPosts.get(jobID);
    }

    /**
     * Retrieves all job posts from the cache.
     *
     * @return an ArrayList of all JobPost objects.
     */
    public ArrayList<JobPost> readAllJobPosts() {
        return new ArrayList<>(cachedJobPosts.values());
    }

}
