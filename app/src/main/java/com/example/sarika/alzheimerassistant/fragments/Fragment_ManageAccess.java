package com.example.sarika.alzheimerassistant.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.network.PatientFriendApiRequest;
import com.example.sarika.alzheimerassistant.network.GsonPostRequest;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link Fragment_ManageAccess#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_ManageAccess extends Fragment implements View.OnClickListener  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.familyLoc)
    CheckBox familyLoc;

    @BindView(R.id.friendsLoc)
    CheckBox friendsLoc;

    @BindView(R.id.neighborsLoc)
    CheckBox neighborsLoc;

    @BindView(R.id.familyNotes)
    CheckBox familyNotes;

    @BindView(R.id.friendsNotes)
    CheckBox friendsNotes;

    @BindView(R.id.neighborsNotes)
    CheckBox neighborsNotes;

    @BindView(R.id.familyVitals)
    CheckBox familyVitals;

    @BindView(R.id.friendsVitals)
    CheckBox friendsVitals;

    @BindView(R.id.neighborsVitals)
    CheckBox neighborsVitals;

    @BindView(R.id.manageAccessBtn)
    Button manageAccessBtn;
    String token;

    boolean familyLocValue, friendsLocValue, neighborsLocValue, familyNotesValue, friendsNotesValue, neighborsNotesValue, familyVitalsValue, friendsVitalsValue, neighborsVitalsValue;
    private static final String TAG = Fragment_ManageAccess.class.getSimpleName();

    // private OnFragmentInteractionListener mListener;

    public Fragment_ManageAccess() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_ManageAccess.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_ManageAccess newInstance(String param1, String param2) {
        Fragment_ManageAccess fragment = new Fragment_ManageAccess();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment__manage_access, container, false);
        ButterKnife.bind(this,view);
        manageAccessBtn.setOnClickListener(this);
        return view;
    }

    public void initializeParameters(){
        familyLocValue = familyLoc.isChecked();
        friendsLocValue = friendsLoc.isChecked();
        neighborsLocValue = neighborsLoc.isChecked();
        familyNotesValue = familyNotes.isChecked();
        friendsNotesValue = friendsNotes.isChecked();
        neighborsNotesValue = neighborsNotes.isChecked();
        familyVitalsValue = familyVitals.isChecked();
        friendsVitalsValue = friendsVitals.isChecked();
        neighborsVitalsValue = neighborsVitals.isChecked();

        JsonObject jsonObjectFamily = new JsonObject();
        jsonObjectFamily.addProperty("location", familyLocValue);
        jsonObjectFamily.addProperty("notes", familyNotesValue);
        jsonObjectFamily.addProperty("vitals", familyVitalsValue);

        JsonObject jsonObjectFriends = new JsonObject();
        jsonObjectFriends.addProperty("location", friendsLocValue);
        jsonObjectFriends.addProperty("notes", friendsNotesValue);
        jsonObjectFriends.addProperty("vitals", friendsVitalsValue);

        JsonObject jsonObjectNeighbors = new JsonObject();
        jsonObjectNeighbors.addProperty("location", neighborsLocValue);
        jsonObjectNeighbors.addProperty("notes", neighborsNotesValue);
        jsonObjectNeighbors.addProperty("vitals", neighborsVitalsValue);

        JsonObject requestObject = new JsonObject();
        requestObject.add("Family", jsonObjectFamily);
        requestObject.add("Friends", jsonObjectFriends);
        requestObject.add("Neighbors", jsonObjectNeighbors);

        sendManageAccessRequest(requestObject);
    }

    public void sendManageAccessRequest(JsonObject requestObject){
        token = PreferenceManager.getTokenFromsharedPreference(getActivity().getApplicationContext());
        Log.i("Params", requestObject.toString());
        GsonPostRequest gsonPostRequest = PatientFriendApiRequest.postManageAccessRequest(

                new Response.Listener<PostResponse>() {
                    @Override
                    public void onResponse(PostResponse response) {
                        onManageFriendSuccess();

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onManageFriendFail();
                    }
                },

                token, requestObject.toString()

        );

        App.addRequest(gsonPostRequest,TAG);
    }


    private void onManageFriendFail() {
        Toast.makeText(getContext(),"Access couldn't be assigned",Toast.LENGTH_LONG).show();
    }

    private void onManageFriendSuccess() {
        Toast.makeText(getActivity().getApplicationContext(),"Access Added",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.manageAccessBtn:
                initializeParameters();
                break;

        }
    }
}
