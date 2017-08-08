package com.example.sarika.alzheimerassistant.network;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.Details;
import com.example.sarika.alzheimerassistant.bean.Person;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.fragments.ContactDetails;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;

import static java.lang.String.format;


/**
 * Created by sarika on 3/20/17.
 */
public class CaregiverApiRequests {

    private static Gson gson = new GsonBuilder().create();


    /**
     * Returns a dummy object's array
     *
     * @param listener is the listener for the correct answer
     * @param errorListener is the listener for the error response
     *
     * @return {@link GsonGetRequest}
     */

    public static GsonGetRequest<Caregiver> getCaregiver
            (
                    @NonNull final Response.Listener<Caregiver> listener,
                    @NonNull final Response.ErrorListener errorListener,
                    String token,
                    String... params
            )
    {
        final String url = Util.SERVER_URL+"/sample";/*BuildConfig.apiDomainName + "/v2/55973508b0e9e4a71a02f05f";*/

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<Caregiver>() {}.getType(),
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
    public static GsonGetRequest<ArrayList<Caregiver>> getCaregvers
    (
            @NonNull final Response.Listener<ArrayList<Caregiver>> listener,
            @NonNull final Response.ErrorListener errorListener,
            String token
    )
    {
        final String url = Util.SERVER_URL+"/patients/getNewCaregivers";/*BuildConfig.apiDomainName + "/v2/5597d86a6344715505576725";*/

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<ArrayList<Caregiver>>() {}.getType(),
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
    public static GsonGetRequest<ArrayList<Caregiver>> getCaregversList
    (
            @NonNull final Response.Listener<ArrayList<Caregiver>> listener,
            @NonNull final Response.ErrorListener errorListener,
            String token
    )
    {
        final String url = Util.SERVER_URL+"/patients/getCaregivers";/*BuildConfig.apiDomainName + "/v2/5597d86a6344715505576725";*/

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<ArrayList<Caregiver>>() {}.getType(),
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
    public static GsonGetRequest<ArrayList<Caregiver>> getCaregversListForPatients
    (
            @NonNull final Response.Listener<ArrayList<Caregiver>> listener,
            @NonNull final Response.ErrorListener errorListener,
            String token,
            String patientId
    )
    {
        final String url = format("%s/patients/getCaregiversForPatients/%s", Util.SERVER_URL, patientId);

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<ArrayList<Caregiver>>() {}.getType(),
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
                    Caregiver caregiver,
                    String token

            ) {
        final String url = Util.SERVER_URL+"/contactDetails/addCaregiver";

        final String caregiverParams = gson.toJson(caregiver);
        JsonObject jsonObject=new JsonObject();
        //jsonObject.addProperty("userId",caregiver.getUserId());
        jsonObject.addProperty("detailId",caregiver.getDetails().getDetailId());

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
            @NonNull final Response.Listener<Details> listener,
            @NonNull final Response.ErrorListener errorListener,
            Details contactDetails,
            String token
    ) {
        final String url = Util.SERVER_URL+"/contactDetails/add";

        final String caregiverParams = gson.toJson(contactDetails);

        final GsonPostRequest gsonPostRequest = new GsonPostRequest(
                Util.getHeader(token),
                url,
                caregiverParams,
                new TypeToken<Details>(){}.getType(),
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
        final String url = Util.SERVER_URL+"/fcm/fcmRegistration";

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
