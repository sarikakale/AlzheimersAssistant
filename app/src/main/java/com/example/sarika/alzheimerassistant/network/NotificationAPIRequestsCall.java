package com.example.sarika.alzheimerassistant.network;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * Created by sarika on 5/9/17.
 */

public class NotificationAPIRequestsCall {

    private static Gson gson = new GsonBuilder().create();

    public static GsonPostRequest postGeofenceNotification
            (
                    @NonNull final Response.Listener<PostResponse> listener,
                    @NonNull final Response.ErrorListener errorListener,
                    String token,
                    String message
            ) {
        final String url = Util.SERVER_URL+"/fcmNotification/add";
        final JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("message",message);

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
