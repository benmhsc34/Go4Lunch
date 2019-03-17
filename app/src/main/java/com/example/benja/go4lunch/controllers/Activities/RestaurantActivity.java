package com.example.benja.go4lunch.controllers.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.example.benja.go4lunch.models.UsersModel;
import com.example.benja.go4lunch.utils.Api;
import com.example.benja.go4lunch.utils.Constants;
import com.example.benja.go4lunch.utils.PlaceDetails;
import com.example.benja.go4lunch.utils.PlaceDetailsResults;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
//    Boolean like = false;

    float numberOfLikes = 0;
    long likes = 0;
    float numberOfPeople = 0;
    float averageLikes;
    private FirestoreRecyclerAdapter<UsersModel, UsersViewHolder> adapter;

    private SharedPreferences mPreferences;
    private String image;
    private String name;
    private String address;
    private String placeId;
    private String restaurantName;

    //views
    private Button goingButton;

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

        goingButton = findViewById(R.id.goingButton);

        //Fetching data from SP
        mPreferences = getSharedPreferences("PREFERENCE_KEY_NAME", MODE_PRIVATE);
        image = mPreferences.getString("image", "A PICTURE OBVIOUSLY");
        name = mPreferences.getString("name", "Corben House");
        address = mPreferences.getString("address", "2290 Av.ALbert Einstein");
        placeId = mPreferences.getString("placeId", "A PLACE ID IDRK");

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

        initialiseGoingToCurrentRestaurant();

        goingButton.setOnClickListener(view -> toggleGoingToCurrentRestaurant());

        //        notebookRef.document(currentFirebaseUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
//            if (documentSnapshot.exists()) {
//                Object restosLiked = documentSnapshot.get("listTesting");
//                arrayList = (ArrayList) restosLiked;
//                if (arrayList != null) {
//                    for (int i = 0; i < arrayList.size(); i++) {
//                        if (arrayList.get(i).equals(name)) {
//                            likePhotoIV.setImageResource(R.drawable.liked);
//                            likeTV.setText("LIKED");
//                            like = true;
//                        }
//                    }
//                }
//            }
//        });

