/*
 * Copyright (c) 2019.
 */

package com.example.homework07;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class BeforeCreateTripFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ImageView create_self_trip, before_create_trip_go_home;
    private RecyclerView shows_self_trips, shows_others_trips;

    private SharedPreferences sharedpreferences;
    private static final String mypreference = "user";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public BeforeCreateTripFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_before_create_trip, container, false);

        create_self_trip = view.findViewById(R.id.create_self_trip);
        shows_self_trips = view.findViewById(R.id.shows_self_trips);
        shows_others_trips = view.findViewById(R.id.shows_others_trips);
        before_create_trip_go_home = view.findViewById(R.id.before_create_trip_go_home);

        create_self_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onCreateTrips();
            }
        });

        sharedpreferences = this.getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        String doc = "";

        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            doc = account.getId();
        } else if (sharedpreferences.contains("UserKey")) {
            doc = sharedpreferences.getString("UserKey", "");
        }

        final String finalDoc = doc;


        db.collection("trips")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Trip> t_self = new ArrayList<>();
                            ArrayList<Trip> t_other = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Trip trip = document.toObject(Trip.class);
                                ArrayList<Users> all_friends = trip.getFriends();

                                boolean name2Exists = all_friends.stream().anyMatch(item -> finalDoc.equals(item.getUid()));

                                if (trip.getCreated_by().equals(finalDoc)) {
                                    t_self.add(trip);
                                } else if(name2Exists) {
                                    t_self.add(trip);
                                }
                                else {
                                    t_other.add(trip);
                                }

                            }

                            BeforeCreateTripAdapter adapter = new BeforeCreateTripAdapter(getActivity(), t_self);
                            shows_self_trips.setAdapter(adapter);
                            shows_self_trips.setLayoutManager(new LinearLayoutManager(getActivity()));

                            BeforeCreateTripAdapter adapter2 = new BeforeCreateTripAdapter(getActivity(), t_other);
                            shows_others_trips.setAdapter(adapter2);
                            shows_others_trips.setLayoutManager(new LinearLayoutManager(getActivity()));

                        } else {
                            Toast.makeText(getActivity(), "No Users registered yet", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        before_create_trip_go_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.profilePage();
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        void onCreateTrips();

        void profilePage();
    }
}
