package com.example.sarika.alzheimerassistant.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sarika on 3/31/17.
 */
public class Patient implements Person{
    @SerializedName("userId")
    private String userId;

    @SerializedName("detailId")
    private Details details;
    @SerializedName("circleId")
    private ArrayList<String> circleId;
    private Login login;


    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public Details getDetails() {
        return details;
    }

    @Override
    public void setDetails(Details details) {
        this.details = details;
    }



    @Override
    public String toString() {
        return "Patient{" +
                "userId='" + userId + '\'' +
                ", details=" + details +
                ", circleId='" + circleId + '\'' +
                '}';
    }

    public ArrayList<String> getCircleId() {
        return circleId;
    }

    public void setCircleId(ArrayList<String> circleId) {
        this.circleId = circleId;
    }
}
