/*
 * Copyright (c) 2019.
 */

package com.example.homework07;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView my_message_body, my_message_body_time;

        public ViewHolder(View itemView) {
            super(itemView);

            my_message_body = (TextView) itemView.findViewById(R.id.my_message_body);
            my_message_body_time = (TextView) itemView.findViewById(R.id.my_message_body_time);
        }
    }

    private ArrayList<Chats> mContacts;
    private Context mc;
    private String id;

    private String doc = "";
    private SharedPreferences sharedpreferences;
    private static final String mypreference = "user";

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;


    // Pass in the contact array into the constructor
    public ChatAdapter(Context c, ArrayList<Chats> contacts, String id_1) {
        mc = c;
        mContacts = contacts;
        id = id_1;
    }

    @Override
    public int getItemViewType(int position) {
        Chats message = (Chats) mContacts.get(position);

        if (message.getUser().getUid().equals(id)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_my, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_other, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Chats c = (mContacts.get(position));

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(c);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(c);
        }
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.my_message_body);
            timeText = itemView.findViewById(R.id.my_message_body_time);
        }

        void bind(Chats message) {
            messageText.setText(message.getMessgae());
            SimpleDateFormat df = new SimpleDateFormat("hh:mm a  MM-dd-yyyy");
            String formattedDate = df.format(message.getTime());
            timeText.setText(formattedDate);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_other_message_body);
            timeText = itemView.findViewById(R.id.text_message_other_time);
            nameText = itemView.findViewById(R.id.text_message_other_name);
        }

        void bind(Chats message) {
            messageText.setText(message.getMessgae());
            SimpleDateFormat df = new SimpleDateFormat("hh:mm a  MM-dd-yyyy");
            String formattedDate = df.format(message.getTime());
            timeText.setText(formattedDate);
            nameText.setText(message.getUser().getFull_name());
        }
    }

}