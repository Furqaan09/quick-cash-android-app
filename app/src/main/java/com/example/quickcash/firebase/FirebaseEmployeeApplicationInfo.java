package com.example.quickcash.firebase;

import androidx.annotation.NonNull;

import com.example.quickcash.util.employeeView.ApplicationData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseEmployeeApplicationInfo {

    private DatabaseReference applicationsRef;
    private DatabaseReference jobPostsRef;

    public FirebaseEmployeeApplicationInfo() {
        this.applicationsRef = FirebaseDatabase.getInstance().getReference("applications");
        this.jobPostsRef = FirebaseDatabase.getInstance().getReference("job_posts");
    }

    /**
     * Return all job applications for a given employee
     * Search applications by employee email
     */
    public void returnEmployeeApplications(String employeeEmail, OnApplicationsLoadedListener listener) {
        applicationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ApplicationData> employeeApplications = new ArrayList<>();
                int totalApplications = (int) snapshot.getChildrenCount(); // Total nodes to process
                int[] processedCount = {0}; // Use array to track completion inside callback

                for (DataSnapshot applicationSnapshot : snapshot.getChildren()) {
                    String applicationId = applicationSnapshot.getKey();
                    String jobId = applicationSnapshot.child("jobId").getValue(String.class);
                    String applicationDate = applicationSnapshot.child("applicationDate").getValue(String.class);
                    String status = applicationSnapshot.child("applicantStatus").getValue(String.class);
                    String applicantEmail = applicationSnapshot.child("applicantEmail").getValue(String.class);
                    String employerRatingStatus = applicationSnapshot.child("employerReview").getValue(String.class);

                    if (employeeEmail.equals(applicantEmail) && jobId != null) {
                        fetchJobDetails(jobId, (jobTitle, companyName, jobLocation, jobPosterId) -> {
                            String jobIdAndTitle = jobTitle + "- #" + jobId;
                            ApplicationData applicationData = new ApplicationData(
                                    jobIdAndTitle,
                                    companyName,
                                    jobLocation,
                                    applicationDate,
                                    status,
                                    jobPosterId,
                                    applicationId,
                                    employerRatingStatus
                            );
                            employeeApplications.add(applicationData);

                            processedCount[0]++; // Increment processed count
                            if (processedCount[0] == totalApplications) {
                                listener.onApplicationsLoaded(employeeApplications); // Notify when all are done
                            }
                        });
                    } else {
                        processedCount[0]++; // Increment even for skipped entries
                        if (processedCount[0] == totalApplications) {
                            listener.onApplicationsLoaded(employeeApplications); // Notify when all are done
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error loading applications: " + error.getMessage());
            }
        });
    }


    private void fetchJobDetails(String jobId, JobDetailsCallback callback) {
        jobPostsRef.child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String jobTitle = snapshot.child("jobTitle").getValue(String.class);
                String companyName = snapshot.child("companyName").getValue(String.class);
                String jobLocation = snapshot.child("location").getValue(String.class);
                String jobPosterID = snapshot.child("jobPosterID").getValue(String.class);
                callback.onJobDetailsFetched(jobTitle, companyName, jobLocation, jobPosterID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error fetching job details: " + error.getMessage());
            }
        });
    }

    public interface OnApplicationsLoadedListener {
        void onApplicationsLoaded(ArrayList<ApplicationData> applications);
    }

    private interface JobDetailsCallback {
        void onJobDetailsFetched(String jobTitle, String companyName, String jobLocation, String employerEmail);
    }
}
