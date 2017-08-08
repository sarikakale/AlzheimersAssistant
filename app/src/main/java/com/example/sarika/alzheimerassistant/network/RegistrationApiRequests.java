package com.example.sarika.alzheimerassistant.network;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.example.sarika.alzheimerassistant.bean.Person;
import com.example.sarika.alzheimerassistant.bean.Details;
import com.example.sarika.alzheimerassistant.bean.Person;
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
public class RegistrationApiRequests {

    private static Gson gson = new GsonBuilder().create();


    /**
     * Returns a dummy object's array
     *
     * @param listener is the listener for the correct answer
     * @param errorListener is the listener for the error response
     *
     * @return {@link GsonGetRequest}
     */

    public static GsonGetRequest<Person> getCaregiver
            (
                    @NonNull final Response.Listener<Person> listener,
                    @NonNull final Response.ErrorListener errorListener,
                    String token,
                    String... params
            )
    {
        final String url = Util.SERVER_URL+"/sample";/*BuildConfig.apiDomainName + "/v2/55973508b0e9e4a71a02f05f";*/

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<Person>() {}.getType(),
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
    public static GsonGetRequest<ArrayList<Person>> getCaregvers
    (
            @NonNull final Response.Listener<ArrayList<Person>> listener,
            @NonNull final Response.ErrorListener errorListener,
            String token
    )
    {
        final String url = Util.SERVER_URL+"";/*BuildConfig.apiDomainName + "/v2/5597d86a6344715505576725";*/

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<ArrayList<Person>>() {}.getType(),
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

    /**
     * Returns a dummy object
     *
     * @param listener is the listener for the correct answer
     * @param errorListener is the listener for the error response
     *@param params is the pamaters having registration Id
     *
     * @return {@link GsonPostRequest}
     */

    public static GsonPostRequest postFcmRegistrationRequest
    (
            @NonNull final Response.Listener<Details> listener,
            @NonNull final Response.ErrorListener errorListener,
            String... params
    ) {
        final String url = Util.SERVER_URL+"/contactDetails/fcmRegistration";

        final JsonObject jsonObject=new JsonObject();
        String token = params[0];
        jsonObject.addProperty("registrationId",params[1]);

        final GsonPostRequest gsonPostRequest = new GsonPostRequest(
                Util.getHeader(token),
                url,
                jsonObject.toString(),
                new TypeToken<Details>(){}.getType(),
                gson,
                listener,
                errorListener

        );
        gsonPostRequest.setShouldCache(false);
        return gsonPostRequest;
    }



}
