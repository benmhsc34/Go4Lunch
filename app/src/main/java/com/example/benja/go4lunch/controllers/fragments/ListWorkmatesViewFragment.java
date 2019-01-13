package com.example.benja.go4lunch.controllers.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.benja.go4lunch.R;
import com.example.benja.go4lunch.controllers.Activities.RestaurantActivity;
import com.example.benja.go4lunch.models.UsersModel;
import com.facebook.share.Share;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class ListWorkmatesViewFragment extends Fragment {

    Context mContext;

    //   private DocumentReference mDocumentReference = FirebaseFirestore.getInstance().document("sampleData/inspiration");

    public ListWorkmatesViewFragment() {
        // Required empty public constructor
    }

    public static ListWorkmatesViewFragment newInstance() {
        ListWorkmatesViewFragment fragment = new ListWorkmatesViewFragment();
        return fragment;
    }


    private FirestoreRecyclerAdapter<UsersModel, UsersViewHolder> adapter;

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

        void setPicture(String picture) {
            ImageView imageView = view.findViewById(R.id.pictureIV);
            Glide.with(getContext())
                    .load(picture)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        }

        void openRestaurant(String restaurantName, String placeId, String restaurantPicture, String address) {

            RelativeLayout relativeLayout = view.findViewById(R.id.relativeLayout);
            relativeLayout.setOnClickListener(view -> {
                SharedPreferences mPreferences = getContext().getSharedPreferences("PREFERENCE_KEY_NAME", Context.MODE_PRIVATE);
                mPreferences.edit().putString("image", restaurantPicture).apply();
                mPreferences.edit().putString("name", restaurantName).apply();
                mPreferences.edit().putString("placeId", placeId).apply();
                mPreferences.edit().putString("address", address).apply();

                Intent myIntent = new Intent(getContext(), RestaurantActivity.class);
                startActivity(myIntent);
            });
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_list_workmates_view, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("utilisateurs");

        FirestoreRecyclerOptions<UsersModel> options = new FirestoreRecyclerOptions.Builder<UsersModel>()
                .setQuery(query, UsersModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<UsersModel, ListWorkmatesViewFragment.UsersViewHolder>(options) {

            @NonNull
            @Override
            public ListWorkmatesViewFragment.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_layout, viewGroup, false);

                return new ListWorkmatesViewFragment.UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull UsersModel model) {


                if (model.getPicture() != null) {
                    if (model.getRestaurantName() != null) {
                        holder.setUserName(model.getUserName() + " is eating at the " + model.getRestaurantName());
                        holder.setPicture(model.getPicture());
                    } else {
                        holder.setUserName(model.getUserName() + " hasn't decided yet");
                        holder.setPicture(model.getPicture());
                    }
                } else {
                    if (model.getRestaurantName() != null) {
                        holder.setUserName(model.getUserName() + " is eating at: " + model.getRestaurantName());
                        holder.setPicture("http://farrellaudiovideo.com/wp-content/uploads/2016/02/default-profile-pic.png");
                    } else {
                        holder.setUserName(model.getUserName() + " hasn't decided yet");
                        holder.setPicture("http://farrellaudiovideo.com/wp-content/uploads/2016/02/default-profile-pic.png");
                    }
                }

                holder.openRestaurant(model.getRestaurantName(), model.getPlaceId(), model.getPictureRestaurant(), model.getAddress());

            }

        };
        recyclerView.setAdapter(adapter);


        return view;
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





  /*  @Override
    public void onStart() {
        mDocumentReference.get().addOnSuccessListener(documentSnapshot -> {

            if (documentSnapshot.exists()) {
                Log.d("sucess", "SUCCESS" + documentSnapshot.getString("quote"));
                String quoteText = documentSnapshot.getString("quote");
                String authorText = documentSnapshot.getString("author");
                theTV.setText("\"" + quoteText + "\"" + "- by -" + authorText);
            }

        }).addOnFailureListener(e -> Log.d("failure", "EPIC FAIL",e));




            db.collection("utilisateurs")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("SUCESS", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("Failure", "Error getting documents.", task.getException());
                        }
                    });

        });



        super.onStart();
    }
*/
}
