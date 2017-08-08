package com.example.sarika.alzheimerassistant.adapter;

import android.content.Context;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.activity.RemindersActivity;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.LocationReminder;
import com.example.sarika.alzheimerassistant.bean.Notes;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.fragments.LocationReminderRowFragment;
import com.example.sarika.alzheimerassistant.fragments.NoteRow;
import com.example.sarika.alzheimerassistant.network.GsonPostRequest;
import com.example.sarika.alzheimerassistant.network.LocationReminderApiRequests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by sarika on 3/27/17.
 */
public class LocationRemindersAdapter extends RecyclerView.Adapter<LocationRemindersAdapter.ViewHolder> {

    private static final String TAG = RemindersActivity.class.getSimpleName();
    LayoutInflater inflater;
    ArrayList<LocationReminder> locationList = null;
    Context context;
    LocationReminderRowFragment lr;
    String token;
    Geocoder geocoder;

    public LocationRemindersAdapter(Context context, ArrayList<LocationReminder> locationList, String token){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.locationList = locationList;
        this.token = token;
        lr = new LocationReminderRowFragment();
        geocoder = new Geocoder(this.context, Locale.getDefault());
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.location_reminder_row,parent,false);
        LocationRemindersAdapter.ViewHolder viewHolder = new LocationRemindersAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LocationReminder locationReminder=locationList.get(position);
        Log.d("LocationRemindr",locationReminder.toString());
        holder.locationreminderrow.setText(locationReminder.getReminderMessage());
        try {
            holder.address.setText(geocoder.getFromLocation(locationReminder.getLocation().getLat(),locationReminder.getLocation().getLng(),1).get(0).getAddressLine(0).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.deleteReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReminder(locationReminder.getReminderId(), token,position);
            }
        });

    }

    public void deleteReminder(String requestId, String token, int position) {
        GsonPostRequest gsonPostRequest = LocationReminderApiRequests.deletReminder(
                new Response.Listener<PostResponse>() {
                    @Override
                    public void onResponse(PostResponse response) {
                        locationList.remove(position);
                        notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Could not Delete Reminder", Toast.LENGTH_LONG).show();

                    }
                },
                requestId,
                token
        );
        App.addRequest(gsonPostRequest,TAG);
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView locationreminderrow;
        TextView address;
        Button deleteReminder;
        public ViewHolder(View itemView) {
            super(itemView);
            locationreminderrow= (TextView) itemView.findViewById(R.id.locationreminderrow);
            address = (TextView) itemView.findViewById(R.id.address);
            deleteReminder= (Button) itemView.findViewById(R.id.delete_reminder);
        }
    }
}
