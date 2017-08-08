package com.example.sarika.alzheimerassistant.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.network.GsonPostRequest;
import com.example.sarika.alzheimerassistant.network.LocationReminderApiRequests;
import com.example.sarika.alzheimerassistant.other.GeofenceErrorMessages;
import com.example.sarika.alzheimerassistant.other.LocationTracker;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;

import org.apache.http.HttpHeaders;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LocationReminderListenerService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this

    private final String TAG = getClass().getSimpleName();

    private LocationTracker locationTracker;
    private Geocoder geocoder;
    private String reminderMessage;

    public LocationReminderListenerService() {
        super("LocationReminderListenerService");
    }
    @Override
    public void onCreate() {
        super.onCreate();

        if(geocoder == null)
            geocoder = new Geocoder(getApplicationContext());

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"LocationRemindersAdapter++++++++++++");
        if (intent != null) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
                String errorMessage = GeofenceErrorMessages.getErrorString(this,
                        geofencingEvent.getErrorCode());
                Log.e(TAG, errorMessage);
                return;
            }

            //HashMap<String,String> hashMap = null;
            //Bundle bundle = intent.getExtras();
            /*
            if(bundle != null) {
                hashMap = (HashMap<String, String>) bundle.getSerializable("HashMap");
            }
            */

            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();
            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();


            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this, geofenceTransition, triggeringGeofences);
            ArrayList<String> requestIds = getGeofenceTransitionDetailsList(this, geofenceTransition, triggeringGeofences);
            switch (geofencingEvent.getGeofenceTransition()){
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    // TODO: 4/28/17 Notify server that patient has entered geo-fence.
                    //POST FCM REQUEST TO SERVER
                    Log.d(TAG,requestIds.get(0));
                   sendNotification(requestIds.get(0));
                    Log.i(TAG, geofenceTransitionDetails);
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    // TODO: 4/28/17 Send periodic messages to the server.
                    //Delete reminder
                    deleteReminder(requestIds.get(0));
                    break;
                default:
                    //Error
                    Log.e(TAG, getString(R.string.geofence_transition_invalid_type,
                            geofenceTransition));
                    break;
            }
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param context               The app context.
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            triggeringGeofencesIdsList = triggeringGeofences.stream().map(Geofence::getRequestId)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + " geo-fence: " + triggeringGeofencesIdsString;
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param context               The app context.
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private ArrayList<String> getGeofenceTransitionDetailsList(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            triggeringGeofencesIdsList = triggeringGeofences.stream().map(Geofence::getRequestId)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return triggeringGeofencesIdsList;
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }


    public void sendNotification(String requestIds){

        final String token = PreferenceManager.getTokenFromsharedPreference(getApplicationContext());

        GsonPostRequest gsonPostRequest= LocationReminderApiRequests.sendNotifications(
                new Response.Listener<PostResponse>() {
                    @Override
                    public void onResponse(PostResponse response) {
                        Toast.makeText(getApplicationContext(),"Notification sent",Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Error in Notification sent",Toast.LENGTH_LONG).show();
                    }
                },
                requestIds,
                token
        );

        App.addRequest(gsonPostRequest,TAG);

    }


    public void deleteReminder(String requestId) {
        final String token = PreferenceManager.getTokenFromsharedPreference(getApplicationContext());

        GsonPostRequest gsonPostRequest = LocationReminderApiRequests.deletReminder(
                new Response.Listener<PostResponse>() {
                    @Override
                    public void onResponse(PostResponse response) {
                            Log.d(TAG,"reminder deleted");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Could not Delete Reminder", Toast.LENGTH_LONG).show();

                    }
                },
                requestId,
                token
        );
        App.addRequest(gsonPostRequest,TAG);
    }



}
