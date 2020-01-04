/*
 * Copyright (c) 2019.
 */

package com.example.homework07;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UpperChat implements Serializable {
    private String chat_uid;
    private ArrayList<Chats> chat_list;

    public UpperChat(String chat_uid, ArrayList<Chats> chat_list) {
        this.chat_uid = chat_uid;
        this.chat_list = chat_list;
    }

    public UpperChat() {
    }

    public String getChat_uid() {
        return chat_uid;
    }

    public void setChat_uid(String chat_uid) {
        this.chat_uid = chat_uid;
    }

    public ArrayList<Chats> getChat_list() {
        return chat_list;
    }

    public void setChat_list(ArrayList<Chats> chat_list) {
        this.chat_list = chat_list;
    }

    public Map tripsMap(){
        Map<String, Object> movieHash = new HashMap<>();
        movieHash.put("chat_uid", this.chat_uid);
        movieHash.put("chat_list", this.chat_list);
        return movieHash;
    }

    UpperChat(Map userMap){
        this.chat_uid = (String) userMap.get("chat_uid");
        this.chat_list = (ArrayList<Chats>) userMap.get("chat_list");
    }
}
