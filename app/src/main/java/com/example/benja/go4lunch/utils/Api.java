package com.example.benja.go4lunch.utils;

import android.database.Observable;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL = "https://maps.googleapis.com/maps/api/place/";

    //Predefined parameters
    String key      = "AIzaSyAR3xMop8hS0cX1S3u70q-EC15TBduuDo4";
    String type     = "restaurant";
    String radius   = "1500";
   // String maxWidth = "2304";

    // Place NearBySearch
    @GET("nearbysearch/json?key="+key+"&radius="+radius+"&type="+type)
    Call<PlaceNearBySearch> getPlaceNearBySearch(@Query("location") String location);

    // Place Details
    @GET("details/json?key=" + key)
    Call<PlaceDetails> getPlaceDetails(@Query("placeid") String placeId);


}
