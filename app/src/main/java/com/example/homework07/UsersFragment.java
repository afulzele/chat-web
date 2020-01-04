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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;


public class UsersFragment extends Fragment {

//    private OnUsersPageFragmentInteractionListener mListener;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView r_v;
    private TextView show_users_title;
    private static final String mypreference = "user";


    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);


        r_v = view.findViewById(R.id.recyclerView);
        show_users_title = view.findViewById(R.id.show_users_title);

        SharedPreferences sharedpreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        String doc = "";

        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Objects.requireNonNull(getContext()));
        if (account != null) {
            doc = account.getId();
        } else if (sharedpreferences.contains("UserKey")) {
            doc = sharedpreferences.getString("UserKey", "");
        }

        final String finalDoc = doc;

        Bundle arguments = getArguments();

        if (arguments != null && arguments.containsKey("friends")) {

            assert this.getArguments() != null;
            final String this_trip_uid = this.getArguments().getString("friends");

            assert this_trip_uid != null;
            db.collection("trips").document(this_trip_uid)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Trip trip = documentSnapshot.toObject(Trip.class);
                            assert trip != null;
                            ArrayList<Users> friends = trip.getFriends();
                            UsersAdapter adapter = new UsersAdapter(friends);
                            r_v.setAdapter(adapter);
                            r_v.setLayoutManager(new LinearLayoutManager(getActivity()));

                            show_users_title.setText(trip.getTitle()+"\n Friends list");

                        }

                    });


        } else {

            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<Users> user_list = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Users user = new Users(document.getData());
                                    if (!user.getUid().equals(finalDoc)) user_list.add(user);
                                }

                                show_users_title.setText("Users");

                                UsersAdapter adapter = new UsersAdapter(user_list);
                                r_v.setAdapter(adapter);
                                r_v.setLayoutManager(new LinearLayoutManager(getActivity()));
                            } else {
                                Toast.makeText(getActivity(), "No Users registered yet", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


        return view;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnUsersPageFragmentInteractionListener) {
//            mListener = (OnUsersPageFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnUsersPageFragmentInteractionListener");
//        }
//    }
//
//    public interface OnUsersPageFragmentInteractionListener {
//        void onFragmentInteraction(Uri uri);
//    }
}
