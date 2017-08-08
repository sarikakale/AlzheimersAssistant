package com.example.sarika.alzheimerassistant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.adapter.CircleAdapter;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.Circle;
import com.example.sarika.alzheimerassistant.fragments.CaregiverList;
import com.example.sarika.alzheimerassistant.network.CaregiverApiRequests;
import com.example.sarika.alzheimerassistant.network.GsonGetRequest;
import com.example.sarika.alzheimerassistant.network.PatientApiRequests;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CircleListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Circle> caregiverList=new ArrayList<>();
    private CircleAdapter circleAdapter;
    private static final String TAG = CaregiverList.class.getSimpleName();
    Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_list);
        gson = new Gson();
        recyclerView = (RecyclerView) findViewById(R.id.circleListId);
        getCaregiversData();
    }

    private void getCaregiversData() {
        String token = PreferenceManager.getTokenFromsharedPreference(getApplicationContext());

        GsonGetRequest gsonGetRequest = PatientApiRequests.getAllDetailsCaregivers(
                new Response.Listener<ArrayList<Circle>>() {
                    @Override
                    public void onResponse(ArrayList<Circle> response) {
                        caregiverList = response;
                        if(!response.isEmpty()) {
                            if (caregiverList.isEmpty() || caregiverList == null) {
                                Log.d(TAG, "No values populated in caregiverList");
                            } else {
                                circleAdapter = new CircleAdapter(CircleListActivity.this, caregiverList, new CircleAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Circle circleName) {
                                        Log.d(TAG, circleName.toString());
                                        showCaregiverDetails(circleName);
                                    }
                                });
                                recyclerView.setLayoutManager(new LinearLayoutManager(CircleListActivity.this));
                                recyclerView.setAdapter(circleAdapter);
                            }
                        } else {
                            Log.d(TAG,"No Circles");
                            Toast.makeText(getApplicationContext(),"No Circles",Toast.LENGTH_LONG).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        caregiverList = null;
                        Log.d(TAG,error.getMessage());
                        Toast.makeText(getApplicationContext(),"Cannot Fecth Circles",Toast.LENGTH_LONG).show();

                    }
                },
                token
        );
        App.addRequest(gsonGetRequest,TAG);
    }

    public void showCaregiverDetails(Circle circleName) {
        Intent intent = new Intent(this, CircleDetailsActivity.class);
        String circleStr = gson.toJson(circleName);
        intent.putExtra(getResources().getString(R.string.circle), circleStr);
        startActivity(intent);
    }

}
