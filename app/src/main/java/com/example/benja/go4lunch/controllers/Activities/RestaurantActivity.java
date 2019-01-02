package com.example.benja.go4lunch.controllers.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.benja.go4lunch.R;
import com.example.benja.go4lunch.utils.Api;
import com.example.benja.go4lunch.utils.PlaceDetails;
import com.example.benja.go4lunch.utils.PlaceDetailsResults;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestaurantActivity extends AppCompatActivity {

    String thephoneNumber;
    String theWebsite = "";

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
        TextView likeTV = findViewById(R.id.likeText);

        ImageView photoRestaurant = findViewById(R.id.picture);
        ImageView likePhotoIV = findViewById(R.id.likeImageButton);

        //Fetching data from SP
        SharedPreferences mPreferences = getSharedPreferences("PREFERENCE_KEY_NAME", MODE_PRIVATE);
        String image = mPreferences.getString("image", "A PICTURE OBVIOUSLY");
        String name = mPreferences.getString("name", "Corben House");
        String address = mPreferences.getString("address", "2290 Av.ALbert Einstein");
        String placeId = mPreferences.getString("placeId", "A PLACE ID IDRK");

        //Setting data to respective Views
        restaurantNameTV.setText(name);
        addressTV.setText(address);
        Picasso.get().load(image).into(photoRestaurant);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences mPreferences = getSharedPreferences("PREFERENCE_KEY_NAME", MODE_PRIVATE);
                Boolean like = mPreferences.getBoolean("like", false);

                if (!like) {
                    like = true;
                    likePhotoIV.setImageResource(R.drawable.liked);
                    likeTV.setText("LIKED");
                } else {
                    like = false;
                    likePhotoIV.setImageResource(R.drawable.like);
                    likeTV.setText("LIKE");
                }

                mPreferences.edit().putBoolean("like", like).apply();
            }
        });

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        Api api = retrofit.create(Api.class);


        Call<PlaceDetails> call1 = api.getPlaceDetails(placeId);
        call1.enqueue(new Callback<PlaceDetails>() {
            @Override
            public void onResponse(Call<PlaceDetails> call, Response<PlaceDetails> detailResponse) {
                PlaceDetails details = detailResponse.body();
                PlaceDetailsResults detailedListResults = details.getResults();


                if (detailedListResults.getWebsite() != null) {

                    theWebsite = detailedListResults.getWebsite();
                    Log.d("wesbitephone", thephoneNumber + "   //   " + theWebsite);
                } else {
                    theWebsite = "https://benjamincorben.com";
                } if (detailedListResults.getPhoneNumber() != null){
                    thephoneNumber = detailedListResults.getPhoneNumber();
                } else{
                    thephoneNumber = "noPhoneNumber";
                }
            }

            @Override
            public void onFailure(Call<PlaceDetails> call, Throwable t) {

            }
        });

        websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (theWebsite.equals("https://benjamincorben.com")) {
                    Toast.makeText(RestaurantActivity.this, "No website for this restaurant", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(RestaurantActivity.this, RestaurantWebViewActivity.class);
                    intent.putExtra("websiteUrl", theWebsite);
                    startActivity(intent);
                }
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thephoneNumber.equals("noPhoneNumber")) {
                    Toast.makeText(RestaurantActivity.this, "No phone number for this restaurant", Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(Intent.ACTION_DIAL);
                    String p = "tel:" + thephoneNumber;
                    i.setData(Uri.parse(p));
                    startActivity(i);
                }
            }
        });


    }
}
