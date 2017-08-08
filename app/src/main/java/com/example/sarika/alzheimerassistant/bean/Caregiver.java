package com.example.sarika.alzheimerassistant.bean;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sarika on 3/6/17.
 */
public class  Caregiver implements Person{
    @SerializedName("userId")
    private String userId;
    @SerializedName("detailId")
    private Details details;
    @SerializedName("facialId")
    FacialLibrary facialId;
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

    public FacialLibrary getFacialId() {
        return facialId;
    }

    public void setFacialId(FacialLibrary facialId) {
        this.facialId = facialId;
    }

    @Override
    public String toString() {
        return "Caregiver{" +
                "userId='" + userId + '\'' +
                ", details=" + details +
                ", facialId='" + facialId + '\'' +
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
