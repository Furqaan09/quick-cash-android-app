package com.example.quickcash.util.employeeView;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;

import java.util.ArrayList;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    // Define the interface for click callbacks.
    public interface OnItemClickListener {
        void onViewOfferClick(ApplicationData applicationData);
        void onRateEmployerClick(ApplicationData applicationData);
    }

    private ArrayList<ApplicationData> applications;
    private ApplicationAdapter.OnItemClickListener listener;

    // Modify the constructor to accept the listener.
    public ApplicationAdapter(ArrayList<ApplicationData> applications, OnItemClickListener listener) {
        this.applications = applications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_application_item_view, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        ApplicationData application = applications.get(position);
        holder.bind(application, listener);
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    public static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitleAndId;
        TextView companyName;
        TextView jobLocation;
        TextView appliedDate;
        TextView applicationStatus;
        Button viewOfferButton;
        Button rateEmployerButton;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitleAndId = itemView.findViewById(R.id.jobTitleAndID);
            companyName = itemView.findViewById(R.id.jobCompany);
            jobLocation = itemView.findViewById(R.id.jobLocation);
            appliedDate = itemView.findViewById(R.id.applicationDate);
            applicationStatus = itemView.findViewById(R.id.statusTextView);
            viewOfferButton = itemView.findViewById(R.id.viewOfferButton);
            rateEmployerButton = itemView.findViewById(R.id.rateEmployerButton);
        }

        public void bind(ApplicationData applicationData, ApplicationAdapter.OnItemClickListener listener) {
            String title = applicationData.getJobIdAndTitle();
            String company = applicationData.getCompanyName();
            String location = applicationData.getJobLocation();
            String date = applicationData.getApplicationDate();
            String status = applicationData.getStatus();
            String ratingStatus = applicationData.getEmployerRatingStatus();

            jobTitleAndId.setText("Job Title: " + title);
            companyName.setText("Company: " + company);
            jobLocation.setText("Location: " + location);
            appliedDate.setText("Application Date: " + date);
            applicationStatus.setText(status);

            manageFragment(status, ratingStatus);

            // Set up click listener for viewDetailsButton
            viewOfferButton.setOnClickListener(v -> {
                listener.onViewOfferClick(applicationData);
            });

            rateEmployerButton.setOnClickListener(v -> {
                listener.onRateEmployerClick(applicationData);
            });
        }

        public void manageFragment(String status, String ratingStatus) {
            if (status.equals("Shortlisted")) {
                applicationStatus.setTextColor(Color.parseColor("#D9C711"));
            } else if (status.equals("Rejected")) {
                applicationStatus.setTextColor(Color.parseColor("#FF0000"));
            } else if (status.equals("Hired")) {
                applicationStatus.setTextColor(Color.parseColor("#1EAB0C"));
            } else if (status.equals("Pending")) {
                applicationStatus.setVisibility(View.GONE);
                viewOfferButton.setVisibility(View.VISIBLE);
            } else if (status.equals("Completed")) {
                applicationStatus.setTextColor(Color.parseColor("#757575"));
                if (ratingStatus.equals("Not Reviewed")) {
                    rateEmployerButton.setVisibility(View.VISIBLE);
                }
            }
        }
    }

}
