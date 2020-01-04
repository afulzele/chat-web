/*
 * Copyright (c) 2019.
 */

package com.example.homework07;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ShowTripInfoFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView trip_page_img, show_trip_info_add_place_img, show_trip_info_chatrroom_img;
    private TextView trip_page_created_by, trip_page_name, show_trip_info_add_place_text, show_trip_info_chatrroom_text;
    private LinearLayout trip_page_chatroom, trip_page_add_place, trip_page_map, trip_page_friends, trip_page_cover_home, trip_page_cover_edit;
    private CardView trip_page_chatroom1, trip_page_add_place1;


    private ProgressBar trip_page_pb;
    private SharedPreferences sharedpreferences;
    private static final String mypreference = "user";


    public ShowTripInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_trip_info, container, false);

        trip_page_cover_edit = view.findViewById(R.id.trip_page_cover_edit);
        trip_page_cover_home = view.findViewById(R.id.trip_page_cover_home);
        trip_page_img = view.findViewById(R.id.trip_page_img);
        trip_page_created_by = view.findViewById(R.id.trip_page_created_by);
        trip_page_name = view.findViewById(R.id.trip_page_name);
        trip_page_chatroom = view.findViewById(R.id.trip_page_chatroom);
        trip_page_add_place = view.findViewById(R.id.trip_page_add_place);
        trip_page_map = view.findViewById(R.id.trip_page_map);
        trip_page_friends = view.findViewById(R.id.trip_page_friends);
        show_trip_info_add_place_text = view.findViewById(R.id.show_trip_info_add_place_text);
        show_trip_info_chatrroom_text = view.findViewById(R.id.show_trip_info_chatrroom_text);
        trip_page_pb = view.findViewById(R.id.trip_page_pb);
        trip_page_chatroom1 = view.findViewById(R.id.trip_page_chatroom1);
        trip_page_add_place1 = view.findViewById(R.id.trip_page_add_place1);
        show_trip_info_add_place_img = view.findViewById(R.id.show_trip_info_add_place_img);
        show_trip_info_chatrroom_img = view.findViewById(R.id.show_trip_info_chatrroom_img);

        trip_page_pb.setVisibility(View.VISIBLE);

        sharedpreferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        String doc = "";

        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account != null) {
            doc = account.getId();
        } else if (sharedpreferences.contains("UserKey")) {
            doc = sharedpreferences.getString("UserKey", "");
        }

        final String finalDoc = doc;

        final String[] this_trip_created_by = {""};

        assert this.getArguments() != null;
        final String this_trip_uid = this.getArguments().getString("trip_uid_info");

        final Latlong latlng = new Latlong();
        final Trip[] trip = {new Trip()};

        db.collection("trips").document(this_trip_uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        trip[0] = documentSnapshot.toObject(Trip.class);
                        String created_by = (String) trip[0].getCreated_by();
                        latlng.setLatitude(trip[0].getMainloc().getLatitude());
                        latlng.setLongitude(trip[0].getMainloc().getLongitude());
                        latlng.setPlace_id(trip[0].getMainloc().getPlace_id());
                        latlng.setTitle(trip[0].getMainloc().getTitle());

                        trip_page_name.setText(trip[0].getTitle());
                        this_trip_created_by[0] = created_by;

                        ArrayList<Users> friends = trip[0].getFriends();
                        ArrayList<String> ids = new ArrayList<>();
                        for (Users x : friends) {
                            ids.add(x.getUid());
                        }

                        if (!ids.contains(finalDoc)) {
                            trip_page_chatroom.setEnabled(false);
                            trip_page_add_place.setEnabled(false);
                            show_trip_info_add_place_img.setImageAlpha(120);
                            show_trip_info_chatrroom_img.setImageAlpha(120);
                            show_trip_info_add_place_text.setTextColor(Color.parseColor("#c2c5cc"));
                            show_trip_info_chatrroom_text.setTextColor(Color.parseColor("#c2c5cc"));
                        }

                        db.collection("users").document(created_by)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            final DocumentSnapshot users_document = task.getResult();
                                            if (users_document.exists()) {
                                                String full_name = (String) users_document.getData().get("full_name");
                                                Picasso.get().load(trip[0].getCover_image_url()).into(trip_page_img);
                                                trip_page_created_by.setText("Created by " + full_name);
                                                trip_page_pb.setVisibility(View.INVISIBLE);

                                            }
                                        }
                                    }
                                });

                    }
                });


        trip_page_cover_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.profilePage();
            }
        });

        trip_page_cover_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("edit_trip_after_edit", trip[0]);
//                ShowTripInfoFragment f_frag = new ShowTripInfoFragment();
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                f_frag.setArguments(bundle);
//                transaction.replace(R.id.main_container, f_frag);
//                transaction.addToBackStack(null);
//                transaction.commit();
            }
        });

        trip_page_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("this_trip_uid", this_trip_uid);
                MapFragment f_frag = new MapFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                f_frag.setArguments(bundle);
                transaction.replace(R.id.main_container, f_frag);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        trip_page_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("friends", this_trip_uid);
                UsersFragment f_frag = new UsersFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                f_frag.setArguments(bundle);
                transaction.replace(R.id.main_container, f_frag);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        trip_page_chatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("trip_info", trip[0]);
                Chat2Fragment f_frag = new Chat2Fragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                f_frag.setArguments(bundle);
                transaction.replace(R.id.main_container, f_frag);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        trip_page_add_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("loc_id", latlng);
                bundle.putString("this_trip_id", this_trip_uid);
                CreateTripFragment f_frag = new CreateTripFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                f_frag.setArguments(bundle);
                transaction.replace(R.id.main_container, f_frag);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


//        trip_page_layout_del.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                db.collection("trips").document(this_trip_uid)
//                        .get()
//                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                            @Override
//                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                Trip trip = documentSnapshot.toObject(Trip.class);
//
//                                ArrayList<Users> friends = trip.getFriends();
//                                ArrayList<Users> new_friends = new ArrayList<>();
//
//                                for (int i = 0; i < friends.size() - 1; i++) {
//                                    if (!finalDoc.equals(friends.get(i).getUid())) {
//                                        new_friends.add(friends.get(i));
//                                    }
//                                }
//
//                                db.collection("trips").document(this_trip_uid)
//                                        .update("friends", new_friends)
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                Toast.makeText(getActivity(), "Removed", Toast.LENGTH_SHORT).show();
//                                                mListener.beforeCreateTrips2();
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                            }
//                                        });
//
//                            }
//                        });
//            }
//        });


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        void beforeCreateTrips2();

        void profilePage();
    }
}
