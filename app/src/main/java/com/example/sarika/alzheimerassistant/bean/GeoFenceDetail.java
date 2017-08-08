package com.example.sarika.alzheimerassistant.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sarika on 4/30/17.getCaregivers/
 */

public class GeoFenceDetail implements Parcelable{

    private final String TAG = getClass().getSimpleName();
    private String id;
    private double lat;
    private double lng;
    private long radius;

    public GeoFenceDetail() {
        id = "";
        lat = 0;
        lng = 0;
        radius = 0;
    }

    private GeoFenceDetail(String id, double lat, double lng, long radius) {
        super();
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
    }

    protected GeoFenceDetail(Parcel in) {
        id = in.readString();

        double[] locationParams = new double[2];
        in.readDoubleArray(locationParams);

        lat = locationParams[0];
        lng = locationParams[1];

        radius = in.readLong();
        Log.d(TAG, "Populated parceled detail !!!");
    }

    public static final Creator<GeoFenceDetail> CREATOR = new Creator<GeoFenceDetail>() {
        @Override
        public GeoFenceDetail createFromParcel(Parcel in) {
            return new GeoFenceDetail(in);
        }

        @Override
        public GeoFenceDetail[] newArray(int size) {
            return new GeoFenceDetail[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getRadius() {
        return radius;
    }

    public void setRadius(long radius) {
        this.radius = radius;
    }

    public JSONObject getJson(){
        JSONObject json = new JSONObject();

        try {
            json.put("latitude", lat);
            json.put("longitude", lng);
            json.put("radius", radius);
            json.put("patientId", id);
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeDoubleArray(new double[]{this.lat, this.lng});
        dest.writeLong(this.radius);
    }
}
