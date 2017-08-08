package com.example.sarika.alzheimerassistant.network;

import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.LocationReminder;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * Created by sarika on 4/2/17.
 */
public class LocationReminderApiRequests {

    private static Gson gson = new GsonBuilder().create();


    /**
     * Returns a dummy object's array
     *
     * @param listener is the listener for the correct answer
     * @param errorListener is the listener for the error response
     *
     * @return {@link GsonGetRequest}
     */

    public static GsonGetRequest<PostResponse> getLocation
    (
            @NonNull final Response.Listener<PostResponse> listener,
            @NonNull final Response.ErrorListener errorListener
    )
    {
        final String url = Util.SERVER_URL+"";/*BuildConfig.apiDomainName + "/v2/55973508b0e9e4a71a02f05f";*/

        return new GsonGetRequest<>
                (
                        url,
                        new TypeToken<PostResponse>() {}.getType(),
                        gson,
                        listener,
                        errorListener
                );
    }

    /**
     * Returns a dummy object's array
     *
     * @param listener is the listener for the correct answer
     * @param errorListener is the listener for the error response
     *
     * @return {@link GsonGetRequest}
     */
    public static GsonGetRequest<ArrayList<LocationReminder>> getLocationReminders
    (
            @NonNull final Response.Listener<ArrayList<LocationReminder>> listener,
            @NonNull final Response.ErrorListener errorListener,
            String token
    )
    {
        final String url = Util.SERVER_URL+"/smartReminder/getReminders";/*BuildConfig.apiDomainName + "/v2/5597d86a6344715505576725";*/

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<ArrayList<LocationReminder>>() {}.getType(),
                        gson,
                        listener,
                        errorListener
                );
    }


    /**
     * Returns a dummy object's array
     *
     * @param listener is the listener for the correct answer
     * @param errorListener is the listener for the error response
     *
     * @return {@link GsonGetRequest}
     */
    public static GsonGetRequest<ArrayList<LocationReminder>> getPatientLocationReminders
    (
            @NonNull final Response.Listener<ArrayList<LocationReminder>> listener,
            @NonNull final Response.ErrorListener errorListener,
            String patientId,
            String token
    )
    {
        final String url = format("%s/smartReminder/getReminders/%s", Util.SERVER_URL, patientId);

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<ArrayList<LocationReminder>>() {}.getType(),
                        gson,
                        listener,
                        errorListener
                );
    }


    /**
     * Returns a dummy object
     *
     * @param listener is the listener for the correct answer
     * @param errorListener is the listener for the error response
     *@param latitude is the caregiver Object having caregiver details
     *@param longitude is the caregiver Object having caregiver details
     *@param locationDescription is the caregiver Object having caregiver details
     *@param locationName is the caregiver Object having caregiver details
     *@param token is the caregiver Object having caregiver details

     * @return {@link GsonPostRequest}
     */

    public static GsonPostRequest postLocation
    (
            @NonNull final Response.Listener<PostResponse> listener,
            @NonNull final Response.ErrorListener errorListener,
            double latitude,
            double longitude,
            String locationName,
            String locationDescription,
            final String token
    ) {
        final String url = Util.SERVER_URL+"/location/add";

        final JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("latitude",latitude);
        jsonObject.addProperty("longitude",longitude);
        jsonObject.addProperty("locationDescription",locationDescription);
        jsonObject.addProperty("locationName",locationName);

        final GsonPostRequest gsonPostRequest = new GsonPostRequest(
                Util.getHeader(token),
                url,
                jsonObject.toString(),
                new TypeToken<PostResponse>(){}.getType(),
                gson,
                listener,
                errorListener

        );

        gsonPostRequest.setShouldCache(false);
        return gsonPostRequest;
    }

    public static GsonPostRequest sendNotifications(
            @NonNull final Response.Listener<PostResponse> listener,
            @NonNull final Response.ErrorListener errorListener,
            ArrayList<String> requestIds,
            String token
    ){
        final String url = Util.SERVER_URL+"/fcmNotification/sendLocationReminders";
        final JsonObject jsonObject=new JsonObject();
        Type listOfRequestIds = new TypeToken<ArrayList<String>>(){}.getType();
        jsonObject.addProperty("requestIds",gson.toJson(requestIds,listOfRequestIds));


        final GsonPostRequest gsonPostRequest = new GsonPostRequest(
                Util.getHeader(token),
                url,
                jsonObject.toString(),
                new TypeToken<PostResponse>(){}.getType(),
                gson,
                listener,
                errorListener

        );

        gsonPostRequest.setShouldCache(false);
        return gsonPostRequest;

    };

    public static GsonPostRequest sendNotifications(
            @NonNull final Response.Listener<PostResponse> listener,
            @NonNull final Response.ErrorListener errorListener,
            String requestIds,
            String token
    ){
        final String url = Util.SERVER_URL+"/fcmNotification/addPatientNotification";
        final JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("requestIds",requestIds);


        final GsonPostRequest gsonPostRequest = new GsonPostRequest(
                Util.getHeader(token),
                url,
                jsonObject.toString(),
                new TypeToken<PostResponse>(){}.getType(),
                gson,
                listener,
                errorListener

        );

        gsonPostRequest.setShouldCache(false);
        return gsonPostRequest;

    };


    public static GsonPostRequest deletReminder(
            @NonNull final Response.Listener<PostResponse> listener,
            @NonNull final Response.ErrorListener errorListener,
            String requestId,
            String token
    ){
        final String url = Util.SERVER_URL+"/smartReminder/deleteReminder";
        final JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("reminderId",requestId);


        final GsonPostRequest gsonPostRequest = new GsonPostRequest(
                Util.getHeader(token),
                url,
                jsonObject.toString(),
                new TypeToken<PostResponse>(){}.getType(),
                gson,
                listener,
                errorListener

        );

        gsonPostRequest.setShouldCache(false);
        return gsonPostRequest;

    };

}
