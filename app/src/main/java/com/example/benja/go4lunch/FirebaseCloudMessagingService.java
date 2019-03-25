package com.example.benja.go4lunch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.benja.go4lunch.controllers.Activities.MainActivity;
import com.example.benja.go4lunch.controllers.Activities.RestaurantActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;


public class FirebaseCloudMessagingService extends FirebaseMessagingService  {

    private final CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("utilisateurs");
    private final ArrayList coworkersComing = new ArrayList();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        showNotification();
    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIF_CHANNEL_ID = "com.example.benja.go4lunch.test";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIF_CHANNEL_ID, "notification", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Here I say the resto and get the users going as well YAYIII");
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        DocumentReference documentReference = FirebaseFirestore.getInstance().document("utilisateurs/" + this.getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(documentSnapshot -> notebookRef.addSnapshotListener((queryDocumentSnapshots, e) -> {

            for (QueryDocumentSnapshot userSnapshot : queryDocumentSnapshots) {
                String whichResto = (String) userSnapshot.get("restaurantName");
                if (whichResto != null) {
                    if (whichResto.equals(documentSnapshot.get("restaurantName"))) {
                        coworkersComing.add(userSnapshot.get("userName"));
                    }
                }
            }
            if (documentSnapshot.get("restaurantName") != null) {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID);
                notificationBuilder.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle("You are eating at " + documentSnapshot.get("restaurantName"))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentText("with " + coworkersComing.toString().replace("[", "").replace("]", ""));

                notificationBuilder.setContentIntent(PendingIntent.getActivity(this, 0,
                        new Intent(this, RestaurantActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));

                notificationManager.notify(new Random().nextInt(), notificationBuilder.build());


            }
        }));
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String s) {
        Log.d("whatIsTheToken", s);

        SharedPreferences mPrefs = getSharedPreferences("SHARED", MODE_PRIVATE);
        mPrefs.edit().putString("token", s).apply();
    }

    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

}
