package com.example.sarika.alzheimerassistant.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sarika on 5/10/17.
 */

public class Location implements Parcelable {

    @SerializedName("latitude")
    private double lat;
    @SerializedName("longitude")
    private double lng;

    protected Location(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    @Override
    public String toString() {
        return "Location{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }

    public Location() {
        this.lat=0.0;
        this.lng=0.0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDoubleArray(new double[]{this.lat, this.lng});
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {

        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public JSONObject getJson(){
        JSONObject json = new JSONObject();

        try {
            json.put("latitude", lat);
            json.put("longitude", lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
