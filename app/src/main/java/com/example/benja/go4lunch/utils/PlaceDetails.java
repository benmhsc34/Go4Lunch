package com.example.benja.go4lunch.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceDetails {

    @SerializedName("results")
    @Expose
    private PlaceDetailsResults results;

    public PlaceDetails(PlaceDetailsResults results) {
        this.results = results;
    }

    public PlaceDetailsResults getResults() {
        return results;
    }
}
