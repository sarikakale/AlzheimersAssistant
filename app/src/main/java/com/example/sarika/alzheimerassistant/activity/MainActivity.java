package com.example.sarika.alzheimerassistant.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.android.gms.common.GoogleApiAvailability;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.caregiverButton)
    public Button caregiverButton;

    @BindView(R.id.caregiverLoginButton)
    public Button caregiverLoginButton;

    @BindView(R.id.patientButton)
    public Button patientButton;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        caregiverButton.setOnClickListener(this);
        caregiverLoginButton.setOnClickListener(this);
        patientButton.setOnClickListener(this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.caregiverLoginButton:
                goToCaregiverLoginActivity();
                break;
            case R.id.caregiverButton:
                goToCaregiverRegistrationActvity();
                break;
            case R.id.patientButton:
                goToPatientRegistrationActivity();
                break;
        }

    }


    private void goToPatientRegistrationActivity() {
        Intent pDetails = new Intent(MainActivity.this, PatientActivities.class);
        startActivity(pDetails);
    }

    private void goToCaregiverLoginActivity() {

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        PreferenceManager.setRoleToSharedPreference(getApplicationContext(),Util.CAREGIVER);
        startActivity(intent);
    }


    public void goToCaregiverRegistrationActvity(){
        Intent cdetails = new Intent(MainActivity.this,CaregiverDetailsActivity.class);
        PreferenceManager.setRoleToSharedPreference(getApplicationContext(),Util.CAREGIVER);
        startActivity(cdetails);
    }
}
