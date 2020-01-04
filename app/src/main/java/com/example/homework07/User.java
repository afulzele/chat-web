package com.example.homework07;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String f_name;
    private String l_name;
    private String gender;
    private String uid;
    private String img;
    private String pass;
    private String uname;
    private ArrayList<Trip> trips;

    public User() {
        this.f_name = f_name;
        this.l_name = l_name;
        this.gender = gender;
        this.uid = uid;
        this.img = img;
        this.pass = pass;
        this.uname = uname;
    }

    public User(String f_name, String l_name, String gender, String uid, String img, String pass, String uname) {
        this.f_name = f_name;
        this.l_name = l_name;
        this.gender = gender;
        this.uid = uid;
        this.img = img;
        this.pass = pass;
        this.uname = uname;
    }

    public User(String f_name, String l_name, String gender, String uid, String img, String pass, String uname, ArrayList<Trip> trips) {
        this.f_name = f_name;
        this.l_name = l_name;
        this.gender = gender;
        this.uid = uid;
        this.img = img;
        this.pass = pass;
        this.uname = uname;
        this.trips = trips;
    }

    public ArrayList<Trip> getTrips() {
        return trips;
    }

    public void setTrips(ArrayList<Trip> trips) {
        this.trips = trips;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getL_name() {
        return l_name;
    }

    public void setL_name(String l_name) {
        this.l_name = l_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public String toString() {
        return "User{" +
                "f_name='" + f_name + '\'' +
                ", l_name='" + l_name + '\'' +
                ", gender='" + gender + '\'' +
                ", uid='" + uid + '\'' +
                ", img='" + img + '\'' +
                ", pass='" + pass + '\'' +
                ", uname='" + uname + '\'' +
                '}';
    }

    public Map toUserHashMap() {
        Map<String, Object> movieHash = new HashMap<>();
        movieHash.put("fname", this.f_name);
        movieHash.put("lname", this.l_name);
        movieHash.put("gender", this.gender);
        movieHash.put("uid", this.uid);
        movieHash.put("img", this.img);
        movieHash.put("password", this.pass);
        movieHash.put("username", this.uname);
        movieHash.put("trips", this.trips);
        return movieHash;
    }

    public Map toUsersHashMap() {
        Map<String, Object> movieHash = new HashMap<>();
        movieHash.put("full_name", this.f_name + " " + this.l_name);
        movieHash.put("uid", this.uid);
        return movieHash;
    }

    public Map creds() {
        Map<String, Object> movieHash = new HashMap<>();
        movieHash.put("username", this.uname);
        movieHash.put("password", this.pass);
        movieHash.put("uid", this.uid);
        return movieHash;
    }

    User(Map userMap) {
        this.f_name = (String) userMap.get("fname");
        this.l_name = (String) userMap.get("lname");
        this.gender = (String) userMap.get("gender");
        this.uid = (String) userMap.get("uid");
        this.img = (String) userMap.get("img");
        this.pass = (String) userMap.get("password");
        this.uname = (String) userMap.get("username");
    }
}
