package com.example.sarika.alzheimerassistant.bean;

import org.parceler.Parcel;

/**
 * Created by sarika on 3/20/17.
 */

@Parcel
public class PostResponse {

    String success;
    String token;


    @Override
    public String toString() {
        return "PostResponse{" +
                "status='" + success + '\'' +
                ", token='" + token + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    String body;

    public String getToken() {
        return token;
    }

    public String getSuccess() {
        return success;
    }

    public String getBody() {
        return body;
    }
}
