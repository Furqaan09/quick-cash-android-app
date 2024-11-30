package com.example.quickcash.firebase;

import android.content.Context;
import android.content.Intent;

import com.example.quickcash.ui.EmployeeActivity;
import com.example.quickcash.ui.EmployerActivity;
import com.example.quickcash.ui.LoginActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


// This class is used to check and sign in a user using Firebase
public class FirebaseLogin {
    // Instance variables
    private FirebaseAuth auth;
    private DatabaseReference dbRef;
    private FirebaseNotificationSubscriptionManager subscriptionManager;
    private FirebasePreferredEmployers preferredEmployers;

    // Constructor
    public FirebaseLogin() {
        this.dbRef = FirebaseDatabase.getInstance().getReference("users");
        this.auth = FirebaseAuth.getInstance();
        this.subscriptionManager = new FirebaseNotificationSubscriptionManager();
        this.preferredEmployers = new FirebasePreferredEmployers();
    }

    // This method is used to authenticate sign in depending on whether the user has an account
    public void checkUserInFirebase(Context context, LoginActivity loginActivity, String email,
                                    String password, String role) {
        String searchRole = role.toLowerCase();
        String searchEmail = email.toLowerCase();

        /* Search for the user under the role node using their email by querying the database to
           return results based on the email
         */
        dbRef.child(searchRole).orderByChild("email").equalTo(searchEmail).get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the results returned by the query
                DataSnapshot snapshot = task.getResult();

                /* Check if there is any data in the returned results
                 * If yes then sign in. If no then the user does not have an account
                 */
                if(snapshot.exists()) {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(signIn -> {
                        // If the email and password matches without any errors, login is successful
                        if (signIn.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();

                            if( user != null && user.isEmailVerified()){
                                loginActivity.setStatusMessage("Login Successful!");

                                // Redirect the user to the activity based on their role
                                if (role.equals("Employee")) {
                                    Intent employee = new Intent(context, EmployeeActivity.class);
                                    employee.putExtra("jobSeekerID", email);
                                    context.startActivity(employee);

                                    // Subscribe to the topics to start getting notifications
                                    subscriptionManager.subscribeToPreferredJobs();
                                    preferredEmployers.subscribeToEmployerTopic();
                                } else {
                                    Intent employer = new Intent(context, EmployerActivity.class);
                                    employer.putExtra("jobPosterID", email);
                                    context.startActivity(employer);
                                }
                            }else{
                                loginActivity.setStatusMessage("Email not verified. Please check your inbox.");
                            }
                        }
                        // If there is an error while signing in the user then get the error string
                        else {
                            String errorMessage = getString(signIn);
                            loginActivity.setStatusMessage(errorMessage);
                        }
                    });
                } else {
                    loginActivity.setStatusMessage("Sorry, we could not find your account!");
                }

            } else {
                loginActivity.setStatusMessage("Error checking user!");
            }
        });
    }

    // Get the error message if the authentication fails
    private String getString(Task<AuthResult> signIn) {
        String errorMessage;
        try {
            throw signIn.getException();
        } catch (FirebaseAuthInvalidCredentialsException e) {
            // Incorrect password
            errorMessage = "Incorrect Password!";
        } catch (Exception e) {
            // General error
            errorMessage = "Login failed: " + e.getMessage();
        }
        return errorMessage;
    }

}
