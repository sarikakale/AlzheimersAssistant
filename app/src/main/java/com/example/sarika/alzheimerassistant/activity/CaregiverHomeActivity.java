package com.example.sarika.alzheimerassistant.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.Patient;
import com.example.sarika.alzheimerassistant.bean.Person;
import com.example.sarika.alzheimerassistant.fragments.CaregiverHomeFragment;
import com.example.sarika.alzheimerassistant.fragments.ContactDetails;
import com.example.sarika.alzheimerassistant.fragments.FacialFragment;
import com.example.sarika.alzheimerassistant.fragments.FindMyFriendsFragment;
import com.example.sarika.alzheimerassistant.fragments.GeofenceFragment;
import com.example.sarika.alzheimerassistant.fragments.MyCirclesFragment;
import com.example.sarika.alzheimerassistant.fragments.MyPatientsFragments;
import com.example.sarika.alzheimerassistant.other.LocationTracker;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.service.FCMRegistrationService;
import com.example.sarika.alzheimerassistant.service.LocationUpdateService;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CaregiverHomeActivity extends AppCompatActivity
        implements FacialFragment.OnFragmentInteractionListener,NavigationView.OnNavigationItemSelectedListener, MyPatientsFragments.MyPatientsFragmentCallbacks,CaregiverHomeFragment.CaregiverHomeCallbacks,GeofenceFragment.GeofenceCallbacks ,MyCirclesFragment.MyCirclesCallback,FindMyFriendsFragment.FindMyFriendsCallback, ContactDetails.OnFragmentInteractionListener
       {

    public static final String TAG = CaregiverHomeActivity.class.getSimpleName();
    private String token;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token = PreferenceManager.getTokenFromsharedPreference(getApplicationContext());
        setContentView(R.layout.activity_caregiver_home);
        //PreferenceManager.setFirstTimeLogin(getApplicationContext(),true);
        if(PreferenceManager.getFirstTimeLogin(getApplicationContext())) {
            setFragment(ContactDetails.newInstance("facial","fragment"));
        }else{
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);
        PreferenceManager.setFirstTimeLogin(getApplicationContext(), false);
        setFragment(MyPatientsFragments.newInstance("MyPatients"));
        startFCMRegistration();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    private void logout() {
        PreferenceManager.deleteTokenFromSharedPreference(getApplicationContext());
        Intent intentMainActivity = new Intent(CaregiverHomeActivity.this,FirstActivity.class);
        startActivity(intentMainActivity);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch(id) {
            case R.id.my_patients:
                updateActionBar(getResources().getString(R.string.my_patients_title));
                setFragment(MyPatientsFragments.newInstance("MyPatients"));
                break;
            case R.id.contact_details:
                updateActionBar(getResources().getString(R.string.find_my_friends_title));
                setFragment(ContactDetails.newInstance("Contact","Details"));
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragment(Fragment fragment){

        //TODO: Unhandled for generic fragment only pop one fragment. have to handle it generic.
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentById(R.id.container) != null) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null).setCustomAnimations(R.anim.transition_of_fragments,R.anim.exit_fragments);
        fragmentTransaction.commitAllowingStateLoss();

    }


    @Override
    public void onPatientSelected(Patient patient) {
        //Set Patient Fragment
        Intent intent=new Intent(CaregiverHomeActivity.this,RemindersActivity.class);
        intent.putExtra(getResources().getString(R.string.patient),patient.getUserId());
        intent.putExtra(getResources().getString(R.string.patient_name),
                String.format("%s %s", patient.getDetails().getName().getFirstName(),
                        patient.getDetails().getName().getLastName()));
        Log.d(TAG,patient.toString());
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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

    @Override
    protected void onStop() {
        super.onStop();
        App.cancelAllRequests(
                TAG
        );

    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    //Start FCM Registration to update the details of caregiver with Registration Token
    public void startFCMRegistration(){
        Intent fcmRegisterIntent = new Intent(CaregiverHomeActivity.this, FCMRegistrationService.class);
        fcmRegisterIntent.putExtra(Util.TOKEN,token);
        startService(fcmRegisterIntent);
    }

    public void updateActionBar(String title){
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle(title);
    }

    @Override
    public void onNextSelected(Person person, Fragment fragment) {
               Bundle bundle=new Bundle();
               gson =new Gson();
               String personDetails = gson.toJson(person);
               bundle.putSerializable("Person", personDetails);
               FragmentManager fragmentManager=getSupportFragmentManager();
               FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
               fragment.setArguments(bundle);

               if (fragmentManager.findFragmentById(R.id.container) != null) {
                   fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
               }
               fragmentTransaction.replace(R.id.container,fragment);
               fragmentTransaction.addToBackStack(null).setCustomAnimations(R.anim.transition_of_fragments,R.anim.exit_fragments);
               fragmentTransaction.commitAllowingStateLoss();
           }
           @Override
           public void onNextSelected(Intent intent) {
               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent);

           }
       }

