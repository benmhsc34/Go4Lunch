package com.example.benja.go4lunch.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpeningHours {
    @SerializedName("open_now")
    @Expose
    private final Boolean openNow;

    public OpeningHours(Boolean openNow) {
        this.openNow = openNow;
    }

    public Boolean getOpenNow() {
        return openNow;
    }


}
