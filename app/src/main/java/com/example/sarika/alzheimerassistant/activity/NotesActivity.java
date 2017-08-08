package com.example.sarika.alzheimerassistant.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.adapter.CaregiverArrayAdapter;
import com.example.sarika.alzheimerassistant.adapter.NotesAdapter;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.Notes;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.network.GsonGetRequest;
import com.example.sarika.alzheimerassistant.network.GsonPostRequest;
import com.example.sarika.alzheimerassistant.network.LoginApiRequests;
import com.example.sarika.alzheimerassistant.network.PatientApiRequests;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Notes> noteList=new ArrayList<>();
    private RecyclerView recyclerView;

    @BindView(R.id.editnotes)
    EditText editnotes;

    @BindView(R.id.notesBtn)
    FloatingActionButton notesBtn;



    String notes;
    private static final String TAG = NotesActivity.class.getSimpleName();
    String token;

    private NotesAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        ButterKnife.bind(this);
        recyclerView = (RecyclerView) findViewById(R.id.rvNotes);
        notesBtn.setOnClickListener(this);
        getNotesRequest();
    }

    public boolean validate() {

        if (notes.isEmpty()) {
            return false;
        }
        return true;

    }

    private void notesFailure() {
        Toast.makeText(getApplicationContext(), "Notes Empty", Toast.LENGTH_LONG).show();
    }

    private void notesSuccess() {
        Toast.makeText(getApplicationContext(), "Notes Saved", Toast.LENGTH_LONG).show();
    }

    private void notesFetchFailure() {
        Toast.makeText(getApplicationContext(), "notes Fetch Failure", Toast.LENGTH_LONG).show();
    }

    public void sentNotesRequest() {
        notes = editnotes.getText().toString();
        token = PreferenceManager.getTokenFromsharedPreference(getApplicationContext());

        if (!validate()) {
            notesFailure();
            return;
        }

        GsonPostRequest gsonPostRequest = PatientApiRequests.postPatientNotes(

                new Response.Listener<Notes>() {
                    @Override
                    public void onResponse(Notes response) {
                        notesSuccess();
                        noteList.add(response);
                        notesAdapter.notifyDataSetChanged();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        notesFailure();
                    }
                },

                token, notes
        );

        App.addRequest(gsonPostRequest, TAG);
    }


    public void getNotesRequest() {
        token = PreferenceManager.getTokenFromsharedPreference(getApplicationContext());
        GsonGetRequest gsonGetRequest = PatientApiRequests.getPatientNotes(

                new Response.Listener<ArrayList<Notes>>() {
                    @Override
                    public void onResponse(ArrayList<Notes> response) {
                        if(!response.isEmpty()) {
                            token = PreferenceManager.getTokenFromsharedPreference(getApplicationContext());

                            noteList = response;
                            Log.i("note-id", response.toString());
                            Log.d(TAG, noteList.toString());
                            if (noteList.isEmpty() || noteList == null) {
                                Log.d(TAG, "No values populated in noteList");
                            }
                            notesAdapter = new NotesAdapter(NotesActivity.this, noteList, token);
                            recyclerView.setLayoutManager(new LinearLayoutManager(NotesActivity.this));
                            recyclerView.setAdapter(notesAdapter);
                            notesFetchFailure();
                        }else{
                            Toast.makeText(getApplicationContext(), "No Notes", Toast.LENGTH_LONG).show();

                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        notesFetchFailure();
                    }
                },

                token

        );


        App.addRequest(gsonGetRequest, TAG);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.notesBtn:
                sentNotesRequest();
                break;

        }
    }
}
