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

    private static final String CREDENTIALS_FILE_PATH = "key.json"; // path to firebase credenials folder
    private static final String PUSH_NOTIFICATION_ENDPOINT = "https://fcm.googleapis.com/v1/projects/quick-cash-64e58/messages:send";
    private final RequestQueue requestQueue;
    private final Context context;

    public FirebasePreferredJobPostNotifier(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context); // init volley RequestQueue for network requests
    }

    /**
     * retrieves OAuth2 access token needed for authenticating requests to FCM.
     * retrieved using the firebase service account credentials
     */
    private void getAccessToken(AccessTokenListener listener) {
        ExecutorService executorService = Executors.newSingleThreadExecutor(); // run token retrieval in background thread
        executorService.execute(() -> {
            try {
                InputStream serviceAccountStream = context.getAssets().open(CREDENTIALS_FILE_PATH); // load service acc json file
                GoogleCredentials googleCredentials = GoogleCredentials
                        .fromStream(serviceAccountStream)
                        .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));

                googleCredentials.refreshIfExpired(); // refresh token if expired
                String token = googleCredentials.getAccessToken().getTokenValue(); // get access token
                listener.onAccessTokenReceived(token);
                Log.d("Token", "Token: " + token); // debugging
            } catch (IOException e) {
                listener.onAccessTokenError(e); // handle errors during token retriebal
            }
        });
        executorService.shutdown(); // shut down executor service
    }

    // interface for handling token retrieval callbacks
    public interface AccessTokenListener {
        void onAccessTokenReceived(String token); // when token is retrieved
        void onAccessTokenError(Exception exception); // error retrieving token
    }

    // sends notification to user of job post with title matching a preferred job
    public void sendJobNotification(String jobTitle, String jobType, String jobLocation, String jobId) {
        // access token to authenticate notification request
        getAccessToken(new AccessTokenListener() {
            @Override
            public void onAccessTokenReceived(String token) {
                try {
                    // notification body
                    JSONObject notificationBody = new JSONObject();
                    notificationBody.put("title", "New job matching your preference!"); // noti title
                    notificationBody.put("body", jobTitle + "\n" + jobType + "\n" + jobLocation); // noti content

                    // message for notification
                    JSONObject message = new JSONObject();
                    message.put("topic", "preferred_job_" + jobTitle.replace(" ", "_"));
                    message.put("notification", notificationBody);

                    // to be sent to FCM
                    JSONObject notificationPayload = new JSONObject();
                    notificationPayload.put("message", message);

                    // post request to FCM endpoint
                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.POST,
                            PUSH_NOTIFICATION_ENDPOINT,
                            notificationPayload,
                            response -> Log.d("NotificationResponse", "Response: " + response.toString()), // log success
                            error -> {
                                Log.e("NotificationError", "Error: " + error.toString()); // log error
                                Toast.makeText(context, "Failed to send notification", Toast.LENGTH_SHORT).show();
                            }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            // headers for authentication and content type
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Content-Type", "application/json; charset=UTF-8");
                            headers.put("Authorization", "Bearer " + token); // bearer token for authentication
                            return headers;
                        }
                    };

                    // add request to volley queue
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
