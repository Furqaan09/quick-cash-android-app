package com.example.quickcash.firebase;

import androidx.annotation.NonNull;

import com.example.quickcash.util.employerView.HiredEmployee;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseHiredEmployees {
    private DatabaseReference applicationsRef;
    private DatabaseReference jobPostsRef;
    private ArrayList<HiredEmployee> hiredEmployees;

    public FirebaseHiredEmployees() {
        this.applicationsRef = FirebaseDatabase.getInstance().getReference("applications");
        this.jobPostsRef = FirebaseDatabase.getInstance().getReference("job_posts");
        this.hiredEmployees = new ArrayList<>();
    }

    public void returnHiredEmployees(String employerId, FirebaseHiredEmployees.OnEmployeesLoadedListener listener) {
        // Get all the job Posts created by the employer using employerID
        jobPostsRef.orderByChild("jobPosterID").equalTo(employerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hiredEmployees.clear();

                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    String jobId = jobSnapshot.getKey();
                    String jobTitle = jobSnapshot.child("jobTitle").getValue(String.class);
                    String companyName = jobSnapshot.child("companyName").getValue(String.class);

                    applicationsRef.orderByChild("jobId").equalTo(jobId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Clear employees list for this jobId to avoid stale data
                            hiredEmployees.removeIf(employee -> employee.getJobTitleAndId().contains(jobId));

                            for (DataSnapshot applicationSnapshot : snapshot.getChildren()) {
                                String status = applicationSnapshot.child("applicantStatus").getValue(String.class);
                                if ("Hired".equals(status) || "Completed".equals(status)) {
                                    String employeeName = applicationSnapshot.child("applicantName").getValue(String.class);
                                    String employeeEmail = applicationSnapshot.child("applicantEmail").getValue(String.class);
                                    String startDate = applicationSnapshot.child("startDate").getValue(String.class);
                                    String salary = applicationSnapshot.child("salary").getValue(String.class);
                                    String reviewStatus = applicationSnapshot.child("employeeReview").getValue(String.class);
                                    String applicationID = applicationSnapshot.getKey();

                                    String jobTitleAndId = jobTitle + " - #" + jobId;
                                    HiredEmployee hiredEmployee = new HiredEmployee(
                                            jobTitleAndId,
                                            companyName,
                                            startDate,
                                            salary,
                                            employeeName,
                                            employeeEmail,
                                            status,
                                            reviewStatus,
                                            applicationID
                                    );
                                    hiredEmployees.add(hiredEmployee);
                                }
                            }
                            listener.onEmployeesLoaded(hiredEmployees); // Signal completion for this jobId
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.err.println("Error loading applications: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error loading Hired Employees: " + error.getMessage());
            }
        });
    }

    public interface OnEmployeesLoadedListener {
        void onEmployeesLoaded(ArrayList<HiredEmployee> hiredEmployees);
    }

}
