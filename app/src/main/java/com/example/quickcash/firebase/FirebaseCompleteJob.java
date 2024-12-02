package com.example.quickcash.firebase;

import androidx.annotation.NonNull;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseCompleteJob {

    private DatabaseReference applicationsRef;

    public FirebaseCompleteJob() {
        this.applicationsRef = FirebaseDatabase.getInstance().getReference("applications");
    }

    /**
     * Find hired applicant for a given job and change status
     * from "hired" to "complete".
     */
    public void setApplicantStatusComplete(String jobID) {
        applicationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot applicationSnapshot : snapshot.getChildren()) {
                    String currentJobId = applicationSnapshot.child("jobId").getValue(String.class);
                    String applicantStatus = applicationSnapshot.child("applicantStatus").getValue(String.class);

                    if (jobID.equals(currentJobId) && "Hired".equalsIgnoreCase(applicantStatus)) {
                        // Change the status to "complete"
                        applicationSnapshot.getRef().child("applicantStatus").setValue("Complete")
                                .addOnSuccessListener(aVoid -> System.out.println("Applicant status updated to complete."))
                                .addOnFailureListener(e -> System.err.println("Failed to update applicant status: " + e.getMessage()));
                        break; // Exit loop once the desired applicant is found and updated
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error reading applications: " + error.getMessage());
            }
        });
    }

    /**
     * Return the salary for a completed job using jobID to be passed
     * to payment page
     */
    public void getSalaryForCompletedJob(String jobID, SalaryCallback callback) {
        applicationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot applicationSnapshot : snapshot.getChildren()) {
                    String currentJobId = applicationSnapshot.child("jobId").getValue(String.class);
                    String applicantStatus = applicationSnapshot.child("applicantStatus").getValue(String.class);

                    if (jobID.equals(currentJobId) && "complete".equalsIgnoreCase(applicantStatus)) {
                        // Salary value is stored in the application. Adjust field name as needed
                        String salary = applicationSnapshot.child("salary").getValue(String.class);
                        if (salary != null) {
                            callback.onSalaryRetrieved(salary); // Pass the salary string to the callback
                        } else {
                            callback.onSalaryRetrieved("Salary not found");
                        }
                        return; // Exit after the first match
                    }
                }
                // If no match found or the status isn't "Complete"
                callback.onSalaryRetrieved("No completed application found for this job.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error reading applications: " + error.getMessage());
                callback.onSalaryRetrieved("Error retrieving salary.");
            }
        });
    }

    /**
     * Callback interface for salary retrieval.
     */
    public interface SalaryCallback {
        void onSalaryRetrieved(String salary);
    }

    /**
     * Check if a job is eligible for payment
     */
    public void isApplicantStatusComplete(String jobID, StatusCheckCallback callback) {
        applicationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isComplete = false;

                for (DataSnapshot applicationSnapshot : snapshot.getChildren()) {
                    String currentJobId = applicationSnapshot.child("jobId").getValue(String.class);
                    String applicantStatus = applicationSnapshot.child("applicantStatus").getValue(String.class);

                    if (jobID.equals(currentJobId) && "complete".equalsIgnoreCase(applicantStatus)) {
                        isComplete = true;
                        break; // Exit the loop once the status is found
                    }
                }

                // Pass the result to the callback
                callback.onStatusChecked(isComplete);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error reading applications: " + error.getMessage());
                callback.onStatusChecked(false); // Return false in case of an error
            }
        });
    }

    /**
     * Callback interface for status check.
     */
    public interface StatusCheckCallback {
        void onStatusChecked(boolean isComplete);
    }

    /**
     * Set the payment status to "Paid" for a given applicant using jobId and status
     */
    public void setPaymentStatusPaid(String jobID) {
        applicationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot applicationSnapshot : snapshot.getChildren()) {
                    String currentJobId = applicationSnapshot.child("jobId").getValue(String.class);
                    String applicantStatus = applicationSnapshot.child("applicantStatus").getValue(String.class);

                    if (jobID.equals(currentJobId) && "Complete".equalsIgnoreCase(applicantStatus)) {
                        // Change the payment status to "Paid"
                        applicationSnapshot.getRef().child("paymentStatus").setValue("Paid")
                                .addOnSuccessListener(aVoid -> System.out.println("Payment status updated to Paid."))
                                .addOnFailureListener(e -> System.err.println("Failed to update payment status: " + e.getMessage()));
                        break; // Exit loop once the desired applicant is found and updated
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error reading applications: " + error.getMessage());
            }
        });
    }

    /**
     * Check if applicant is paid using jobID and status
     */
    public void isPaymentStatusPaid(String jobID, PaymentStatusCheckCallback callback) {
        applicationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isPaid = false;

                for (DataSnapshot applicationSnapshot : snapshot.getChildren()) {
                    String currentJobId = applicationSnapshot.child("jobId").getValue(String.class);
                    String paymentStatus = applicationSnapshot.child("paymentStatus").getValue(String.class);

                    if (jobID.equals(currentJobId) && "Paid".equalsIgnoreCase(paymentStatus)) {
                        isPaid = true;
                        break; // Exit the loop once the status is found
                    }
                }

                // Pass the result to the callback
                callback.onPaymentStatusChecked(isPaid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error reading applications: " + error.getMessage());
                callback.onPaymentStatusChecked(false); // Return false in case of an error
            }
        });
    }

    public interface PaymentStatusCheckCallback {
        void onPaymentStatusChecked(boolean isPaid);
    }


}
