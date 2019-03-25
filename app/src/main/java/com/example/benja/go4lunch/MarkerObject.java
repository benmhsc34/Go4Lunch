package com.example.benja.go4lunch;

public class MarkerObject {

    private final String address;
    private final String name;
    private final String photos;
    private final String placeId;

    public MarkerObject(String address, String name, String photos, String placeId) {
        this.address = address;
        this.name = name;
        this.photos = photos;
        this.placeId = placeId;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getPhotos() {
        return photos;
    }

    public String getPlaceId() {
        return placeId;
    }
}
