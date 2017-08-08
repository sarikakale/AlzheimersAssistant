package com.example.sarika.alzheimerassistant.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.activity.PatientActivities;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.Address;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.Details;
import com.example.sarika.alzheimerassistant.bean.Name;
import com.example.sarika.alzheimerassistant.bean.Patient;
import com.example.sarika.alzheimerassistant.bean.Person;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.network.CaregiverApiRequests;
import com.example.sarika.alzheimerassistant.network.GsonGetRequest;
import com.example.sarika.alzheimerassistant.network.GsonPostRequest;
import com.example.sarika.alzheimerassistant.network.PatientApiRequests;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.gson.Gson;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactDetails extends Fragment implements Validator.ValidationListener,View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = ContactDetails.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Gson gson;
    Validator validator;

    @NotEmpty
    @BindView(R.id.cfirstName)
    TextInputEditText firstName ;

    @BindView(R.id.cmiddleName)
    TextInputEditText middleName;

    @NotEmpty
    @BindView(R.id.clastName)
    TextInputEditText lastName;

    @NotEmpty
    @BindView(R.id.caddr)
    TextInputEditText addressStreet;

    @NotEmpty
    @BindView(R.id.ccity)
    TextInputEditText city ;

    @NotEmpty
    @BindView(R.id.cstate)
    TextInputEditText state;

    @NotEmpty
    @BindView(R.id.ccountry)
    TextInputEditText country ;

    @NotEmpty
    @Digits(integer = 10, message = "Zipcode expected")
    @BindView(R.id.czip)
    TextInputEditText zipCode ;


    @NotEmpty
    @Pattern(regex = "(?:\\(\\d{3}\\)|\\d{3})[- ]?\\d{3}[- ]?\\d{4}", message = "Please enter a valid phone number")
    @BindView(R.id.cphone)
    TextInputEditText phone;

    @NotEmpty
    @Email
    @BindView(R.id.cemail)
    TextInputEditText email;


    @BindView(R.id.facial_next)
    FloatingActionButton nextButton;
    Patient patient;
    String token;

    private OnFragmentInteractionListener mListener;

    public ContactDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactDetails newInstance(String param1, String param2) {
        ContactDetails fragment = new ContactDetails();
        Bundle args = new Bundle();
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
        final View view =  inflater.inflate(R.layout.fragment_contact_details, container, false);
        ButterKnife.bind(this,view);
        validator = new Validator(this);
        validator.setValidationListener(this);
        nextButton.setOnClickListener(this);
        return view;
    }

    public Person sendPackage(Person person){

    //Initialize Beans
        Address address=new Address();
        Details details=new Details();
        Name name=new Name();

        name.setFirstName(firstName.getText().toString());
        name.setLastName(lastName.getText().toString());
        name.setMiddleName(middleName.getText().toString());

        address.setStreet(addressStreet.getText().toString());
        address.setCity(city.getText().toString());
        address.setState(state.getText().toString());
        address.setCountry(country.getText().toString());
        address.setZipcode(Long.parseLong(zipCode.getText().toString()));


        details.setAddress(address);
        details.setPhoneNumber(phone.getText().toString());
        Log.d(TAG,PreferenceManager.getUserName(getActivity().getApplicationContext()));
        if(PreferenceManager.getUserName(getActivity().getApplicationContext())!=null) {
            details.setEmail(PreferenceManager.getUserName(getActivity().getApplicationContext()));
        }else{
            details.setEmail(email.getText().toString());
        }
        details.setName(name);

        person.setDetails(details);

        return person;

    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    @Override
    public void onStop() {
        super.onStop();
        App.cancelAllRequests(TAG);
    }

    @Override
    public void onValidationSucceeded() {
            sendDataToLoginPage();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.facial_next:
                validator.validate();
                break;
        }

    }

    public void sendDataToLoginPage(){
        //onNextSelected
        Person person = null;
        token = PreferenceManager.getTokenFromsharedPreference(getActivity().getApplicationContext());
        if(PreferenceManager.getRoleFromsharedPreference(getActivity().getApplicationContext()).equals(Util.CAREGIVER)) {
           person = new Caregiver();
        }else{
            if(PreferenceManager.getRoleFromsharedPreference(getActivity().getApplicationContext()).equals(Util.PATIENT)){
                person = new Patient();
                patient = (Patient)person;
            }
        }
         person = sendPackage(person);
        if(PreferenceManager.getRoleFromsharedPreference(getActivity().getApplicationContext()).equals(Util.CAREGIVER))
             mListener.onNextSelected(person,FacialFragment.newInstance("Facial","Fragment"));
        if(PreferenceManager.getRoleFromsharedPreference(getActivity().getApplicationContext()).equals(Util.PATIENT)) {
            //post contact details
            postContactDetails(person.getDetails());
        }
    }

    private void postContactDetails(Details details ) {
        GsonPostRequest gsonPostRequest = CaregiverApiRequests.postContactDetailsRequest(
                new Response.Listener<Details>() {
                    @Override
                    public void onResponse(Details response) {
                        Log.i(TAG,response.toString());
                        patient.getDetails().setDetailId(response.getDetailId());
                        //post patient details
                        postPatientDetails();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(),"Could not Save Caregiver Details", Toast.LENGTH_LONG).show();
                    }
                },
                details,
                token

        );
        App.addRequest(gsonPostRequest,TAG);

    }

    private void postPatientDetails() {


            GsonPostRequest gsonPostRequest = PatientApiRequests.postPatientRegisterRequest(

                    new Response.Listener<PostResponse>() {
                        @Override
                        public void onResponse(PostResponse response) {
                            Log.d(TAG,response.getSuccess()+ " "+response.getBody());
                            Toast.makeText(getActivity().getApplicationContext(),"Patient Details Saved Successfully", Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(getActivity(), PatientActivities.class);
                            PreferenceManager.setFirstTimeLogin(getActivity().getApplicationContext(),false);
                            mListener.onNextSelected(intent);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG,error.getMessage());
                            Toast.makeText(getActivity().getApplicationContext(),"Could not Save Caregiver Details", Toast.LENGTH_LONG).show();

                        }
                    },
                    patient,
                    token
            );

            App.addRequest(gsonPostRequest,TAG);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onNextSelected(Person person,Fragment fragment);
        void onNextSelected(Intent intent);
    }
}
