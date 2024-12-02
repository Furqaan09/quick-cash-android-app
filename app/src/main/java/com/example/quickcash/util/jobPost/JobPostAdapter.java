package com.example.quickcash.util.jobPost;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.firebase.FirebaseCompleteJob;
import com.example.quickcash.util.employerView.ProcessPaymentActivity;

import java.util.ArrayList;

public class JobPostAdapter extends RecyclerView.Adapter<JobPostAdapter.JobPostViewHolder> {

    // Define the interface for click callbacks.
    public interface OnItemClickListener {
        void onViewDetailsClick(JobPost jobPost);
    }

    private ArrayList<JobPost> jobPostList;
    private OnItemClickListener listener;
    private static String role;

    // Modify the constructor to accept the listener.
    public JobPostAdapter(ArrayList<JobPost> jobPostList, OnItemClickListener listener, String role) {
        this.jobPostList = jobPostList;
        this.listener = listener;
        this.role = role;
    }

    @NonNull
    @Override
    public JobPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item_view, parent, false);
        return new JobPostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull JobPostViewHolder holder, int position) {
        JobPost jobPost = jobPostList.get(position);
        holder.bind(jobPost, listener);
    }

    @Override
    public int getItemCount() {
        return jobPostList.size();
    }

    public static class JobPostViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle;
        TextView jobLocation;
        TextView jobType;
        TextView postedDate;
        Button viewDetailsButton;
        Button markCompleteButton;
        Button processPaymentButton;
        FirebaseCompleteJob completeJob;

        public JobPostViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            jobLocation = itemView.findViewById(R.id.jobLocation);
            jobType = itemView.findViewById(R.id.jobType);
            postedDate = itemView.findViewById(R.id.postedDate);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
            markCompleteButton = itemView.findViewById(R.id.markCompleteButton);
            processPaymentButton = itemView.findViewById(R.id.processPaymentButton);
            completeJob = new FirebaseCompleteJob();
        }

        public void bind(JobPost jobPost, OnItemClickListener listener) {
            jobTitle.setText("Job Title: " + jobPost.getJobTitle());
            jobLocation.setText("Location: " + jobPost.getLocation());
            jobType.setText("Job Type: " + jobPost.getJobType());
            postedDate.setText("Posted Date: " + jobPost.getPostedDate());

            // Set up click listener for viewDetailsButton
            viewDetailsButton.setOnClickListener(v -> {
                listener.onViewDetailsClick(jobPost);
            });

            if (role.equals("employer")){
                // Handle Mark Complete button functionality
                markCompleteButton.setOnClickListener(v -> {
                    // Change applicant status to "Complete"
                    completeJob.setApplicantStatusComplete(jobPost.getJobID());
                    // Hide the Mark Complete button
                    markCompleteButton.setVisibility(View.GONE);
                    processPaymentButton.setVisibility(View.VISIBLE);
                });

                // Handle Process Payment button visibility and functionality
                completeJob.isApplicantStatusComplete(jobPost.getJobID(), isComplete -> {
                    if (isComplete) {
                        markCompleteButton.setVisibility(View.GONE);
                        processPaymentButton.setVisibility(View.VISIBLE);
                    } else {
                        processPaymentButton.setVisibility(View.GONE);
                    }
                });

                // Hide payment button if payment has already been made
                completeJob.isPaymentStatusPaid(jobPost.getJobID(), isPaid -> {
                    if (isPaid) {
                        // Payment has been made
                        markCompleteButton.setVisibility(View.GONE);
                        processPaymentButton.setVisibility(View.GONE);
                    }
                });

                processPaymentButton.setOnClickListener(view -> {
                    // Navigate to the payment page
                    Context context = itemView.getContext(); // Get the context from the view
                    Intent intent = new Intent(context, ProcessPaymentActivity.class);
                    intent.putExtra("jobId", jobPost.getJobID());

                    // Get the salary for the completed job and move to payment page
                    completeJob.getSalaryForCompletedJob(jobPost.getJobID(), salary -> {
                        // Handle the retrieved salary (e.g., display it on the UI)
                        intent.putExtra("salary", salary);
                        context.startActivity(intent);
                    });
                });
            } else {
                markCompleteButton.setVisibility(View.GONE);
                processPaymentButton.setVisibility(View.GONE);
            }
        }
    }
}
