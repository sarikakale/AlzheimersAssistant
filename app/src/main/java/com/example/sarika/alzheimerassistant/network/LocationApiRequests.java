package com.example.sarika.alzheimerassistant.network;

import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sarika on 4/2/17.
 */
public class LocationApiRequests {

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
    public static GsonGetRequest<ArrayList<PostResponse>> getLocation
    (
            @NonNull final Response.Listener<ArrayList<PostResponse>> listener,
            @NonNull final Response.ErrorListener errorListener,
            String... params
    )
    {
        final String url = Util.SERVER_URL+"";/*BuildConfig.apiDomainName + "/v2/5597d86a6344715505576725";*/

        return new GsonGetRequest<>
                (
                        url,
                        new TypeToken<ArrayList<PostResponse>>() {}.getType(),
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



}
