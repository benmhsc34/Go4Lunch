package com.example.benja.go4lunch.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceDetails {

    @SerializedName("result")
    @Expose
    private PlaceDetailsResults result;

    public PlaceDetails(PlaceDetailsResults results) {
        this.result = results;
    }

    public PlaceDetailsResults getResults() {
        return result;
    }
}
