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
import com.example.quickcash.util.jobPost.JobPost;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.auth.FirebaseAuth;
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

public class FirebasePreferredJobPostNotifier {

    private static final String CREDENTIALS_FILE_PATH = "key.json";
    private static final String PUSH_NOTIFICATION_ENDPOINT = "https://fcm.googleapis.com/v1/projects/quick-cash-64e58/messages:send";
    private final RequestQueue requestQueue;
    private final Context context;

    public FirebasePreferredJobPostNotifier(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    private void getAccessToken(AccessTokenListener listener) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                InputStream serviceAccountStream = context.getAssets().open(CREDENTIALS_FILE_PATH);
                GoogleCredentials googleCredentials = GoogleCredentials
                        .fromStream(serviceAccountStream)
                        .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));

                googleCredentials.refreshIfExpired();
                String token = googleCredentials.getAccessToken().getTokenValue();
                listener.onAccessTokenReceived(token);
                Log.d("Token", "Token: " + token);
            } catch (IOException e) {
                listener.onAccessTokenError(e);
            }
        });
        executorService.shutdown();
    }

    // interface for handling token retrieval callbacks
    public interface AccessTokenListener {
        void onAccessTokenReceived(String token);
        void onAccessTokenError(Exception exception);
    }

    // Sends notification to user of job post with title matching a preferred job
    public void sendJobNotification(JobPost jobPost, String role) {
        // Format the job title to comply with Firebase topic naming rules
        String formattedJobTitle = jobPost.getJobTitle().replace(" ", "_");

        // Access token for Firebase authentication
        getAccessToken(new AccessTokenListener() {
            @Override
            public void onAccessTokenReceived(String token) {
                try {
                    // Build the notification body
                    JSONObject notificationBody = new JSONObject();
                    notificationBody.put("title", "A new job for " + jobPost.getJobTitle() + " has been posted!");
                    notificationBody.put("body", jobPost.getJobType() + "\n" + jobPost.getLocation());

                    // Include job post details in the data payload
                    JSONObject dataPayload = new JSONObject();
                    dataPayload.put("jobId", jobPost.getJobID());
                    dataPayload.put("jobPosterId", jobPost.getJobPosterID());
                    dataPayload.put("jobTitle", jobPost.getJobTitle());
                    dataPayload.put("companyName", jobPost.getCompanyName());
                    dataPayload.put("description", jobPost.getJobDescription());
                    dataPayload.put("jobType", jobPost.getJobType());
                    dataPayload.put("experienceLevel", jobPost.getExperienceLevel());
                    dataPayload.put("industry", jobPost.getIndustry());
                    dataPayload.put("jobLocation", jobPost.getLocation());
                    dataPayload.put("postedDate", jobPost.getPostedDate());
                    dataPayload.put("role", role); // Include the role for redirection logic

                    // Build the message payload
                    JSONObject message = new JSONObject();
                    message.put("topic", "preferred_job_" + formattedJobTitle);
                    message.put("notification", notificationBody);
                    message.put("data", dataPayload); // Attach data payload to the message

                    JSONObject notificationPayload = new JSONObject();
                    notificationPayload.put("message", message);

                    // Send the notification
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

                    // Add the request to the Volley queue
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
