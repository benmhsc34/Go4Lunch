package com.example.benja.go4lunch.api;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressWarnings("WeakerAccess")
public class RestaurantHelper {
    private static final String COLLECTION_NAME = "restaurants";

    // --- COLLECTION REFERENCE ---

    private static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

  /*  public static Task<Void> createRestaurant(Restaurant restaurant) {

        Restaurant restaurantToCreate = new Restaurant( restaurant.getIdentifier(), restaurant.getName(),
                restaurant.getAddress(), restaurant.getOpeningTime(), restaurant.getDistance(),
                restaurant.getNbrParticipants(), restaurant.getNbrLikes(), restaurant.getPhotoUrl(),
                restaurant.getWebSiteUrl(), restaurant.getType(), restaurant.getLat(), restaurant.getLng(),
                restaurant.getPhone());

        return RestaurantHelper.getRestaurantsCollection().document(restaurant.getIdentifier()).set(restaurantToCreate);
    }
*/
    // --- GET ---

    // --- UPDATE ---

    // --- DELETE ---

}
