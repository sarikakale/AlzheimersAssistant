package com.example.sarika.alzheimerassistant.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.GeoFenceDetail;
import com.example.sarika.alzheimerassistant.fragments.GeofenceFragment;
import com.example.sarika.alzheimerassistant.other.LocationTracker;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpHeaders;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.angrybyte.numberpicker.view.ActualNumberPicker;

public class CaregiverGeofenceSetupActivity extends AppCompatActivity
        implements LocationTracker.LocationCallback,
        PlaceSelectionListener,
        GoogleMap.OnCameraIdleListener{

    private final String TAG = getClass().getSimpleName();
    private final int MY_PERMISSIONS_REQUEST_LOCATION_PERMISSION = 33;
    private static final int REQUEST_CHECK_SETTINGS = 8;

    private PlaceAutocompleteFragment autocompleteFragment;
    private GoogleMap googleMap;
    private Geocoder geocoder;
    private Circle radiusCircle;
    private LocationTracker locationTracker;

    //Final Details to be submitted
    private GeoFenceDetail detailToSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_geofence_setup);

        //detailToSubmit = new GeoFenceDetail();
        detailToSubmit = getIntent()
                .getParcelableExtra(GeofenceFragment.GEOFENCE_DETAIL_PARCEL_KEY);

        try {
            Log.d(TAG, "Populated detail !!!"+detailToSubmit.getJson().toString(2));
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        geocoder = new Geocoder(this, Locale.getDefault());
        locationTracker = new LocationTracker(this, this);

        /*
        Set up UI elements
        */
        setupRadiusPicker();

        autocompleteFragment =  (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete);

        setupSubmitButton();
    }

    private void setupRadiusPicker(){

        ActualNumberPicker radiusPicker = (ActualNumberPicker) findViewById(R.id.actual_picker);
        detailToSubmit.setRadius(radiusPicker.getValue());
        radiusPicker.setListener((oldValue, newValue) -> {
            Log.d(TAG, "New value is : "+newValue);
            detailToSubmit.setRadius(newValue);
            if(radiusCircle != null)
                radiusCircle.setRadius(newValue);
        });
        //TODO plot location on map
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        setupMapOnReadyListener(mapFragment);

    }

    private void setupMapOnReadyListener(MapFragment mapFragment){
        mapFragment.getMapAsync(map -> {
            googleMap = map;
            // TODO: 5/1/17 Start the permission workflow
            Log.d(TAG, "Entered onMapReady for geofence fragment!!!");
            // TODO: 4/30/17 Enable location
            if(checkPermissions()){
                locationTracker.connect();
                startMonitoring();
            }
        });
    }

    public boolean checkPermissions(){
        boolean isPermissionEnabled = true;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            isPermissionEnabled = getLocationPermissions();
        }
        //Permissions are enabled. So start monitoring the location.
        if(isPermissionEnabled){
            // Turn on location without opening location settings.
            enableLocationServices(getApplicationContext());
        }
        return isPermissionEnabled;
    }

    public boolean getLocationPermissions(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                // Show a dialog asking the user for permissions.
                try {
                    new AlertDialog.Builder(this)
                            .setTitle("Location Permission Needed")
                            .setMessage("This app needs the Location permission, please accept " +
                                    "to use location functionality")
                            .setPositiveButton("OK", (dialogInterface, i) -> {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION_PERMISSION );
                            })
                            .create()
                            .show();

                } catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION_PERMISSION);

                // MY_PERMISSIONS_REQUEST_LOCATION_PERMISSION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        }
        return true;
    }

    private void enableLocationServices(Context context) {

        if(!isLocationEnabled(context)){
            //Location needs to be enabled via a dialog box.
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationTracker.getmLocationRequest());
            builder.setAlwaysShow(true);
            locationTracker.connect();
            startMonitoring();

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(locationTracker.getmGoogleApiClient(),
                            builder.build());
            Log.d(TAG, "Enabling location services. ");
            result.setResultCallback(result1 -> {
                Log.d(TAG, "Pending result obtained.");
                final Status status = result1.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the " +
                                "user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(TAG, "Location settings are inadequate, and cannot be fixed " +
                                "here. Dialog not created.");
                        break;
                }
            });
        } else{
            // Location is already ON. Start monitoring.
            startLocationUpdates();

        }
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(),
                        Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void setupSubmitButton(){
        Button submitBtn = (Button) findViewById(R.id.submitButton);
        submitBtn.setOnClickListener(v -> {
            //Make Volley request
            String url = String.format("%s/geoFence/setFence", Util.getServerUrl());
            final String token = PreferenceManager
                    .getTokenFromsharedPreference(getApplicationContext());
            try {
                Log.d(TAG, " Detail to be submitted is as follows "+
                        detailToSubmit.getJson().toString(2) + " with token "+token);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT,
                    url, detailToSubmit.getJson(),
                    response -> {
                        try {
                            String responseStr = String.format("Saved Detail as follows : %s",
                                    response.toString(2));
                            Log.d(TAG, responseStr);
                            //Show Toast
                            Toast.makeText(this, responseStr,
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Log.e(TAG, "Error on sending request : "+ error.getMessage())){

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put(HttpHeaders.AUTHORIZATION, token);
                    return headers;
                }
            };
            App.addRequest(request, TAG);
        });
    }

    @Override
    public void handleNewLocation(Location location) {
        // Marker method required for Location tracker
    }

    public void startMonitoring(){
        positionMyLocationButton();
        this.googleMap.setOnCameraIdleListener(this);
        //Tell the auto complete text field to listen for location changes
        autocompleteFragment.setOnPlaceSelectedListener(this);

        //If geo-fence was preset, move camera to the pre-set location
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(detailToSubmit.getLat(),
                detailToSubmit.getLng())));

        //Add radius circle
        CameraPosition position= this.googleMap.getCameraPosition();
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center( new LatLng(position.target.latitude,
                position.target.longitude));
        circleOptions.fillColor(0x5500ff00);
        //circleOptions.fillColor(Color.GREEN);
        circleOptions.strokeWidth(1);
        circleOptions.radius(detailToSubmit.getRadius());
        radiusCircle = this.googleMap.addCircle(circleOptions);
    }



    public void positionMyLocationButton(){
        //Set position of My Location button
        if (findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent())
                    .findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 60, 60);
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        // TODO: Move camera to selected place
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
        Log.i(TAG, "Place: " + place.getName());
    }

    @Override
    public void onError(Status status) {
        Log.e(TAG, String.format("Error encountered on selecting place : %s",
                status.getStatusMessage()));
    }

    @Override
    public void onCameraIdle() {
        Log.d(TAG, "Camera stopped moving");
        CameraPosition currPosition = googleMap.getCameraPosition();
        radiusCircle.setCenter(new LatLng(currPosition.target.latitude,
                currPosition.target.longitude));
        detailToSubmit.setLat(currPosition.target.latitude);
        detailToSubmit.setLng(currPosition.target.longitude);

        //googleMap.addCircle(circleOptions);
        Log.d(TAG, "Circle added with radius" + detailToSubmit.getRadius());

        //Set address in autocomplete text
        try {
            List<Address> geoCodedPlaces = geocoder.getFromLocation(currPosition.target.latitude,
                    currPosition.target.longitude, 1);
            if(!geoCodedPlaces.isEmpty()){
                autocompleteFragment.setText(geoCodedPlaces.get(0).getAddressLine(0));
                Log.d(TAG, "Geolocated position : " + geocoder.getFromLocation(
                        currPosition.target.latitude, currPosition.target.longitude, 1)
                        .get(0).getAddressLine(0));
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    /**
     * Called when a result is received after requesting permissions.
     * @param requestCode The request code submitted for obtaining location.
     * @param permissions Stores the permissions granted.
     * @param grantResults Stores the results of permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_LOCATION_PERMISSION:

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permission granted
                    Log.d(TAG, "Location permission granted");
                    enableLocationServices(this);
                } else {
                    //Permission denied
                    Log.d(TAG, "Location permission denied");
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Entered onActivityResult ");
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    public void startLocationUpdates(){
        // Research how to implement.
        // Location permissions are enabled and location is switched on.
        try {
            googleMap.setMyLocationEnabled(true);

        } catch (SecurityException e){
            Log.e(TAG, e.getMessage());
        }
    }
}
