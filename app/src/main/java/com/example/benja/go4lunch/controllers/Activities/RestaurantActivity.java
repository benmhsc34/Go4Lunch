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
import android.support.v7.widget.Toolbar;
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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
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

import butterknife.BindView;
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
    long likes = 0;
    float numberOfPeople = 0;
    float averageLikes;
    private FirestoreRecyclerAdapter<UsersModel, UsersViewHolder> adapter;



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
                    if (detailedListResults.getPhoneNumber() != null) {
                        thephoneNumber = detailedListResults.getPhoneNumber();
                    } else {
                        thephoneNumber = "noPhoneNumber";
                    }
                } else {
                    theWebsite = "https://benjamincorben.com";
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
                        mPreferences.edit().putString("goingRestaurant", "").apply();
                    }

                }
        );
        mDocRef.get().addOnSuccessListener(documentSnapshot -> {
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

        notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {
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
            averageLikes = numberOfLikes / numberOfPeople;
            Log.d("whatisitworth", averageLikes + "");
            if (averageLikes == 0.0) {
                starOne.setVisibility(View.INVISIBLE);
                starTwo.setVisibility(View.INVISIBLE);
                starThree.setVisibility(View.INVISIBLE);

            }
            if (0.1 >= averageLikes) {
                starOne.setVisibility(View.VISIBLE);
                starTwo.setVisibility(View.INVISIBLE);
                starThree.setVisibility(View.INVISIBLE);

            } else if (0.4 >= averageLikes) {
                starOne.setVisibility(View.VISIBLE);
                starTwo.setVisibility(View.VISIBLE);
                starThree.setVisibility(View.INVISIBLE);

            } else {
                starOne.setVisibility(View.VISIBLE);
                starTwo.setVisibility(View.VISIBLE);
                starThree.setVisibility(View.VISIBLE);

            }

        });



        likeButton.setOnClickListener(view -> {
            Log.d("whatshappening", "we click");

            DocumentReference mDocReference = FirebaseFirestore.getInstance().document("restaurants/" + name);


            if (!like) {
                likePhotoIV.setImageResource(R.drawable.liked);
                likeTV.setText("LIKED");

                mDocReference.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        likes = (long) documentSnapshot.get("likes");
                    }
                });

                Map<String, Object> dataToSave = new HashMap<>();

                dataToSave.put("likes", likes + 1);
                mDocReference.set(dataToSave, SetOptions.merge());


                mDocRef.get().addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        Object restosLiked = documentSnapshot.get("listTesting");
                        arrayList = (ArrayList) restosLiked;
                        arrayList.add(name);
                        like = true;

                    } else {
                        arrayList.add(name);
                        Log.d("whatshappening", "ADD");

                        like = true;
                    }
                    Map<String, Object> arrayMapList = new HashMap<>();
                    arrayMapList.put("listTesting", arrayList);
                    mDocRef.update(arrayMapList);
                });


            } else {
                Log.d("whatshappening", "ELSE");

                likePhotoIV.setImageResource(R.drawable.like);
                likeTV.setText("LIKE");


                mDocReference.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        likes = (long) documentSnapshot.get("likes");
                    }
                });


                Map<String, Object> dataToSave = new HashMap<>();
                dataToSave.put("likes", likes - 1);
                mDocReference.set(dataToSave, SetOptions.merge());


                mDocRef.get().addOnSuccessListener(documentSnapshot -> {

                    Object restosLiked;
                    if (documentSnapshot.exists()) {
                        restosLiked = documentSnapshot.get("listTesting");

                        arrayList = (ArrayList) restosLiked;
                        if (arrayList != null) {
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (arrayList.get(i).equals(name)) {
                                    arrayList.remove(i);
                                    Log.d("whatshappening", "remove");

                                    like = false;

                                }
                                like = false;

                            }
                            like = false;

                        }
                        like = false;

                    }
                    Map<String, Object> arrayMapList = new HashMap<>();
                    arrayMapList.put("listTesting", arrayList);
                    mDocRef.update(arrayMapList);
                });


            }
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
                Toast.makeText(RestaurantActivity.this, R.string.no_phone, Toast.LENGTH_LONG).show();
            } else {
                Intent i = new Intent(Intent.ACTION_DIAL);
                String p = "tel:" + thephoneNumber;
                i.setData(Uri.parse(p));
                startActivity(i);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("utilisateurs");

        FirestoreRecyclerOptions<UsersModel> options = new FirestoreRecyclerOptions.Builder<UsersModel>()
                .setQuery(query, UsersModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<UsersModel, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull UsersModel model) {

                //Avoid crash
                if (model.getRestaurantName() != null) {

                    //Check if user should be added to this Restaurant
                    if (model.getRestaurantName().equals(name)) {

                        //See if the user is the current user (in that case use 2nd person instead of 3rd (it's all about the details))
                        if (model.getUserName().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {

                            if (model.getPicture() != null) {
                                    holder.setUserName((String) getText(R.string.you_eating_here));
                                    holder.setPicture(model.getPicture());

                            } else {
                                if (model.getRestaurantName() != null) {
                                    holder.setUserName((String) getText(R.string.you_eating_here));
                                    holder.setPicture("http://farrellaudiovideo.com/wp-content/uploads/2016/02/default-profile-pic.png");
                                } else {
                                    holder.setUserName(model.getUserName() + getText(R.string.hasnt_decided));
                                    holder.setPicture("http://farrellaudiovideo.com/wp-content/uploads/2016/02/default-profile-pic.png");
                                }
                            }
                        } else {

                            if (model.getPicture() != null) {
                                if (model.getRestaurantName() != null) {
                                    holder.setUserName(model.getUserName() + " " + getText(R.string.eating_here));
                                    holder.setPicture(model.getPicture());
                                }
                            } else {
                                if (model.getRestaurantName() != null) {
                                    holder.setUserName(model.getUserName()  + " " + getText(R.string.eating_here));
                                    holder.setPicture("http://farrellaudiovideo.com/wp-content/uploads/2016/02/default-profile-pic.png");
                                } else {
                                    holder.setUserName(model.getUserName() + getText(R.string.hasnt_decided));
                                    holder.setPicture("http://farrellaudiovideo.com/wp-content/uploads/2016/02/default-profile-pic.png");
                                }
                            }
                        }
                    } else {
                        holder.setPicture(null);
                        holder.setUserName("You know I ain't broke ahh");
                        holder.setHeight(0);

                    }
                }
            }


            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_layout, viewGroup, false);

                return new UsersViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
    }


    private class UsersViewHolder extends RecyclerView.ViewHolder {
        private View view;

        UsersViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        void setUserName(String userName) {
            TextView textView = view.findViewById(R.id.userNameTV);
            textView.setText(userName);
        }

        void setHeight(int height) {
            RelativeLayout relativeLayout = view.findViewById(R.id.relativeLayout);
            relativeLayout.getLayoutParams().height = height;
        }

        void setPicture(String picture) {
            ImageView imageView = view.findViewById(R.id.pictureIV);
            Glide.with(RestaurantActivity.this)
                    .load(picture)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }


    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
