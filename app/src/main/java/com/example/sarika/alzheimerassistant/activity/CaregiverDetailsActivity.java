package com.example.sarika.alzheimerassistant.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.Person;
import com.example.sarika.alzheimerassistant.fragments.ContactDetails;
import com.example.sarika.alzheimerassistant.fragments.FacialFragment;
import com.example.sarika.alzheimerassistant.fragments.LoginCredentials;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.other.ActivityResultBus;
import com.example.sarika.alzheimerassistant.other.ActivityResultEvent;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.google.gson.Gson;

public class CaregiverDetailsActivity extends AppCompatActivity implements LoginCredentials.OnFragmentInteractionListener, ContactDetails.OnFragmentInteractionListener, FacialFragment.OnFragmentInteractionListener {

    private Button nextButton;

    private static final String TAG = CaregiverDetailsActivity.class.getSimpleName();

    Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_details);
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        LoginCredentials loginCredentials=new LoginCredentials();
        fragmentTransaction.add(R.id.FragmentContainer,loginCredentials).commit();

    }


    @Override
    public void onNextSelected(Person person, Fragment fragment) {

        Bundle bundle=new Bundle();
        gson =new Gson();
        String personDetails = gson.toJson(person);
        bundle.putSerializable("Person", personDetails);
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragment.setArguments(bundle);

        if (fragmentManager.findFragmentById(R.id.FragmentContainer) != null) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fragmentTransaction.replace(R.id.FragmentContainer,fragment);
        fragmentTransaction.addToBackStack(null).setCustomAnimations(R.anim.transition_of_fragments,R.anim.exit_fragments);
        fragmentTransaction.commitAllowingStateLoss();

    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        ActivityResultBus.getInstance().postQueue(
                new ActivityResultEvent(requestCode, resultCode, data));
    }

    @Override
    public void onNextSelected(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        PreferenceManager.setFirstTimeLogin(getApplicationContext(),true);
        startActivity(intent);

    }
}
