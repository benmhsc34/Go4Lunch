package com.example.benja.go4lunch.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.benja.go4lunch.R;
import com.example.benja.go4lunch.models.UsersModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class ListWorkmatesViewFragment extends Fragment {

 //   private DocumentReference mDocumentReference = FirebaseFirestore.getInstance().document("sampleData/inspiration");

    public ListWorkmatesViewFragment() {
        // Required empty public constructor
    }

    public static ListWorkmatesViewFragment newInstance() {
        ListWorkmatesViewFragment fragment = new ListWorkmatesViewFragment();
        return fragment;
    }



    private FirestoreRecyclerAdapter<UsersModel, UsersViewHolder> adapter;

    private class UsersViewHolder extends RecyclerView.ViewHolder {
        private View view;

        UsersViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        void setUserName(String userName) {
            TextView textView = view.findViewById(R.id.userNameTV);
            textView.setText(userName);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_list_workmates_view, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("utilisateurs")
                .orderBy("userName", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<UsersModel> options = new FirestoreRecyclerOptions.Builder<UsersModel>()
                .setQuery(query, UsersModel.class)
                .build();



        adapter = new FirestoreRecyclerAdapter<UsersModel, ListWorkmatesViewFragment.UsersViewHolder>(options) {

            @NonNull
            @Override
            public ListWorkmatesViewFragment.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_layout, viewGroup, false);

                return new ListWorkmatesViewFragment.UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ListWorkmatesViewFragment.UsersViewHolder holder, int position, @NonNull UsersModel model) {

                holder.setUserName(model.getUserName());

            }

        };
        recyclerView.setAdapter(adapter);


        return view;
    }





  /*  @Override
    public void onStart() {
        mDocumentReference.get().addOnSuccessListener(documentSnapshot -> {

            if (documentSnapshot.exists()) {
                Log.d("sucess", "SUCCESS" + documentSnapshot.getString("quote"));
                String quoteText = documentSnapshot.getString("quote");
                String authorText = documentSnapshot.getString("author");
                theTV.setText("\"" + quoteText + "\"" + "- by -" + authorText);
            }

        }).addOnFailureListener(e -> Log.d("failure", "EPIC FAIL",e));




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
*/
}
