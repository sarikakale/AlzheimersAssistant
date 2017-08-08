package com.example.sarika.alzheimerassistant.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.activity.RemindersActivity;
import com.example.sarika.alzheimerassistant.adapter.CaregiverArrayAdapter;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.network.CaregiverApiRequests;
import com.example.sarika.alzheimerassistant.network.GsonGetRequest;
import com.example.sarika.alzheimerassistant.network.GsonPostRequest;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.util.Util;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyCirclesFragment.MyCirclesCallback} interface
 * to handle interaction events.
 * Use the {@link MyCirclesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCirclesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = RemindersActivity.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    ArrayList<Caregiver> caregiverList;
    CaregiverArrayAdapter caregiverArrayAdapter;
    private MyCirclesCallback mListener;

    public MyCirclesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MyCirclesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyCirclesFragment newInstance(String param1) {
        MyCirclesFragment fragment = new MyCirclesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
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
        View view =  inflater.inflate(R.layout.fragment_my_circles, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.circleList);

        if(PreferenceManager.getRoleFromsharedPreference(getActivity().getApplicationContext()).equals(Util.PATIENT)) {
            getCaregivers();
        }else {
            if (PreferenceManager.getRoleFromsharedPreference(getActivity().getApplicationContext()).equals(Util.CAREGIVER)){
                getCaregiversForPAtients();
            }

        }
        return view;
    }

    private void getCaregiversForPAtients() {
        final String token = PreferenceManager.getTokenFromsharedPreference(getActivity().getApplicationContext());
        final String patientId = getActivity().getIntent().getStringExtra(getResources()
                .getString(R.string.patient));
        GsonGetRequest gsonGeRequest= CaregiverApiRequests.getCaregversListForPatients(
                new Response.Listener<ArrayList<Caregiver>>() {
                    @Override
                    public void onResponse(ArrayList<Caregiver> response) {
                        if (!response.isEmpty()) {
                            caregiverList = response;
                            Log.d(TAG, response.toString());
                            caregiverArrayAdapter = new CaregiverArrayAdapter(getActivity(), caregiverList, new CaregiverArrayAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(Caregiver caregiver) {

                                }
                            });
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(caregiverArrayAdapter);
                        } else {

                            Toast.makeText(getActivity().getApplicationContext(), "No Friends", Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(), "No Friends", Toast.LENGTH_SHORT).show();

                    }
                },
                token,
                patientId
        );
        App.addRequest(gsonGeRequest,TAG);

    }

    private void getCaregivers() {
        final String token= PreferenceManager.getTokenFromsharedPreference(getActivity().getApplicationContext());
        GsonGetRequest gsonGeRequest= CaregiverApiRequests.getCaregversList(
                new Response.Listener<ArrayList<Caregiver>>() {
                    @Override
                    public void onResponse(ArrayList<Caregiver> response) {
                        if (!response.isEmpty()) {
                            caregiverList = response;
                            Log.d(TAG, response.toString());
                            caregiverArrayAdapter = new CaregiverArrayAdapter(getActivity(), caregiverList, new CaregiverArrayAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(Caregiver caregiver) {

                                }
                            });
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(caregiverArrayAdapter);
                        } else {

                            Toast.makeText(getActivity().getApplicationContext(), "No Friends", Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(), "No Friends", Toast.LENGTH_SHORT).show();

                    }
                },
                token
        );
        App.addRequest(gsonGeRequest,TAG);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onMyCircleSelected(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyCirclesCallback) {
            mListener = (MyCirclesCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface MyCirclesCallback {
        // TODO: Update argument type and name
        void onMyCircleSelected(Uri uri);
    }
}
