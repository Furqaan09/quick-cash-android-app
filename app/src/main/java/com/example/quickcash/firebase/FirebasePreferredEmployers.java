package com.example.quickcash.firebase;


import androidx.annotation.NonNull;

import com.example.quickcash.util.employeeView.PreferredEmployer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebasePreferredEmployers {

    // Instance variables
    private DatabaseReference prefEmployerRef;
    private DatabaseReference employerRef;
    private FirebaseNotificationSubscriptionManager subscriptionManager;

    // Constructor
    public FirebasePreferredEmployers() {
        this.prefEmployerRef = FirebaseDatabase.getInstance().getReference("preferred_employers");
        this.employerRef = FirebaseDatabase.getInstance().getReference("users/employer");
        this.subscriptionManager = new FirebaseNotificationSubscriptionManager();
    }

    public String getCurrentUserUID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // This method is used to return all preferred employers for the logged in employee
    public void returnAllPreferredEmployers(OnEmployersLoadedListener listener) {
        String currentUserUID = getCurrentUserUID();

        // Get the current users UID node under the preferred_employers node
        prefEmployerRef.child(currentUserUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<PreferredEmployer> preferredEmployers = new ArrayList<>();
                int totalEmployers = (int) snapshot.getChildrenCount(); // Count total preferred employers

                // No preferred employers, return empty list
                if (totalEmployers == 0) {
                    listener.onEmployersLoaded(preferredEmployers);
                    return;
                }

                // for each preferred employer node under the employee UID, get the employer UID
                for (DataSnapshot employerInfo : snapshot.getChildren()) {
                    String employerUID = employerInfo.getKey();

                    // Fetch employer details using employer UID
                    employerRef.child(employerUID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot employerSnapshot) {
                            String name = employerSnapshot.child("name").getValue(String.class);
                            String email = employerSnapshot.child("email").getValue(String.class);
                            String phone = employerSnapshot.child("phone").getValue(String.class);

                            // Create a new PreferredEmployer object with the data and add it to the list
                            PreferredEmployer employer = new PreferredEmployer(name, email, phone, employerUID);
                            preferredEmployers.add(employer);

                            // Once done return the list
                            listener.onEmployersLoaded(new ArrayList<>(preferredEmployers));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.err.println("Error loading employer info: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error loading preferred employers: " + error.getMessage());
                listener.onEmployersLoaded(new ArrayList<>()); // Return empty list on error
            }
        });
    }

    // This method removes the employer from the preferred employers list by deleting the node in firebase
    public void removePreferredEmployer(String employerUID, OnEmployerRemovedListener listener) {
        String currentUserUID = getCurrentUserUID();

        prefEmployerRef.child(currentUserUID).child(employerUID).removeValue((error, ref) -> {
            if (error == null) {
                // Unsubscribe from the topic to stop receiving notifications from this employer
                subscriptionManager.unsubscribeFromEmployerTopic();
                listener.onSuccess();
            }
        });
    }

    // This method adds an Employer to the Preferred employer list by creating a new node in firebase
    public void addPreferredEmployer(String employerEmail) {
        String currentUserUID = getCurrentUserUID();

        employerRef.orderByChild("email").equalTo(employerEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot employerSnapshot : snapshot.getChildren()) {
                    String employerUID = employerSnapshot.getKey();

                    prefEmployerRef.child(currentUserUID).child(employerUID).setValue(true).addOnSuccessListener(aVoid -> {
                        // Subscribe to the topic to start receiving notifications from this employer
                        subscriptionManager.subscribeToEmployerTopic();
                    });
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error loading employer info: " + error.getMessage());
            }
        });
    }


    public interface OnEmployersLoadedListener {
        void onEmployersLoaded(ArrayList<PreferredEmployer> preferredEmployers);
    }

    public interface OnEmployerRemovedListener {
        void onSuccess();
    }

}
