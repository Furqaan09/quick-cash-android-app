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

    // Modify the constructor to accept the listener.
    public JobPostAdapter(ArrayList<JobPost> jobPostList, OnItemClickListener listener) {
        this.jobPostList = jobPostList;
        this.listener = listener;
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

        public JobPostViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            jobLocation = itemView.findViewById(R.id.jobLocation);
            jobType = itemView.findViewById(R.id.jobType);
            postedDate = itemView.findViewById(R.id.postedDate);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
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
        }
    }
}
