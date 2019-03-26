package com.example.benja.go4lunch;

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

import com.example.benja.go4lunch.controllers.Activities.RestaurantActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class AlertReceiver extends BroadcastReceiver {

    Context mContext;
    private final CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("utilisateurs");
    private final ArrayList coworkersComing = new ArrayList();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("receiverReceives", "I receive");
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        mContext = context;
        showNotification();
    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIF_CHANNEL_ID = "com.example.benja.go4lunch.test";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIF_CHANNEL_ID, "notification", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Here I say the resto and get the users going as well YAYIII");
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        DocumentReference documentReference = FirebaseFirestore.getInstance().document("utilisateurs/" + getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(documentSnapshot -> notebookRef.addSnapshotListener((queryDocumentSnapshots, e) -> {

            for (QueryDocumentSnapshot userSnapshot : Objects.requireNonNull(queryDocumentSnapshots)) {
                String whichResto = (String) userSnapshot.get("restaurantName");
                if (whichResto != null) {
                    if (whichResto.equals(documentSnapshot.get("restaurantName")) && userSnapshot.get("email") != getCurrentUser().getEmail()) {
                        coworkersComing.add(userSnapshot.get("userName"));
                    }
                }
            }
            if (documentSnapshot.get("restaurantName") != null) {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, NOTIF_CHANNEL_ID);
                notificationBuilder.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle("You are eating at " + documentSnapshot.get("restaurantName"))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentText("with " + coworkersComing.toString().replace("[", "").replace("]", ""));

                notificationBuilder.setContentIntent(PendingIntent.getActivity(mContext, 0,
                        new Intent(mContext, RestaurantActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));

                notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
            }
        }));
    }

    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

}
