package com.example.benja.go4lunch.controllers.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.benja.go4lunch.R;
import com.example.benja.go4lunch.controllers.fragments.ListWorkmatesViewFragment;
import com.example.benja.go4lunch.models.UsersModel;
import com.example.benja.go4lunch.utils.Api;
import com.example.benja.go4lunch.utils.PlaceDetails;
import com.example.benja.go4lunch.utils.PlaceDetailsResults;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.GREEN;

public class RestaurantActivity extends AppCompatActivity {

    String thephoneNumber;
    String theWebsite = "";
    //   String restaurantName = "initial";
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("utilisateurs/" + currentFirebaseUser.getUid());
    private CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("utilisateurs");
    ArrayList arrayList = new ArrayList<>();
    ArrayList likeAverage = new ArrayList<>();
    Boolean like = false;
    Boolean alreadyGoing = false;
    float numberOfLikes = 0;
    float numberOfPeople = 0;
    float averageLikes;


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
        ImageView starOne = findViewById(R.id.starOne);
        ImageView starTwo = findViewById(R.id.starTwo);
        ImageView starThree = findViewById(R.id.starThree);

        Button goingButton = findViewById(R.id.goingButton);


        //Fetching data from SP
        SharedPreferences mPreferences = getSharedPreferences("PREFERENCE_KEY_NAME", MODE_PRIVATE);
        String image = mPreferences.getString("image", "A PICTURE OBVIOUSLY");
        String name = mPreferences.getString("name", "Corben House");
        String address = mPreferences.getString("address", "2290 Av.ALbert Einstein");
        String placeId = mPreferences.getString("placeId", "A PLACE ID IDRK");
        String restaurantName = mPreferences.getString("goingRestaurant", "something");

        //Setting data to respective Views
        restaurantNameTV.setText(name);
        addressTV.setText(address);
        Picasso.get().load(image).into(photoRestaurant);

        //Setting the stars invisible by default
        starOne.setVisibility(View.INVISIBLE);
        starTwo.setVisibility(View.INVISIBLE);
        starThree.setVisibility(View.INVISIBLE);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        Api api = retrofit.create(Api.class);


        Call<PlaceDetails> call1 = api.getPlaceDetails(placeId);
        call1.enqueue(new Callback<PlaceDetails>() {
            @Override
            public void onResponse(Call<PlaceDetails> call, Response<PlaceDetails> detailResponse) {
                PlaceDetails details = detailResponse.body();
                PlaceDetailsResults detailedListResults = details.getResults();


                if (detailedListResults != null) {

                    theWebsite = detailedListResults.getWebsite();
                } else {
                    theWebsite = "https://benjamincorben.com";
                }
                if (detailedListResults.getPhoneNumber() != null) {
                    thephoneNumber = detailedListResults.getPhoneNumber();
                } else {
                    thephoneNumber = "noPhoneNumber";
                }
            }

            @Override
            public void onFailure(Call<PlaceDetails> call, Throwable t) {

            }
        });


        if (restaurantName.equals(name)) {
            goingButton.setText("✔");
            goingButton.setTextColor(GREEN);
            goingButton.setTextSize(25);
            alreadyGoing = true;
        }

        goingButton.setOnClickListener(view -> {

                    if (!(alreadyGoing)) {
                        goingButton.setText("✔");
                        goingButton.setTextColor(GREEN);
                        goingButton.setTextSize(25);

                        Map<String, Object> dataToSave = new HashMap<>();
                        dataToSave.put("restaurantName", name);
                        mDocRef.set(dataToSave, SetOptions.merge());

                        Map<String, Object> placeIdMap = new HashMap<>();
                        placeIdMap.put("placeId", placeId);
                        mDocRef.set(placeIdMap, SetOptions.merge());


                        Map<String, Object> pictureMap = new HashMap<>();
                        pictureMap.put("pictureRestaurant", image);
                        mDocRef.set(pictureMap, SetOptions.merge());

                        Map<String, Object> addressMap = new HashMap<>();
                        addressMap.put("address", address);
                        mDocRef.set(addressMap, SetOptions.merge());




                        mPreferences.edit().putInt("alreadyGoing", 1).apply();
                        mPreferences.edit().putString("goingRestaurant", name).apply();
                        alreadyGoing = true;

                    } else {
                        goingButton.setText("Going?");
                        goingButton.setTextColor(BLACK);
                        goingButton.setTextSize(15);

                        alreadyGoing = false;


                        Map<String, Object> updates = new HashMap<>();
                        updates.put("restaurantName", FieldValue.delete());

                        mDocRef.update(updates);
                        mPreferences.edit().putInt("alreadyGoing", 0).apply();
                    }

                }
        );