//        notebookRef.get().addOnSuccessListener((queryDocumentSnapshots) -> {
//            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//
//                Object listTesting = documentSnapshot.get("listTesting");
//                likeAverage = (ArrayList) listTesting;
//
//                if (likeAverage != null) {
//                    for (int i = 0; i < likeAverage.size(); i++) {
//                        if (likeAverage.get(i).equals(name)) {
//                            numberOfLikes++;
//                        }
//                    }
//                }

        //           }

        DocumentReference mDocReference = FirebaseFirestore.getInstance().document("restaurants/" + name);
        mDocReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.get("likes") != null) {
                Log.d("testt", "likeButton: \"likes\" != null");
                String floatResto = documentSnapshot.get("likes").toString();
                numberOfLikes = Integer.parseInt(floatResto);

                likePhotoIV.setImageResource(R.drawable.liked);
                likeTV.setText("LIKED");
            } else {

                Log.d("testt", "likeButton: \"likes\" = null");
                Map<String, Object> newLikesField = new HashMap<>();
                newLikesField.put("likes", 0);
                mDocReference.set(newLikesField, SetOptions.merge());
                numberOfLikes = 0;

                likePhotoIV.setImageResource(R.drawable.like);
                likeTV.setText("LIKE");
            }
            Log.d("whatisitworth", numberOfLikes + "");
            if (numberOfLikes == 3) {
                Log.d("testt", "likeButton: likes = 3");

                starOne.setVisibility(View.INVISIBLE);
                starTwo.setVisibility(View.INVISIBLE);
                starThree.setVisibility(View.INVISIBLE);

            }
            if (numberOfLikes == 1) {
                Log.d("testt", "likeButton: likes = 1");
                starOne.setVisibility(View.VISIBLE);
                starTwo.setVisibility(View.INVISIBLE);
                starThree.setVisibility(View.INVISIBLE);

            } else if (numberOfLikes == 2) {
                Log.d("testt", "likeButton: likes = 2");
                starOne.setVisibility(View.VISIBLE);
                starTwo.setVisibility(View.VISIBLE);
                starThree.setVisibility(View.INVISIBLE);

            } else {
                Log.d("testt", "likeButton: else");
                starOne.setVisibility(View.INVISIBLE);
                starTwo.setVisibility(View.INVISIBLE);
                starThree.setVisibility(View.INVISIBLE);

            }
        });

        //update listTesting on the basis of the updated like/unlike status
        notebookRef.document(currentFirebaseUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {

            if (documentSnapshot.exists()) {
                Object restosLiked = documentSnapshot.get("listTesting");
                arrayList = (ArrayList) restosLiked;
                if (arrayList.contains(name)) {
                    Log.d("testt", "notebookRef removing");
                    arrayList.remove(name);
                } else {
                    Log.d("testt", "notebookRef adding");
                    arrayList.add(name);
                }
//                    like = true;
            }

            Map<String, Object> arrayMapList = new HashMap<>();
            arrayMapList.put("listTesting", arrayList);
//                notebookRef.document(currentFirebaseUser.getUid()).update(arrayMapList);
            mDocRef.update(arrayMapList);
        });


        likeButton.setOnClickListener(view -> {
            Log.d("whatshappening", "we click");

//            DocumentReference mDocReference = FirebaseFirestore.getInstance().document("restaurants/" + name);

            Map<String, Object> dataToSave = new HashMap<>();

            //get current like/unlike status
            mDocRef.get().addOnSuccessListener(documentSnapshot -> {
                Object restosLiked = documentSnapshot.get("listTesting");
                arrayList = (ArrayList) restosLiked;

                final long[] likes = new long[1];

                mDocReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(
                        @Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e
                    ) {
                        Log.d("testt", "likeButton click onEvent");
                        likes[0] = (long) documentSnapshot.get("likes");
                    }
                });

                if (arrayList.contains(name)) {
                    Log.d("testt", "likeButton click contains(name)");
                    arrayList.remove(name);
                    likePhotoIV.setImageResource(R.drawable.like);
                    likeTV.setText("LIKE");

                    Log.d("likes_false", likes[0] + "");
                    dataToSave.put("likes", likes[0] - 1);
                } else {
                    Log.d("testt", "likeButton click doesn't contain(name)");
                    arrayList.add(name);
                    likePhotoIV.setImageResource(R.drawable.liked);
                    likeTV.setText("LIKED");

                    Log.d("likes_true", likes[0] + "");
                    dataToSave.put("likes", likes[0] + 1);
                }
            });

            //update like/unlike status
            mDocReference.set(dataToSave, SetOptions.merge());
            Map<String, Object> arrayMapList = new HashMap<>();
            arrayMapList.put("listTesting", arrayList);
//                notebookRef.document(currentFirebaseUser.getUid()).update(arrayMapList);
            mDocRef.update(arrayMapList);

