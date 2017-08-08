package com.example.sarika.alzheimerassistant.network;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.example.sarika.alzheimerassistant.bean.Login;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * Created by sarika on 3/27/17.
 */
public class LoginApiRequests {

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

    public static GsonPostRequest postloginRequest
    (
            @NonNull final Response.Listener<PostResponse> listener,
            @NonNull final Response.ErrorListener errorListener,
            String...params
    ) {
        final String url = Util.SERVER_URL+"/auth/authenticate";

        final JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("email",params[0]);
        jsonObject.addProperty("password",params[1]);
        jsonObject.addProperty("role",params[2]);


       final GsonPostRequest gsonPostRequest = new GsonPostRequest(
               Util.getHeader(),
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

    public static GsonPostRequest postloginRegistrationRequest
            (
                    @NonNull final Response.Listener<Login> listener,
                    @NonNull final Response.ErrorListener errorListener,
                    String...params
            ) {
        final String url = Util.SERVER_URL+"/auth/register";

        final JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("email",params[0]);
        jsonObject.addProperty("password",params[1]);
        jsonObject.addProperty("role",params[2]);

        final GsonPostRequest gsonPostRequest = new GsonPostRequest(
                url,
                jsonObject.toString(),
                new TypeToken<Login>(){}.getType(),
                gson,
                listener,
                errorListener

        );
        gsonPostRequest.setShouldCache(false);
        return gsonPostRequest;
    }


}
