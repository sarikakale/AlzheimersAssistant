package com.example.sarika.alzheimerassistant.service;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
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
import com.example.sarika.alzheimerassistant.network.NotificationAPIRequestsCall;
import com.example.sarika.alzheimerassistant.other.GeofenceErrorMessages;
import com.example.sarika.alzheimerassistant.other.LocationTracker;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpHeaders;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
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
public class GeofenceTransitionsIntentService extends IntentService implements LocationTracker.LocationCallback{

    private final String TAG = getClass().getSimpleName();

    private LocationTracker locationTracker;
    private Geocoder geocoder;
    private volatile static boolean isExited;
    private String caregiverId;

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(locationTracker == null){
            locationTracker = new LocationTracker(getApplicationContext(), this,
                    getResources().getInteger(R.integer.default_geofence_exit_interval));
        }
        if(geocoder == null)
            geocoder = new Geocoder(getApplicationContext());
        isExited = false;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
                String errorMessage = GeofenceErrorMessages.getErrorString(this,
                        geofencingEvent.getErrorCode());
                Log.e(TAG, errorMessage);
                return;
            }
            caregiverId = intent.getStringExtra(getResources().getString(R.string.caregiverId));

            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();
            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this, geofenceTransition, triggeringGeofences);
            switch (geofencingEvent.getGeofenceTransition()){
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    // TODO: 4/28/17 Notify server that patient has entered geo-fence.
                    isExited = false;
                    Log.i(TAG, geofenceTransitionDetails);
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    // TODO: 4/28/17 Send periodic messages to the server.
                    isExited = true;
                    locationTracker.connect();
                    Log.i(TAG, geofenceTransitionDetails);
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


    @Override
    public void handleNewLocation(Location location) {

        try {
            if(isExited){
                Address address = geocoder.getFromLocation
                        (location.getLatitude(), location.getLongitude(), 1 ).get(0);
                Log.d(TAG, String.format("Last reported location of patient is %s %s : %s" ,
                        location.getLongitude(), location.getLongitude(), address.getAddressLine(0)));
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> Toast.makeText(getApplicationContext(),
                        String.format("Last reported location of patient is %s : (%s, %s)",
                                address.getAddressLine(0),
                                location.getLatitude(), location.getLongitude()),
                        Toast.LENGTH_LONG).show());

                //TODO Make a server call to store last location of patient.
                JsonObjectRequest request = getFenceBreachRequest(address, location);
                GsonPostRequest gsonPostRequest=postGeofenceFCMNotification(address,location);
                App.addRequest(request, TAG);
                App.addRequest(gsonPostRequest,TAG);
            } else {
                //Patient is not outside the geo-fence.
                Log.d(TAG, "Stopping posting breach messages. Patient may have re-entered geo-fence");
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        locationTracker.getmGoogleApiClient(),
                        locationTracker);
                locationTracker.disconnect();
                stopSelf();
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private GsonPostRequest postGeofenceFCMNotification(Address address, Location location) {
        final String token = PreferenceManager
                .getTokenFromsharedPreference(getApplicationContext());
            String message = new StringBuffer(address.getLocality())
                                            .append(" ")
                                            .append(address.getAddressLine(0))
                                            .toString();
            GsonPostRequest gsonPostRequest= NotificationAPIRequestsCall.postGeofenceNotification(
                    new Response.Listener<PostResponse>() {
                        @Override
                        public void onResponse(PostResponse response) {

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    },
                    token,
                    message
            );
        return gsonPostRequest;
    }

    private JsonObjectRequest getFenceBreachRequest(Address address, Location location)
            throws JSONException {

        final String token = PreferenceManager
                .getTokenFromsharedPreference(getApplicationContext());


        String url = String.format("%s/geoFence/fenceBreach", Util.SERVER_URL);

        JSONObject jsonObject =  new JSONObject();
        jsonObject.put("latitude", location.getLatitude());
        jsonObject.put("longitude", location.getLongitude());
        jsonObject.put("locationName", address.getAddressLine(0));
        jsonObject.put("locationDescription", address.getLocality());
        jsonObject.put(getResources().getString(R.string.caregiverId), caregiverId);
        return new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        Log.d( TAG,
                                String.format("Successfully posted breach information at : %s",
                                        response.toString(2)));
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                },
                error -> Log.e(TAG, error.getMessage())){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put(HttpHeaders.AUTHORIZATION, token);
                return headers;
            }
        };
    }


}

