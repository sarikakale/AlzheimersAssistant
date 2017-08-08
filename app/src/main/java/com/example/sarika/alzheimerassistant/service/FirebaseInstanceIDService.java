package com.example.sarika.alzheimerassistant.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.sarika.alzheimerassistant.util.Util;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

/**
 * Created by sarika on 4/16/17.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private String TAG = getClass().getName();
    private static final String FCM_TOKEN = "fcmToken";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "FCM Registration Token: " + token);
      /*  try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Intent intent = new Intent(this, FCMRegistrationService.class);
        startService(intent);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + refreshedToken);
    }

    private void storeRegIdInPref(String token, Context context) {

        com.example.sarika.alzheimerassistant.other.PreferenceManager.storeRegIdInPref(token,context);

    }
}
