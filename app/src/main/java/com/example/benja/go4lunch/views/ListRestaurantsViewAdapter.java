package com.example.benja.go4lunch.views;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.benja.go4lunch.R;
import com.example.benja.go4lunch.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class ListRestaurantsViewAdapter extends RecyclerView.Adapter<ListRestaurantsViewAdapter.ViewHolder> {

    private List<Restaurant> restaurantList;
    private Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
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

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Restaurant restaurantItem = restaurantList.get(i);

        viewHolder.restaurantName.setText(restaurantItem.getName());
        viewHolder.restaurantAddress.setText(restaurantItem.getAddress());
        if (restaurantItem.getOpeningTime()){
            viewHolder.restaurantOpeningHours.setText("OPEN");
            viewHolder.restaurantOpeningHours.setTextColor(GREEN);

        } else {
            viewHolder.restaurantOpeningHours.setText("CLOSED");
            viewHolder.restaurantOpeningHours.setTextColor(RED);
        }
        viewHolder.restaurantDistanceToUser.setText(restaurantItem.getDistance());
        Picasso.get().load(restaurantItem.getPhotoUrl()).into(viewHolder.restaurantImage);

    /*    viewHolder.linearLayout.setOnClickListener(view -> {
            Intent myIntent = new Intent(context, WebviewActivity.class);
            myIntent.putExtra("websiteUrl", restaurantItem.getUrlWebsite());
            context.startActivity(myIntent);
            //    viewHolder.relativeLayout.setBackgroundColor(R.color.colorPrimaryDark);
        }); */
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView restaurantName;
        public TextView restaurantAddress;
        public TextView restaurantOpeningHours;
        public TextView restaurantDistanceToUser;
        public ImageView restaurantImage;
        public LinearLayout linearLayout;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            restaurantName = itemView.findViewById(R.id.fragment_list_restaurant_view_item_name);
            restaurantAddress = itemView.findViewById(R.id.fragment_list_restaurant_view_item_address);
            restaurantOpeningHours = itemView.findViewById(R.id.fragment_list_restaurant_view_item_opening_hours);
            restaurantDistanceToUser = itemView.findViewById(R.id.fragment_list_restaurant_view_item_distance);
            restaurantImage = itemView.findViewById(R.id.fragment_list_restaurant_view_item_image);
            linearLayout = itemView.findViewById(R.id.linearLayout);

            ButterKnife.bind(this, itemView);


            itemView.setOnClickListener(view -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) ;
                    {
                        mListener.onItemClick(position);
                    }
                }
            });

        }
    }


}


