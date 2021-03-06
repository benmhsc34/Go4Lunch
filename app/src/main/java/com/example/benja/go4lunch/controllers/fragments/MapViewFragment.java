package com.example.benja.go4lunch.controllers.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.benja.go4lunch.MarkerObject;
import com.example.benja.go4lunch.base.BaseFragment;
import com.example.benja.go4lunch.controllers.Activities.MainActivity;
import com.example.benja.go4lunch.controllers.Activities.RestaurantActivity;
import com.example.benja.go4lunch.models.Restaurant;
import com.example.benja.go4lunch.R;
import com.example.benja.go4lunch.api.Api;
import com.example.benja.go4lunch.utils.PlaceNearBySearch;
import com.example.benja.go4lunch.utils.PlaceNearBySearchResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;


/**************************************************************************************************
 *
 *  FRAGMENT that displays the Restaurant List
 *  ------------------------------------------
 *  IN = Last Know Location : Location
 *
 **************************************************************************************************/
public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // For debug
    private static final String TAG = MapViewFragment.class.getSimpleName();

    // Parameter for the construction of the fragment
    private static final String KEY_LAST_KNOW_LOCATION = "KEY_LAST_KNOW_LOCATION";

    // For add Google Map in Fragment
    private SupportMapFragment mMapFragment;

    // ==> For use Api Google Play Service : map
    private GoogleMap mMap;

    // ==> For update UI Location
    private static final float DEFAULT_ZOOM = 16f;

    private double latitude;
    private double longitude;

    private String photoReferences;
    private final CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("utilisateurs");
    private String userSelectedRestaurant;
    private int j = 0;


    private Marker marker;

    @Override
    public boolean onMarkerClick(Marker marker) {
        MarkerObject markerObject = (MarkerObject) marker.getTag();
        Log.d("logloglog", Objects.requireNonNull(markerObject).getName());

        Intent myIntent = new Intent(getContext(), RestaurantActivity.class);

        SharedPreferences mPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("PREFERENCE_KEY_NAME", MODE_PRIVATE);
        mPreferences.edit().putString("image", markerObject.getPhotos()).apply();
        mPreferences.edit().putString("name", markerObject.getName()).apply();
        mPreferences.edit().putString("address", markerObject.getAddress()).apply();
        mPreferences.edit().putString("placeId", markerObject.getPlaceId()).apply();

        getContext().startActivity(myIntent);
        return true;
    }

    // ==> CallBack
    // Interface for ShowSnakeBar
    public interface ShowSnackBarListener {
    }

    public interface UpdateFrag {
        void updatefrag();
    }


    public interface UpdateMapView {
        void updateMapView();
    }


    @SuppressWarnings("WeakerAccess")
    @SuppressLint("ValidFragment")
    public MapViewFragment() {
        // Required empty public constructor
    }

    // ---------------------------------------------------------------------------------------------
    //                               FRAGMENT INSTANTIATION
    // ---------------------------------------------------------------------------------------------
    public static MapViewFragment newInstance(Location lastKnownLocation) {

        // Create new fragment
        MapViewFragment mapViewFragment = new MapViewFragment();

        // Create bundle and add it some data
        Bundle args = new Bundle();
        final Gson gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
        String json = gson.toJson(lastKnownLocation);
        args.putString(KEY_LAST_KNOW_LOCATION, json);

        mapViewFragment.setArguments(args);

        return mapViewFragment;
    }

    // ---------------------------------------------------------------------------------------------
    //                                    ENTRY POINT
    // ---------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");

        // Get data from Bundle (created in method newInstance)
        // Restoring the Date with a Gson Object
        Gson gson = new Gson();
        //==> For use Api Google Play Service : Location
        // _ The geographical location where the device is currently located.
        // _ That is, the last-known location retrieved by the Fused Location Provider.
        Location lastKnownLocation = gson.fromJson(Objects.requireNonNull(getArguments()).getString(KEY_LAST_KNOW_LOCATION, ""), Location.class);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        // Load Restaurant List of the ViewModel
        // Restaurants List
        Map<String, Restaurant> listRestaurants = getRestaurantMapOfTheModel();

        // Configure the Maps Service of Google
        configurePlayServiceMaps();


        ((MainActivity) Objects.requireNonNull(getActivity())).updateApi(() -> {

            SharedPreferences mPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("PREFERENCE_KEY_NAME", MODE_PRIVATE);
            double latitude = Double.parseDouble(Objects.requireNonNull(mPreferences.getString("viewportLatitude", "12")));
            double longitude = Double.parseDouble(Objects.requireNonNull(mPreferences.getString("viewportLongitude", "12")));
            String restaurantClicked = mPreferences.getString("theRestaurantClicked", "Corben House");

            //Defining api call
            Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            Api api = retrofit.create(Api.class);
            Call<PlaceNearBySearch> call = api.getPlaceNearBySearch(latitude + "," + longitude);

            call.enqueue(new Callback<PlaceNearBySearch>() {
                @Override
                public void onResponse(Call<PlaceNearBySearch> call, Response<PlaceNearBySearch> response) {
                    PlaceNearBySearch articles = response.body();
                    List<PlaceNearBySearchResult> theListOfResults = articles.getResults();
                    for (int i = 0; i < theListOfResults.size(); i++) {
                        if (theListOfResults.get(i).getName().equals(restaurantClicked)) {
                            j++;
                        }
                    }
                    if (j == 1) {
                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
                        mMap.moveCamera(center);
                        j = 0;
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setTitle("This restaurant is far from you");
                        alertDialog.setMessage("We recommend you choose another restaurant");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                (dialog, which) -> dialog.dismiss());
                        alertDialog.show();
                    }

                }

                @Override
                public void onFailure(Call<PlaceNearBySearch> call, Throwable t) {

                }
            });

        });


        return rootView;
    }

    // ---------------------------------------------------------------------------------------------
    //                            GOOGLE PLAY SERVICE : MAPS
    // ---------------------------------------------------------------------------------------------
    private void configurePlayServiceMaps() {
        Log.d(TAG, "configurePlayServiceMaps: ");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            mMapFragment.getMapAsync(this);
        }
        // Build the map
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_map_view, mMapFragment).commit();
    }


    /**
     * Manipulates the map when it's available
     **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        mMap = googleMap;

        // Disable 3D Building
        mMap.setBuildingsEnabled(false);

        //
        mMap.setOnMarkerClickListener(this);


        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        Api api = retrofit.create(Api.class);


        Call<PlaceNearBySearch> call = api.getPlaceNearBySearch(latitude + "," + longitude);
       call.enqueue(new Callback<PlaceNearBySearch>() {
            @Override
            public void onResponse(Call<PlaceNearBySearch> call, Response<PlaceNearBySearch> response) {
                PlaceNearBySearch articles = response.body();
                List<PlaceNearBySearchResult> nearbyPlacesList = articles.getResults();


                notebookRef.addSnapshotListener((queryDocumentSnapshots, e) -> {

                    for (PlaceNearBySearchResult nearbyPlace : nearbyPlacesList) {

                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(nearbyPlace.getGeometry().getLocation().getLat(), nearbyPlace.getGeometry().getLocation().getLng()))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_orange)));

                        for (QueryDocumentSnapshot userSnapshot : Objects.requireNonNull(queryDocumentSnapshots)) {

                            userSelectedRestaurant = userSnapshot.getString("restaurantName");

                            if (userSelectedRestaurant != null) {
                                if (userSelectedRestaurant.equals(nearbyPlace.getName())) {
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(nearbyPlace.getGeometry().getLocation().getLat(), nearbyPlace.getGeometry().getLocation().getLng()))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_green)));
                                    break;
                                }
                            }

                            for (int i = 0; i < nearbyPlacesList.size(); i++) {
                                if (nearbyPlacesList.get(i).getName().equals(nearbyPlace.getName())) {
                                    if (nearbyPlacesList.get(i).getPhotos() != null) {
                                        photoReferences = nearbyPlacesList.get(i).getPhotos().get(0).getPhotoReference();
                                    }
                                }
                            }

                            marker.setTag(new MarkerObject(nearbyPlace.getAddress(), nearbyPlace.getName(), "https://maps.googleapis.com/maps/api/place/photo?"
                                    + "maxwidth=2304"
                                    + "&photoreference=" + photoReferences
                                    + "&key=AIzaSyAR3xMop8hS0cX1S3u70q-EC15TBduuDo4", nearbyPlace.getPlaceId()));
                        }
                    }

                });
            }


            @Override
            public void onFailure(Call<PlaceNearBySearch> call, Throwable t) {

            }
        });

        ((MainActivity) Objects.requireNonNull(getActivity())).updateMap(() -> call.clone().enqueue(new Callback<PlaceNearBySearch>() {
            @Override
            public void onResponse(Call<PlaceNearBySearch> call, Response<PlaceNearBySearch> response) {
                PlaceNearBySearch articles = response.body();
                List<PlaceNearBySearchResult> nearbyPlacesList = articles.getResults();

                mMap.clear();
                notebookRef.addSnapshotListener((queryDocumentSnapshots, e) -> {

                    for (PlaceNearBySearchResult nearbyPlace : nearbyPlacesList) {

                        SharedPreferences mPrefs = Objects.requireNonNull(getContext()).getSharedPreferences("SHARED", MODE_PRIVATE);
                        String searchInput = mPrefs.getString("searchInput", "");

                        if (nearbyPlace.getName().contains(Objects.requireNonNull(searchInput))) {

                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(nearbyPlace.getGeometry().getLocation().getLat(), nearbyPlace.getGeometry().getLocation().getLng()))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_orange)));
                        }
                        for (QueryDocumentSnapshot userSnapshot : Objects.requireNonNull(queryDocumentSnapshots)) {

                            userSelectedRestaurant = userSnapshot.getString("restaurantName");
                            if (nearbyPlace.getName().contains(searchInput)) {

                                if (userSelectedRestaurant != null) {
                                    if (userSelectedRestaurant.equals(nearbyPlace.getName())) {
                                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(nearbyPlace.getGeometry().getLocation().getLat(), nearbyPlace.getGeometry().getLocation().getLng()))
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_green)));
                                        break;
                                    }
                                }
                            }

                            for (int i = 0; i < nearbyPlacesList.size(); i++) {
                                if (nearbyPlacesList.get(i).getName().equals(nearbyPlace.getName())) {
                                    if (nearbyPlacesList.get(i).getPhotos() != null) {
                                        photoReferences = nearbyPlacesList.get(i).getPhotos().get(0).getPhotoReference();
                                    }
                                }
                            }

                            marker.setTag(new MarkerObject(nearbyPlace.getAddress(), nearbyPlace.getName(), "https://maps.googleapis.com/maps/api/place/photo?"
                                    + "maxwidth=2304"
                                    + "&photoreference=" + photoReferences
                                    + "&key=AIzaSyAR3xMop8hS0cX1S3u70q-EC15TBduuDo4", nearbyPlace.getPlaceId()));
                        }
                    }

                });
            }


            @Override
            public void onFailure(Call<PlaceNearBySearch> call, Throwable t) {

            }
        }));


        //Cute blue dot
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setTiltGesturesEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        //ZOOM
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(latitude,
                        longitude));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

        // Show current Location
        showCurrentLocation();

    }


    // ---------------------------------------------------------------------------------------------
    //                                       METHODS
    // ---------------------------------------------------------------------------------------------

    /**
     * Method that places the map on a current location
     */

    private void showCurrentLocation() {
        Log.d(TAG, "showCurrentLocation: ");
        Log.d(TAG, "showCurrentLocation: mLastKnownLocation.getLatitude()  = " + latitude);
        Log.d(TAG, "showCurrentLocation: mLastKnownLocation.getLongitude() = " + longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latitude,
                        longitude), DEFAULT_ZOOM));

    }


    /**
     * Method use for CallBacks to the Welcome Activity
     */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        SharedPreferences preferences = context.getSharedPreferences("PREFERENCE_KEY_NAME", 0);
        latitude = Double.valueOf(Objects.requireNonNull(preferences.getString("locationLatitude", "0")));
        longitude = Double.valueOf(Objects.requireNonNull(preferences.getString("locationLongitude", "0")));
        Log.d("ajbxng", String.valueOf(latitude));
        // CallBack for ShowSnackBar
        if (!(context instanceof ShowSnackBarListener)) {
            throw new ClassCastException(context.toString()
                    + " must implement showSnackBarListener");
        }
    }


}