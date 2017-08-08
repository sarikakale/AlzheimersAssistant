package com.example.sarika.alzheimerassistant.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.activity.CaregiverGeofenceSetupActivity;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.GeoFenceDetail;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.util.Util;

import org.apache.http.HttpHeaders;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.lang.String.format;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GeofenceFragment.GeofenceCallbacks} interface
 * to handle interaction events.
 * Use the {@link GeofenceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeofenceFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String TAG = getClass().getSimpleName();

    private GeofenceCallbacks mListener;

    @BindView(R.id._currentGeoFenceDetails)
    TextView currentGeofenceDetails;

    @BindView(R.id.setupGeofenceButton)
    Button setupButton;

    @BindView(R.id.progressBar2)
    ProgressBar progressBar;

    public static final String GEOFENCE_DETAIL_PARCEL_KEY = "gfDetailParcel";

    private GeoFenceDetail gfDetail;
    private String patientName;
    private Geocoder geocoder;

    public GeofenceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GeofenceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeofenceFragment newInstance(String param1) {
        GeofenceFragment fragment = new GeofenceFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO: 5/1/17 Clean up
        // 4/30/17 Set the ID in the gfDetail object
        Log.d(TAG, "Entered geoFence fragment !!");

        View view = inflater.inflate(R.layout.fragment_geofence, container, false);
        ButterKnife.bind(this,view);

        geocoder = new Geocoder(getContext(), Locale.getDefault());

        // Inflate the layout for this fragment
        return view;
    }

    private JsonArrayRequest constructGetDetailRequest() throws JSONException {

        final String token = PreferenceManager
                .getTokenFromsharedPreference(getActivity().getApplicationContext());
        /*
        Variables after making changes
        */
        String patientId = gfDetail.getId();
        String url = format("%s/geoFence/getFence/%s", Util.SERVER_URL, patientId);
        Log.d(TAG, "Geofence request URL is : "+url);

        return new JsonArrayRequest(Request.Method.GET,
                url, null ,
                response -> {
                    // 5/1/17 Populate the textView
                    try {
                        currentGeofenceDetails.setVisibility(View.VISIBLE);
                        setupButton.setVisibility(View.VISIBLE);

                        currentGeofenceDetails.setEnabled(true);
                        setupButton.setEnabled(true);
                        JSONObject currFenceDtls = response.getJSONObject(0);
                        currentGeofenceDetails.setText(
                                generateGeoFenceDetailsFromResponse(currFenceDtls));
                        progressBar.setVisibility(View.INVISIBLE);

                    } catch (JSONException | IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }, error -> {
            // 5/1/17 If geo-fence does not exist then set a message

            currentGeofenceDetails.setVisibility(View.VISIBLE);
            setupButton.setVisibility(View.VISIBLE);

            Log.e(TAG, "Geofence does not exist for user : "+ error.getMessage());
            currentGeofenceDetails.setText(getResources()
                    .getString(R.string.geofenceNotExistMsg));
            currentGeofenceDetails.setEnabled(true);
            setupButton.setEnabled(true);
            progressBar.setVisibility(View.INVISIBLE);
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put(HttpHeaders.AUTHORIZATION, token);
                return headers;
            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GeofenceCallbacks) {
            mListener = (GeofenceCallbacks) context;
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
     *
     * @param response JSON Object which holds the details response after querying from the
     *                 server.
     * @return A string which can be set to the text field to be displayed to the user.
     * @throws JSONException When JSON is not correctly parsed.
     * @throws IOException Sometimess
     */
    private String generateGeoFenceDetailsFromResponse(JSONObject response) throws JSONException,
            IOException {

        JSONObject location = response.getJSONObject("location");
        List<android.location.Address> addresses = geocoder.getFromLocation(location.getDouble("latitude"),
                location.getDouble("longitude"),1);



        String output = format(Locale.getDefault(),
                " Patient Name : %s%s%s\n Location : %s\n Radius :%dm", patientName,
                format(Locale.getDefault(),"\n Latitude %.2f", location.getDouble("latitude")),
                format(Locale.getDefault(),"\n Longitude %.2f", location.getDouble("longitude")),
                addresses.get(0).getAddressLine(0), response.getLong("radius"));

        gfDetail.setLat(location.getDouble("latitude"));
        gfDetail.setLng(location.getDouble("longitude"));
        gfDetail.setRadius(response.getLong("radius"));
        return output;
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
    public interface GeofenceCallbacks {
        // TODO: Update argument type and name
        void onGeofenceSelected(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, " On Resume called");

        // 5/1/17 Populate the text view with the current details
        try {
            if(gfDetail == null)
                gfDetail = new GeoFenceDetail();
            gfDetail.setId(getActivity().getIntent().getStringExtra(getResources()
                    .getString(R.string.patient)));
            patientName = getActivity().getIntent().getStringExtra(getResources()
                    .getString(R.string.patient_name));
            setupButton.setOnClickListener(v -> {
                // TODO: 5/1/17 Start the activity here
                Log.d(TAG, "Activity can be started here. ");
                Intent intent = new Intent(getActivity(), CaregiverGeofenceSetupActivity.class);
                intent.putExtra(GEOFENCE_DETAIL_PARCEL_KEY, gfDetail);
                startActivity(intent);
            });
            App.addRequest( constructGetDetailRequest(), TAG);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

    }
}
