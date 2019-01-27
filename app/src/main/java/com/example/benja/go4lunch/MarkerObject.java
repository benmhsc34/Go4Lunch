package com.example.benja.go4lunch;

public class MarkerObject {

    String address;
    String name;
    String photos;
    String placeId;

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
