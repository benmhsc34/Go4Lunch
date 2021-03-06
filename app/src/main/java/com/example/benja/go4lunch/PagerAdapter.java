package com.example.benja.go4lunch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.benja.go4lunch.controllers.fragments.ListRestaurantsViewFragment;
import com.example.benja.go4lunch.controllers.fragments.ListWorkmatesViewFragment;
import com.example.benja.go4lunch.controllers.fragments.MapViewFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private final int mNoOfTabs;
    private final android.location.Location lastKnownLocation;
    public PagerAdapter(FragmentManager fm, int noOfTabs, android.location.Location lastKnownLocation) {
        super(fm);
        this.mNoOfTabs = noOfTabs;
        this.lastKnownLocation = lastKnownLocation;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return MapViewFragment.newInstance(lastKnownLocation);
            case 1:
                return ListRestaurantsViewFragment.newInstance();
            case 2:
                return ListWorkmatesViewFragment.newInstance();
            default:
                return MapViewFragment.newInstance(lastKnownLocation);

        }
    }


    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
