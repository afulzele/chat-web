package com.example.homework07;

import java.util.HashMap;
import java.util.Map;

public class Users {
    private String full_name, uid;

    public Users(String full_name, String uid) {
        this.full_name = full_name;
        this.uid = uid;
    }

    public Users() {
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "Users{" +
                "full_name='" + full_name + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }

    public Map creds(){
        Map<String, Object> movieHash = new HashMap<>();
        movieHash.put("full_name", this.full_name);
        movieHash.put("uid", this.uid);
        return movieHash;
    }

    public Map tripsMap(){
        Map<String, Object> movieHash = new HashMap<>();
        movieHash.put("uid", this.uid);
        movieHash.put("full_name", this.full_name);
        return movieHash;
    }

    Users(Map userMap){
        this.full_name = (String) userMap.get("full_name");
        this.uid = (String) userMap.get("uid");
    }
}
