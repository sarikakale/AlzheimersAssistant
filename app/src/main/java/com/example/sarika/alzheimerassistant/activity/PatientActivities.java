package com.example.sarika.alzheimerassistant.activity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.GeoFenceDetail;
import com.example.sarika.alzheimerassistant.bean.LocationReminder;
import com.example.sarika.alzheimerassistant.bean.Person;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.fragments.ContactDetails;
import com.example.sarika.alzheimerassistant.network.GsonGetRequest;
import com.example.sarika.alzheimerassistant.network.GsonPostRequest;
import com.example.sarika.alzheimerassistant.network.LocationReminderApiRequests;
import com.example.sarika.alzheimerassistant.network.PatientApiRequests;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.service.FCMRegistrationService;
import com.example.sarika.alzheimerassistant.service.GeofenceTransitionsIntentService;
import com.example.sarika.alzheimerassistant.service.LocationReminderListenerService;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.apache.http.HttpHeaders;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PatientActivities extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ContactDetails.OnFragmentInteractionListener {
    public Button logout;
    public Button addFriends;
    public Button reminders;
    public Button notes;
    public Button remindMe;
    public Button patients_circles;
    private GoogleApiClient mGoogleApiClient;
    private GeoFenceDetail detail;
    private LocationReminder locationReminder;
    private String caregiverId;
    private List<Geofence> mGeofences;
    private List<Geofence> mLocationReminderGeofences;
    private PendingIntent mGeofencePendingIntent;
    private PendingIntent mLocationReminderGeofencesPendingIntent;
    private Intent mGeofenceIntent;
    private Intent mLocationReminderIntent;
    private HashMap<String, String> reminderIdsMessages;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION_PERMISSION = 8;
    private static final int REQUEST_CHECK_SETTINGS = 33;
    private final String TAG = getClass().getSimpleName();
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void init() {

        //logout = (Button) findViewById(R.id.logout);
        /*logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
*/
        addFriends = (Button) findViewById(R.id.addfriend);
        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(PatientActivities.this, AddFriendActivity.class);
                startActivity(add);
            }
        });

        reminders = (Button) findViewById(R.id.reminders);
        reminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent remind = new Intent(PatientActivities.this, RemindersActivity.class);
                startActivity(remind);
            }
        });

        remindMe = (Button) findViewById(R.id.remindMe);
        remindMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent remind = new Intent(PatientActivities.this, RemindByPhoto.class);
                startActivity(remind);
            }
        });

        notes = (Button) findViewById(R.id.notes);
        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent note = new Intent(PatientActivities.this, NotesActivity.class);
                startActivity(note);
            }
        });


        patients_circles = (Button) findViewById(R.id.patients_circles);
        patients_circles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent circle = new Intent(PatientActivities.this, CircleListActivity.class);
                startActivity(circle);
            }
        });

        if (PreferenceManager.getFirstTimeLogin(getApplicationContext())) {
            postCreateCollection();
            setFragment(ContactDetails.newInstance("facial","fragment"));
            PreferenceManager.setFirstTimeLogin(getApplicationContext(), false);
        }else{

        }

    }
    public void setFragment(Fragment fragment){

        //TODO: Unhandled for generic fragment only pop one fragment. have to handle it generic.
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentById(R.id.container_frame) != null) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_frame, fragment);
        fragmentTransaction.addToBackStack(null).setCustomAnimations(R.anim.transition_of_fragments,R.anim.exit_fragments);
        fragmentTransaction.commitAllowingStateLoss();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.caregiver_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.logout){
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void postCreateCollection() {

        GsonGetRequest gsonGetRequest = PatientApiRequests.createCollection(

                new Response.Listener<PostResponse>() {
                    @Override
                    public void onResponse(PostResponse response) {
                        Log.d(TAG, response.getSuccess() + " " + response.getBody());
                        PreferenceManager.setFirstTimeLogin(getApplicationContext(), false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "ERROR"+error.getLocalizedMessage());
                    }
                },
                getToken()
        );

        App.addRequest(gsonGetRequest,TAG);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_activities);
        ButterKnife.bind(this);
        //  PreferenceManager.setFirstTimeLogin(getApplicationContext(), true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        setToken(PreferenceManager.getTokenFromsharedPreference(getApplicationContext()));
        init();
        startFCMRegistration();
        // TODO: 5/2/17 Check if geo-fence has been setup and begin monitoring
        buildGoogleApiClient();

    }

    public void buildGoogleApiClient() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        boolean isPermissionEnabled = true;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
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
        if (isPermissionEnabled) {
            // Turn on location without opening location settings.
            enableLocationServices(this.getBaseContext());
        }
    }


    public boolean getLocationPermissions() {
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
                                        MY_PERMISSIONS_REQUEST_LOCATION_PERMISSION);
                            })
                            .create()
                            .show();

                } catch (Exception e) {
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

    /**
     * Called when a result is received after requesting permissions.
     *
     * @param requestCode  The request code submitted for obtaining location.
     * @param permissions  Stores the permissions granted.
     * @param grantResults Stores the results of permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION_PERMISSION:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted
                    Log.d(TAG, "Location permission granted");
                    enableLocationServices(this);
                } else {
                    //Permission denied
                    Log.d(TAG, "Location permission denied");
                }
        }
    }

    private void enableLocationServices(Context context) {

        if (!isLocationEnabled(context)) {
            //Location needs to be enabled via a dialog box.
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(10000 / 2);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(result1 -> {
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
                            status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
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
        } else {
            // Location is already ON. Start monitoring.
            populateGeoFenceDetail();
            populateLocationReminders();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        populateGeoFenceDetail();
                        populateLocationReminders();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(),
                        Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    //Populate Location Reminders
    public void populateLocationReminders() {
        // Research how to implement.
        // Location permissions are enabled and location is switched on.
        final String token = PreferenceManager
                .getTokenFromsharedPreference(getApplicationContext());

        GsonGetRequest gsonGetRequest = LocationReminderApiRequests.getLocationReminders(
                new Response.Listener<ArrayList<LocationReminder>>() {
                    @Override
                    public void onResponse(ArrayList<LocationReminder> response) {
                        if (!response.isEmpty()) {
                            for (LocationReminder reminder : response) {
                                Log.d(TAG, reminder.toString());
                                setupGeofence(reminder);
                            }
                            buildGeofencingListener();
                        } else {
                            Log.d(TAG, "No reminders set for this patient");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "No reminders set for this patient");
                    }
                },
                token
        );

        App.addRequest(gsonGetRequest, TAG);
    }

    // Setting up a geofence for location reminders
    public void setupGeofence(LocationReminder reminder) {
        // GFDetail is now populated. Set up the geo-fence.
        if (mLocationReminderGeofences == null)
            mLocationReminderGeofences = new ArrayList<>();

        mLocationReminderGeofences.add(new Geofence.Builder()
                .setRequestId(reminder.getReminderId())

                .setCircularRegion(
                        reminder.getLocation().getLat(),
                        reminder.getLocation().getLng(),
                        50
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(1000 * 15)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

    }

    //Building a geofence listener for Location Reminders
    private void buildGeofencingListener() {
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getLocationReminderGeofencingRequest(),
                    getlocationReminderGeoFencePendingIntent()
            ).setResultCallback(status ->
                    Log.d(TAG, "Geo-fences have been successfully added."));

        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    //Setting list of geofences to geofencing request
    private GeofencingRequest getLocationReminderGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mLocationReminderGeofences);
        return builder.build();
    }

    //Setting a pending Intent with reminder message
    private PendingIntent getlocationReminderGeoFencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mLocationReminderGeofencesPendingIntent != null) {
            return mLocationReminderGeofencesPendingIntent;
        }
        if (mLocationReminderIntent == null) {
            mLocationReminderIntent = new Intent(this, LocationReminderListenerService.class);
            //Bundle extras = new Bundle();
            //extras.putSerializable("HashMap",reminderIdsMessages);
            //mLocationReminderIntent.putExtras(extras);
        }
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mLocationReminderGeofencesPendingIntent = PendingIntent.getService(this, 0, mLocationReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return mLocationReminderGeofencesPendingIntent;
    }


    public void populateGeoFenceDetail() {
        // Research how to implement.
        // Location permissions are enabled and location is switched on.
        if (detail == null)
            detail = new GeoFenceDetail();
        final String token = PreferenceManager
                .getTokenFromsharedPreference(getApplicationContext());


        String url = String.format("%s/geoFence/getFence", Util.SERVER_URL);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                url, null,
                response -> {
                    if(response.length() > 0){
                        try {
                            JSONObject currFenceDtls = response.getJSONObject(0);
                            JSONObject location = currFenceDtls.getJSONObject("location");
                            detail.setLat(location.getDouble("latitude"));
                            detail.setLng(location.getDouble("longitude"));
                            detail.setRadius(currFenceDtls.getLong("radius"));
                            detail.setId(currFenceDtls.getString(getResources()
                                    .getString(R.string.patientId)));
                            caregiverId = currFenceDtls.getString(getResources()
                                    .getString(R.string.caregiverId));
                            setupGeofence();

                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    } else {
                        Log.d(TAG, " No geo-fences set for this patient.");
                    }

                }, error -> {
            // 5/1/17 If geo-fence does not exist then set a message

            Log.e(TAG, "Geofence does not exist for user : " + error.getMessage());

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put(HttpHeaders.AUTHORIZATION, token);
                return headers;
            }
        };

        App.addRequest(request, TAG);
    }

    public void setupGeofence() {
        // GFDetail is now populated. Set up the geo-fence.
        if (mGeofences == null)
            mGeofences = new ArrayList<>();

        mGeofences.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(detail.getId())

                .setCircularRegion(
                        detail.getLat(),
                        detail.getLng(),
                        detail.getRadius()
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(1000 * 15)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT
                        | Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(status ->
                    Log.d(TAG, "Geo-fences have been successfully added."));

        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER
                | GeofencingRequest.INITIAL_TRIGGER_EXIT | GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofences);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        if (mGeofenceIntent == null) {
            mGeofenceIntent = new Intent(this, GeofenceTransitionsIntentService.class);
            mGeofenceIntent.putExtra(getResources().getString(R.string.caregiverId), caregiverId);
        }
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, mGeofenceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void logout() {

        //remove geofences
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                // This is the same pending intent that was used in addGeofences().
                getGeofencePendingIntent()
        ).setResultCallback(status ->
                Log.d(TAG, "Geo-fences have been successfully removed.")); // Result processed in onResult().
        //remove Location Reminder Geofences
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                // This is the same pending intent that was used in addGeofences().
                getlocationReminderGeoFencePendingIntent()
        ).setResultCallback(status ->
                Log.d(TAG, "Geo-fences have been successfully removed.")); // Result processed in onResult().
        PreferenceManager.deleteTokenFromSharedPreference(getApplicationContext());
        Intent intentMainActivity = new Intent(PatientActivities.this, FirstActivity.class);
        startActivity(intentMainActivity);

    }

    //Start FCM Registration to update the details of caregiver with Registration Token
    public void startFCMRegistration(){
        Intent fcmRegisterIntent = new Intent(PatientActivities.this, FCMRegistrationService.class);
        fcmRegisterIntent.putExtra(Util.TOKEN,token);
        startService(fcmRegisterIntent);
    }

    @Override
    public void onNextSelected(Person person, Fragment fragment) {
        //No need
    }

    @Override
    public void onNextSelected(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}