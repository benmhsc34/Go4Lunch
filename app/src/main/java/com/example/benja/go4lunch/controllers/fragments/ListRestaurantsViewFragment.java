package com.example.benja.go4lunch.controllers.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.benja.go4lunch.R;
import com.example.benja.go4lunch.api.RestaurantHelper;
import com.example.benja.go4lunch.base.BaseFragment;
import com.example.benja.go4lunch.controllers.Activities.RestaurantCardActivity;
import com.example.benja.go4lunch.models.Restaurant;
import com.example.benja.go4lunch.views.ListRestaurantsViewAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListRestaurantsViewFragment extends BaseFragment {

    // FOR TRACES
    private static final String TAG = ListRestaurantsViewFragment.class.getSimpleName();

    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    @BindView(R.id.fragment_list_restaurant_view_recycler_view)
    RecyclerView mRecyclerView;

    // View of the Fragment
    private View mListView;

    // Declare Adapter of the RecyclerView
    private ListRestaurantsViewAdapter mAdapter;

    public ListRestaurantsViewFragment() {
        // Required empty public constructor
    }
    // ---------------------------------------------------------------------------------------------
    //                                  FRAGMENT INSTANTIATION
    // ---------------------------------------------------------------------------------------------
    public static ListRestaurantsViewFragment newInstance() {
        Log.d(TAG, "newInstance: ");

        // Create new fragment
        return new ListRestaurantsViewFragment();
    }
    // ---------------------------------------------------------------------------------------------
    //                                    ENTRY POINT
    // ---------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");

        // Inflate the layout for this fragment
        mListView = inflater.inflate(R.layout.fragment_list_restaurants_view, container, false);

        // Telling ButterKnife to bind all views in layout
        ButterKnife.bind(this, mListView);

        return mListView;
    }

}
