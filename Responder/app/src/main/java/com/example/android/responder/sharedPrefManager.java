package com.example.android.responder;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by GrayShadow on 4/9/17.
 */

public class sharedPrefManager {

    private static final String SHARED_PREF_NAME="firebaseSharedPref";
    private static final String KEY_ACCESS_TOKEN="token";

    private static Context mCtx;
    private static sharedPrefManager mInstance;

    private sharedPrefManager(Context context)
    {
        mCtx = context;
    }

    public static synchronized sharedPrefManager getInstance(Context context)
    {
        if(mInstance == null)
            mInstance = new sharedPrefManager(context);
        return mInstance;
    }

    public boolean storeToken(String token)
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();

        return true;
    }

    public String getToken()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

}
