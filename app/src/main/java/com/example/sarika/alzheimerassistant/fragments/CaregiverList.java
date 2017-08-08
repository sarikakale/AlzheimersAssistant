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
import com.example.sarika.alzheimerassistant.adapter.CaregiverArrayAdapter;
import com.example.sarika.alzheimerassistant.adapter.PatientArrayAdapter;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.Patient;
import com.example.sarika.alzheimerassistant.network.CaregiverApiRequests;
import com.example.sarika.alzheimerassistant.network.GsonGetRequest;
import com.example.sarika.alzheimerassistant.network.PatientApiRequests;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CaregiverList.OnCaregiverSelected} interface
 * to handle interaction events.
 * Use the {@link CaregiverList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CaregiverList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = CaregiverList.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Caregiver> caregiverList=new ArrayList<>();
    private RecyclerView recyclerView;
    private CaregiverArrayAdapter caregiverArrayAdapter;
    private OnCaregiverSelected mListener;

    public CaregiverList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CaregiverList.
     */
    // TODO: Rename and change types and number of parameters
    public static CaregiverList newInstance(String param1, String param2) {
        CaregiverList fragment = new CaregiverList();
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
        View view =  inflater.inflate(R.layout.fragment_caregiver_list, container, false);
        //Recycler View
        recyclerView = (RecyclerView) view.findViewById(R.id.caregiverList);
        getData();
        return view;
    }

    private void getData() {
        String token = PreferenceManager.getTokenFromsharedPreference(getActivity());

        GsonGetRequest gsonGetRequest = CaregiverApiRequests.getCaregvers(
                new Response.Listener<ArrayList<Caregiver>>() {
                    @Override
                    public void onResponse(ArrayList<Caregiver> response) {
                        if(!response.isEmpty() || response == null) {
                            caregiverList = response;
                            if (caregiverList.isEmpty() || caregiverList == null) {
                                Log.d(TAG, "No values populated in caregiverList");
                            } else {
                                caregiverArrayAdapter = new CaregiverArrayAdapter(getActivity(), caregiverList, new CaregiverArrayAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Caregiver caregiver) {
                                        mListener.onCaregiverSelected(caregiver);
                                    }
                                });
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerView.setAdapter(caregiverArrayAdapter);
                            }
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(),"No Caregivers",Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        caregiverList = null;
                        Log.d(TAG,error.getMessage());
                    }
                },
                token
        );
        App.addRequest(gsonGetRequest,TAG);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCaregiverSelected) {
            mListener = (OnCaregiverSelected) context;
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCaregiverSelected {
        // TODO: Update argument type and name
        void onCaregiverSelected(Caregiver caregiver);
    }
}