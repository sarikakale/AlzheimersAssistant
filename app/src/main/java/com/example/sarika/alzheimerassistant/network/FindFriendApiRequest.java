package com.example.sarika.alzheimerassistant.network;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.example.sarika.alzheimerassistant.bean.Patient;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * Created by Siddhi Pai on 30-04-2017.
 */

public class FindFriendApiRequest {
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

    public static GsonGetRequest<Patient> getFriendsRequest
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

}
