package com.example.homework07;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {

    private OnLoginFragmentInteractionListener mListener;
    private EditText login_username, login_password;
    private Button btn_login;
    private TextView login_signup;
    private SignInButton btn_login_google;

    private SharedPreferences sharedpreferences;
    private static final String mypreference = "user";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        login_username = view.findViewById(R.id.login_username);
        login_password = view.findViewById(R.id.login_password);
        btn_login = view.findViewById(R.id.btn_login);
        login_signup = view.findViewById(R.id.signup_login);
        btn_login_google = view.findViewById(R.id.btn_login_google);

        sharedpreferences = this.getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (login_username.getText().toString().equals("")){
                    login_username.setError("Required");
                }
                if (login_password.getText().toString().equals("")){
                    login_password.setError("Required");
                }
                if (!login_username.getText().toString().equals("") && !login_password.getText().toString().equals("")) {

                    db.collection("credentials").document(login_username.getText().toString().toLowerCase().trim())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();

                                        if(document.getData().get("password").equals(login_password.getText().toString())){
                                            SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                                            prefsEditor.putString("UserKey", String.valueOf(document.getData().get("uid")));
                                            prefsEditor.commit();
                                            mListener.profilePage();
                                        } else {
                                            login_password.setError("Incorrect Password!!");
                                        }


                                    } else {
                                        Toast.makeText(getActivity(), "Login Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        login_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                login_username.setText("");
//                login_password.setText("");
                mListener.signUpPage();
            }
        });

        btn_login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.loginGoogleButton();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener) {
            mListener = (OnLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginFragmentInteractionListener");
        }
    }

    public interface OnLoginFragmentInteractionListener {
        void signUpPage();
        void loginGoogleButton();
        void profilePage();
    }
}
