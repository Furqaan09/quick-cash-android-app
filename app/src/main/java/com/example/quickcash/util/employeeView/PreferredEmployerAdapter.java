package com.example.quickcash.util.employeeView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;

import java.util.ArrayList;

public class PreferredEmployerAdapter extends RecyclerView.Adapter<PreferredEmployerAdapter.PreferredEmployerViewHolder> {
    // Define the interface for click callbacks.
    public interface OnItemClickListener {
        void onClickRemoveButton(PreferredEmployer preferredEmployer);
    }

    private ArrayList<PreferredEmployer> preferredEmployers;
    private OnItemClickListener listener;

    // Modify the constructor to accept the listener.
    public PreferredEmployerAdapter(ArrayList<PreferredEmployer> preferredEmployers, OnItemClickListener listener) {
        this.preferredEmployers = preferredEmployers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PreferredEmployerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.preferred_employer_item_view, parent, false);
        return new PreferredEmployerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferredEmployerViewHolder holder, int position) {
        PreferredEmployer employer = preferredEmployers.get(position);
        holder.bind(employer, listener);
    }

    @Override
    public int getItemCount() {
        return preferredEmployers.size();
    }

    public static class PreferredEmployerViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView email;
        TextView phone;
        Button removeButton;

        public PreferredEmployerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.employerNameTitle);
            email = itemView.findViewById(R.id.employerEmailTextView);
            phone = itemView.findViewById(R.id.employerPhoneTextView);
            removeButton = itemView.findViewById(R.id.removeEmployerButton);
        }

        public void bind(PreferredEmployer employer, OnItemClickListener listener) {
            String employerName = employer.getName();
            String employerEmail = employer.getEmail();
            String employerPhone = employer.getPhone();

            name.setText("Employer Name: " + employerName);
            email.setText("Email: " + employerEmail);
            phone.setText("Phone: " + employerPhone);

            // Set up click listener for remove Button
            removeButton.setOnClickListener(v -> listener.onClickRemoveButton(employer));
        }
    }

}
