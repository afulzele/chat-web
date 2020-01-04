package com.example.homework07;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class EditFragment extends Fragment {

    private OnEditFragmentInteractionListener mListener;

    private ImageView edit_img;
    private EditText edit_fname, edit_lname, edit_uname, edit_pass, edit_cpass;
    private RadioGroup rgg;
    private RadioButton rbm, rbf, rbo;
    private final String[] flag_image = {""};
    private Boolean isThere = false;

    private SharedPreferences sharedpreferences;
    private static final String mypreference = "user";

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private User user;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public EditFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        edit_img = view.findViewById(R.id.edit_img);
        edit_fname = view.findViewById(R.id.edit_fname);
        edit_lname = view.findViewById(R.id.edit_lname);
        edit_uname = view.findViewById(R.id.edit_uname);
        edit_pass = view.findViewById(R.id.edit_pass);
        edit_cpass = view.findViewById(R.id.edit_cpass);
        rgg = view.findViewById(R.id.radio_group_edit_gender);
        rbm = view.findViewById(R.id.radio_button_edit_male);
        rbf = view.findViewById(R.id.radio_button_edit_female);
        rbo = view.findViewById(R.id.radio_button_edit_other);
        Button btn_edit = view.findViewById(R.id.btn_edit);

        sharedpreferences = this.getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {

            db.collection("user").document(account.getId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    isThere = true;
                                    user = new User(document.getData());

                                    edit_fname.setText(user.getF_name());
                                    edit_lname.setText(user.getL_name());

                                    if (user.getGender().toString().toLowerCase().equals("male")) {
                                        rbm.setChecked(true);
                                        flag_image[0] = "Male";
                                    } else if (user.getGender().toString().toLowerCase().equals("female")) {
                                        rbf.setChecked(true);
                                        flag_image[0] = "Female";
                                    } else if (user.getGender().toString().toLowerCase().equals("other")) {
                                        rbo.setChecked(true);
                                        flag_image[0] = "Other";
                                    }

                                    edit_uname.setText(user.getUname());
                                    edit_uname.setEnabled(false);
                                    edit_pass.setEnabled(false);
                                    edit_pass.setVisibility(View.INVISIBLE);
                                    edit_cpass.setEnabled(false);
                                    edit_cpass.setVisibility(View.INVISIBLE);


                                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                    StorageReference storageRef = firebaseStorage.getReference();
                                    storageRef.child("images/" + account.getId() + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Picasso.get().load(uri).transform(new CircleTransform()).into(edit_img);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Picasso.get().load(user.getImg()).transform(new CircleTransform()).into(edit_img);
                                        }
                                    });

                                } else {
                                    final Uri url = account.getPhotoUrl();
                                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                    StorageReference storageRef = firebaseStorage.getReference();
                                    storageRef.child("images/" + account.getId() + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Picasso.get().load(uri).transform(new CircleTransform()).into(edit_img);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Picasso.get().load(url).transform(new CircleTransform()).into(edit_img);
                                        }
                                    });

                                    edit_fname.setText(account.getGivenName());
                                    edit_lname.setText(account.getFamilyName());
                                    edit_uname.setText("");
                                    edit_pass.setEnabled(false);
                                    edit_pass.setVisibility(View.INVISIBLE);
                                    edit_cpass.setEnabled(false);
                                    edit_cpass.setVisibility(View.INVISIBLE);
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
                            user = new User(document.getData());
                            edit_fname.setText(user.getF_name());
                            edit_lname.setText(user.getL_name());

                            if (user.getGender().toString().toLowerCase().equals("male")) {
                                rbm.setChecked(true);
                                flag_image[0] = "Male";
                            } else if (user.getGender().toString().toLowerCase().equals("female")) {
                                rbf.setChecked(true);
                                flag_image[0] = "Female";
                            } else if (user.getGender().toString().toLowerCase().equals("other")) {
                                rbo.setChecked(true);
                                flag_image[0] = "Other";
                            }

                            edit_uname.setText(user.getUname());
                            edit_uname.setEnabled(false);
                            edit_pass.setText(user.getPass());
                            edit_cpass.setText(user.getPass());
                            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                            StorageReference storageRef = firebaseStorage.getReference();
                            storageRef.child(user.getImg()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(uri).transform(new CircleTransform()).into(edit_img);
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

        edit_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        rgg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (i == R.id.radio_button_edit_male) {
                    flag_image[0] = "Male";
                } else if (i == R.id.radio_button_edit_female) {
                    flag_image[0] = "Female";
                } else if (i == R.id.radio_button_edit_other) {
                    flag_image[0] = "Other";
                }
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_img.getDrawable() == null)
                    Toast.makeText(getActivity(), "Upload a Image", Toast.LENGTH_SHORT).show();
                else if (rgg.getCheckedRadioButtonId() == -1)
                    Toast.makeText(getActivity(), "Select a gender", Toast.LENGTH_SHORT).show();
                if (edit_fname.getText().toString().equals("")) edit_fname.setError("Required");
                if (edit_lname.getText().toString().equals("")) edit_lname.setError("Required");
                if (edit_uname.getText().toString().equals("")) edit_uname.setError("Required");
                if (edit_pass.getText().toString().equals("") && edit_pass.getVisibility() == View.VISIBLE)
                    edit_pass.setError("Required");
                if (edit_cpass.getText().toString().equals("") && edit_cpass.getVisibility() == View.VISIBLE)
                    edit_cpass.setError("Required");
                if (!edit_pass.getText().toString().equals(edit_cpass.getText().toString()))
                    edit_cpass.setError("Password doesn't match");
                else if (!edit_fname.getText().equals("") && !edit_lname.getText().equals("") && !edit_uname.getText().toString().equals("")
                        && rgg.getCheckedRadioButtonId() != -1 && edit_img.getDrawable() != null) {

                    final String doc;

                    final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
                    if (account != null) {
                        doc = account.getId();
                        user = new User(edit_fname.getText().toString(), edit_lname.getText().toString(),
                                flag_image[0], doc, account.getPhotoUrl().toString(),
                                "", edit_uname.getText().toString().toLowerCase());
                        final Map<String, Object> userMap = user.toUserHashMap();
                        final Map<String, Object> usersMap = user.toUsersHashMap();

                        if (isThere) {

                            db.collection("user").document(String.valueOf(doc))
                                    .update(userMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                db.collection("users").document(String.valueOf(doc))
                                                        .update(usersMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    Toast.makeText(getActivity(), "Successfully Edited!!", Toast.LENGTH_SHORT).show();
                                                                    mListener.profilePage();

                                                                } else {
                                                                    Toast.makeText(getActivity(), "Unsuccessful!!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                            } else {
                                                Toast.makeText(getActivity(), "Unsuccessful!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            db.collection("user").document(String.valueOf(doc))
                                    .set(userMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                db.collection("users").document(String.valueOf(doc))
                                                        .set(usersMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    Toast.makeText(getActivity(), "Successfully Edited!!", Toast.LENGTH_SHORT).show();
                                                                    mListener.profilePage();

                                                                } else {
                                                                    Toast.makeText(getActivity(), "Unsuccessful!!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                            } else {
                                                Toast.makeText(getActivity(), "Unsuccessful!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }


                    } else if (sharedpreferences.contains("UserKey")) {
                        doc = sharedpreferences.getString("UserKey", "");

                        System.out.println("__________________---------------------"+doc);

                        user = new User(edit_fname.getText().toString(), edit_lname.getText().toString(),
                                flag_image[0], String.valueOf(doc), "images/" + doc + ".png",
                                edit_pass.getText().toString(), edit_uname.getText().toString().toLowerCase());

                        final Map<String, Object> userMap = user.toUserHashMap();
                        final Map<String, Object> usersMap = user.toUsersHashMap();
                        final Map<String, Object> creds = user.creds();

                        db.collection("user").document(String.valueOf(doc))
                                .update(userMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            db.collection("users").document(String.valueOf(doc))
                                                    .update(usersMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                db.collection("credentials").document(user.getUname())
                                                                        .update(creds)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(getActivity(), "Successfully Edited!!", Toast.LENGTH_SHORT).show();
                                                                                    mListener.profilePage();
                                                                                }
                                                                            }
                                                                        });

                                                            } else {
                                                                Toast.makeText(getActivity(), "Unsuccessful!!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                        } else {
                                            Toast.makeText(getActivity(), "Unsuccessful!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }

                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEditFragmentInteractionListener) {
            mListener = (OnEditFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEditFragmentInteractionListener");
        }
    }

    public interface OnEditFragmentInteractionListener {
        void onProfilePageSignOut();
        void profilePage();
    }

    //    TAKE PHOTO USING CAMERA...
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            uploadImage(imageBitmap);
        }
    }

    private void uploadImage(Bitmap photoBitmap) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();

        String doc = "";
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            doc = String.valueOf(account.getId());
        } else if (sharedpreferences.contains("UserKey")) {
            doc = sharedpreferences.getString("UserKey", "");
        }

        String random_name = "images/" + doc + ".png";

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
                    Log.d(TAG, "Image Download URL" + task.getResult());

                    String imageURL = task.getResult().toString();
                    Picasso.get().load(imageURL).transform(new CircleTransform()).into(edit_img);
                }
            }
        });

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                edit_img.setEnabled(false);
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                p_b.setProgress((int) progress);
            }

        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    edit_img.setEnabled(true);
//                    p_b.setProgress(0);
                }
            }
        });
    }
}
