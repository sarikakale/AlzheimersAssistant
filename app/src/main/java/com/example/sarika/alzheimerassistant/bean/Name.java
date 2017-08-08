package com.example.sarika.alzheimerassistant.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sarika on 3/6/17.
 */
public class Name {

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    @Override
    public String toString() {
        return firstName+" "+middleName+" "+lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("middleName")
    private String middleName;
    @SerializedName("lastName")
    private String lastName;

}
