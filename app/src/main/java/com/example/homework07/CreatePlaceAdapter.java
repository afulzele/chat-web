/*
 * Copyright (c) 2019.
 */

package com.example.homework07;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CreatePlaceAdapter extends RecyclerView.Adapter<CreatePlaceAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView search_places_rv_name;
        private ImageView search_places_rv_add_del;

        public ViewHolder(View itemView) {
            super(itemView);
            search_places_rv_name = itemView.findViewById(R.id.search_places_rv_name);
            search_places_rv_add_del = itemView.findViewById(R.id.search_places_rv_add_del);
        }
    }

    private ArrayList<Latlong> mLatlong;
    private Context mc;
    private String this_trip_id;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private View view;

    public CreatePlaceAdapter(Context c, ArrayList<Latlong> latlong, String id) {
        mc = c;
        mLatlong = latlong;
        this_trip_id = id;
    }

    @Override
    public CreatePlaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.search_places_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CreatePlaceAdapter.ViewHolder viewHolder, int position) {
        final Latlong llp = mLatlong.get(position);

        TextView search_places_rv_name = viewHolder.search_places_rv_name;
        final ImageView search_places_rv_add_del = viewHolder.search_places_rv_add_del;

        search_places_rv_name.setText(llp.getTitle());


        db.collection("trips").document(this_trip_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        final Trip trip = documentSnapshot.toObject(Trip.class);
                        ArrayList<Latlong> self_trip_list = trip.getGeometry();
                        ArrayList<String> ids = new ArrayList<>();

                        for (Latlong x : self_trip_list) {
                            ids.add(x.getPlace_id());
                        }
                        if (ids.contains(llp.getPlace_id())) {
                            search_places_rv_add_del.setImageResource(R.drawable.del_red);
                        } else {
                            search_places_rv_add_del.setImageResource(R.drawable.add_green);
                        }
                    }
                });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.collection("trips").document(this_trip_id)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                final Trip trip = documentSnapshot.toObject(Trip.class);
                                ArrayList<Latlong> self_trip_list = trip.getGeometry();
                                ArrayList<String> ids = new ArrayList<>();

                                for (Latlong x : self_trip_list) {
                                    ids.add(x.getPlace_id());
                                }
                                if (ids.contains(llp.getPlace_id())) {

                                    for (int i = 0; i < self_trip_list.size(); i++) {
                                        if (self_trip_list.get(i).getPlace_id().equals(llp.getPlace_id())){
                                            self_trip_list.remove(i);
                                        }
                                    }
                                    Toast.makeText(mc, "Place Removed!!", Toast.LENGTH_SHORT).show();
                                } else {
                                    self_trip_list.add(llp);
                                    Toast.makeText(mc, "Place Added!!", Toast.LENGTH_SHORT).show();
                                }
                                db.collection("trips").document(this_trip_id)
                                        .update("geometry", self_trip_list)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("trip_uid_info", this_trip_id);
                                                ShowTripInfoFragment f_frag = new ShowTripInfoFragment();
                                                f_frag.setArguments(bundle);
                                                FragmentTransaction transaction = ((AppCompatActivity) mc).getSupportFragmentManager().beginTransaction();
                                                transaction.replace(R.id.main_container, f_frag);
                                                transaction.commit();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });


                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return mLatlong.size();
    }
}