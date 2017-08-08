package com.example.sarika.alzheimerassistant.network;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.Circle;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by sarika on 4/1/17.
 */
public class CircleApiRequests {

    private static Gson gson = new GsonBuilder().create();


    /**
     * Returns a dummy object's array
     *
     * @param listener is the listener for the correct answer
     * @param errorListener is the listener for the error response
     *
     * @return {@link GsonGetRequest}
     */

    public static GsonGetRequest<Circle> getCircle
    (
            @NonNull final Response.Listener<Circle> listener,
            @NonNull final Response.ErrorListener errorListener,
            String token,
            String... params
    )
    {
        final String url =Util.SERVER_URL+ "";/*BuildConfig.apiDomainName + "/v2/55973508b0e9e4a71a02f05f";*/

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<Circle>() {}.getType(),
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
    public static GsonGetRequest<ArrayList<Circle>> getCircles
    (
            @NonNull final Response.Listener<ArrayList<Circle>> listener,
            @NonNull final Response.ErrorListener errorListener,
            String token
    )
    {
        final String url = Util.SERVER_URL+"";

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<ArrayList<Circle>>() {}.getType(),
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
     *@param caregiver is the caregiver Object having caregiver details
     *
     * @return {@link GsonPostRequest}
     */

    public static GsonPostRequest postCircleRegisterRequest
    (
            @NonNull final Response.Listener<PostResponse> listener,
            @NonNull final Response.ErrorListener errorListener,
            Caregiver caregiver
    ) {
        final String url =Util.SERVER_URL+"";

        final String caregiverParams = gson.toJson(caregiver);

        final GsonPostRequest gsonPostRequest = new GsonPostRequest(
                url,
                caregiverParams,
                new TypeToken<PostResponse>(){}.getType(),
                gson,
                listener,
                errorListener

        );
        gsonPostRequest.setShouldCache(false);
        return gsonPostRequest;
    }


}
