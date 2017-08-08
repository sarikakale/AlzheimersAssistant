package com.example.sarika.alzheimerassistant.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.fragments.FindMyFriendsFragment;
import com.example.sarika.alzheimerassistant.fragments.GeofenceFragment;
import com.example.sarika.alzheimerassistant.fragments.MyCirclesFragment;
import com.example.sarika.alzheimerassistant.fragments.SaveByLocationFragment;
import com.example.sarika.alzheimerassistant.fragments.SaveByPhotoFragment;
import com.example.sarika.alzheimerassistant.other.LocationTracker;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.util.Util;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RemindersActivity extends AppCompatActivity implements MyCirclesFragment.MyCirclesCallback,FindMyFriendsFragment.FindMyFriendsCallback,GeofenceFragment.GeofenceCallbacks,SaveByPhotoFragment.OnPhotoUpload,LocationTracker.LocationCallback {
    private static final String TAG = RemindersActivity.class.getSimpleName();
    private Geocoder geocoder;
    private String locationDescription;
    private LocationTracker mLocationTracker;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private int mSelectedItem;
    private static final String SELECTED_ITEM = "arg_selected_item";
    @BindView(R.id.bottomNavigation)
    BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        ButterKnife.bind(this);
        if(PreferenceManager.getRoleFromsharedPreference(getApplicationContext()).equals(Util.PATIENT)){
            mBottomNavigationView.getMenu().removeItem(R.id.menu_geofence);
        }
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });
    }

    private void setFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.reminderActivity) != null) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reminderActivity, fragment);
        fragmentTransaction.addToBackStack(null).setCustomAnimations(R.anim.transition_of_fragments,R.anim.exit_fragments);
        fragmentTransaction.commitAllowingStateLoss();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void selectFragment(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_geofence:
                setFragment(GeofenceFragment.newInstance("Geofence"));
                break;
            case R.id.menu_savebyperson:
                setFragment(SaveByPhotoFragment.newInstance("facial","Capture"));
                break;
            case R.id.menu_savebylocation:
                setFragment(SaveByLocationFragment.newInstance("location","fragment"));
                break;
            case R.id.menu_my_friends:
                setFragment(FindMyFriendsFragment.newInstance("MyCircles"));
                break;
            case R.id.menu_my_circles:
                setFragment(MyCirclesFragment.newInstance("mycircles"));
                break;
        }

        // update selected item
        mSelectedItem = item.getItemId();

        //Uncheck other item
        for (int i = 0; i< mBottomNavigationView.getMenu().size(); i++) {
            MenuItem menuItem = mBottomNavigationView.getMenu().getItem(i);
            boolean flag  = menuItem.getItemId() == item.getItemId();
            menuItem.setChecked(flag);
        }
        updateToolbarText(item.getTitle());
    }

    private void updateToolbarText(CharSequence text) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // Permission Denied
                    Toast.makeText(RemindersActivity.this, "LOCATION Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void checkAccessFineLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            geocoder= new Geocoder(this, Locale.getDefault());
            mLocationTracker = new LocationTracker(this, this);
        } else {

            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    REQUEST_CODE_ASK_PERMISSIONS);

        }
    }


    @Override
    public String getLocation() {

        if(locationDescription!=null){
            return locationDescription;
        }else{
            return null;
        }

    }

    @Override
    public void handleNewLocation(Location location) {
        Log.d(TAG,String.valueOf(location.getLongitude()));


        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
        } catch (IOException e) {
            e.printStackTrace();
        }

         locationDescription = addresses.get(0).getAddressLine(0)+
                 " "+addresses.get(0).getLocality()+
                 "  "+addresses.get(0).getCountryName();
    }


    @Override
    protected void onResume(){
        super.onResume();
        //setUpMapIfNeeded();
        //mLocationTracker.connect();
    }

    @Override
    protected void onPause(){
        super.onPause();
        //mLocationTracker.disconnect();
    }


    @Override
    public void onGeofenceSelected(Uri uri) {

    }

    @Override
    public void onMyFriendSelected(Uri uri) {

    }

    @Override
    public void onMyCircleSelected(Uri uri) {

    }
}
