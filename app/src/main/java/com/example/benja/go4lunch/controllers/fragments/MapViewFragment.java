package com.example.benja.go4lunch.controllers.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.benja.go4lunch.base.BaseFragment;
import com.example.benja.go4lunch.models.Go4LunchViewModel;
import com.example.benja.go4lunch.models.Restaurant;
import com.example.benja.go4lunch.R;
import com.example.benja.go4lunch.api.RestaurantHelper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.reflect.Modifier.PRIVATE;


/**************************************************************************************************
 *
 *  FRAGMENT that displays the Restaurant List
 *  ------------------------------------------
 *  IN = Last Know Location : Location
 *
 **************************************************************************************************/
public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

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

    // Restaurants List
    Map<String, Restaurant> mListRestaurants;

    //==> For use Api Google Play Service : Location
    // _ The geographical location where the device is currently located.
    // _ That is, the last-known location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    double latitude;
    double longitude;


    // ==> CallBack
    // Interface for ShowSnakeBar
    public interface ShowSnackBarListener {
        void showSnackBar(String message);
    }

    // Interface Object for use CallBack
    ShowSnackBarListener mListener;


    private MapViewFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");

        // Get data from Bundle (created in method newInstance)
        // Restoring the Date with a Gson Object
        Gson gson = new Gson();
        mLastKnownLocation = gson.fromJson(getArguments().getString(KEY_LAST_KNOW_LOCATION, ""), Location.class);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        // Load Restaurant List of the ViewModel
        mListRestaurants = getRestaurantMapOfTheModel();

        // Configure the Maps Service of Google
        configurePlayServiceMaps();


        return rootView;
    }

    // ---------------------------------------------------------------------------------------------
    //                            GOOGLE PLAY SERVICE : MAPS
    // ---------------------------------------------------------------------------------------------
    public void configurePlayServiceMaps() {
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

        // Activate OnMarkerClickListener
        mMap.setOnInfoWindowClickListener(this);



        //Cute blue dot
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("normallogstatment", "in");

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
        Log.d("normallogstatment", "out");

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

        // Display Restaurants Markers and activate Listen on the participants number
        //  DisplayAndListensMarkers();
    }

    // ---------------------------------------------------------------------------------------------
    //                                       ACTIONS
    // ---------------------------------------------------------------------------------------------
    // Click on Restaurants Map Marker
    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "onMarkerClick: ");

        //Launch Restaurant Card Activity with restaurantIdentifier
   /*     Toolbox.startActivity(getActivity(),RestaurantCardActivity.class,
                RestaurantCardActivity.KEY_DETAILS_RESTAURANT_CARD,
                marker.getTag().toString()); */
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

        // Update Location UI
      //  updateLocationUI();
    }


    /**
     * Creating restaurant markers on the map and activating for each of them
     * a listener to change the number of participants in order to change their color in real time
     */
 /*   protected void DisplayAndListensMarkers() {
        Log.d(TAG, "fireStoreListener: ");

        Set<Map.Entry<String, Restaurant>> setListRestaurant = getRestaurantMapOfTheModel().entrySet();
        Iterator<Map.Entry<String, Restaurant>> it = setListRestaurant.iterator();
        while (it.hasNext()) {
            Map.Entry<String, Restaurant> restaurant = it.next();

            Log.d(TAG, "fireStoreListener: identifier Restaurant = " + restaurant.getValue().getIdentifier());

            // Declare a Marker for current Restaurant
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(restaurant.getValue().getLat()),
                                    Double.parseDouble(restaurant.getValue().getLng())
                            )
                    )
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_orange))
                    .title(restaurant.getValue().getName())
            );
            marker.setTag(restaurant.getValue().getIdentifier());

            listenNbrParticipantsForUpdateMarkers(restaurant.getValue(), marker);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latitude,
                        longitude), DEFAULT_ZOOM));

        // Update Location UI
        MapViewFragment.this.updateLocationUI();
    }

    /**
     * Enables listening to the number of participants in each restaurant
     * to enable marker color change in real time
     */ /*
    public void listenNbrParticipantsForUpdateMarkers(Restaurant restaurant, Marker marker) {

        RestaurantHelper
                .getRestaurantsCollection()
                .document(restaurant.getIdentifier())
                .addSnapshotListener((restaurant1, e) -> {
                    if (e != null) {
                        Log.d(TAG, "fireStoreListener.onEvent: Listen failed: " + e);
                        return;
                    }
                    if (restaurant1 != null) {
                        Log.d(TAG, "fireStoreListener.onEvent: identifier Restaurant = " + restaurant1.get("identifier"));
                        Log.d(TAG, "fireStoreListener.onEvent: nbrParticipants = " + restaurant1.get("nbrParticipants"));
                        if (Integer.parseInt(restaurant1.get("nbrParticipants").toString()) == 0) {
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_orange));
                        } else {
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_green));
                        }
                    }
                });
    }
*/

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
  /*  public void updateLocationUI() {
        Log.d(TAG, "updateLocationUI: ");
        if (mMap != null) {
            try {
                Go4LunchViewModel model = ViewModelProviders.of(getActivity()).get(Go4LunchViewModel.class);
                Log.d(TAG, "updateLocationUI: isLocationPermissionGranted = " + model.isLocationPermissionGranted());
                if (model.isLocationPermissionGranted()) {
                    Log.d(TAG, "updateLocationUI: Permission Granted");
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                } else {
                    Log.d(TAG, "updateLocationUI: Permission not Granted");
                    mMap.setMyLocationEnabled(false);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            } catch (SecurityException e) {
                Log.e("updateLocationUI %s", e.getMessage());
            }
        }
    }

*/
    /**
     * Method use for CallBacks to the Welcome Activity
     */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        SharedPreferences preferences = context.getSharedPreferences("PREFERENCE_KEY_NAME", 0);
        latitude = Double.valueOf(preferences.getString("locationLatitude", "0"));
        longitude = Double.valueOf(preferences.getString("locationLongitude", "0"));
        Log.d("ajbxng", String.valueOf(latitude));
        // CallBack for ShowSnackBar
        if (context instanceof ShowSnackBarListener) {
            mListener = (ShowSnackBarListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement showSnackBarListener");
        }
    }
}
