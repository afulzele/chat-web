/*
 * Copyright (c) 2019.
 */

package com.example.homework07;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class BeforeCreateTripAdapter extends RecyclerView.Adapter<BeforeCreateTripAdapter.ViewHolder> {

    private SharedPreferences sharedpreferences;
    private static final String mypreference = "user";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private View view;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView before_trip_rv_name, before_trip_rv_byname;
        public ImageView before_trip_rv_add_del;

        public ViewHolder(View itemView) {
            super(itemView);

            before_trip_rv_name = (TextView) itemView.findViewById(R.id.before_trip_rv_name);
            before_trip_rv_byname = (TextView) itemView.findViewById(R.id.before_trip_rv_byname);
            before_trip_rv_add_del = (ImageView) itemView.findViewById(R.id.before_trip_rv_add_del);
        }
    }

    private ArrayList<Trip> mTrip;
    private Context mc;

    public BeforeCreateTripAdapter(Context c, ArrayList<Trip> trip) {
        mTrip = trip;
        mc = c;
    }

    @Override
    public BeforeCreateTripAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.show_trips_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BeforeCreateTripAdapter.ViewHolder viewHolder, final int position) {
        final Trip main_trip = mTrip.get(position);

        TextView before_trip_rv_name = viewHolder.before_trip_rv_name;
        final TextView before_trip_rv_byname = viewHolder.before_trip_rv_byname;
        final ImageView before_trip_rv_add_del = viewHolder.before_trip_rv_add_del;

        before_trip_rv_name.setText(main_trip.getTitle());

        sharedpreferences = mc.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String doc = "";
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mc);
        if (account != null) doc = account.getId();
        else if (sharedpreferences.contains("UserKey")) doc = sharedpreferences.getString("UserKey", "");
        final String finalDoc = doc;

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("trip_uid_info", mTrip.get(position).getUid());
                ShowTripInfoFragment f_frag = new ShowTripInfoFragment();
                FragmentTransaction transaction = ((AppCompatActivity) mc).getSupportFragmentManager().beginTransaction();
                f_frag.setArguments(bundle);
                transaction.replace(R.id.main_container, f_frag);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        db.collection("trips").document(main_trip.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            final DocumentSnapshot trips_document = task.getResult();
                            if (trips_document.exists()) {
                                final Trip trip = trips_document.toObject(Trip.class);

                                db.collection("users").document(trip.getCreated_by())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {

                                                        Users users = new Users(document.getData());
                                                        before_trip_rv_byname.setText("by " + users.getFull_name());

                                                        ArrayList<Object> friends_list = (ArrayList<Object>) trips_document.getData().get("friends");
                                                        final ArrayList<Users> updated_list = new ArrayList<>();
                                                        for (Object x : friends_list) {
                                                            Map<Users, Object> u = (Map<Users, Object>) x;
                                                            final Users u2 = new Users(String.valueOf(u.get("full_name")), String.valueOf(u.get("uid")));
                                                            updated_list.add(u2);
                                                            if (users.getUid().equals(finalDoc) || u2.getUid().equals(finalDoc)) {
                                                                before_trip_rv_add_del.setImageResource(R.drawable.del_red);
                                                                before_trip_rv_add_del.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {

                                                                        if (trip.getCreated_by().equals(finalDoc)) {

                                                                            db.collection("trips").document(trip.getUid())
                                                                                    .delete()
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            db.collection("user").document(finalDoc)
                                                                                                    .get()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                DocumentSnapshot document = task.getResult();
                                                                                                                if (document.exists()) {
                                                                                                                    if (document.getData().get("trips") != null) {

                                                                                                                        ArrayList<String> updateTrip = (ArrayList<String>) document.getData().get("trips");
                                                                                                                        for (int i = 0; i < updateTrip.size(); i++) {
                                                                                                                            String t = updateTrip.get(i);
                                                                                                                            if (t.equals(trip.getUid())) {
                                                                                                                                updateTrip.remove(i);
                                                                                                                            }
                                                                                                                        }

                                                                                                                        db.collection("user").document(finalDoc)
                                                                                                                                .update("trips", updateTrip)
                                                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                                                        Toast.makeText(mc, "Deleted", Toast.LENGTH_SHORT).show();
                                                                                                                                        mTrip.remove(position);
                                                                                                                                        notifyItemRemoved(position);
                                                                                                                                        notifyItemRangeChanged(position, mTrip.size());
                                                                                                                                        notifyDataSetChanged();
                                                                                                                                        shiftToBeforePage();
                                                                                                                                    }
                                                                                                                                })
                                                                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                                                                    @Override
                                                                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                                                                    }
                                                                                                                                });
                                                                                                                    }

                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    })
                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                        }
                                                                                    });
                                                                        } else {
                                                                            updated_list.remove(u2);
                                                                            db.collection("trips").document(trip.getUid())
                                                                                    .update("friends", updated_list)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            shiftToBeforePage();
                                                                                        }
                                                                                    })
                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                        }
                                                                                    });
                                                                        }

                                                                    }
                                                                });
                                                            }
                                                            else {
                                                                before_trip_rv_add_del.setImageResource(R.drawable.add_green);
                                                                before_trip_rv_add_del.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        db.collection("trips").document(main_trip.getUid())
                                                                                .get()
                                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            DocumentSnapshot get_trips_to_add = task.getResult();
                                                                                            if (get_trips_to_add.exists()) {
                                                                                                Trip get_trip_to_add = get_trips_to_add.toObject(Trip.class);
                                                                                                ArrayList<Users> friends_list = get_trip_to_add.getFriends();
                                                                                                db.collection("users").document(finalDoc)
                                                                                                        .get()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                if (task.isSuccessful()) {
                                                                                                                    DocumentSnapshot document = task.getResult();
                                                                                                                    if (document.exists()) {

                                                                                                                        Users users = new Users(document.getData());
                                                                                                                        updated_list.add(users);

                                                                                                                        db.collection("trips").document(trip.getUid())
                                                                                                                                .update("friends", updated_list)
                                                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                                                        mTrip.remove(position);
                                                                                                                                        notifyItemRemoved(position);
                                                                                                                                        notifyItemRangeChanged(position, mTrip.size());
                                                                                                                                        notifyDataSetChanged();
                                                                                                                                        shiftToBeforePage();
                                                                                                                                    }
                                                                                                                                })
                                                                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                                                                    @Override
                                                                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                                                                    }
                                                                                                                                });

                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        });

                                                                                            }
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                });
                                                            }

                                                        }
                                                    }
                                                }
                                            }
                                        });


                            }
                        }
                    }
                });


    }

    @Override
    public int getItemCount() {
        return mTrip.size();
    }

    public void shiftToBeforePage() {
        BeforeCreateTripFragment f_frag = new BeforeCreateTripFragment();
        FragmentTransaction transaction = ((AppCompatActivity) mc).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, f_frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

