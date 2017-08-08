package com.example.sarika.alzheimerassistant.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Response;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Siddhi Pai on 30-04-2017.
 */

public class PatientFriendApiRequest {
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

    public static GsonPostRequest postAddFriendRequest
    (
            @NonNull final Response.Listener<PostResponse> listener,
            @NonNull final Response.ErrorListener errorListener,
            String...params
    ) {
        final String url= Util.SERVER_URL+"/caregivers/addCircle";

        final JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("name",params[1]);
        jsonObject.addProperty("email",params[2]);
        jsonObject.addProperty("circleName",params[3]);
        jsonObject.addProperty("caregiverId",params[4]);

        final String token = params[0];

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

    public static GsonPostRequest postManageAccessRequest
            (
                    @NonNull final Response.Listener<PostResponse> listener,
                    @NonNull final Response.ErrorListener errorListener,
                    String token,
                    String...params
            ) {
        final String url = Util.SERVER_URL+"/patients/changeCircleAccess";

        final JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("accessLevel",params[0]);

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
