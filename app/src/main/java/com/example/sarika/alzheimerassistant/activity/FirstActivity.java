package com.example.sarika.alzheimerassistant.activity;

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

import butterknife.BindView;
import butterknife.ButterKnife;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener  {
    @BindView(R.id.caregiverButton)
    Button caregiverButton;
    @BindView(R.id.patientButton)
    public Button patientButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        ButterKnife.bind(this);
        caregiverButton.setOnClickListener(this);
        patientButton.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.caregiverButton:
                PreferenceManager.setRoleToSharedPreference(getApplicationContext(), Util.CAREGIVER);
                goRegistrationActvity();
                break;
            case R.id.patientButton:
                PreferenceManager.setRoleToSharedPreference(getApplicationContext(), Util.PATIENT);
                goRegistrationActvity();
                break;
        }

    }

    private void goRegistrationActvity() {
        Intent details = new Intent(FirstActivity.this, RegisterLoginActivity.class);
        startActivity(details);
    }


}
