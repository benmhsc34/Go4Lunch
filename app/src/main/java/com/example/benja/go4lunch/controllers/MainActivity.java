package com.example.benja.go4lunch.controllers;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.benja.go4lunch.R;
import com.example.benja.go4lunch.api.UserHelper;
import com.example.benja.go4lunch.base.BaseActivity;
import com.example.benja.go4lunch.controllers.fragments.MapViewFragment;
import com.example.benja.go4lunch.models.User;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements  NavigationView.OnNavigationItemSelectedListener, MapViewFragment.ShowSnackBarListener {

    @BindView(R.id.activity_welcome_coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.activity_welcome_bottom_navigation) BottomNavigationView bottomNavigationView;
    @BindView(R.id.activity_welcome_drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.activity_welcome_nav_view) NavigationView mNavigationView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    // Declare three fragment for used with the Bottom Navigation view
    private Fragment mMapViewFragment;
    private Fragment mListRestaurantsViewFragment;
    private Fragment mListWorkmatesViewFragment;
    // Declare an object fragment which will contain the active fragment
    private Fragment mActiveFragment;
    // Declare an object fragment Manager
    private FragmentManager mFragmentManager;

    // For determinate Location
    // -------------------------
    // Default location if not permission granted ( Paris )
    private final LatLng mDefaultLocation = new LatLng(48.844304, 2.374377);
    // The geographical location where the device is currently located.
    // That is, the last-known location retrieved by the Fused Location Provider.
    // OR the default Location if permission not Granted
    Location mLastKnownLocation;
    // The entry point to the Fused Location Provider.
    //private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.configureDrawerLayout();
        this.configureToolBar();
        this.configureNavigationHeader();
        this.configureNavigationView();
        this.configureBottomView();
    }

    @Override
    protected View getCoordinatorLayout() {
        return mCoordinatorLayout;
    }


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }

    protected void configureToolBar() {

        // Change the toolbar Tittle
        setTitle("I'm Hungry!");
        // Sets the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

        View navigationHeader = mNavigationView.inflateHeaderView(R.layout.activity_welcome_nav_header);
        ImageView userPhoto = navigationHeader.findViewById(R.id.navigation_header_user_photo);
        TextView userName = navigationHeader.findViewById(R.id.navigation_header_user_name);
        TextView userEmail = navigationHeader.findViewById(R.id.navigation_header_user_email);

        if (this.getCurrentUser() != null){

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
    private void configureBottomView(){

        // Configure the BottomNavigationView Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> updateMainFragment(item.getItemId()));

        // Add three fragments used by the FragmentManager and activates only the Fragment MapViewFragment
        addFragmentsInFragmentManager();
    }
    // >> ACTIONS <-------
    private Boolean updateMainFragment(Integer integer){
        switch (integer) {
            case R.id.action_map_view:
                // Hide the active fragment and activates the fragment mMapViewFragment
                mFragmentManager.beginTransaction().hide(mActiveFragment).show(mMapViewFragment).commit();
                mActiveFragment = mMapViewFragment;
                break;
            case R.id.action_list_view:
                // Hide the active fragment and activates the fragment mListViewFragment
                mFragmentManager.beginTransaction().hide(mActiveFragment).show(mListRestaurantsViewFragment).commit();
                mActiveFragment = mListRestaurantsViewFragment;
                break;
            case R.id.action_workmates:
                // Hide the active fragment and activates the fragment mWorkmatesFragment
                mFragmentManager.beginTransaction().hide(mActiveFragment).show(mListWorkmatesViewFragment).commit();
                mActiveFragment = mListWorkmatesViewFragment;
                break;
        }
        return true;
    }



    // >> ACTIONS <-------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle Navigation Item Click
        int id = item.getItemId();

        switch (id) {
            case R.id.activity_welcome_drawer_your_lunch:
                // Get additional data from FireStore : restaurantIdentifier of the User choice
                UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
                    User currentUser = documentSnapshot.toObject(User.class);
                    if (currentUser.getRestaurantIdentifier() != null ) {
                        // Go to restaurant card
                     //   goToRestaurantActivity(currentUser);
                    }
                });
            case R.id.activity_welcome_drawer_settings:
                break;
            case R.id.activity_welcome_drawer_logout:
         //       this.signOutUserFromFireBase();
                break;
            default:
                break;
        }
        // Close menu drawer
        this.mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
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

    @Override
    public void showSnackBar(String message) {

    }

    // ---------------------------------------------------------------------------------------------
    //                                      FRAGMENTS
    // ---------------------------------------------------------------------------------------------
    private void addFragmentsInFragmentManager(){

        //Instantiate fragment used by BottomNavigationView
        mMapViewFragment = MapViewFragment.newInstance(mLastKnownLocation);
     //   mListRestaurantsViewFragment = ListRestaurantsViewFragment.newInstance();
     //   mListWorkmatesViewFragment = ListWorkmatesViewFragment.newInstance(null);

        // Save the active Fragment
        mActiveFragment = mMapViewFragment;

        // Obtain SupportFragmentManager Object
        mFragmentManager = getSupportFragmentManager();
        // Add the three fragment in fragmentManager and leave active only the fragment MapViewFragment

        mFragmentManager.beginTransaction()
                .add(R.id.activity_welcome_frame_layout_bottom_navigation, mMapViewFragment,"MapViewFragment")
                .commit();
    }
}
