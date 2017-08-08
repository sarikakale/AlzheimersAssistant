package com.example.sarika.alzheimerassistant.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.network.GsonPostRequest;
import com.example.sarika.alzheimerassistant.network.PatientApiRequests;
import com.example.sarika.alzheimerassistant.network.PatientFriendApiRequest;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_AddFriend extends Fragment implements View.OnClickListener{


    @BindView(R.id.addBtn)
    Button addFriendBtn;

    @BindView(R.id.editText)
    EditText editText;

    @BindView(R.id.editEmail)
    EditText editEmail;

    @BindView(R.id.family)
    CheckBox familyCheck;

    @BindView(R.id.friends)
    CheckBox friendsCheck;

    @BindView(R.id.neighbors)
    CheckBox neighborsCheck;

    Bundle bundle;
     String caregiverName;
    String caregiverEmail;
    String caregiverId;

   
    boolean circleValueFamily, circleFriends,circleNeighbors;
    String circleValue;
    private static final String TAG = Fragment_AddFriend.class.getSimpleName();



    public Fragment_AddFriend() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_fragment__add_friend, container, false);
        ButterKnife.bind(this,view);
        bundle = getArguments();
        caregiverName = (String) bundle.getSerializable(getResources().getString(R.string.caregiver_name));
        caregiverEmail = (String) bundle.getSerializable(getResources()
                .getString(R.string.caregiver_email));
        caregiverId = (String) bundle.getSerializable(getResources().getString(R.string.caregiver));
        Log.d(TAG, caregiverName+" "+caregiverEmail+" "+caregiverId);
        editText.setText(caregiverName);
        editEmail.setText(caregiverEmail);
        addFriendBtn.setOnClickListener(this);
        return view;


    }

    public void initializeParameters(){
        circleValueFamily = familyCheck.isChecked();
        circleFriends = friendsCheck.isChecked();
        circleNeighbors = neighborsCheck.isChecked();
        Log.i("Params1", caregiverName + caregiverEmail);
        if (circleValueFamily) circleValue = "Family";
        else if(circleFriends) circleValue = "Friends";
        else circleValue = "Neighbors";

        Log.i("Params", caregiverName + caregiverEmail + circleValue);
        if(!validate()){
            onAddFriendFailed();
            return;
        }



        sendAddFriendRequest(caregiverName, caregiverEmail, circleValue,caregiverId);
    }

    public void sendAddFriendRequest(String name, String email, String circle, String caregiverId){

        final String token  = PreferenceManager.getTokenFromsharedPreference(getActivity().getApplicationContext());
        Log.i("Params",token);
        GsonPostRequest gsonPostRequest = PatientFriendApiRequest.postAddFriendRequest(

                new Response.Listener<PostResponse>() {
                    @Override
                    public void onResponse(PostResponse response) {
                        addCaregverToCollection(caregiverId);

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onAddFriendFailed();
                    }
                },

               token ,name, email, circle, caregiverId
        );

        App.addRequest(gsonPostRequest,TAG);
    }

    public void addCaregverToCollection(String caregiverId){
        final String token  = PreferenceManager.getTokenFromsharedPreference(getActivity().getApplicationContext());

        GsonPostRequest gsonPostRequest = PatientApiRequests.postCaregiverToPatientCollection(
                new Response.Listener<PostResponse>() {
                    @Override
                    public void onResponse(PostResponse response) {
                        onAddFriendSuccess();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onAddFriendFailed();
                    }
                },
               token,
               caregiverId
        );
        App.addRequest(gsonPostRequest,TAG);

    }


    public boolean validate(){

        if(caregiverName.isEmpty() || caregiverEmail.isEmpty()){
            return false;
        }
        return true;

    }

    private void onAddFriendFailed() {
        Toast.makeText(getContext(),"Add Friend Failed",Toast.LENGTH_LONG).show();
    }

    private void onAddFriendSuccess() {
        Toast.makeText(getActivity().getApplicationContext(),"Friend Added",Toast.LENGTH_LONG).show();
    }



    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.addBtn:
                initializeParameters();
                break;

        }
    }
}

