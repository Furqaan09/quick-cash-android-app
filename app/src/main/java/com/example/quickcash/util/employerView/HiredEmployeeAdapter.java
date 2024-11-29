package com.example.quickcash.util.employerView;

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

public class HiredEmployeeAdapter extends RecyclerView.Adapter<HiredEmployeeAdapter.HiredEmployeeViewHolder> {

    // Define the interface for click callbacks.
    public interface OnItemClickListener {
        void onMarkCompleteClick(HiredEmployee employee);
        void onRateEmployeeClick(HiredEmployee employee);
    }

    private ArrayList<HiredEmployee> hiredEmployees;
    private OnItemClickListener listener;

    // Modify the constructor to accept the listener.
    public HiredEmployeeAdapter(ArrayList<HiredEmployee> hiredEmployees, OnItemClickListener listener) {
        this.hiredEmployees= hiredEmployees;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HiredEmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hired_employee_item_view, parent, false);
        return new HiredEmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HiredEmployeeViewHolder holder, int position) {
        HiredEmployee employee = hiredEmployees.get(position);
        holder.bind(employee, listener);
    }

    @Override
    public int getItemCount() {
        return hiredEmployees.size();
    }

    public static class HiredEmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitleAndId;
        TextView jobCompany;
        TextView startDate;
        TextView salary;
        TextView employeeName;
        TextView employeeEmail;
        TextView jobStatus;
        Button markAsCompleteButton;
        Button rateEmployeeButton;

        public HiredEmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitleAndId = itemView.findViewById(R.id.jobTitleAndID);
            jobCompany = itemView.findViewById(R.id.jobCompany);
            startDate = itemView.findViewById(R.id.startDateTextView);
            salary = itemView.findViewById(R.id.salaryTextView);
            employeeName = itemView.findViewById(R.id.employeeNameTextView);
            employeeEmail = itemView.findViewById(R.id.employeeEmailTextView);
            jobStatus = itemView.findViewById(R.id.jobStatusTextView);
            markAsCompleteButton = itemView.findViewById(R.id.markAsCompleteButton);
            rateEmployeeButton = itemView.findViewById(R.id.rateEmployeeButton);
        }

        public void bind(HiredEmployee employee, OnItemClickListener listener) {
            String title = employee.getJobTitleAndId();
            String company = employee.getJobCompany();
            String jobSalary = employee.getSalary();
            String stDate = employee.getStartDate();
            String empEmail = employee.getEmployeeEmail();
            String empName = employee.getEmployeeName();
            String status = employee.getJobStatus();
            String ratingStatus = employee.getRatingStatus();

            jobTitleAndId.setText("Job Title: " + title);
            jobCompany.setText("Company: " + company);
            startDate.setText("Start Date: " + stDate);
            salary.setText("Salary: " + jobSalary);
            employeeName.setText("Employee Name: " + empName);
            employeeEmail.setText("Employee Email: " + empEmail);

            manageFragment(status, ratingStatus);

            // Set up click listener for viewDetailsButton
            markAsCompleteButton.setOnClickListener(v -> {
                listener.onMarkCompleteClick(employee);
            });

            rateEmployeeButton.setOnClickListener(v -> {
                listener.onRateEmployeeClick(employee);
            });
        }

        // Manage the buttons based on the retrieved status
        public void manageFragment(String status, String ratingStatus) {
            if (status.equals("Hired")) {
                jobStatus.setText("In Progress");
            } else if (status.equals("Completed")) {
                jobStatus.setTextColor(Color.parseColor("#757575"));
                jobStatus.setText(status);
                markAsCompleteButton.setVisibility(View.GONE);
                if (ratingStatus.equals("Not Reviewed")) {
                    rateEmployeeButton.setVisibility(View.VISIBLE);
                }
            }
        }
    }

}
