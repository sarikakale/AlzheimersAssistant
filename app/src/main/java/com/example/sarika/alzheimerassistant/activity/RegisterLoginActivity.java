package com.example.sarika.alzheimerassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.example.sarika.alzheimerassistant.R;

public class RegisterLoginActivity extends AppCompatActivity implements View.OnClickListener {


    public Button login;


    public Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);
        login = (Button)findViewById(R.id.login);
        login.setOnClickListener(this);
        register = (Button)findViewById(R.id.register);
        register.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.register:
                goToRegistrationActvity();
                break;
            case R.id.login:
                goToLoginActivity();
                break;
        }

    }

    private void goToRegistrationActvity() {
        Intent pDetails = new Intent(RegisterLoginActivity.this, CaregiverDetailsActivity.class);
        startActivity(pDetails);
    }


    public void goToLoginActivity(){
        Intent details = new Intent(RegisterLoginActivity.this,LoginActivity.class);
        startActivity(details);
    }
}
