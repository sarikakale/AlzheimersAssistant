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
import com.example.sarika.alzheimerassistant.adapter.PatientArrayAdapter;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.Patient;
import com.example.sarika.alzheimerassistant.network.GsonGetRequest;
import com.example.sarika.alzheimerassistant.network.PatientApiRequests;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyPatientsFragments.MyPatientsFragmentCallbacks} interface
 * to handle interaction events.
 * Use the {@link MyPatientsFragments#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPatientsFragments extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = MyPatientsFragments.class.getSimpleName();
    private MyPatientsFragmentCallbacks mListener;
    private ArrayList<Patient> patientList = new ArrayList<Patient>();

    private RecyclerView recyclerView;

    private PatientArrayAdapter patientArrayAdapter;

    public MyPatientsFragments() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MyPatientsFragments.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPatientsFragments newInstance(String param1) {
        MyPatientsFragments fragment = new MyPatientsFragments();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
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
        View view = inflater.inflate(R.layout.fragment_my_patients_fragments, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.patientList);
        //Volley Patient List
        getData();



        return view;
    }

    public void getData(){

        String token = PreferenceManager.getTokenFromsharedPreference(getActivity());

        GsonGetRequest gsonGetRequest = PatientApiRequests.getPatientList(
                new Response.Listener<ArrayList<Patient>>() {
                    @Override
                    public void onResponse(ArrayList<Patient> response) {
                        if(!response.isEmpty() || response == null) {
                            patientList = response;

                            if (patientList.isEmpty() || patientList == null) {
                                Log.d(TAG, "No values populated in patientList");
                            } else {

                                patientArrayAdapter = new PatientArrayAdapter(getActivity(), patientList, new PatientArrayAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Patient patient) {
                                        mListener.onPatientSelected(patient);
                                    }
                                });

                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerView.setAdapter(patientArrayAdapter);


                            }
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(),"No Patients",Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        patientList = null;
                        Log.d(TAG,error.getMessage());
                    }
                },
                token
        );

        App.addRequest(gsonGetRequest,TAG);



    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Patient patient) {
        if (mListener != null) {
            mListener.onPatientSelected(patient);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyPatientsFragmentCallbacks) {
            mListener = (MyPatientsFragmentCallbacks) context;
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
    public interface MyPatientsFragmentCallbacks {
        // TODO: Update argument type and name
        void onPatientSelected(Patient patient);
    }
}
