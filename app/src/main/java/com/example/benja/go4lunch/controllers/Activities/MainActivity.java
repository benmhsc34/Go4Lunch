package com.example.benja.go4lunch.controllers.Activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
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
import com.example.benja.go4lunch.controllers.fragments.ListRestaurantsViewFragment;
import com.example.benja.go4lunch.controllers.fragments.MapViewFragment;
import com.example.benja.go4lunch.models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, MapViewFragment.ShowSnackBarListener {



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


    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;

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

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.configureDrawerLayout();
        this.configureToolBar();
        this.configureNavigationHeader();
        this.configureNavigationView();
        this.configureBottomView();
/*
        Circle drawCircle = mMapViewFragment.addCircle(new CircleOptions()
                .center(new LatLng(mLastKnownLocation.getLatitude(),  mLastKnownLocation.Longi()))
                .radius(1000)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));
*/



        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                SharedPreferences mPreferences = getSharedPreferences("PREFERENCE_KEY_NAME", MODE_PRIVATE);
                mPreferences.edit().putString("locationLatitude", String.valueOf(location.getLatitude())).apply();
                mPreferences.edit().putString("locationLongitude", String.valueOf(location.getLongitude())).apply();


                Log.d("locationlocation", String.valueOf(location.getLatitude()));
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
            return;
        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
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

    @Override
    protected View getCoordinatorLayout() {
        return mCoordinatorLayout;
    }


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }

    protected void configureToolBar() {

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
   //     TextView userName = mNavigationView.findViewById(R.id.navigation_header_user_name);
   //     TextView userEmail = mNavigationView.findViewById(R.id.navigation_header_user_email);

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

            Log.d("facebookemail", email);

            //Update views with data
      //      userName.setText(username);
      //      userEmail.setText(email);
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
                // Get additional data from FireStore : restaurantIdentifier of the User choice
                UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
                    User currentUser = documentSnapshot.toObject(User.class);
                    if (currentUser.getRestaurantIdentifier() != null) {
                        // Go to restaurant card
                        //   goToRestaurantActivity(currentUser);
                    }
                });
            case R.id.activity_welcome_drawer_settings:
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
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    // 3 - Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin) {
                    case SIGN_OUT_TASK:
                        finish();
                        break;
                    case DELETE_USER_TASK:
                        finish();
                        break;
                    default:
                        break;

                }
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

    @Override
    public void showSnackBar(String message) {

    }

    // ---------------------------------------------------------------------------------------------
    //                                      FRAGMENTS
    // ---------------------------------------------------------------------------------------------
    private void addFragmentsInFragmentManager() {

        //Instantiate fragment used by BottomNavigationView
        mMapViewFragment = MapViewFragment.newInstance(mLastKnownLocation);
        mListRestaurantsViewFragment = ListRestaurantsViewFragment.newInstance();
        //   mListWorkmatesViewFragment = ListWorkmatesViewFragment.newInstance(null);

        // Save the active Fragment
        mActiveFragment = mMapViewFragment;

        // Obtain SupportFragmentManager Object
        mFragmentManager = getSupportFragmentManager();
        // Add the three fragment in fragmentManager and leave active only the fragment MapViewFragment

      /*  mFragmentManager.beginTransaction()
                .replace(R.id.activity_welcome_frame_layout_bottom_navigation, mListRestaurantsViewFragment,"ListViewFragment")
                .commit();
*/
        mFragmentManager.beginTransaction()
                .replace(R.id.activity_welcome_frame_layout_bottom_navigation, mMapViewFragment, "MapViewFragment")
                .commit();
    }



    private Boolean updateMainFragment(Integer integer) {
        switch (integer) {
            case R.id.action_map_view:
                // Hide the active fragment and activates the fragment mMapViewFragment
                // mFragmentManager.beginTransaction().hide(mActiveFragment).show(mMapViewFragment).commit();
                mActiveFragment = mMapViewFragment;
                break;
            case R.id.action_list_view:
                // Hide the active fragment and activates the fragment mListViewFragment
                //   mFragmentManager.beginTransaction().hide(mActiveFragment).show(mListRestaurantsViewFragment).commit();
                mActiveFragment = mListRestaurantsViewFragment;
                break;
            case R.id.action_workmates:
                // Hide the active fragment and activates the fragment mWorkmatesFragment
                //   mFragmentManager.beginTransaction().hide(mActiveFragment).show(mMapViewFragment).commit();
                mActiveFragment = mMapViewFragment;
                break;
        }
        mFragmentManager.beginTransaction()
                .replace(R.id.activity_welcome_frame_layout_bottom_navigation, mActiveFragment, "MapViewFragment")
                .commit();
        return true;

    }

}
