package com.example.benja.go4lunch.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.example.benja.go4lunch.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;

/**
 * Created by Philippe on 12/01/2018.
 */

public abstract class BaseActivity extends AppCompatActivity {

    // --------------------
    // LIFE CYCLE
    // --------------------


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getFragmentLayout());

        // Get & serialise all views
        ButterKnife.bind(this);
    }

    protected abstract int getFragmentLayout();

    // --------------------
    // UI
    // --------------------

    // ---------------------------------------------------------------------------------------------
    //                                        PERMISSIONS
    // ---------------------------------------------------------------------------------------------
    // Return Current User
    protected FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // ---------------------------------------------------------------------------------------------
    //                                       ERROR HANDLER
    // ---------------------------------------------------------------------------------------------
    protected OnFailureListener onFailureListener(){
        return e -> Toast.makeText(BaseActivity.this.getApplicationContext(),
                BaseActivity.this.getString(R.string.error_unknown_error),
                Toast.LENGTH_LONG).show();
    }
}
