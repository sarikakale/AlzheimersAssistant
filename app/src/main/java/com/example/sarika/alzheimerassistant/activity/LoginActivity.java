package com.example.sarika.alzheimerassistant.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.network.GsonPostRequest;
import com.example.sarika.alzheimerassistant.network.LoginApiRequests;

import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.loginUserName)
    EditText loginUserName;

    @BindView(R.id.loginPassword)
    EditText loginPassword;

    @BindView(R.id.loginButton)
    Button loginButton;

    String loginUserNameStr;
    String loginPasswordStr;
    SharedPreferences sharedPreferences;
    ProgressDialog dialog;

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        loginButton.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

       App.cancelAllRequests(TAG);
    }




    public void validateLogin(){

        loginUserNameStr = loginUserName.getText().toString();
        loginPasswordStr = loginPassword.getText().toString();

        if(!validate()){
            onSigninFailed();
            return;
        }

        loginButton.setEnabled(false);
        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setIndeterminate(true);
        dialog.setMessage("Authenticating...");
        dialog.show();
        //Sending Login Credentials
        String role = PreferenceManager.getRoleFromsharedPreference(getApplicationContext());
        Log.d(TAG,role);
        GsonPostRequest gsonPostRequest = LoginApiRequests.postloginRequest(

                new Response.Listener<PostResponse>() {
                    @Override
                    public void onResponse(PostResponse response) {
                        loginButton.setEnabled(true);
                        //Store in Token sharedPReferences
                        Log.d(TAG,response.getToken());
                        PreferenceManager.setTokenToSharedPreference(getApplicationContext(),response.getToken());
                        dialog.dismiss();
                        // Login
                        onSigninSuccess();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Login Failed
                        dialog.dismiss();
                        onSigninFailed();
                    }
                },

                loginUserNameStr, loginPasswordStr, role

        );

          App.addRequest(gsonPostRequest,TAG);
    }

    private void onSigninFailed() {
        Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
    }

    private void onSigninSuccess() {
        Intent intent;
        String role= PreferenceManager.getRoleFromsharedPreference(getApplicationContext());
        if(role.equals(Util.CAREGIVER)) {
            intent = new Intent(LoginActivity.this, CaregiverHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else if(role.equals(Util.PATIENT)) {
            intent = new Intent(LoginActivity.this, PatientActivities.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    public boolean validate(){
        if(loginPasswordStr.isEmpty() || loginUserNameStr.isEmpty()){
            return false;
        }
        return true;

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.loginButton:
                validateLogin();
                break;
        }
    }
}
