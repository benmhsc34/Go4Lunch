package com.example.benja.go4lunch.controllers.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.benja.go4lunch.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;


public class ListWorkmatesViewFragment extends Fragment {

    private DocumentReference mDocumentReference = FirebaseFirestore.getInstance().document("sampleData/inspiration");

    public ListWorkmatesViewFragment() {
        // Required empty public constructor
    }

    public static ListWorkmatesViewFragment newInstance() {
        ListWorkmatesViewFragment fragment = new ListWorkmatesViewFragment();
        return fragment;
    }

    TextView theTV;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_workmates_view, container, false);

        theTV = view.findViewById(R.id.tvTV);

        return view;
    }

    @Override
    public void onStart() {
        mDocumentReference.get().addOnSuccessListener(documentSnapshot -> {
/*
            if (documentSnapshot.exists()) {
                Log.d("sucess", "SUCCESS" + documentSnapshot.getString("quote"));
                String quoteText = documentSnapshot.getString("quote");
                String authorText = documentSnapshot.getString("author");
                theTV.setText("\"" + quoteText + "\"" + "- by -" + authorText);
            }

        }).addOnFailureListener(e -> Log.d("failure", "EPIC FAIL",e));
*/

            db.collection("utilisateurs")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("SUCESS", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("Failure", "Error getting documents.", task.getException());
                        }
                    });

        });



        super.onStart();
    }

}
