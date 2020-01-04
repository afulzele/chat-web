/*
 * Copyright (c) 2019.
 */

package com.example.homework07;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class Chat2Fragment extends Fragment {

    //    private OnFragmentInteractionListener mListener;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView reyclerview_message_list;
    private String doc = "";

    private Trip trip = new Trip();

    private SharedPreferences sharedpreferences;
    private static final String mypreference = "user";
    private ArrayList<Chats> messages = null;

    public Chat2Fragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat2, container, false);

        final EditText edittext_chatbox = view.findViewById(R.id.edittext_chatbox);
        ImageView button_chatbox_send = view.findViewById(R.id.button_chatbox_send);

        reyclerview_message_list = view.findViewById(R.id.reyclerview_message_list);

        sharedpreferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account != null) {
            doc = account.getId();
        } else if (sharedpreferences.contains("UserKey")) {
            doc = sharedpreferences.getString("UserKey", "");
        }

        Bundle arguments = getArguments();

        if (arguments != null && arguments.containsKey("trip_info")) {
            trip = (Trip) arguments.getSerializable("trip_info");

            db.collection("chats").document(trip.getChat_uid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot document) {
                            UpperChat ch = document.toObject(UpperChat.class);
                            if (document.getData() == null) {
                                messages = new ArrayList<>();
                            } else {
                                messages = ch.getChat_list();
                            }


                            for (Chats c : messages) {

//                                System.out.println(formattedDate);
                            }

                            ChatAdapter adapter = new ChatAdapter(getContext(), messages, doc);
                            reyclerview_message_list.setAdapter(adapter);
                            reyclerview_message_list.setLayoutManager(new LinearLayoutManager(getActivity()));
                            if (messages.size() > 10)
                                reyclerview_message_list.scrollToPosition(messages.size() - 1);
                        }
                    });

        }


        db.collection("chats").document(trip.getChat_uid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        String source = snapshot != null && snapshot.getMetadata().hasPendingWrites() ? "Local" : "Server";

                        if (snapshot != null && snapshot.exists()) {
//                                    System.out.println("009080980"+snapshot.getData());
                            if (messages != null) {
                                ChatAdapter adapter = new ChatAdapter(getContext(), messages, doc);
                                reyclerview_message_list.setAdapter(adapter);
                                reyclerview_message_list.setLayoutManager(new LinearLayoutManager(getActivity()));
                                if (messages.size() > 8)
                                    reyclerview_message_list.scrollToPosition(messages.size() - 1);
                            }
                        } else {
                        }
                    }
                });


        button_chatbox_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = edittext_chatbox.getText().toString();

                final Chats chat = new Chats();
                chat.setMessgae(message);
                chat.setTime(Calendar.getInstance().getTime());

                db.collection("users").document(doc)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                String user_name = (String) documentSnapshot.getData().get("full_name");
                                String uid = (String) documentSnapshot.getData().get("uid");
                                Users user = new Users();
                                user.setFull_name(user_name);
                                user.setUid(uid);
                                chat.setUser(user);

                                messages.add(chat);

                                db.collection("chats").document(trip.getChat_uid())
                                        .update("chat_list", messages)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                System.out.println("---done");
                                                edittext_chatbox.setText("");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
                            }
                        });


                db.collection("chats").document(trip.getChat_uid())
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    return;
                                }
                                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites() ? "Local" : "Server";

                                if (snapshot != null && snapshot.exists()) {
//                                    System.out.println("009080980"+snapshot.getData());
                                    ChatAdapter adapter = new ChatAdapter(getContext(), messages, doc);
                                    reyclerview_message_list.setAdapter(adapter);
                                    reyclerview_message_list.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    if (messages.size() > 10)
                                        reyclerview_message_list.scrollToPosition(messages.size() - 1);
                                } else {
                                }
                            }
                        });


            }
        });


        return view;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