        mDocRef.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot.exists()) {
                Object restosLiked = documentSnapshot.get("listTesting");
                arrayList = (ArrayList) restosLiked;
                if (arrayList != null) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i).equals(name)) {
                            likePhotoIV.setImageResource(R.drawable.liked);
                            likeTV.setText("LIKED");
                            like = true;

                        }
                    }
                }
            }
        });

        likeButton.setOnClickListener(view -> {

            if (!like) {
                likePhotoIV.setImageResource(R.drawable.liked);
                likeTV.setText("LIKED");

                mDocRef.addSnapshotListener((documentSnapshot, e) -> {

                    if (documentSnapshot.exists()) {
                        Object restosLiked = documentSnapshot.get("listTesting");
                        arrayList = (ArrayList) restosLiked;
                        if (arrayList != null) {
                            arrayList.add(name);
                        }

                    } else {
                        arrayList.add(name);
                    }
                });
                Map<String, Object> arrayMapList = new HashMap<>();
                arrayMapList.put("listTesting", arrayList);
                mDocRef.update(arrayMapList);
                like = true;
            } else {
                likePhotoIV.setImageResource(R.drawable.like);
                likeTV.setText("LIKE");
                mDocRef.addSnapshotListener((documentSnapshot, e) -> {

                    Object restosLiked;
                    if (documentSnapshot.exists()) {
                        restosLiked = documentSnapshot.get("listTesting");

                        arrayList = (ArrayList) restosLiked;
                        if (arrayList != null) {
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (arrayList.get(i).equals(name)) {
                                    arrayList.remove(i);
                                }
                            }
                        }
                    }

                });
                Map<String, Object> arrayMapList = new HashMap<>();
                arrayMapList.put("listTesting", arrayList);
                mDocRef.update(arrayMapList);
                like = false;

            }
        });


        notebookRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                Object listTesting = documentSnapshot.get("listTesting");
                likeAverage = (ArrayList) listTesting;

                if (likeAverage != null) {
                    for (int i = 0; i < likeAverage.size(); i++) {
                        if (likeAverage.get(i).equals(name)) {
                            numberOfLikes++;
                        }
                    }
                }
                numberOfPeople++;

            }
            averageLikes = numberOfLikes  /  numberOfPeople;
            Log.d("whatisitworth", averageLikes + "");
            if (averageLikes == 0.0){
                starOne.setVisibility(View.INVISIBLE);
                starTwo.setVisibility(View.INVISIBLE);
                starThree.setVisibility(View.INVISIBLE);

            }
            if (0.1 >= averageLikes){
                starOne.setVisibility(View.VISIBLE);
                starTwo.setVisibility(View.INVISIBLE);
                starThree.setVisibility(View.INVISIBLE);

            }
            else if (0.4 >= averageLikes){
                starOne.setVisibility(View.VISIBLE);
                starTwo.setVisibility(View.VISIBLE);
                starThree.setVisibility(View.INVISIBLE);

            }
            else {
                starOne.setVisibility(View.VISIBLE);
                starTwo.setVisibility(View.VISIBLE);
                starThree.setVisibility(View.VISIBLE);

            }

        });


        websiteButton.setOnClickListener(view -> {
        });

        websiteButton.setOnClickListener(view -> {

            if (theWebsite.equals("https://benjamincorben.com")) {
                Toast.makeText(RestaurantActivity.this, "No website for this restaurant", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(RestaurantActivity.this, RestaurantWebViewActivity.class);
                intent.putExtra("websiteUrl", theWebsite);
                startActivity(intent);
            }
        });

        callButton.setOnClickListener(view -> {
            if (thephoneNumber.equals("noPhoneNumber")) {
                Toast.makeText(RestaurantActivity.this, "No phone number for this restaurant", Toast.LENGTH_LONG).show();
            } else {
                Intent i = new Intent(Intent.ACTION_DIAL);
                String p = "tel:" + thephoneNumber;
                i.setData(Uri.parse(p));
                startActivity(i);
            }
        });


    }


}
