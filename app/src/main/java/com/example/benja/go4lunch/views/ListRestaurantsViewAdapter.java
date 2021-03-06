package com.example.benja.go4lunch.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.benja.go4lunch.R;
import com.example.benja.go4lunch.controllers.Activities.RestaurantActivity;
import com.example.benja.go4lunch.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Color.RED;

public class ListRestaurantsViewAdapter extends RecyclerView.Adapter<ListRestaurantsViewAdapter.ViewHolder> {

    private final List<Restaurant> restaurantList;
    private final Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ListRestaurantsViewAdapter(List<Restaurant> listItems, Context context) {
        this.restaurantList = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_list_restaurants_view_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Restaurant restaurantItem = restaurantList.get(i);

        viewHolder.restaurantName.setText(restaurantItem.getName());
        viewHolder.restaurantAddress.setText(restaurantItem.getAddress());

        if (restaurantItem.getOpeningTime()) {
            viewHolder.restaurantOpeningHours.setText(R.string.open);
            viewHolder.restaurantOpeningHours.setTextColor(Color.parseColor("#2b7f37"));

        } else {
            viewHolder.restaurantOpeningHours.setText(R.string.closed);
            viewHolder.restaurantOpeningHours.setTextColor(RED);
        }
        viewHolder.restaurantDistanceToUser.setText(restaurantItem.getDistance());

        int numberOfLikes = (int) restaurantItem.getNbrLikes();
        if (numberOfLikes == 3) {
            Log.d("testt", "likeButton: likes = 3");

            viewHolder.starOne.setVisibility(View.VISIBLE);
            viewHolder.starTwo.setVisibility(View.VISIBLE);
            viewHolder.starThree.setVisibility(View.VISIBLE);


        } else if (numberOfLikes == 1) {
            Log.d("testt", "likeButton: likes = 1");
            viewHolder.starOne.setVisibility(View.VISIBLE);
            viewHolder.starTwo.setVisibility(View.INVISIBLE);
            viewHolder.starThree.setVisibility(View.INVISIBLE);

        } else if (numberOfLikes == 2) {
            Log.d("testt", "likeButton: likes = 2");
            viewHolder.starOne.setVisibility(View.VISIBLE);
            viewHolder.starTwo.setVisibility(View.VISIBLE);
            viewHolder.starThree.setVisibility(View.INVISIBLE);

        } else {
            Log.d("testt", "likeButton: else");
            viewHolder.starOne.setVisibility(View.INVISIBLE);
            viewHolder.starTwo.setVisibility(View.INVISIBLE);
            viewHolder.starThree.setVisibility(View.INVISIBLE);

        }
        viewHolder.numberOfPeople.setText("(" +  numberOfLikes + ")");


        Picasso.get().load(restaurantItem.getPhotoUrl()).into(viewHolder.restaurantImage);

        viewHolder.linearLayout.setOnClickListener(view -> {
            Intent myIntent = new Intent(context, RestaurantActivity.class);

            SharedPreferences mPreferences = context.getSharedPreferences("PREFERENCE_KEY_NAME", MODE_PRIVATE);
            mPreferences.edit().putString("image", restaurantItem.getPhotoUrl()).apply();
            mPreferences.edit().putString("name", restaurantItem.getName()).apply();
            mPreferences.edit().putString("address", restaurantItem.getAddress()).apply();
            mPreferences.edit().putString("placeId", restaurantItem.getPlaceId()).apply();


            context.startActivity(myIntent);
            //    viewHolder.relativeLayout.setBackgroundColor(R.color.colorPrimaryDark);
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView restaurantName;
        final TextView restaurantAddress;
        final TextView restaurantOpeningHours;
        final TextView restaurantDistanceToUser;
        final ImageView restaurantImage;
        final LinearLayout linearLayout;
        final ImageView starOne;
        final ImageView starTwo;
        final ImageView starThree;
        final TextView numberOfPeople;


        ViewHolder(@NonNull final View itemView) {
            super(itemView);

            restaurantName = itemView.findViewById(R.id.fragment_list_restaurant_view_item_name);
            restaurantAddress = itemView.findViewById(R.id.fragment_list_restaurant_view_item_address);
            restaurantOpeningHours = itemView.findViewById(R.id.fragment_list_restaurant_view_item_opening_hours);
            restaurantDistanceToUser = itemView.findViewById(R.id.fragment_list_restaurant_view_item_distance);
            restaurantImage = itemView.findViewById(R.id.fragment_list_restaurant_view_item_image);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            starThree = itemView.findViewById(R.id.fragment_list_restaurant_view_item_star_three);
            starTwo = itemView.findViewById(R.id.fragment_list_restaurant_view_item_star_two);
            starOne = itemView.findViewById(R.id.fragment_list_restaurant_view_item_star_one);
            numberOfPeople = itemView.findViewById(R.id.fragment_list_restaurant_view_item_participants);


            ButterKnife.bind(this, itemView);


            itemView.setOnClickListener(view -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        mListener.onItemClick(position);
                    }
                }
            });

        }
    }


}


