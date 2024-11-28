package com.example.quickcash.firebase;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirebasePreferredEmployerJobPostNotifier {

    private static final String CREDENTIALS_FILE_PATH = "key.json";
    private static final String PUSH_NOTIFICATION_ENDPOINT = "https://fcm.googleapis.com/v1/projects/quick-cash-64e58/messages:send";
    private final RequestQueue requestQueue;
    private final Context context;

    private String employerUID;
    private String employerName;

    public FirebasePreferredEmployerJobPostNotifier(Context context, String employerUID) {
        this.context = context;
        this.employerUID = employerUID;
        this.requestQueue = Volley.newRequestQueue(context);
        setEmployerName();
    }

    private void setEmployerName() {
        DatabaseReference employerRef = FirebaseDatabase.getInstance().getReference("users/employer");
        employerRef.child(employerUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                employerName = snapshot.child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Employer Details", "Error loading employer details "+error);
            }
        });
    }

    private void getAccessToken(AccessTokenListener listener) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                InputStream serviceAccountStream = context.getAssets().open(CREDENTIALS_FILE_PATH);
                GoogleCredentials googleCredentials = GoogleCredentials
                        .fromStream(serviceAccountStream)
                        .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));

                googleCredentials.refreshIfExpired(); // This will refresh the token if it's expired
                String token = googleCredentials.getAccessToken().getTokenValue();
                listener.onAccessTokenReceived(token);
                Log.d("token", "token" + token);
            } catch (IOException e) {
                listener.onAccessTokenError(e);
            }
        });
        executorService.shutdown();
    }

    public interface AccessTokenListener {
        void onAccessTokenReceived(String token);

        void onAccessTokenError(Exception exception);
    }


    public void sendJobNotification(String jobTitle, String jobId, String jobType, String jobLocation) {
        getAccessToken(new AccessTokenListener() {
            @Override
            public void onAccessTokenReceived(String token) {
                try {
                    // Build the notification payload
                    JSONObject notificationBody = new JSONObject();
                    notificationBody.put("title", employerName + " has just posted a new job");
                    notificationBody.put("body", jobTitle + " - #" + jobId + "\n" + jobType + "\n" + jobLocation);

                    JSONObject message = new JSONObject();
                    message.put("topic", "preferred_employer_" + employerUID);
                    message.put("notification", notificationBody);

                    JSONObject notificationPayload = new JSONObject();
                    notificationPayload.put("message", message);

                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.POST,
                            PUSH_NOTIFICATION_ENDPOINT,
                            notificationPayload,
                            response -> Log.d("NotificationResponse", "Response: " + response.toString()),
                            error -> {
                                Log.e("NotificationError", "Error: " + error.toString());
                                Toast.makeText(context, "Failed to send notification", Toast.LENGTH_SHORT).show();
                            }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Content-Type", "application/json; charset=UTF-8");
                            headers.put("Authorization", "Bearer " + token);
                            return headers;
                        }
                    };

                    requestQueue.add(request);
                } catch (JSONException e) {
                    Log.e("NotificationJSONException", "Error creating JSON: " + e.getMessage());
                }
            }

            @Override
            public void onAccessTokenError(Exception exception) {
                Log.e("AccessTokenError", "Error retrieving access token: " + exception.getMessage());
            }
        });
    }
}