package com.example.android.responder;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by GrayShadow on 4/9/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "notification";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        //Log.d(TAG, remoteMessage.getNotification().toString());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        notifyUser(remoteMessage.getFrom(), remoteMessage.getNotification().getBody());
    }

    public void notifyUser(String from, String notification)
    {
        MyNotificationManger mNotificationManager = new MyNotificationManger(getApplicationContext());
        mNotificationManager.showNotification(from, notification, new Intent(getApplicationContext(), WelcomePatient.class));
    }
}
