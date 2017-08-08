package com.example.sarika.alzheimerassistant.service;

import android.app.IntentService;
import android.content.Intent;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarika.alzheimerassistant.activity.CaregiverHomeActivity;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.network.GsonPostRequest;
import com.example.sarika.alzheimerassistant.network.LocationReminderApiRequests;
import com.example.sarika.alzheimerassistant.other.LocationTracker;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.util.Util;

/**
 * Created by sarika on 4/3/17.
 */
public class LocationUpdateService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     *
     */

    public static final String TAG = CaregiverHomeActivity.class.getSimpleName();

    private LocationTracker mLocationTracker;
    public LocationUpdateService(){
        super("LocationUpdateService");

    }
    public LocationUpdateService(String name) {
        super(name);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        double[] location = intent.getDoubleArrayExtra(Util.MSG_TO_SERVICE);
        String locationDescription = intent.getStringExtra(Util.LOCATION_DESC);
        String locationName = intent.getStringExtra(Util.LOCATION_NAME);
        postLocationInfo(location,locationDescription,locationName);
    }

    private void postLocationInfo(double[] location,String locationDescription, String locationName ) {

        String token = PreferenceManager.getTokenFromsharedPreference(getApplicationContext());
        GsonPostRequest gsonPostRequest = LocationReminderApiRequests.postLocation(
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
                location[0],location[1],locationDescription,locationName, token

        );


            App.addRequest(gsonPostRequest,TAG);

    }


}
