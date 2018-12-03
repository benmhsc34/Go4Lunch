package com.example.benja.go4lunch.controllers.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.benja.go4lunch.R;
import com.example.benja.go4lunch.api.RestaurantHelper;
import com.example.benja.go4lunch.api.UserHelper;
import com.example.benja.go4lunch.base.BaseActivity;
import com.example.benja.go4lunch.models.Restaurant;
import com.example.benja.go4lunch.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class RestaurantCardActivity extends BaseActivity {

    @Override
    protected View getCoordinatorLayout() {
        return null;
    }

    @Override
    public int getFragmentLayout() {
        return 0;
    }
}
