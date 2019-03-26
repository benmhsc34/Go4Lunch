package com.example.benja.go4lunch.models;

import android.support.annotation.Nullable;

import java.util.Map;

public class User {
    public String mUid;
    public String mUserName;
    @Nullable
    public String mRestaurantIdentifier;
    @Nullable public String mRestaurantName;
    @Nullable public Map<String, String> mListRestaurantLiked;
    @Nullable public String mUrlPicture;

    // Blank constructor necessary for use with FireBase
    public User(String uid, String userName, String urlPicture) { }

}
