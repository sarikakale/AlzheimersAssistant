
package com.example.sarika.alzheimerassistant.network;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.Circle;
import com.example.sarika.alzheimerassistant.bean.Notes;
import com.example.sarika.alzheimerassistant.bean.Patient;
import com.example.sarika.alzheimerassistant.bean.Person;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sarika on 4/1/17.
 */
public class PatientApiRequests {

    private static Gson gson = new GsonBuilder().create();


    /**
     * Returns a dummy object's array
     *
     * @param listener is the listener for the correct answer
     * @param errorListener is the listener for the error response
     *
     * @return {@link GsonGetRequest}
     */

    public static GsonGetRequest<Patient> getPatients
    (
            @NonNull final Response.Listener<Patient> listener,
            @NonNull final Response.ErrorListener errorListener,
            String token,
            String... params
    )
    {
        final String url = Util.SERVER_URL+"";/*BuildConfig.apiDomainName + "/v2/55973508b0e9e4a71a02f05f";*/

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<Patient>() {}.getType(),
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
    public static GsonGetRequest<ArrayList<Patient>> getPatientList
    (
            @NonNull final Response.Listener<ArrayList<Patient>> listener,
            @NonNull final Response.ErrorListener errorListener,
            String token
    )
    {
        final String url = Util.SERVER_URL+"/caregivers/getPatients";/*BuildConfig.apiDomainName + "/v2/5597d86a6344715505576725";*/

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<ArrayList<Patient>>() {}.getType(),
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
     *@param patient is the caregiver Object having caregiver details
     *
     * @return {@link GsonPostRequest}
     */

    public static GsonPostRequest postPatientRegisterRequest
    (
            @NonNull final Response.Listener<PostResponse> listener,
            @NonNull final Response.ErrorListener errorListener,
            Patient patient,
            String token
    ) {
        final String url = Util.SERVER_URL+"/contactDetails/addPatient";

        JsonObject jsonObject=new JsonObject();
        //jsonObject.addProperty("userId",patient.getUserId());
        jsonObject.addProperty("detailId",patient.getDetails().getDetailId());

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

    public static GsonPostRequest<PostResponse> postCaregiverToPatientCollection(
            @NonNull final Response.Listener<PostResponse> listener,
            @NonNull final Response.ErrorListener errorListener,
            String token,
            String caregiverId
    ){
        final String url = Util.SERVER_URL+"/facialLibrary/addFaceToCollection";
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("caregiverId",caregiverId);

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

    public static GsonGetRequest<PostResponse> createCollection(
            @NonNull final Response.Listener<PostResponse> listener,
            @NonNull final Response.ErrorListener errorListener,
            String token
    ){
        final String url = Util.SERVER_URL+"/facialLibrary/createFaceCollection";

        final GsonGetRequest gsonGetRequest = new GsonGetRequest(
                Util.getHeader(token),
                url,
                new TypeToken<PostResponse>(){}.getType(),
                gson,
                listener,
                errorListener
        );

        gsonGetRequest.setShouldCache(false);
        return gsonGetRequest;

    }

    public static GsonPostRequest postPatientNotes
            (
                    @NonNull final Response.Listener<Notes> listener,
                    @NonNull final Response.ErrorListener errorListener,
                    String token,
                    String... params
            ) {
        final String url = Util.SERVER_URL+"/notes/add";
        final JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("message",params[0]);

        final GsonPostRequest gsonPostRequest = new GsonPostRequest(
                Util.getHeader(token),
                url,
                jsonObject.toString(),
                new TypeToken<Notes>(){}.getType(),
                gson,
                listener,
                errorListener

        );
        gsonPostRequest.setShouldCache(false);
        return gsonPostRequest;
    }


    public static GsonGetRequest getPatientNotes
            (
                    @NonNull final Response.Listener<ArrayList<Notes>> listener,
                    @NonNull final Response.ErrorListener errorListener,
                    String token
            )
    {
        final String url = Util.SERVER_URL+"/notes/get";

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<ArrayList<Notes>>(){}.getType(),
                        gson,
                        listener,
                        errorListener
                );
    }

    public static GsonPostRequest deletePatientNotes
            (
                    @NonNull final Response.Listener<PostResponse> listener,
                    @NonNull final Response.ErrorListener errorListener,
                    String token,
                    String... params
            ) {
        final String url = Util.SERVER_URL+"/notes/delete";
        final JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("_id",params[0]);

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


    public static GsonGetRequest<ArrayList<Circle>> getAllDetailsCaregivers
            (
                    @NonNull final Response.Listener<ArrayList<Circle>> listener,
                    @NonNull final Response.ErrorListener errorListener,
                    String token
            )
    {
        final String url = Util.SERVER_URL+"/patients/getCircleDetails";/*BuildConfig.apiDomainName + "/v2/5597d86a6344715505576725";*/

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<ArrayList<Circle>>() {}.getType(),
                        gson,
                        listener,
                        errorListener
                );
    }


}

