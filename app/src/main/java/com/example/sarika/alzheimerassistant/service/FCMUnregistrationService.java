package com.example.sarika.alzheimerassistant.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import static com.example.sarika.alzheimerassistant.util.Util.TOPICS;

/**
 * Created by sarika on 4/19/17.
 */

public class FCMUnregistrationService extends IntentService {
    private static final String TAG=FCMRegistrationService.class.getSimpleName();

    public FCMUnregistrationService(){super(TAG);}
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FCMUnregistrationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String token = null;
        FirebaseInstanceId instanceID = null;
        try {
            instanceID = FirebaseInstanceId.getInstance();
            token =instanceID.getToken();
            //Unsubscribe topics
            unsubscribeTopics(token);
            instanceID.deleteInstanceId();
            PreferenceManager.getSharedPreference(getApplicationContext()).edit().remove(Util.SENT_TOKEN_TO_SERVER).apply();
            // pass along this data
            sendUnregistrationToServer();
        }catch(IOException e){
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sendUnregistrationToServer();
        }

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Util.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

    }

    private void sendUnregistrationToServer() {
        PreferenceManager.getSharedPreference(getApplicationContext()).edit().putBoolean(Util.SENT_TOKEN_TO_SERVER, false).apply();
    }

    private void unsubscribeTopics(String token) throws IOException, IOException {
        FirebaseMessaging pubSub = FirebaseMessaging.getInstance();
        if (token != null) {
            for (String topic : Util.TOPICS) {
                pubSub.unsubscribeFromTopic("/topics/" + topic);
            }
        }
    }
}
