package com.example.sarika.alzheimerassistant.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.activity.RemindersActivity;
import com.example.sarika.alzheimerassistant.adapter.AlbumsAdapter;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.FacialLibrary;
import com.example.sarika.alzheimerassistant.network.FacialLibraryApiRequests;
import com.example.sarika.alzheimerassistant.network.GsonGetRequest;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.util.Util;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FindMyFriendsFragment.FindMyFriendsCallback} interface
 * to handle interaction events.
 * Use the {@link FindMyFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindMyFriendsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = RemindersActivity.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    private ArrayList<String> albumList;

    private FindMyFriendsCallback mListener;

    public FindMyFriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FindMyFriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindMyFriendsFragment newInstance(String param1) {
        FindMyFriendsFragment fragment = new FindMyFriendsFragment();
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
        View view= inflater.inflate(R.layout.fragment_find_my_friends, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        albumList = new ArrayList<>();
        if(PreferenceManager.getRoleFromsharedPreference(getActivity().getApplicationContext()).equals(Util.PATIENT)) {
            prepareAlbums();
        }else {
            if (PreferenceManager.getRoleFromsharedPreference(getActivity().getApplicationContext()).equals(Util.CAREGIVER)){
                prepareAlbumsForPatients();
            }

        }

            return view;
    }

    private void prepareAlbums() {
        final String token = PreferenceManager.getTokenFromsharedPreference(getActivity().getApplicationContext());
        GsonGetRequest gsonGetRequest= FacialLibraryApiRequests.getImages(
                new Response.Listener<ArrayList<String>>() {
                    @Override
                    public void onResponse(ArrayList<String> response) {
                        albumList = response;
                        adapter = new AlbumsAdapter(getActivity(), albumList);

                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(adapter);

                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                },
                token
        );
        App.addRequest(gsonGetRequest,TAG);

    }

    private void prepareAlbumsForPatients() {
        final String token = PreferenceManager.getTokenFromsharedPreference(getActivity().getApplicationContext());
        final String patientId = getActivity().getIntent().getStringExtra(getResources()
                .getString(R.string.patient));

        GsonGetRequest gsonGetRequest= FacialLibraryApiRequests.getPatientImages(
                new Response.Listener<ArrayList<String>>() {
                    @Override
                    public void onResponse(ArrayList<String> response) {
                        albumList = response;
                        adapter = new AlbumsAdapter(getActivity(), albumList);

                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(adapter);

                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                },
                token,
                patientId
        );
        App.addRequest(gsonGetRequest,TAG);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onMyFriendSelected(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FindMyFriendsCallback) {
            mListener = (FindMyFriendsCallback) context;
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
    public interface FindMyFriendsCallback {
        // TODO: Update argument type and name
        void onMyFriendSelected(Uri uri);
    }
}
