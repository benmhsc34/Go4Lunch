package com.example.benja.go4lunch.models;

import android.support.annotation.Nullable;

public class Restaurant {

//    private String mIdentifier;             // Restaurant Identifier
    private final String mName;                   // Name of the Restaurant
    @Nullable private final String mAddress;      // Address of Restaurant
    @Nullable private final Boolean mOpeningTime;  // Opening time of Restaurant
    @Nullable private final String mDistance;     // Distance where the restaurant is from the current position
//    @Nullable private long mNbrParticipants;// Number of participants
    private final Object mNbrLikes;        // Number of likes that the restaurant got
    @Nullable private final String mPhotoUrl;     // URL of the Restaurant photo
    @Nullable private final String mPlaceId;
//    @Nullable private String mWebSiteUrl;   // URL of the Web site
//    @Nullable private String mType;         // Type of the Restaurant
//    @Nullable private String mLat;          // Latitude  of the Restaurant on the Map
//    @Nullable private String mLng;          // Longitude of the Restaurant on the Map
//    @Nullable private String mPhone;        // Phone number to call the Restaurant


    public Restaurant(String name, @Nullable String address, @Nullable Boolean openingTime, @Nullable String distance, @Nullable String photoUrl, @Nullable String placeId, Object nbrLikes) {
        mName = name;
        mAddress = address;
        mOpeningTime = openingTime;
        mDistance = distance;
        mNbrLikes = nbrLikes;
        mPhotoUrl = photoUrl;
        mPlaceId = placeId;
    }

    public String getName() {
        return mName;
    }

    @Nullable
    public String getAddress() {
        return mAddress;
    }

    @Nullable
    public Boolean getOpeningTime() {
        return mOpeningTime;
    }

    @Nullable
    public String getDistance() {
        return mDistance;
    }

    public Object getNbrLikes() {
        return mNbrLikes;
    }

    @Nullable
    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    @Nullable
    public String getPlaceId() {
        return mPlaceId;
    }
}