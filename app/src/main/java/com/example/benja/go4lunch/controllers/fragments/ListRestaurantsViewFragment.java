package com.example.benja.go4lunch.controllers.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.benja.go4lunch.R;
import com.example.benja.go4lunch.base.BaseFragment;
import com.example.benja.go4lunch.models.Restaurant;
import com.example.benja.go4lunch.utils.Api;
import com.example.benja.go4lunch.utils.PlaceNearBySearch;
import com.example.benja.go4lunch.utils.PlaceNearBySearchResult;
import com.example.benja.go4lunch.views.ListRestaurantsViewAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListRestaurantsViewFragment extends BaseFragment {


    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
//    @BindView(R.id.fragment_list_restaurant_view_recycler_view)
    RecyclerView mRecyclerView;

    // View of the Fragment
    private View mListView;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Restaurant> restaurantList;

    String photoReferences;

    // Declare Adapter of the RecyclerView
    private ListRestaurantsViewAdapter mAdapter;

    public ListRestaurantsViewFragment() {
    }

    // ---------------------------------------------------------------------------------------------
    //                                  FRAGMENT INSTANTIATION
    // ---------------------------------------------------------------------------------------------
    public static ListRestaurantsViewFragment newInstance() {

        // Create new fragment
        return new ListRestaurantsViewFragment();
    }

    // ---------------------------------------------------------------------------------------------
    //                                    ENTRY POINT
    // ---------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_list_restaurants_view, container, false);
        recyclerView = rootView.findViewById(R.id.fragment_list_restaurant_view_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        restaurantList = new ArrayList<>();
        adapter = new ListRestaurantsViewAdapter(restaurantList, getContext());


        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        Api api = retrofit.create(Api.class);


        Call<PlaceNearBySearch> call = api.getPlaceNearBySearch(latitude + "," + longitude);

        recyclerView.setAdapter(adapter);
        call.enqueue(new Callback<PlaceNearBySearch>() {
            @Override
            public void onResponse(Call<PlaceNearBySearch> call, Response<PlaceNearBySearch> response) {
                PlaceNearBySearch articles = response.body();
                List<PlaceNearBySearchResult> theListOfResults = articles.getResults();


                for (int i = 0; i < theListOfResults.size(); i++) {
                    if (theListOfResults.get(i).getPhotos() != null) {
                        photoReferences = theListOfResults.get(i).getPhotos().get(0).getPhotoReference();
                    } else {
                        photoReferences = "CmRaAAAA0-j6NJjMJf_0-AXUEIl2CFiU1djE4V5inVAiHFXafJILjxZiLLisEdQDx_m9133Pbe2TWPJ_KVhyTQSHW_4J_LmkGKmgwoTphY9Ul1vO8dbd4oFXbzb8zEz7eK751glhEhBLRvmxTtdf6gOhkX2Y9_4IGhSbVmaZ8vWI3o3oVrzjGehz6Ck1zA";
                    }

                    //DISTANCE BETWEEN RESTAURANT AND USER LOCATION USING SIMPLE MATHS:
                    double userLatitudeDegres = latitude;
                    double userLongitudeDegres = longitude;
                    double restaurantLatitudeDegres = theListOfResults.get(i).getGeometry().getLocation().getLat();
                    double restaurantLongitudeDegres = theListOfResults.get(i).getGeometry().getLocation().getLng();

                    double userLatitude = userLatitudeDegres * (Math.PI / 180);
                    double userLongitude = userLongitudeDegres * (Math.PI / 180);
                    double restaurantLatitude = restaurantLatitudeDegres * (Math.PI / 180);
                    double restaurantLongitude = restaurantLongitudeDegres * (Math.PI / 180);

                    double reallyPreciseDistance = 6367445 * Math.acos(Math.sin(userLatitude) * (Math.sin(restaurantLatitude)) + Math.cos(userLatitude) * Math.cos(restaurantLatitude) * Math.cos(userLongitude - restaurantLongitude));
                    int distance = (int) reallyPreciseDistance;


                    if (theListOfResults.get(i).getOpeningHours() != null) {
                        Restaurant restaurantItem = new Restaurant(theListOfResults.get(i).getName(),
                                theListOfResults.get(i).getAddress(),
                                theListOfResults.get(i).getOpeningHours().getOpenNow(),
                                distance + "m",
                                "https://maps.googleapis.com/maps/api/place/photo?"
                                        + "maxwidth=2304"
                                        + "&photoreference=" + photoReferences
                                        + "&key=AIzaSyAR3xMop8hS0cX1S3u70q-EC15TBduuDo4");
                        restaurantList.add(restaurantItem);
                    } else {
                        Restaurant restaurantItem = new Restaurant(theListOfResults.get(i).getName(),
                                theListOfResults.get(i).getAddress(),
                                false,
                                distance + "m",
                                "https://maps.googleapis.com/maps/api/place/photo?"
                                        + "maxwidth=2304"
                                        + "&photoreference=" + photoReferences
                                        + "&key=AIzaSyAR3xMop8hS0cX1S3u70q-EC15TBduuDo4");
                        restaurantList.add(restaurantItem);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<PlaceNearBySearch> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("JSON", t.getMessage());

            }
        });


        return rootView;
    }

    double latitude;
    double longitude;

    @Override
    public void onAttach(Context context) {

        SharedPreferences preferences = context.getSharedPreferences("PREFERENCE_KEY_NAME", 0);
        latitude = Double.valueOf(preferences.getString("locationLatitude", "0"));
        longitude = Double.valueOf(preferences.getString("locationLongitude", "0"));

        super.onAttach(context);
    }
}