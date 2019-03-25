package com.example.benja.go4lunch.models;

import android.support.annotation.Nullable;

import java.util.Map;

public class User {
    private String mUid;
    private String mUserName;
    @Nullable
    private String mRestaurantIdentifier;
    @Nullable private String mRestaurantName;
    @Nullable private Map<String, String> mListRestaurantLiked;
    @Nullable private String mUrlPicture;

    // Blank constructor necessary for use with FireBase
    public User(String uid, String userName, String urlPicture) { }

}
