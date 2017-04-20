package com.example.android.responder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by GrayShadow on 4/9/17.
 */

public class MyNotificationManger {

    private Context mCtx;

    public static final int NOTIFICATION_ID = 2248;

    public MyNotificationManger(Context ctx)
    {
        mCtx = ctx;
    }

    public void showNotification(String from, String notification, Intent intent)
    {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                mCtx,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mCtx);
        Notification mNotification = builder.setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(from)
                .setContentText(notification)
                .build();

        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mNotification);
    }
}
