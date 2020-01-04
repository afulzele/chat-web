/*
 * Copyright (c) 2019.
 */

package com.example.homework07;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Chats {
    private String messgae;
    private Date time;
    private Users user;

    public Chats(String messgae, Date time, Users user) {
        this.messgae = messgae;
        this.time = time;
        this.user = user;
    }

    public Chats() {
    }

    public String getMessgae() {
        return messgae;
    }

    public void setMessgae(String messgae) {
        this.messgae = messgae;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }


    public Map tripsMap(){
        Map<String, Object> movieHash = new HashMap<>();
        movieHash.put("messgae", this.messgae);
        movieHash.put("time", this.time);
        movieHash.put("user", this.user);
        return movieHash;
    }

    Chats(Map userMap){
        this.messgae = (String) userMap.get("messgae");
        this.time = (Date) userMap.get("time");
        this.user = (Users) userMap.get("user");
    }

    @Override
    public String toString() {
        return "Chats{" +
                "messgae='" + messgae + '\'' +
                ", time=" + time +
                ", user=" + user +
                '}';
    }
}
