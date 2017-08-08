package com.example.sarika.alzheimerassistant.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.adapter.CircleCaregiverAdapter;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.Circle;
import com.example.sarika.alzheimerassistant.fragments.Fragment_AddFriend;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CircleDetailsActivity extends AppCompatActivity {
    Bundle bundle;
    RecyclerView recyclerView;
    CircleCaregiverAdapter circleCaregiverAdapter;


    private static final String TAG = CircleDetailsActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.circlecaregiverActivity);
        Gson gson = new Gson();
        Circle circle = gson.fromJson(getIntent().getStringExtra(getResources().getString(R.string.circle)),Circle.class);
        updateActionBar(circle.getCircleName());
        ArrayList<Caregiver> caregiverArrayList = circle.getCaregivers();
        if(caregiverArrayList!=null || caregiverArrayList.isEmpty()){
            circleCaregiverAdapter = new CircleCaregiverAdapter(CircleDetailsActivity.this,caregiverArrayList);
            recyclerView.setLayoutManager(new LinearLayoutManager(CircleDetailsActivity.this));
            recyclerView.setAdapter(circleCaregiverAdapter);
        }else{

            Toast.makeText(this, "No Caregivers in Circle",
                    Toast.LENGTH_LONG).show();
        }

    }
    public void updateActionBar(String title){
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle(title);
    }
}