//            if (!like) {
//                likePhotoIV.setImageResource(R.drawable.liked);
//                likeTV.setText("LIKED");
//
//                mDocReference.get().addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        likes = (long) documentSnapshot.get("likes");
//                    }
//                });
//
//                Map<String, Object> dataToSave = new HashMap<>();
//
//                dataToSave.put("likes", likes + 1);
//                mDocReference.set(dataToSave, SetOptions.merge());
//
//
//            } else {
//                Log.d("whatshappening", "ELSE");
//
//                mDocRef.get().addOnSuccessListener(documentSnapshot -> {
//
//                    Object restosLiked;
//                    if (documentSnapshot.exists()) {
//                        restosLiked = documentSnapshot.get("listTesting");
//
//                        arrayList = (ArrayList) restosLiked;
//                        if (arrayList != null) {
//                            for (int i = 0; i < arrayList.size(); i++) {
//                                if (arrayList.get(i).equals(name)) {
//                                    arrayList.remove(i);
//                                    Log.d("whatshappening", "remove");
//
//                                    like = false;
//
//                                }
//                                like = false;
//
//                            }
//                            like = false;
//
//                        }
//                        like = false;
//
//                    }
//                    Map<String, Object> arrayMapList = new HashMap<>();
//                    arrayMapList.put("listTesting", arrayList);
//                    mDocRef.update(arrayMapList);
//                });
//
//
//            }
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

                String userPicture = model.getPicture();

                //Avoid crash
                if (model.getRestaurantName() != null && model.getRestaurantName().equals(name)) {
                    //Check if user should be added to this Restaurant
                    holder.itemView.setVisibility(View.VISIBLE);
                    //See if the user is the current user (in that case use 2nd person instead of 3rd (it's all
                    // about the details))
                    if (model.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        holder.setUserName(getString(R.string.you_eating_here));
                    } else {
                        holder.setUserName(model.getUserName() + " " + getString(R.string.eating_here));
                    }
                    holder.setPicture(userPicture == null ? Constants.DEFAULT_PROFILE_PIC_URL : userPicture);
                } else {
                    holder.setPicture(null);
                    holder.setUserName("You know I ain't broke ahh");
                    holder.itemView.setVisibility(View.GONE);
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

    private void initialiseGoingToCurrentRestaurant() {
        notebookRef.document(currentFirebaseUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.contains("restaurantName") && document.get("restaurantName")
                    .equals(name)) {
                    Log.d("testt", "init going ui");
                    updateToGoingUI();
                } else {
                    Log.d("testt", "init not-going ui");
                    updateToNotGoingUI();
                }
            } else {
                Log.d("testt", "get failed with ", task.getException());
            }
        });
    }

    private void toggleGoingToCurrentRestaurant() {
        notebookRef.document(currentFirebaseUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists() && document.contains("restaurantName") && document.get("restaurantName")
                    .equals(name)) {
                    Log.d("testt", "toggle to not-going ui");
                    markThisRestaurantAsNotGoing();
                } else {
                    Log.d("testt", "toggle to going ui");
                    markThisRestaurantAsGoing();
                }
            } else {
                Log.d("testt", "get failed with ", task.getException());
            }
        });
    }

    private void markThisRestaurantAsGoing() {
        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put("restaurantName", name);
        notebookRef.document(currentFirebaseUser.getUid()).set(dataToSave, SetOptions.merge());

        Map<String, Object> placeIdMap = new HashMap<>();
        placeIdMap.put("placeId", placeId);
        notebookRef.document(currentFirebaseUser.getUid()).set(placeIdMap, SetOptions.merge());


        Map<String, Object> pictureMap = new HashMap<>();
        pictureMap.put("pictureRestaurant", image);
        notebookRef.document(currentFirebaseUser.getUid()).set(pictureMap, SetOptions.merge());

        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("address", address);
        notebookRef.document(currentFirebaseUser.getUid()).set(addressMap, SetOptions.merge());

        mPreferences.edit().putInt("alreadyGoing", 1).apply();
        mPreferences.edit().putString("goingRestaurant", name).apply();

        updateToGoingUI();
    }

    private void markThisRestaurantAsNotGoing() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("restaurantName", FieldValue.delete());
        updates.put("placeId", FieldValue.delete());
        updates.put("pictureRestaurant", FieldValue.delete());
        updates.put("address", FieldValue.delete());

        notebookRef.document(currentFirebaseUser.getUid()).update(updates);

        mPreferences.edit().putInt("alreadyGoing", 0).apply();
        mPreferences.edit().putString("goingRestaurant", "").apply();

        updateToNotGoingUI();
    }

    private void updateToNotGoingUI() {
        goingButton.setText("Going?");
        goingButton.setTextColor(BLACK);
        goingButton.setTextSize(15);
    }

    private void updateToGoingUI() {
        goingButton.setText("✔");
        goingButton.setTextColor(GREEN);
        goingButton.setTextSize(25);
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
