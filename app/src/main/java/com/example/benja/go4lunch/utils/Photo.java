package com.example.benja.go4lunch.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {

    @SerializedName("photo_reference")
    @Expose
    private String photoReference;

    public Photo(String photoReference) {
        this.photoReference = photoReference;
    }

    public String getPhotoReference() {
        return photoReference;
    }
}
