package com.example.benja.go4lunch.views;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.benja.go4lunch.R;
import com.example.benja.go4lunch.controllers.Activities.RestaurantActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class secondAlertReceiver extends BroadcastReceiver {

    Context mContext;
    private final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private final CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("utilisateurs");
    private final ArrayList<Object> coworkersComing = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("receiverReceives", "I receive");

        mContext = context;

        if (Objects.requireNonNull(intent.getAction()).equals("my.second.action.string")) {
            notebookRef.document(currentFirebaseUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Map<String, Object> dataToSave = new HashMap<>();
                    dataToSave.put("restaurantName", null);
                    notebookRef.document(Objects.requireNonNull(currentFirebaseUser).getUid()).set(dataToSave, SetOptions.merge());
                }
            });
        }
    }
}
