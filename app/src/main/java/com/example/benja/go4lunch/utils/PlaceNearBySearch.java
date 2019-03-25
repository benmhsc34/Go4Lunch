package com.example.benja.go4lunch.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceNearBySearch {
    @SerializedName("results")
    @Expose
    private final List<PlaceNearBySearchResult> results;

    public PlaceNearBySearch(List<Object> htmlAttributions, String nextPageToken, List<PlaceNearBySearchResult> results, String status) {
        this.results = results;
    }

    public List<PlaceNearBySearchResult> getResults() {
        return results;
    }

}
