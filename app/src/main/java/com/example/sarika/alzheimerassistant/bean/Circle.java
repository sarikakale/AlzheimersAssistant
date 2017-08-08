package com.example.sarika.alzheimerassistant.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sarika on 4/15/17.
 */

public class Circle {

    String circleName;

    @SerializedName("caregiver_details")
    ArrayList<Caregiver> caregivers;

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

    public ArrayList<Caregiver> getCaregivers() {
        return caregivers;
    }

    public void setCaregivers(ArrayList<Caregiver> caregivers) {
        this.caregivers = caregivers;
    }

    @Override
    public String toString() {
        return "Circle{" +
                "circleName='" + circleName + '\'' +
                ", caregivers=" + caregivers +
                '}';
    }
}
