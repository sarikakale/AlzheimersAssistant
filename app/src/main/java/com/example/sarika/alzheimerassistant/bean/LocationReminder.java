package com.example.sarika.alzheimerassistant.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sarika on 5/7/17.
 */

public class LocationReminder implements Parcelable {

    private final String TAG = getClass().getSimpleName();



    @SerializedName("_id")
    private String reminderId;
    @SerializedName("patientId")
    private String id;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @SerializedName("location")
    private Location location;
    private long radius;
    @SerializedName("message")
    private String reminderMessage;



    public LocationReminder() {
        id = "";
        location=new Location();
        radius = 0;
        reminderMessage = "";
    }

    private LocationReminder(String id, Location location, long radius, String reminderMessage) {
        super();
        this.id = id;
        this.location=location;
        this.radius = radius;
        this.reminderMessage = reminderMessage;
    }

    protected LocationReminder(Parcel in) {
        id = in.readString();


        location = in.readParcelable(Location.class.getClassLoader());
        radius = in.readLong();

        reminderMessage = in.readString();
        reminderId = in.readString();
        Log.d(TAG, "Populated parceled detail !!!");
    }

    public static final Creator<LocationReminder> CREATOR = new Creator<LocationReminder>() {
        @Override
        public LocationReminder createFromParcel(Parcel in) {
            return new LocationReminder(in);
        }

        @Override
        public LocationReminder[] newArray(int size) {
            return new LocationReminder[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public long getRadius() {
        return radius;
    }

    public void setRadius(long radius) {
        this.radius = radius;
    }

    public String getReminderMessage() {
        return reminderMessage;
    }

    public void setReminderMessage(String reminderMessage) {
        this.reminderMessage = reminderMessage;
    }

    public String getReminderId() {
        return reminderId;
    }

    public void setReminderId(String reminderId) {
        this.reminderId = reminderId;
    }

    public JSONObject getJson(){
        JSONObject json = new JSONObject();

        try {
            json.put("latitude", location.getLat());
            json.put("longitude",location.getLng());
            json.put("radius", radius);
            json.put("patientId", id);
            json.put("message",reminderMessage);
            json.put("_id",reminderId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "LocationRemindersAdapter{" +
                "TAG='" + TAG + '\'' +
                ", reminderId='" + reminderId + '\'' +
                ", id='" + id + '\'' +
                ", location=" + location +
                ", radius=" + radius +
                ", reminderMessage='" + reminderMessage + '\'' +
                '}';
    }

    public String getTAG() {
        return TAG;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeParcelable(this.location,flags);
        dest.writeLong(this.radius);
    }
}
