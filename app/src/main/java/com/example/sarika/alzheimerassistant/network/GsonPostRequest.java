package com.example.sarika.alzheimerassistant.network;

/**
 * Created by sarika on 3/20/17.
 */
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Makes a post request and converts the response from JsonElement into a
 * list of objects/object using with Google Gson.
 */
public class GsonPostRequest<T> extends JsonRequest<T>
{
    private final Gson gson;
    private final Type type;
    private final Response.Listener<T> listener;
    private  Map<String, String> headers;


    /**
     * Make a POST request and return a parsed object from JSON.
     *
     * @param headers header of the request
     * @param url URL of the request to make
     * @param type is the type of the object to be returned
     * @param listener is the listener for the right answer
     * @param errorListener  is the listener for the wrong answer
     */
    public GsonPostRequest
    (
            Map<String,String> headers,
            @NonNull final String url,
            @NonNull final String body,
            @NonNull final Type type,
            @NonNull final Gson gson,
            @NonNull final Response.Listener<T> listener,
            @NonNull final Response.ErrorListener errorListener
    )
    {
        super(Method.POST, url, body, listener, errorListener);
        Log.v("Request: ", body);
        this.gson = gson;
        this.type = type;
        this.listener = listener;
        this.headers = headers;
    }

    /**
     * Make a POST request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param type is the type of the object to be returned
     * @param listener is the listener for the right answer
     * @param errorListener  is the listener for the wrong answer
     */
    public GsonPostRequest
    (
            @NonNull final String url,
            @NonNull final String body,
            @NonNull final Type type,
            @NonNull final Gson gson,
            @NonNull final Response.Listener<T> listener,
            @NonNull final Response.ErrorListener errorListener
    )
    {
        super(Method.POST, url, body, listener, errorListener);
        Log.v("Request: ", body);
        this.gson = gson;
        this.type = type;
        this.listener = listener;
    }



    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response)
    {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response)
    {
        try
        {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            Log.v("Response: ", json);

            return (Response<T>) Response.success
                    (
                            gson.fromJson(json, type),
                            HttpHeaderParser.parseCacheHeaders(response)
                    );
        }
        catch (UnsupportedEncodingException e)
        {
            return Response.error(new ParseError(e));
        }
        catch (JsonSyntaxException e)
        {
            return Response.error(new ParseError(e));
        }
    }


}
