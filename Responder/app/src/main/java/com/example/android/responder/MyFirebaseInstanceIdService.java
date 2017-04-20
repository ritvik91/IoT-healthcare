package com.example.android.responder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.*;

/**
 * Created by GrayShadow on 4/8/17.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public static final String BROADCAST_TOKEN = "fcmBroadcastToken";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("RefreshedToken", "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        getApplicationContext().sendBroadcast(new Intent(BROADCAST_TOKEN));
        storeToken(refreshedToken);
    }

    private void storeToken(String token)
    {
        sharedPrefManager.getInstance(getApplicationContext()).storeToken(token);
    }
}
