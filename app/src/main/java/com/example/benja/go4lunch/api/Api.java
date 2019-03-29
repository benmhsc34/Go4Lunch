package com.example.benja.go4lunch.api;

import com.example.benja.go4lunch.utils.PlaceDetails;
import com.example.benja.go4lunch.utils.PlaceNearBySearch;

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

    //Presentation links

    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyAR3xMop8hS0cX1S3u70q-EC15TBduuDo4&radius=1500&type=restaurant&location=43.61077209,3.876716

    //https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyAR3xMop8hS0cX1S3u70q-EC15TBduuDo4&placeid=ChIJae76A6CvthIRrulM_EFrIkk

}
