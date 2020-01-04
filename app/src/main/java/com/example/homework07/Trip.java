/*
 * Copyright (c) 2019.
 */

package com.example.homework07;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Trip implements Serializable {
    private String uid, title, loc_uid, cover_image_url, chat_uid;
    private String created_by;
    private Latlong mainloc;
    private ArrayList<Chats> chatroom;
    private ArrayList<Latlong> geometry;
    private ArrayList<Users> friends;
    private Tripogle googletrip;

    public Trip(String uid, String title, String loc_uid, String cover_image_url, String chat_uid, String created_by, Latlong mainloc, ArrayList<Chats> chatroom, ArrayList<Latlong> geometry, ArrayList<Users> friends, Tripogle googletrip) {
        this.uid = uid;
        this.title = title;
        this.loc_uid = loc_uid;
        this.cover_image_url = cover_image_url;
        this.chat_uid = chat_uid;
        this.created_by = created_by;
        this.mainloc = mainloc;
        this.chatroom = chatroom;
        this.geometry = geometry;
        this.friends = friends;
        this.googletrip = googletrip;
    }

    public Trip() {}

    public Latlong getMainloc() {
        return mainloc;
    }

    public void setMainloc(Latlong mainloc) {
        this.mainloc = mainloc;
    }

    public Tripogle getGoogletrip() {
        return googletrip;
    }

    public void setGoogletrip(Tripogle googletrip) {
        this.googletrip = googletrip;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLoc_uid() {
        return loc_uid;
    }

    public void setLoc_uid(String loc_uid) {
        this.loc_uid = loc_uid;
    }

    public String getCover_image_url() {
        return cover_image_url;
    }

    public void setCover_image_url(String cover_image_url) {
        this.cover_image_url = cover_image_url;
    }

    public String getChat_uid() {
        return chat_uid;
    }

    public void setChat_uid(String chat_uid) {
        this.chat_uid = chat_uid;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public ArrayList<Chats> getChatroom() {
        return chatroom;
    }

    public void setChatroom(ArrayList<Chats> chatroom) {
        this.chatroom = chatroom;
    }

    public ArrayList<Latlong> getGeometry() {
        return geometry;
    }

    public void setGeometry(ArrayList<Latlong> geometry) {
        this.geometry = geometry;
    }

    public ArrayList<Users> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Users> friends) {
        this.friends = friends;
    }

    public Map tripsMap(){
        Map<String, Object> movieHash = new HashMap<>();
        movieHash.put("uid", this.uid);
        movieHash.put("title", this.title);
//        movieHash.put("location uid", this.loc_uid);
        movieHash.put("mainloc", this.mainloc);
        movieHash.put("cover_image_url", this.cover_image_url);
        movieHash.put("chat_uid", this.chat_uid);
        movieHash.put("created_by", this.created_by);
//        movieHash.put("chatroom", this.chatroom);
        movieHash.put("geometry", this.geometry);
        movieHash.put("friends", this.friends);
//        movieHash.put("googletrip", this.googletrip);
        return movieHash;
    }

    Trip(Map userMap){
        this.uid = (String) userMap.get("uid");
        this.title = (String) userMap.get("title");
//        this.mainloc = (Latlong) userMap.get("mainloc");
//        this.loc_uid = (String) userMap.get("location uid");
        this.cover_image_url = (String) userMap.get("cover_image_url");
        this.chat_uid = (String) userMap.get("chat_uid");
        this.created_by = (String) userMap.get("created_by");
//        this.chatroom = (ArrayList<Chats>) userMap.get("chatroom");
//        this.geometry = (ArrayList<Latlong>) userMap.get("geometry");
//        this.friends = (ArrayList<User>) userMap.get("friends");
//        this.googletrip = (Tripogle) userMap.get("googletrip");
    }

    @Override
    public String toString() {
        return "Trip{" +
                "uid='" + uid + '\'' +
                ", title='" + title + '\'' +
                ", loc_uid='" + loc_uid + '\'' +
                ", cover_image_url='" + cover_image_url + '\'' +
                ", chat_uid='" + chat_uid + '\'' +
                ", created_by=" + created_by +
                ", mainloc=" + mainloc +
                ", chatroom=" + chatroom +
                ", geometry=" + geometry +
                ", friends=" + friends +
                ", googletrip=" + googletrip +
                '}';
    }
}
