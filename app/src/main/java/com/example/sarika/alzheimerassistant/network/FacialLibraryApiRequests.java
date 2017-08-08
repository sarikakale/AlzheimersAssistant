package com.example.sarika.alzheimerassistant.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.FacialLibrary;
import com.example.sarika.alzheimerassistant.bean.LocationReminder;
import com.example.sarika.alzheimerassistant.bean.Login;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * Created by sarika on 3/20/17.
 */
public class FacialLibraryApiRequests {

    private static final String TAG = "FacialLibrary";

    private static Gson gson = new GsonBuilder().create();
    /**
     * Returns a dummy object
     *
     * @param listener is the listener for the correct answer
     * @param errorListener is the listener for the error response
     *@param params image details
     *
     * @return {@link GsonPostRequest}
     */

    public static GsonPostRequest postImageDetails
    (
            @NonNull final Response.Listener<PostResponse> listener,
            @NonNull final Response.ErrorListener errorListener,
            String...params
    ) {
        final String url = Util.SERVER_URL+"/facialLibrary/add";

        final JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("ImageName",params[0]);
        jsonObject.addProperty("ImageDescription",params[1]);
        jsonObject.addProperty("ImageUrl",params[2]);
        jsonObject.addProperty("ImageMetadata",params[3]);
        String token = params[4];

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


    public static GsonPostRequest postReminderMessage
            (
                    @NonNull final Response.Listener<PostResponse> listener,
                    @NonNull final Response.ErrorListener errorListener,
                    String...params
            ){

        final String url = Util.SERVER_URL+"/imageReminder/addOnlyReminder";

        final JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("caregiverId",params[0]);
        jsonObject.addProperty("name",params[1]);
        jsonObject.addProperty("message",params[2]);
        jsonObject.addProperty("patientId",params[3]);

        String token = params[4];

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
     * Returns a dummy object's array
     *
     * @param listener is the listener for the correct answer
     * @param errorListener is the listener for the error response
     *
     * @return {@link GsonGetRequest}
     */
    public static GsonGetRequest<ArrayList<String>> getImages
    (
            @NonNull final Response.Listener<ArrayList<String>> listener,
            @NonNull final Response.ErrorListener errorListener,
            String token
    )
    {
        final String url = Util.SERVER_URL+"/facialLibrary/getImages";/*BuildConfig.apiDomainName + "/v2/5597d86a6344715505576725";*/

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<ArrayList<String>>() {}.getType(),
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
    public static GsonGetRequest<ArrayList<String>> getPatientImages
    (
            @NonNull final Response.Listener<ArrayList<String>> listener,
            @NonNull final Response.ErrorListener errorListener,
            String token,
            String patientId
    )
    {
        final String url = format("%s/facialLibrary/getFriendsImage/%s", Util.SERVER_URL, patientId);

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<ArrayList<String>>() {}.getType(),
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
    public static GsonGetRequest<FacialLibrary> getImage
    (
            @NonNull final Response.Listener<FacialLibrary> listener,
            @NonNull final Response.ErrorListener errorListener,
            String token,
            String facialId
    )
    {
        final String url = format("%s/facialLibrary/getImage/%s", Util.SERVER_URL, facialId);

        return new GsonGetRequest<>
                (       Util.getHeader(token),
                        url,
                        new TypeToken<FacialLibrary>() {}.getType(),
                        gson,
                        listener,
                        errorListener
                );
    }


}
