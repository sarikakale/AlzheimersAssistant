package com.example.sarika.alzheimerassistant.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.activity.CaregiverGeofenceSetupActivity;
import com.example.sarika.alzheimerassistant.activity.NotesActivity;
import com.example.sarika.alzheimerassistant.activity.RemindersActivity;
import com.example.sarika.alzheimerassistant.activity.SaveByLocationActivity;
import com.example.sarika.alzheimerassistant.adapter.LocationRemindersAdapter;
import com.example.sarika.alzheimerassistant.adapter.NotesAdapter;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.GeoFenceDetail;
import com.example.sarika.alzheimerassistant.bean.LocationReminder;
import com.example.sarika.alzheimerassistant.network.GsonGetRequest;
import com.example.sarika.alzheimerassistant.network.LocationReminderApiRequests;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.util.Util;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.sarika.alzheimerassistant.fragments.GeofenceFragment.GEOFENCE_DETAIL_PARCEL_KEY;

public class SaveByLocationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = SaveByLocationFragment.class.getSimpleName();
    // TODO: Rename and change types of parameters


    private LocationReminder locationReminder;
    private String patientName;
    private RecyclerView recyclerView;
    private String token;
    private String patientId;
    private LocationRemindersAdapter locationRemindersAdapter;

    @BindView(R.id.setupLocationReminder)
    FloatingActionButton setupButton;

    public SaveByLocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SaveByLocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SaveByLocationFragment newInstance(String param1, String param2) {
        SaveByLocationFragment fragment = new SaveByLocationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_save_by_location, container, false);
        ButterKnife.bind(this,view);
        recyclerView =  (RecyclerView) view.findViewById(R.id.getLocationReminders);
        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(locationReminder == null)
            locationReminder = new LocationReminder();
        locationReminder.setId(getActivity().getIntent().getStringExtra(getResources()
                .getString(R.string.patient)));
        patientName = getActivity().getIntent().getStringExtra(getResources()
                .getString(R.string.patient_name));
        setupButton.setOnClickListener(v -> {
            // TODO: 5/1/17 Start the activity here
            Log.d(TAG, "Activity can be started here. ");
            Intent intent = new Intent(getActivity(), SaveByLocationActivity.class);
            intent.putExtra(Util.LOCATION_REMINDER_DETAIL_PARCEL_KEY, locationReminder);
            startActivity(intent);
        });
        // Get Reminders
        if(PreferenceManager.getRoleFromsharedPreference(getActivity().getApplicationContext()).equals(Util.PATIENT)){
            getLocationReminders();
        }
        if(PreferenceManager.getRoleFromsharedPreference(getActivity().getApplicationContext()).equals(Util.CAREGIVER)){
            getPatientLocationReminders();
        }
    }

    private void getLocationReminders() {
        token = PreferenceManager
                .getTokenFromsharedPreference(getActivity().getApplicationContext());
        GsonGetRequest gsonGetRequest = LocationReminderApiRequests.getLocationReminders(
                new Response.Listener<ArrayList<LocationReminder>>() {
                    @Override
                    public void onResponse(ArrayList<LocationReminder> response) {
                        if (!response.isEmpty()) {
                            Log.d(TAG,response.toString());
                            locationRemindersAdapter=new LocationRemindersAdapter(getActivity(),response, token);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(locationRemindersAdapter);
                        } else {
                            Log.d(TAG, "No reminders set for this patient");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(), "No Location Reminder", Toast.LENGTH_LONG).show();
                    }
                },
                token
        );

        App.addRequest(gsonGetRequest, TAG);
    }

    private void getPatientLocationReminders() {
        token = PreferenceManager
                .getTokenFromsharedPreference(getActivity().getApplicationContext());
        patientId = getActivity().getIntent().getStringExtra(getResources()
                .getString(R.string.patient));
        GsonGetRequest gsonGetRequest = LocationReminderApiRequests.getPatientLocationReminders(
                new Response.Listener<ArrayList<LocationReminder>>() {
                    @Override
                    public void onResponse(ArrayList<LocationReminder> response) {
                        if (!response.isEmpty()) {
                            Log.d(TAG,response.toString());

                            locationRemindersAdapter=new LocationRemindersAdapter(getActivity().getApplicationContext(),response, token);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                            recyclerView.setAdapter(locationRemindersAdapter);
                        } else {
                            Log.d(TAG, "No reminders set for this patient");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(), "No Location Reminder", Toast.LENGTH_LONG).show();

                    }
                },
                patientId,
                token
        );

        App.addRequest(gsonGetRequest, TAG);
    }


}
