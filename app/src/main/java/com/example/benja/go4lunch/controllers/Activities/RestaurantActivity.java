package com.example.benja.go4lunch.controllers.Activities;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.benja.go4lunch.R;
import com.squareup.picasso.Picasso;

public class RestaurantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        //Declaring different Views
        LinearLayout callButton = findViewById(R.id.callButton);
        LinearLayout likeButton = findViewById(R.id.likeButton);
        LinearLayout websiteButton = findViewById(R.id.websiteButton);

        TextView restaurantNameTV = findViewById(R.id.name);
        TextView addressTV = findViewById(R.id.address);

        ImageView photoRestaurant = findViewById(R.id.picture);

        //Fetching data from SP
        SharedPreferences mPreferences = getSharedPreferences("PREFERENCE_KEY_NAME", MODE_PRIVATE);
        String image = mPreferences.getString("image", "haha");
        String name = mPreferences.getString("name", "Corben House");
        String address = mPreferences.getString("address", "2290 Av.ALbert Einstein");
        String website =  mPreferences.getString("website", "benjamincorben.com");
        String phoneNumber =  mPreferences.getString("phoneNumber", "0467151583");

        //Setting data to respective Views
        restaurantNameTV.setText(name);
        addressTV.setText(address);
        Picasso.get().load(image).into(photoRestaurant);


    }
}
