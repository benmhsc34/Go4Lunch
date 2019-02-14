package com.example.benja.go4lunch.controllers.Activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.benja.go4lunch.R;
import com.example.benja.go4lunch.api.UserHelper;
import com.example.benja.go4lunch.base.BaseActivity;
import com.example.benja.go4lunch.models.UsersModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class LoginActivity extends BaseActivity {

    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    protected Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }


    @Override
    protected View getCoordinatorLayout() {
        return null;
    }

    @Override
    public int getFragmentLayout() { return R.layout.activity_login; }


    //FOR DATA
    // 1 - Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;

    // --------------------
    // ACTIONS
    // --------------------


    @Override
    protected void onResume() {
        super.onResume();
        // 5 - Update UI when activity is resuming
    //    this.updateUIWhenResuming();

        FirebaseApp.initializeApp(this);
     //   Twitter.initialize(this);

        Button connectionButton = findViewById(R.id.main_activity_button_login);

        connectionButton.setOnClickListener(view -> {
            // 4 - Start appropriate activity
            if (isCurrentUserLogged()){
                Log.d("userislogged","true");
                startWelcomeActivity();
            } else {
                Log.d("userislogged","false");
                startSignInActivity();
            }
        });

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.benja.go4lunch",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }



    // --------------------
    // NAVIGATION
    // --------------------

    // 2 - Launch Sign-In Activity
    private void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(), //EMAIL
                                        new AuthUI.IdpConfig.GoogleBuilder().build(), // SUPPORT GOOGLE
                                        new AuthUI.IdpConfig.TwitterBuilder().build(), //TWITTER
                                        new AuthUI.IdpConfig.FacebookBuilder().build())) // FACEBOOK
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.mainlogo)
                        .build(),
                RC_SIGN_IN);

    }
/*
    // 3 - Launching Profile Activity
    private void startProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
*/
    //FOR DESIGN
    // 1 - Get Coordinator Layout
    @BindView(R.id.main_activity_coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 4 - Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }


    // --------------------
    // UI
    // --------------------

    //Show Snack Bar with a message
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }
/*
    //Update UI when activity is resuming
    private void updateUIWhenResuming(){
        this.buttonLogin.setText(this.isCurrentUserLogged() ? getString(R.string.button_login_text_logged) : getString(R.string.button_login_text_not_logged));
    }
*/
    // --------------------
    // UTILS
    // --------------------

    // 3 - Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                this.createUserInFirestore();
                showSnackBar(this.coordinatorLayout, getString(R.string.connection_succeed));
                startWelcomeActivity();
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }

    // --------------------
    // REST REQUEST
    // --------------------

    // 1 - Http request that create user in firestore
    private void createUserInFirestore(){

        if (this.getCurrentUser() != null){

            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();

            UserHelper.createUser(uid, username, urlPicture).addOnFailureListener(this.onFailureListener());
        }
    }

    private void startWelcomeActivity(){

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference mDocRef = FirebaseFirestore.getInstance().document("utilisateurs/" + currentFirebaseUser.getUid());

        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put("userName", currentFirebaseUser.getDisplayName());
        dataToSave.put("email", currentFirebaseUser.getEmail());
        mDocRef.set(dataToSave, SetOptions.merge()).addOnSuccessListener(aVoid -> Log.d("success", "Files have successfully been sent to the Firestore")).addOnFailureListener(e -> Log.d("failure", "Files have failed", e));

        Map<String, Object> imageToSave = new HashMap<>();
        imageToSave.put("picture", currentFirebaseUser.getPhotoUrl().toString());
        mDocRef.set(imageToSave, SetOptions.merge());

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

}