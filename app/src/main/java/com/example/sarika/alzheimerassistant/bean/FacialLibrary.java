package com.example.sarika.alzheimerassistant.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sarika on 5/1/17.
 */

public class FacialLibrary {
    @Override
    public String toString() {
        return "FacialLibrary{" +
                "facialId='" + facialId + '\'' +
                ", imageName='" + imageName + '\'' +
                ", imageDescription='" + imageDescription + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", imageMetadata='" + imageMetadata + '\'' +
                '}';
    }

    public String getFacialId() {
        return facialId;
    }

    public void setFacialId(String facialId) {
        this.facialId = facialId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ImageMetadata getImageMetadata() {
        return imageMetadata;
    }

    public void setImageMetadata(ImageMetadata imageMetadata) {
        this.imageMetadata = imageMetadata;
    }

    @SerializedName("_id")
    private String facialId;
    @SerializedName("imageName")
    private String imageName;
    @SerializedName("imageDescription")
    private String imageDescription;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("imageMetadata")
    private ImageMetadata imageMetadata;
}
