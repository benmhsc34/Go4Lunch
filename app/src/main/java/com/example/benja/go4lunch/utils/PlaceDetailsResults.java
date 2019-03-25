package com.example.benja.go4lunch.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceDetailsResults {

    @SerializedName("website")
    @Expose
    private final String website;
    @SerializedName("formatted_phone_number")
    @Expose
    private final String phoneNumber;

    public PlaceDetailsResults(String website, String phoneNumber) {
        this.website = website;
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
