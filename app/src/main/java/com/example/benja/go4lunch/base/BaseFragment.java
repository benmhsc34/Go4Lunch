package com.example.benja.go4lunch.base;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.benja.go4lunch.models.Go4LunchViewModel;
import com.example.benja.go4lunch.models.Restaurant;

import java.util.Map;
import java.util.Objects;

public class BaseFragment extends Fragment {

    private static final String TAG = BaseFragment.class.getSimpleName();


    // ---------------------------------------------------------------------------------------------
    //                                        VIEW MODEL ACCESS
    // ---------------------------------------------------------------------------------------------
    // Get current restaurant list of the Model
    protected Map<String,Restaurant> getRestaurantMapOfTheModel(){
        Log.d(TAG, "getRestaurantMapOfTheModel: ");

        Go4LunchViewModel model = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(Go4LunchViewModel.class);

        return model.getListRestaurant();
    }
}
