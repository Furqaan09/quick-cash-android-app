package com.example.quickcash.util.notificationService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.quickcash.R;
import com.example.quickcash.ui.EmployeeActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

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

        // Check if the notification contains a payload
        if (message.getNotification() != null) {
            String title = message.getNotification().getTitle();
            String body = message.getNotification().getBody();

            // Extract additional data if present
            Map<String, String> data = message.getData();
            String jobTitle = data.get("jobTitle");
            String jobId = data.get("jobId");
            String jobType = data.get("jobType");
            String jobLocation = data.get("jobLocation");


            // Create an intent that opens the activity showing the notification details
            Intent intent = new Intent(this, EmployeeActivity.class);
            intent.putExtra("jobTitle", jobTitle);
            intent.putExtra("jobId", jobId);
            intent.putExtra("jobType", jobType);
            intent.putExtra("jobLocation", jobLocation);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Create a notification channel
            String channelId = "preferred_employer_notifications";
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Preferred Employer Notifications",
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

}
