package com.puristit.livechat.fcm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Anas on 3/18/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{

    private Context mContext;
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String TOKEN = "token";

    @Override
    public void onTokenRefresh()
    {
        mContext = this;

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("Token", "Token: " + refreshedToken);
        // Notify UI that registration has completed, so the progress indicator can be hidden.

        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        registrationComplete.putExtra(TOKEN, refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

    }
}