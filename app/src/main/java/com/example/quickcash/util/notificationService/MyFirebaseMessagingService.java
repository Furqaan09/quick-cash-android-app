package com.example.quickcash.util.notificationService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.quickcash.R;
import com.example.quickcash.ui.EmployeeActivity;
import com.example.quickcash.ui.JobDetailsActivity;
import com.example.quickcash.util.jobPost.JobPost;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String PREFERRED_EMPLOYER_CHANNEL = "preferred_employer_notifications";
    private static final String PREFERRED_JOBS_CHANNEL = "preferred_jobs_notifications";

    // Creating a token, registering with the firebase messaging service
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM Token", "Token: " + token);
    }

    // Main method
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d("message received","received"+message);

        Map<String, String> data = message.getData();

        // Check if the notification contains a payload
        if (message.getNotification() != null) {
            String title = message.getNotification().getTitle();
            String body = message.getNotification().getBody();

            // If data contains role key then it is a preferred "job" notification else preferred "employer"
            if (data.containsKey("role")) {
                // Extract job details from the data payload
                String jobId = data.get("jobId");
                String jobPosterId = data.get("jobPosterId");
                String jobTitle = data.get("jobTitle");
                String companyName = data.get("companyName");
                String description = data.get("description");
                String jobType = data.get("jobType");
                String experienceLevel = data.get("experienceLevel");
                String industry = data.get("industry");
                String jobLocation = data.get("jobLocation");
                String postedDate = data.get("postedDate");
                String role = data.get("role");

                // Create a JobPost object to pass to the activity
                JobPost jobPost = new JobPost(
                        jobId,
                        jobPosterId,
                        jobTitle,
                        jobLocation,
                        jobType,
                        postedDate,
                        companyName,
                        description,
                        experienceLevel,
                        industry
                );

                // Intent to open JobDetailsActivity with job details
                Intent intent = new Intent(this, JobDetailsActivity.class);
                intent.putExtra("jobPost", jobPost);
                intent.putExtra("role", role);

                PendingIntent pendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                // Create and display the notification for preferred jobs
                createNotification(title, body, pendingIntent, PREFERRED_JOBS_CHANNEL, "Preferred Jobs Notifications");
            } else {
                // Intent to open EmployeeActivity
                Intent intent = new Intent(this, EmployeeActivity.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                // Create and display the notification for preferred jobs
                createNotification(title, body, pendingIntent, PREFERRED_EMPLOYER_CHANNEL, "Preferred Employer Notifications");
            }
        }
    }

    private void createNotification(String title, String body, PendingIntent pendingIntent, String channelId, String channelName) {
        // Create notification channel
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Display the notification
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }
}
