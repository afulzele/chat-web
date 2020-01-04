/*
 * Copyright (c) 2019.
 */

package com.example.homework07;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class EditTripFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ImageView edit_trip_img;
    private EditText edit_trip_name;
    private Button btn_save_edited_trip;
    private ProgressBar edit_trip_pb;
    private String uid;
    private static int RESULT_LOAD_IMAGE = 1;
    private Bitmap currentImage;
    private String image_url;
    private Boolean isNewImage = false;
    private ArrayList<String> checkTrip;

    private SharedPreferences sharedpreferences;
    private static final String mypreference = "user";
    private String doc = "";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public EditTripFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_trip, container, false);

        edit_trip_img = view.findViewById(R.id.edit_trip_img);
        edit_trip_name = view.findViewById(R.id.edit_trip_name);
        btn_save_edited_trip = view.findViewById(R.id.btn_save_edited_trip);
        edit_trip_pb = view.findViewById(R.id.edit_trip_pb);

        edit_trip_pb.setVisibility(View.INVISIBLE);

        sharedpreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        final Bundle arguments = getArguments();

        assert arguments != null;
        final Trip trips = (Trip) arguments.getSerializable("trip_info");
        System.out.println("-----------------------" + trips);
        assert trips != null;
        Picasso.get().load(trips.getCover_image_url()).transform(new CircleTransform()).into(edit_trip_img);
        edit_trip_name.setText(trips.getTitle());
        image_url = trips.getCover_image_url();

        UUID uuid = UUID.randomUUID();
        uid = uuid.toString();

        edit_trip_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        btn_save_edited_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_trip_img.getDrawable() == null)
                    Toast.makeText(getActivity(), "Upload a Image", Toast.LENGTH_SHORT).show();
                if (edit_trip_name.getText().toString().equals(""))
                    edit_trip_name.setError("Required");
                if (edit_trip_img.getDrawable() != null && !edit_trip_name.getText().toString().equals("")) {
                    if (arguments != null && arguments.containsKey("edit_trip_after_edit")) {
                        Trip trip = (Trip) arguments.getSerializable("edit_trip_after_edit");
                        System.out.println("-----trip"+trip);
                    } else {
                        Trip edited_trip = trips;
                        edited_trip.setTitle(edit_trip_name.getText().toString());
                        edited_trip.setCover_image_url(image_url);
                        new GetTripResultFromGoogle().execute(edited_trip);
                    }
                }
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
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        void beforeCreateTrips2();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri photoUri = data.getData();
            if (photoUri != null) {
                try {
                    isNewImage = true;
                    currentImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
                    Picasso.get().load(photoUri).transform(new CircleTransform()).into(edit_trip_img);
                    uploadImage(currentImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadImage(Bitmap photoBitmap) {
        edit_trip_pb.setVisibility(View.VISIBLE);
        btn_save_edited_trip.setEnabled(false);
        btn_save_edited_trip.setVisibility(View.INVISIBLE);
        edit_trip_img.setVisibility(View.INVISIBLE);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();

        String random_name = "trip_images/" + uid + ".png";

        final StorageReference imageRepo = storageReference.child(random_name);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRepo.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: " + "Image Uploaded!!!");
            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                return null;
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imageRepo.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String imageURL = task.getResult().toString();
                    Picasso.get().load(imageURL).transform(new CircleTransform()).into(edit_trip_img);
                    image_url = imageURL;
                }
            }
        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                edit_trip_img.setEnabled(false);
                edit_trip_pb.setVisibility(View.VISIBLE);
                btn_save_edited_trip.setEnabled(false);
                edit_trip_img.setVisibility(View.INVISIBLE);
                btn_save_edited_trip.setVisibility(View.INVISIBLE);
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            }

        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    edit_trip_img.setEnabled(true);
                    edit_trip_pb.setVisibility(View.INVISIBLE);
                    btn_save_edited_trip.setEnabled(true);
                    edit_trip_img.setVisibility(View.VISIBLE);
                    btn_save_edited_trip.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        super.onStop();
        if (currentImage != null) {
            currentImage.recycle();
            currentImage = null;
            System.gc();
        }
    }

    private class GetTripResultFromGoogle extends AsyncTask<Trip, Void, Trip> {

        @Override
        protected Trip doInBackground(Trip... tripogle) {
//            final Trip tripogle1 = tripogle[0];
            final Trip trip = tripogle[0];
            trip.setUid(uid);

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
            if (account != null) {
                doc = String.valueOf(account.getId());
            } else if (sharedpreferences.contains("UserKey")) {
                doc = sharedpreferences.getString("UserKey", "");
            }

            final String finalDoc = doc;

            db.collection("user").document(finalDoc)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    User user = new User(document.getData());

                                    ArrayList<Latlong> locs = new ArrayList<>();
                                    locs.add(trip.getMainloc());
                                    trip.setGeometry(locs);
//                                    if (isNewImage) {
//                                        uploadImage(currentImage);
//
//                                    }

                                    if (document.getData().get("trips") != null) {
                                        checkTrip = (ArrayList<String>) document.getData().get("trips");
                                        checkTrip.add(uid);
                                    } else if (document.getData().get("trips") == null) {
                                        checkTrip = new ArrayList<>();
                                        checkTrip.add(uid);
                                    }

                                    trip.setCreated_by(user.getUid());
                                    final String chat_uid = "chat_" + uid;
                                    trip.setChat_uid(chat_uid);
                                    trip.setLoc_uid("");
                                    trip.setChatroom(new ArrayList<Chats>());
                                    trip.setCover_image_url(image_url);
                                    Users users_new = new Users(user.getF_name() + " " + user.getL_name(), user.getUid());
                                    ArrayList<Users> friends = new ArrayList<>();
                                    friends.add(users_new);

                                    trip.setFriends(friends);



                                    db.collection("user").document(finalDoc)
                                            .update("trips", checkTrip)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    final Map<String, Object> tripMap = trip.tripsMap();

                                                    db.collection("trips").document(uid)
                                                            .set(tripMap)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    db.collection("trips").document(uid)
                                                                            .set(tripMap)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                    Map<String, Object> city = new HashMap<>();
                                                                                    city.put("chat_uid", chat_uid);
                                                                                    city.put("chat_list", new ArrayList<Chats>());

                                                                                    db.collection("chats").document(chat_uid)
                                                                                            .set(city)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {

                                                                                                    edit_trip_pb.setVisibility(View.INVISIBLE);
                                                                                                    btn_save_edited_trip.setEnabled(true);
                                                                                                    edit_trip_img.setVisibility(View.VISIBLE);
                                                                                                    mListener.beforeCreateTrips2();

                                                                                                }
                                                                                            })
                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                                @Override
                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                    Log.w(TAG, "Error writing document", e);
                                                                                                }
                                                                                            });

                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Log.w(TAG, "Error writing document", e);
                                                                                }
                                                                            });

                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.w(TAG, "Error writing document", e);
                                                                }
                                                            });
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

            return trip;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            edit_trip_pb.setVisibility(View.VISIBLE);
            btn_save_edited_trip.setEnabled(false);
            edit_trip_img.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(final Trip trip) {
            super.onPostExecute(trip);

        }


    }


}
