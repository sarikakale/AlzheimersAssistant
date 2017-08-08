package com.example.sarika.alzheimerassistant.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Siddhi Pai on 05-05-2017.
 */

public class Notes {
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    @SerializedName("_id")
    String Id;
    String patientID;

    @Override
    public String toString() {
        return "Notes{" +
                "patientID='" + patientID + '\'' +
                ", note='" + note + '\'' +
                ", id='" + Id + '\'' +
                '}';
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }
    @SerializedName("Note")
    String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
