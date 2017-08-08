package com.example.sarika.alzheimerassistant.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.Details;
import com.example.sarika.alzheimerassistant.network.CaregiverApiRequests;
import com.example.sarika.alzheimerassistant.network.GsonPostRequest;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

/**
 * Created by sarika on 4/16/17.
 */

public class FCMRegistrationService extends IntentService {

    private static String TAG = FCMRegistrationService.class.getName();

    public FCMRegistrationService(){super(TAG);}

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FCMRegistrationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String fcmToken = null;
        String token = intent.getStringExtra(Util.TOKEN);
        try {
            // request token that will be used by the server to send push notifications
            fcmToken = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "FCM Registration Token: " + fcmToken);
            // save token
            storeRegIdInPref(fcmToken,getApplicationContext());
            // pass along this data
            sendRegistrationToServer(token,fcmToken);
            // Subscribe to topic channels
            subscribeTopics(fcmToken);
        } catch (IOException e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            com.example.sarika.alzheimerassistant.other.PreferenceManager.getSharedPreference(getApplicationContext()).edit().putBoolean(Util.SENT_TOKEN_TO_SERVER, false).apply();

        }

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Util.REGISTRATION_COMPLETE);
        if(fcmToken != null) {
            registrationComplete.putExtra("token", fcmToken);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

    }

    private void subscribeTopics(String token) throws IOException {
        FirebaseMessaging pubSub = FirebaseMessaging.getInstance();
        for (String topic : Util.TOPICS) {
            pubSub.subscribeToTopic("/topics/" + topic);
        }
    }

    private void sendRegistrationToServer(String token, String fcmToken)  {

        //POST REQUEST
        GsonPostRequest gsonPostRequest = CaregiverApiRequests.postFcmRegistrationRequest(
                new Response.Listener<Details>() {
                    @Override
                    public void onResponse(Details response) {
                        Log.d(TAG,response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                },
                token, fcmToken
        );
        App.addRequest(gsonPostRequest,TAG);

    }

    private void storeRegIdInPref(String token, Context context) {

        com.example.sarika.alzheimerassistant.other.PreferenceManager.storeRegIdInPref(token,context);

    }


}
