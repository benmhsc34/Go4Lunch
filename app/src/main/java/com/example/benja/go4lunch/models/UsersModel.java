package com.example.benja.go4lunch.models;

public class UsersModel {

    private String userName;
    private String email;
    private String picture;
    private String restaurantName;
    private String pictureRestaurant;
    private String address;
    private String placeId;

    public UsersModel() {
    }

    public UsersModel(String userName, String picture, String restaurantName, String pictureRestaurant, String address, String placeId, String email) {
        this.userName = userName;
        this.picture = picture;
        this.restaurantName = restaurantName;
        this.pictureRestaurant = pictureRestaurant;
        this.address = address;
        this.placeId = placeId;
        this.email = email;
    }

    public String getPictureRestaurant() {
        return pictureRestaurant;
    }

    public String getUserName() {
        return userName;
    }

    public String getPicture() {
        return picture;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getAddress() {
        return address;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getEmail() {
        return email;
    }
}
