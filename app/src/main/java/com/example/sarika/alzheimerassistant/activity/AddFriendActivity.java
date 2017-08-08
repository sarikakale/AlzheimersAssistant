package com.example.sarika.alzheimerassistant.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.fragments.CaregiverList;
import com.example.sarika.alzheimerassistant.fragments.Fragment_AddFriend;
import com.example.sarika.alzheimerassistant.fragments.Fragment_ManageAccess;

public class AddFriendActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CaregiverList.OnCaregiverSelected {

    private static final String TAG = AddFriendActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_add_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager manager = getSupportFragmentManager();
        if (id == R.id.nav_addfriend) {
            CaregiverList caregiverListFragment=new CaregiverList();
            if(manager.findFragmentById(R.id.fragment_main)!=null) {
                manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            manager.beginTransaction().replace(R.id.fragment_main, CaregiverList.newInstance("caregiver","list")).
                    addToBackStack(null).commitAllowingStateLoss();
        } else if(R.id.nav_manage == id){
            Fragment_ManageAccess fragment_manageAccess = Fragment_ManageAccess.newInstance("Manage","Access");
            if(manager.findFragmentById(R.id.fragment_main)!=null) {
                manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            manager.beginTransaction().replace(R.id.fragment_main, fragment_manageAccess).
                    addToBackStack(null).commitAllowingStateLoss();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onCaregiverSelected(Caregiver caregiver) {
        //Set Caregiver Fragment
        Fragment_AddFriend fragment=new Fragment_AddFriend();
        //TODO: Unhandled for generic fragment only pop one fragment. have to handle it generic.
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.caregiver),caregiver.getUserId());
        bundle.putString(getResources().getString(R.string.caregiver_name),
                String.format("%s %s", caregiver.getDetails().getName().getFirstName(),
                        caregiver.getDetails().getName().getLastName()));
        bundle.putString(getResources().getString(R.string.caregiver_email),caregiver.getDetails().getEmail());
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.add_activity_fragment) != null) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FrameLayout frameLayout= (FrameLayout) findViewById(R.id.add_activity_fragment);
        frameLayout.removeAllViews();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.add_activity_fragment, fragment);
        fragmentTransaction.addToBackStack(null).setCustomAnimations(R.anim.transition_of_fragments,R.anim.exit_fragments);
        fragmentTransaction.commitAllowingStateLoss();
    }

}
