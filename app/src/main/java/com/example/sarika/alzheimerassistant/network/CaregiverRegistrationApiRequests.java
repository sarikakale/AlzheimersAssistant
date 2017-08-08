package com.example.sarika.alzheimerassistant.network;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.example.sarika.alzheimerassistant.bean.Person;
import com.example.sarika.alzheimerassistant.bean.Details;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.fragments.ContactDetails;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


/**
 * Created by sarika on 3/20/17.
 */
public class CaregiverRegistrationApiRequests {

    private static Gson gson = new GsonBuilder().create();


    /**
     * Returns a dummy object's array
     *
     * @param listener is the listener for the correct answer
     * @param errorListener is the listener for the error response
     *
     * @return {@link GsonGetRequest}
     */

    public static GsonGetRequest<PostResponse> getCaregiver
            (
                    @NonNull final Response.Listener<PostResponse> listener,
                    @NonNull final Response.ErrorListener errorListener
            )
    {
        final String url = Util.SERVER_URL+"/sample";/*BuildConfig.apiDomainName + "/v2/55973508b0e9e4a71a02f05f";*/

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
    public static GsonGetRequest<ArrayList<PostResponse>> getCaregvers
    (
            @NonNull final Response.Listener<ArrayList<PostResponse>> listener,
            @NonNull final Response.ErrorListener errorListener,
            String... params
    )
    {
        final String url = Util.SERVER_URL+"/patients/getCaregivers";/*BuildConfig.apiDomainName + "/v2/5597d86a6344715505576725";*/

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
     *@param caregiver is the caregiver Object having caregiver details
     *
     * @return {@link GsonPostRequest}
     */

    public static GsonPostRequest postCaregiverRegisterRequest
            (
                    @NonNull final Response.Listener<PostResponse> listener,
                    @NonNull final Response.ErrorListener errorListener,
                    Person caregiver,
                    final String token

            ) {
        final String url = Util.SERVER_URL+"/caregivers/add";

        final String caregiverParams = gson.toJson(caregiver);

        final GsonPostRequest gsonPostRequest = new GsonPostRequest(
                Util.getHeader(token),
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



    /**
     * Returns a dummy object
     *
     * @param listener is the listener for the correct answer
     * @param errorListener is the listener for the error response
     *@param contactDetails is the caregiver Object having caregiver details
     *
     * @return {@link GsonPostRequest}
     */

    public static GsonPostRequest postContactDetailsRequest
    (
            @NonNull final Response.Listener<PostResponse> listener,
            @NonNull final Response.ErrorListener errorListener,
            Details contactDetails
    ) {
        final String url = Util.SERVER_URL+"/contactDetails/add";

        final String caregiverParams = gson.toJson(contactDetails);

        final GsonPostRequest gsonPostRequest = new GsonPostRequest(
                Util.getHeader("JWT eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1OGFlMTM1ZTAzMDU4NzIzMmNjNTE0OTciLCJlbWFpbCI6ImhpbWFuc2h1LmphaW4xMUBnbWFpbC5jb20iLCJyb2xlIjoiY2FyZWdpdmVyIiwiaWF0IjoxNDkyMTExOTY2LCJleHAiOjE0OTI3MTY3NjZ9.Ya_XQeB1orIM8_PLFA5fiIFN95TFb_CoJU3Mkyo7CMs"),
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
