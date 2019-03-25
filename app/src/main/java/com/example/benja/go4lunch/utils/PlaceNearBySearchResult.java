package com.example.benja.go4lunch.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceNearBySearchResult {

    @SerializedName("vicinity")
    @Expose
    private final String address;
    @SerializedName("name")
    @Expose
    private final String name;
    @SerializedName("opening_hours")
    @Expose
    private final OpeningHours openingHours;
    @SerializedName("place_id")
    @Expose
    private final String placeId;
    @SerializedName("geometry")
    @Expose
    private final Geometry geometry;
    @SerializedName("photos")
    @Expose
    private final List<Photo> photos;

    public PlaceNearBySearchResult(String icon, String address, String id, String name, OpeningHours openingHours, String placeId, String reference, String distance, Geometry geometry, List<Photo> photos) {
        this.address = address;
        this.name = name;
        this.openingHours = openingHours;
        this.placeId = placeId;
        this.geometry = geometry;
        this.photos = photos;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public String getPlaceId() {
        return placeId;
    }

}
