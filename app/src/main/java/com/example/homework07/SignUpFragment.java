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
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class SignUpFragment extends Fragment {

    private OnSignUpFragmentInteractionListener mListener;

    private ImageView signup_img;
    private EditText signup_fname, signup_lname, signup_pass, signup_cpass, signup_uname;
    private RadioGroup rgg;
    private RadioButton rbm, rbf, rbo;
    private Button btn_signup;
    private TextView signup_login;
    private final String[] flag_image = {""};

    private int uniqueIdGenerated = 0;

    private SharedPreferences sharedpreferences;
    private static final String mypreference = "user";

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap bitmapUpload = null;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        signup_img = view.findViewById(R.id.signup_img);
        signup_fname = view.findViewById(R.id.signup_fname);
        signup_lname = view.findViewById(R.id.signup_lname);
        signup_uname = view.findViewById(R.id.signup_uname);
        rgg = view.findViewById(R.id.radio_group_gender);
        rbm = view.findViewById(R.id.radio_button_male);
        rbf = view.findViewById(R.id.radio_button_female);
        rbo = view.findViewById(R.id.radio_button_other);
        signup_pass = view.findViewById(R.id.signup_pass);
        signup_cpass = view.findViewById(R.id.signup_cpass);
        btn_signup = view.findViewById(R.id.btn_signup);
        signup_login = view.findViewById(R.id.signup_login);

        final int r = MainActivity.generateRandom(99999999);
        sharedpreferences = this.getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        uniqueIdGenerated = r;

        signup_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(r);
            }
        });

        rgg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radio_button_male) {
                    flag_image[0] = "Male";
                } else if (i == R.id.radio_button_female) {
                    flag_image[0] = "Female";
                } else if (i == R.id.radio_button_other) {
                    flag_image[0] = "Other";
                }
            }
        });

//        signup_fname.setText("Abhishek");
//        signup_lname.setText("Fulzele");
//        signup_cpass.setText("123456");
//        signup_pass.setText("123456");
//        signup_uname.setText("bune_nnuma");

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (signup_img.getDrawable() == null)
                    Toast.makeText(getActivity(), "Upload a Image", Toast.LENGTH_SHORT).show();
                else if (rgg.getCheckedRadioButtonId() == -1)
                    Toast.makeText(getActivity(), "Select a gender", Toast.LENGTH_SHORT).show();
                if (signup_fname.getText().toString().equals("")) signup_fname.setError("Required");
                if (signup_lname.getText().toString().equals("")) signup_lname.setError("Required");
                if (signup_uname.getText().toString().equals("")) signup_uname.setError("Required");
                if (signup_pass.getText().toString().equals("")) signup_pass.setError("Required");
                if (signup_cpass.getText().toString().equals("")) signup_cpass.setError("Required");
                if (!signup_pass.getText().toString().equals(signup_cpass.getText().toString()))
                    signup_cpass.setError("Required");
                if (!signup_fname.getText().equals("") && !signup_lname.getText().equals("") && !signup_uname.getText().toString().equals("")
                        && rgg.getCheckedRadioButtonId() != -1 && signup_img.getDrawable() != null
                        && !signup_pass.getText().toString().equals("") && !signup_cpass.getText().toString().equals("")
                        && signup_pass.getText().toString().equals(signup_cpass.getText().toString())) {

                    final User user = new User(signup_fname.getText().toString(), signup_lname.getText().toString(),
                            flag_image[0], String.valueOf(uniqueIdGenerated), "images/" + uniqueIdGenerated + ".png",
                            signup_pass.getText().toString(), signup_uname.getText().toString().toLowerCase());
                    final Map<String, Object> userMap = user.toUserHashMap();
                    final Map<String, Object> usersMap = user.toUsersHashMap();
                    final Map<String, Object> creds = user.creds();


                    db.collection("user").document(String.valueOf(r))
                            .set(userMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        db.collection("users").document(String.valueOf(r))
                                                .set(usersMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                                                            prefsEditor.putString("UserKey", String.valueOf(uniqueIdGenerated));
                                                            prefsEditor.commit();
                                                            db.collection("credentials").document(user.getUname())
                                                                    .set(creds)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
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
        });

        signup_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.loginPage();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSignUpFragmentInteractionListener) {
            mListener = (OnSignUpFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSignUpFragmentInteractionListener");
        }
    }

    public interface OnSignUpFragmentInteractionListener {
        void loginPage();

        void profilePage();
    }

    //    TAKE PHOTO USING CAMERA...
    private void dispatchTakePictureIntent(int uid) {
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
            bitmapUpload = imageBitmap;
            uploadImage(bitmapUpload);
        }
    }

    private void uploadImage(Bitmap photoBitmap) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();

        String random_name = "images/" + uniqueIdGenerated + ".png";

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
                    Picasso.get().load(imageURL).transform(new CircleTransform()).into(signup_img);
                }
            }
        });

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                signup_img.setEnabled(false);
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                p_b.setProgress((int) progress);
            }

        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    signup_img.setEnabled(true);
//                    p_b.setProgress(0);
                }
            }
        });
    }
}
