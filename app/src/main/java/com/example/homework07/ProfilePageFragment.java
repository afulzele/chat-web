package com.example.homework07;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

public class ProfilePageFragment extends Fragment {

    private OnProfilePageFragmentInteractionListener mListener;

    private ImageView profile_page_img;
    private LinearLayout profile_page_trips, profile_page_users, profile_page_edit, profile_page_signout;
    private TextView profile_page_fname, profile_page_lname, profile_page_gender, profile_page_uname;

    private SharedPreferences sharedpreferences;
    private static final String mypreference = "user";

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProfilePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

        sharedpreferences = this.getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        profile_page_img = view.findViewById(R.id.profile_page_img);
        profile_page_trips = view.findViewById(R.id.profile_page_trips);
        profile_page_users = view.findViewById(R.id.profile_page_users);
        profile_page_edit = view.findViewById(R.id.profile_page_edit);
        profile_page_signout = view.findViewById(R.id.profile_page_signout);
        profile_page_fname = view.findViewById(R.id.profile_page_fname);
        profile_page_lname = view.findViewById(R.id.profile_page_lname);
        profile_page_gender = view.findViewById(R.id.profile_page_gender);
        profile_page_uname = view.findViewById(R.id.profile_page_uname);


        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {

            System.out.println("acount--------------------------------------------" + account.getId());

            db.collection("user").document(account.getId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    final User user = new User(document.getData());


                                    profile_page_fname.setText(user.getF_name());
                                    profile_page_lname.setText(user.getL_name());
                                    profile_page_uname.setText("@" + user.getUname());
                                    profile_page_gender.setText(user.getGender());

                                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                    StorageReference storageRef = firebaseStorage.getReference();
                                    storageRef.child("images/" + account.getId() + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Picasso.get().load(uri).transform(new CircleTransform()).into(profile_page_img);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Picasso.get().load(user.getImg()).transform(new CircleTransform()).into(profile_page_img);
                                        }
                                    });
                                } else {
                                    Uri url = account.getPhotoUrl();
                                    Picasso.get().load(url).transform(new CircleTransform()).into(profile_page_img);
                                    profile_page_fname.setText(account.getGivenName());
                                    profile_page_lname.setText(account.getFamilyName());
                                    profile_page_uname.setText("@" + account.getGivenName() + account.getFamilyName());
                                }
                            }
                        }
                    });


        } else if (sharedpreferences.contains("UserKey")) {
            String doc = sharedpreferences.getString("UserKey", "");
            DocumentReference docRef = db.collection("user").document(doc);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = new User(document.getData());
                            profile_page_fname.setText(user.getF_name());
                            profile_page_lname.setText(user.getL_name());
                            profile_page_gender.setText(user.getGender());
                            profile_page_uname.setText("@" + user.getUname());
                            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                            StorageReference storageRef = firebaseStorage.getReference();
                            storageRef.child(user.getImg()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(uri).transform(new CircleTransform()).into(profile_page_img);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });

                        } else {
                            Toast.makeText(getActivity(), "Profile error. Sign in again!!", Toast.LENGTH_SHORT).show();
                            mListener.onProfilePageSignOut();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        profile_page_trips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {mListener.beforeCreateTrips();
            }
        });

        profile_page_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.userPage();
            }
        });

        profile_page_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.editPage();
            }
        });

        profile_page_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {mListener.onProfilePageSignOut();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProfilePageFragmentInteractionListener) {
            mListener = (OnProfilePageFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnProfilePageFragmentInteractionListener");
        }
    }

    public interface OnProfilePageFragmentInteractionListener {
        void onProfilePageSignOut();
        void editPage();
        void userPage();
        void beforeCreateTrips();
    }


}
