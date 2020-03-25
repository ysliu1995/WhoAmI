package com.ncbci.whoami.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ncbci.whoami.Activity.MainActivity;
import com.ncbci.whoami.R;

public class FCMService extends FirebaseMessagingService {
    static final String TAG = "FCMService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Initial service");
        initFCM();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Check if message contains a data payload.
        Log.d(TAG, remoteMessage.getNotification().getTitle()+"");
        Log.d(TAG, remoteMessage.getNotification().getTitle());
        Log.d(TAG, remoteMessage.getNotification().getBody());
        ShowNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void initFCM(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                Log.d(TAG, "token: " + task.getResult().getToken());
                sendRegistrationToServer(task.getResult().getToken());
            }
        });
    }

    private void sendRegistrationToServer(String token) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth != null){
            DatabaseReference mdatabse = FirebaseDatabase.getInstance().getReference();
            mdatabse.child("Users").child(mAuth.getUid()).child("fcmToken").setValue(token);
        }
    }

    private void ShowNotification(String title, String body){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_ID = "Who Am I";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_ID, "Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Let's walk channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationbuilder = new NotificationCompat.Builder(this, NOTIFICATION_ID);
        Intent ResultIntent = new Intent(this, MainActivity.class);
        PendingIntent ResultPendingIntent = PendingIntent.getActivity(this, 1, ResultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationbuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(body)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        notificationManager.notify(1, notificationbuilder.build());
    }
}
