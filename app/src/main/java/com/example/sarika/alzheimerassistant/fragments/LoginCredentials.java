package com.example.sarika.alzheimerassistant.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.example.sarika.alzheimerassistant.activity.FirstActivity;
import com.example.sarika.alzheimerassistant.activity.LoginActivity;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.Details;
import com.example.sarika.alzheimerassistant.bean.Login;
import com.example.sarika.alzheimerassistant.bean.Patient;
import com.example.sarika.alzheimerassistant.bean.Person;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.network.CaregiverApiRequests;
import com.example.sarika.alzheimerassistant.network.GsonPostRequest;
import com.example.sarika.alzheimerassistant.network.LoginApiRequests;
import com.example.sarika.alzheimerassistant.network.PatientApiRequests;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.gson.Gson;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginCredentials.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginCredentials#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginCredentials extends Fragment implements Validator.ValidationListener ,View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = LoginCredentials.class.getSimpleName();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Bundle bundle;
    private String personString;
    private Person person;
    private OnFragmentInteractionListener mListener;
    String role;

    private Validator validator;
    Gson gson=new Gson();

    @BindView(R.id.username)
    EditText username;

    @Password(min = 2)
    @BindView(R.id.password)
    EditText password;

    @ConfirmPassword
    @BindView(R.id.confirmpwd)
    EditText confirmPassword;

    @BindView(R.id.login_credentials)
    Button nextButton;

    Patient patient;

    public LoginCredentials() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginCredentials.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginCredentials newInstance(String param1, String param2) {
        LoginCredentials fragment = new LoginCredentials();
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
        View view = inflater.inflate(R.layout.fragment_login_credentials, container, false);
        ButterKnife.bind(this,view);
    //    bundle = getArguments();
//        personString = (String) bundle.getSerializable("Person");
        role = PreferenceManager.getRoleFromsharedPreference(getActivity().getApplicationContext());
        if(role.equals(Util.CAREGIVER)) {
            person=new Caregiver();
        }else{
            if(role.equals(Util.PATIENT)) {
                person=new Patient();
            }

        }
        validator = new Validator(this);
        validator.setValidationListener(this);
        nextButton.setOnClickListener(this);
        return view;
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
    public void onValidationSucceeded() {
        Login login=new Login();
        login.setUsername(username.getText().toString());
        login.setPassword(password.getText().toString());
        login.setRole(PreferenceManager.getRoleFromsharedPreference(getActivity().getApplicationContext()));
        person.setLogin(login);

        if(role.equals(Util.PATIENT)) {
            postLoginDetails(person.getLogin());
        }
        else if(role.equals(Util.CAREGIVER)) {
            postLoginDetails(person.getLogin());
        }
    }

    private void postLoginDetails(Login login) {

        GsonPostRequest gsonPostRequest = LoginApiRequests.postloginRegistrationRequest(
                new Response.Listener<Login>() {
                    @Override
                    public void onResponse(Login response) {
                    Log.i(TAG,response.toString());
                    PreferenceManager.setUserDetails(getActivity().getApplicationContext(),response.getUserId(),login.getUsername());
                    Toast.makeText(getActivity().getApplicationContext(),"Login Details Saved",Toast.LENGTH_LONG).show();
                    PreferenceManager.setFirstTimeLogin(getActivity().getApplicationContext(),true);
                    Intent intent=new Intent(getActivity(), FirstActivity.class);
                    mListener.onNextSelected(intent);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                },
                login.getUsername(), login.getPassword(),login.getRole()
        );
        App.addRequest(gsonPostRequest,TAG);

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
            case R.id.login_credentials:
                validator.validate();
                break;
        }

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
        void onNextSelected(Person person, Fragment fragment);
        void onNextSelected(Intent intent);
    }

}