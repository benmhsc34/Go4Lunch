package com.example.benja.go4lunch.controllers.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.benja.go4lunch.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class SettingsActivity extends AppCompatActivity {

    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document("sampleData/inspiration");

    @Override
    protected void onStart() {
        super.onStart();
        mDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                TextView display = findViewById(R.id.TV);

                if (documentSnapshot.exists()) {
                    String quote = documentSnapshot.getString("quote");
                    String author = documentSnapshot.getString("author");

                    display.setText(quote + " -- " + author);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Button saveButton = findViewById(R.id.saveButton);
        Button fetchButton = findViewById(R.id.fetchButton);

        saveButton.setOnClickListener(view -> saveQuote());

        fetchButton.setOnClickListener(view -> fetchQuote());


    }

    public void saveQuote() {

        EditText quoteView = findViewById(R.id.quote);
        EditText authorView = findViewById(R.id.author);

        String quote = quoteView.getText().toString();
        String author = authorView.getText().toString();

        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put("quote", quote);
        dataToSave.put("author", author);
        mDocRef.set(dataToSave).addOnSuccessListener(aVoid -> Log.d("sucess", "Files have successfully been sent to the Firestore")).addOnFailureListener(e -> Log.d("failure", "Files have failed", e));
    }

    public void fetchQuote() {
        TextView display = findViewById(R.id.TV);

        mDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String quote = documentSnapshot.getString("quote");
                String author = documentSnapshot.getString("author");

                display.setText(quote + " -- " + author);
            }
        });

    }
}
