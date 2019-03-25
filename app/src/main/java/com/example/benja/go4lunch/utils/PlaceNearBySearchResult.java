package com.example.benja.go4lunch.utils;

import com.google.firebase.firestore.ServerTimestamp;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceNearBySearchResult {

    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("vicinity")
    @Expose
    private String address;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("geometry")
    @Expose
    private final Geometry geometry;
    @SerializedName("photos")
    @Expose
    private final List<Photo> photos;

    public PlaceNearBySearchResult(String icon, String address, String id, String name, OpeningHours openingHours, String placeId, String reference, String distance, Geometry geometry, List<Photo> photos) {
        this.icon = icon;
        this.address = address;
        this.id = id;
        this.name = name;
        this.openingHours = openingHours;
        this.placeId = placeId;
        this.reference = reference;
        this.distance = distance;
        this.geometry = geometry;
        this.photos = photos;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
