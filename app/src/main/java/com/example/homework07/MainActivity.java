package com.example.homework07;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentInteractionListener,
        SignUpFragment.OnSignUpFragmentInteractionListener, ProfilePageFragment.OnProfilePageFragmentInteractionListener,
        EditFragment.OnEditFragmentInteractionListener, BeforeCreateTripFragment.OnFragmentInteractionListener,
        EditTripFragment.OnFragmentInteractionListener, ShowTripInfoFragment.OnFragmentInteractionListener{

    public static FragmentManager fragmentManager;
    private int RC_SIGN_IN = 0;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG = "demo";
    private SharedPreferences sharedpreferences;
    private static final String mypreference = "user";

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap bitmapUpload = null;

    //    private String client_id = "420419258364-adihho6rluj8ovk9152tauc79a34p5c4.apps.googleusercontent.com";
    private String client_id = "420419258364-cg59amp8vltigv18u0o4maqn6sd831rv.apps.googleusercontent.com";
    private String client_secret = "7Cm5KMfDpFdoFgsENycXuyvs";

    public static Context contextOfApplication;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(client_id, false)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        contextOfApplication = getApplicationContext();

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        if (isConnected()) {
            final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account != null) {
                db.collection("users").document(account.getId())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        profilePage();
                                    } else {
//                                        googleSignOut();
                                        editPage();
                                    }
                                } else {
                                    loginPage();
                                }
                            }
                        });
            } else if (sharedpreferences.contains("UserKey")) {
                profilePage();
            } else {
                loginPage();
            }
        } else {
            internetToastMaker();
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void googleSignOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Sign Out Successful", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sharedPLogout() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            db.collection("users").document(account.getId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    profilePage();
                                } else {
                                    editPage();
                                }
                            } else {
                                loginPage();
                            }
                        }
                    });
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

//    public void setUp(String accountNo, String serverAuthCode) throws IOException {
//        HttpTransport httpTransport = new NetHttpTransport();
//        JacksonFactory jsonFactory = new JacksonFactory();
//
//        String clientId = client_id;
//        String clientSecret = client_secret;
//
//        // Or your redirect URL for web based applications.
//        String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";
//
//        GoogleTokenResponse tokenResponse =
//                new GoogleAuthorizationCodeTokenRequest(httpTransport,jsonFactory,clientId,clientSecret,serverAuthCode,redirectUrl)
//                .execute();
//
//        GoogleCredential credential = new GoogleCredential.Builder()
//                .setTransport(httpTransport)
//                .setJsonFactory(jsonFactory)
//                .setClientSecrets(clientId, clientSecret)
//                .build();
//
//        credential.setFromTokenResponse(tokenResponse);
//
//        PeopleService peopleService = new PeopleService.Builder(httpTransport, jsonFactory, credential).build();
//
//        Person profile = peopleService.people().get(String.valueOf(accountNo))
//                .setPersonFields("names,emailAddresses")
//                .execute();
//
//
//    }

//    final int r = generateRandom(99999999);

    public static int generateRandom(int maximumValue) {
        SecureRandom ranGen = new SecureRandom();
        return ranGen.nextInt(maximumValue);
    }

    private void internetToastMaker() {
        Toast.makeText(MainActivity.this, "Internet Not Connected!!", Toast.LENGTH_SHORT).show();
    }


    public void loginPage() {
        LoginFragment f_frag = new LoginFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, f_frag);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void loginGoogleButton() {
        googleSignIn();
    }


    @Override
    public void signUpPage() {
        SignUpFragment f_frag = new SignUpFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, f_frag);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    public void profilePage() {
        ProfilePageFragment f_frag = new ProfilePageFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, f_frag);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void editPage() {
        EditFragment f_frag = new EditFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, f_frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void userPage() {
        UsersFragment f_frag = new UsersFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, f_frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void beforeCreateTrips() {
        BeforeCreateTripFragment f_frag = new BeforeCreateTripFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, f_frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void beforeCreateTrips2() {
        BeforeCreateTripFragment f_frag = new BeforeCreateTripFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, f_frag);
        transaction.commit();
    }

    @Override
    public void onCreateTrips() {
        CreateTripFragment f_frag = new CreateTripFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, f_frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onProfilePageSignOut() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            googleSignOut();
        } else if (sharedpreferences.contains("UserKey")) {
            sharedPLogout();
        }
        loginPage();
    }
}
