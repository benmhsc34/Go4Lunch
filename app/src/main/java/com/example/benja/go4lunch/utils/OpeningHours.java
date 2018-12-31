package com.example.benja.go4lunch.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.Period;
import java.util.List;

public class OpeningHours {
    @SerializedName("open_now")
    @Expose
    private Boolean openNow;


    public OpeningHours(Boolean openNow) {
        this.openNow = openNow;

    }

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }


}
