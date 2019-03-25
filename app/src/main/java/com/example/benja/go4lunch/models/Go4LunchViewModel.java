package com.example.benja.go4lunch.models;


import android.arch.lifecycle.ViewModel;

import java.util.LinkedHashMap;
import java.util.Map;

public class Go4LunchViewModel extends ViewModel {

    // For save the status of the location permission granted
    private boolean mLocationPermissionGranted;

    // For save Restaurants List
    private final Map<String,Restaurant> mListRestaurant = new LinkedHashMap<>();

    public Map<String,Restaurant> getListRestaurant() {
        return mListRestaurant;
    }

}
