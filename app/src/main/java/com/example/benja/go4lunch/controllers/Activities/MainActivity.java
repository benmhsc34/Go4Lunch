package com.example.benja.go4lunch.controllers.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.benja.go4lunch.AlertReceiver;
import com.example.benja.go4lunch.PagerAdapter;
import com.example.benja.go4lunch.PlaceAutocompleteAdapter;
import com.example.benja.go4lunch.R;
import com.example.benja.go4lunch.base.BaseActivity;
import com.example.benja.go4lunch.controllers.fragments.ListRestaurantsViewFragment;
import com.example.benja.go4lunch.controllers.fragments.ListWorkmatesViewFragment;
import com.example.benja.go4lunch.controllers.fragments.MapViewFragment;
import com.example.benja.go4lunch.models.PlaceInfo;
import com.example.benja.go4lunch.views.ScrollableViewPager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, MapViewFragment.ShowSnackBarListener, GoogleApiClient.OnConnectionFailedListener {


    @BindView(R.id.activity_welcome_coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.activity_welcome_bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.activity_welcome_drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.activity_welcome_nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.viewpager)
    ScrollableViewPager mViewPager;


    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;

    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;

    private PlaceInfo mPlace;

    private Location mLastKnownLocation;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private ListRestaurantsViewFragment.UpdateList mUpdateList;

    public void updateList(ListRestaurantsViewFragment.UpdateList listener) {
        mUpdateList = listener;
    }

    private MapViewFragment.UpdateMapView mUpdateMapView ;
    public void updateMap(MapViewFragment.UpdateMapView listener) {
        mUpdateMapView = listener;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.configureDrawerLayout();
        this.configureToolBar();
        this.configureNavigationHeader();
        this.configureNavigationHeader();
        this.configureNavigationView();
        this.configureBottomView();
        this.configureAlarmManager();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("TAG", "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = Objects.requireNonNull(task.getResult()).getToken();

                    SharedPreferences mPrefs = getSharedPreferences("SHARED", MODE_PRIVATE);
                    mPrefs.edit().putString("token", token).apply();

                    // Log and toast
                    Log.d("tokenValue", token);
                });

        PagerAdapter viewPagerAdapter = new PagerAdapter(getSupportFragmentManager(), 3, mLastKnownLocation);

        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.setCanScroll(false);

        SharedPreferences mPreferences = this.getSharedPreferences("PREFERENCE_KEY_NAME", MODE_PRIVATE);
        String locationLatitude = mPreferences.getString("locationLatitude", "43.61076833333333");
        String locationLongitude = mPreferences.getString("locationLongitude", "3.8767149999999995");

        double latMin = Double.parseDouble(Objects.requireNonNull(locationLatitude)) - 0.0012753;
        double latMax = Double.parseDouble(locationLatitude) + 0.0012753;
        double longMin = Double.parseDouble(Objects.requireNonNull(locationLongitude)) - 0.05174298;
        double longMax = Double.parseDouble(locationLongitude) + 0.05174298;

        LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(latMin, longMin), new LatLng(latMax, longMax));


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        //Declaring search edit text
        AutoCompleteTextView searchEditText = mToolbar.findViewById(R.id.myEditText);
        GeoDataClient geoDataClient = Places.getGeoDataClient(this, null);
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, geoDataClient, LAT_LNG_BOUNDS, null);
        searchEditText.setVisibility(View.INVISIBLE);
        searchEditText.setOnItemClickListener(mAutoCompleteClickListener);
        searchEditText.setAdapter(mPlaceAutocompleteAdapter);

        //Make auto-suggestions and keyboard disappear when DONE button clicked
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                searchEditText.dismissDropDown();
                mPreferences.edit().putString("searchInput", searchEditText.getText().toString()).apply();
                mUpdateList.updateList();
                mUpdateMapView.updateMapView();

            }
            return true;
        });

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                SharedPreferences mPreferences = getSharedPreferences("PREFERENCE_KEY_NAME", MODE_PRIVATE);
                mPreferences.edit().putString("locationLatitude", String.valueOf(location.getLatitude())).apply();
                mPreferences.edit().putString("locationLongitude", String.valueOf(location.getLongitude())).apply();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }

        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }
    }

    private void configureAlarmManager() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 47);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else if (item.getItemId() == R.id.activity_main_menu_toolbar_search) {

            //Declaring search edit text
            Toolbar theToolbar = findViewById(R.id.toolbar);
            AutoCompleteTextView searchEditText = theToolbar.findViewById(R.id.myEditText);
            searchEditText.getBackground().mutate().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
            searchEditText.setOnTouchListener((v, event) -> {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (searchEditText.getRight() - searchEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        searchEditText.setText("");
                        return true;
                    }
                }
                return false;
            });



            if (searchEditText.getVisibility() == View.INVISIBLE) {
                searchEditText.setVisibility(VISIBLE);
                item.setIcon(R.drawable.ic_clear_white_24dp);
            } else {
                searchEditText.setVisibility(INVISIBLE);
                searchEditText.setText("");
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                SharedPreferences mPrefs = this.getSharedPreferences("PREFERENCE_KEY_NAME", MODE_PRIVATE);
                mPrefs.edit().putString("searchInput", "").apply();
                searchEditText.clearComposingText();
                item.setIcon(R.drawable.search_white);
                mUpdateList.updateList();
                mUpdateMapView.updateMapView();

            }
        }
        return super.onOptionsItemSelected(item);
        
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }
        }
    }

    protected View getCoordinatorLayout() {
        return mCoordinatorLayout;
    }


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }

    private void configureToolBar() {

        // Change the toolbar Title
        setTitle("I'm Hungry!");
        // Sets the Toolbar
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflate the toolbar  and add it to the Toolbar
        // With one search button
        getMenuInflater().inflate(R.menu.activity_welcome_menu_toolbar, menu);
        return true;
    }

    // ---------------------------------------------------------------------------------------------
    //                                     NAVIGATION DRAWER
    // ---------------------------------------------------------------------------------------------
    // >> CONFIGURATION <-------
    // Configure Drawer Layout and connects him the ToolBar and the NavigationView
    private void configureDrawerLayout() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(Color.parseColor("#FFFFFF"));
        toggle.syncState();
    }

    // Configure NavigationView
    private void configureNavigationView() {

        // Configure NavigationHeader
        configureNavigationHeader();
        // Mark as selected the first menu item
        this.mNavigationView.getMenu().getItem(0).setChecked(true);
        // Subscribes to listen the navigationView
        mNavigationView.setNavigationItemSelectedListener(this);


    }

    // Configure NavigationHeader
    private void configureNavigationHeader() {

        ImageView userPhoto = mNavigationView.getHeaderView(0).findViewById(R.id.navigation_header_user_photo);
        TextView userName = mNavigationView.getHeaderView(0).findViewById(R.id.navigation_header_user_name);
        TextView userEmail = mNavigationView.getHeaderView(0).findViewById(R.id.navigation_header_user_email);

        if (this.getCurrentUser() != null) {

            //Get picture URL from FireBase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(userPhoto);

            }

            //Get email & username from FireBase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail())
                    ? "Email" : this.getCurrentUser().getEmail();
            String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName())
                    ? "Username" : this.getCurrentUser().getDisplayName();


            //Update views with data
            userName.setText(username);
            userEmail.setText(email);
        }
    }

    // ---------------------------------------------------------------------------------------------
    //                                 BOTTOM NAVIGATION VIEW
    // ---------------------------------------------------------------------------------------------
    // Configure the BottomNavigationView
    private void configureBottomView() {

        // Configure the BottomNavigationView Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> updateMainFragment(item.getItemId()));

        // Add three fragments used by the FragmentManager and activates only the Fragment MapViewFragment
        addFragmentsInFragmentManager();
    }


    // >> ACTIONS <-------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle Navigation Item Click
        int id = item.getItemId();

        switch (id) {
            case R.id.activity_welcome_drawer_your_lunch:

                DocumentReference documentReference = FirebaseFirestore.getInstance().document("utilisateurs/" + this.getCurrentUser().getUid());
                documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.getString("restaurantName") != null) {
                        String name = documentSnapshot.getString("restaurantName");
                        String picture = documentSnapshot.getString("pictureRestaurant");
                        String placeId = documentSnapshot.getString("placeId");
                        String address = documentSnapshot.getString("address");

                        SharedPreferences mPreferences = getSharedPreferences("PREFERENCE_KEY_NAME", MODE_PRIVATE);
                        mPreferences.edit().putString("name", name).apply();
                        mPreferences.edit().putString("image", picture).apply();
                        mPreferences.edit().putString("placeId", placeId).apply();
                        mPreferences.edit().putString("address", address).apply();

                        Intent myRestaurantIntent = new Intent(this, RestaurantActivity.class);
                        startActivity(myRestaurantIntent);
                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                        builder1.setMessage("You haven't chosen a restaurant yet");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Ok",
                                (dialog, id1) -> dialog.cancel());

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                });


                break;

            case R.id.activity_welcome_drawer_settings:
                Intent myIntent = new Intent(this, SettingsActivity.class);
                startActivity(myIntent);
                break;
            case R.id.activity_welcome_drawer_logout:
                this.signOutUserFromFirebase();
                break;
            default:
                break;
        }
        // Close menu drawer
        this.mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }


    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted());
    }

    // 3 - Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted() {
        return aVoid -> {
            switch (MainActivity.SIGN_OUT_TASK) {
                case SIGN_OUT_TASK:
                    finish();
                    break;
                case DELETE_USER_TASK:
                    finish();
                    break;
                default:
                    break;

            }
        };
    }


    @Override
    public void onBackPressed() {
        // Close the menu so open and if the touch return is pushed
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    // ---------------------------------------------------------------------------------------------
    //                                      FRAGMENTS
    // ---------------------------------------------------------------------------------------------
    private void addFragmentsInFragmentManager() {

        //Instantiate fragment used by BottomNavigationView
        // Declare three fragment for used with the Bottom Navigation view
        Fragment mapViewFragment = MapViewFragment.newInstance(mLastKnownLocation);
        Fragment listRestaurantsViewFragment = ListRestaurantsViewFragment.newInstance();
        Fragment listWorkmatesViewFragment = ListWorkmatesViewFragment.newInstance();
        //   mListWorkmatesViewFragment = ListWorkmatesViewFragment.newInstance(null);

        // Save the active Fragment
        // Declare an object fragment which will contain the active fragment

        // Obtain SupportFragmentManager Object
        // Declare an object fragment Manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Add the three fragment in fragmentManager and leave active only the fragment MapViewFragment

      /*  mFragmentManager.beginTransaction()
                .replace(R.id.activity_welcome_frame_layout_bottom_navigation, mListRestaurantsViewFragment,"ListViewFragment")
                .commit();

        mFragmentManager.beginTransaction()
                .replace(R.id.activity_welcome_frame_layout_bottom_navigation, mMapViewFragment, "MapViewFragment")
                .commit(); */
    }


    @SuppressWarnings("SameReturnValue")
    private Boolean updateMainFragment(Integer integer) {
        switch (integer) {
            case R.id.action_map_view:
                // Hide the active fragment and activates the fragment mMapViewFragment
                // mFragmentManager.beginTransaction().hide(mActiveFragment).show(mMapViewFragment).commit();
                mViewPager.setCurrentItem(0);
                break;
            case R.id.action_list_view:
                // Hide the active fragment and activates the fragment mListViewFragment
                //   mFragmentManager.beginTransaction().hide(mActiveFragment).show(mListRestaurantsViewFragment).commit();
                //   mActiveFragment = mListRestaurantsViewFragment;
                mViewPager.setCurrentItem(1);

                break;
            case R.id.action_workmates:
                // Hide the active fragment and activates the fragment mWorkmatesFragment
                //   mFragmentManager.beginTransaction().hide(mActiveFragment).show(mMapViewFragment).commit();
                // mActiveFragment = mMapViewFragment;
                mViewPager.setCurrentItem(2);
                break;
        }
   /*     mFragmentManager.beginTransaction()
                .replace(R.id.activity_welcome_frame_layout_bottom_navigation, mActiveFragment, "MapViewFragment")
                .commit(); */
        return true;

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /*
     -------------------------------------  google places autocomplete API suggestions ------------------------------------
   */

    private MapViewFragment.UpdateFrag updatfrag;

    public void updateApi(MapViewFragment.UpdateFrag listener) {
        updatfrag = listener;
    }


    private final ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = places -> {
        if (!places.getStatus().isSuccess()) {
            Log.d("TAG", "Place didn't complete successfully" + places.getStatus().toString());
            places.release();
        } else {
            final Place place = places.get(0);

            try {

                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(Objects.requireNonNull(place.getAddress()).toString());
                mPlace.setAttributions(Objects.requireNonNull(place.getAttributions()).toString());
                mPlace.setLatlng(place.getLatLng());
                mPlace.setRating(place.getRating());
                mPlace.setPhoneNumber(Objects.requireNonNull(place.getPhoneNumber()).toString());
                mPlace.setWebsite(place.getWebsiteUri());

                Log.d("TAG", mPlace.toString());
            } catch (NullPointerException e) {
                Log.e("Error", e.getMessage());
            }

            SharedPreferences mPreferences = this.getSharedPreferences("PREFERENCE_KEY_NAME", MODE_PRIVATE);
            mPreferences.edit().putString("viewportLatitude", Objects.requireNonNull(place.getViewport()).getCenter().latitude + "").apply();
            mPreferences.edit().putString("viewportLongitude", place.getViewport().getCenter().longitude + "").apply();
            mPreferences.edit().putString("theRestaurantClicked", place.getName() + "").apply();
            updatfrag.updatefrag();
            mPreferences.edit().putString("searchInput", place.getName() + "").apply();
            mUpdateList.updateList();
            mUpdateMapView.updateMapView();

            places.release();
        }
    };

    private final AdapterView.OnItemClickListener mAutoCompleteClickListener = (parent, view, position, id) -> {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
        final String placeId = Objects.requireNonNull(item).getPlaceId();

        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
    };


}
