/*
 * Copyright (c) 2019.
 */

package com.example.homework07;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Latlong implements Serializable {

    private Double latitude, longitude;
    private String place_id, title;

    public Latlong(Double latitude, Double longitude, String place_id, String title) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.place_id = place_id;
        this.title = title;
    }

    public Latlong() {
    }

    public Latlong(Object mainloc) {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    @Override
    public String toString() {
        return "Latlong{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", place_id='" + place_id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public Map latlong(){
        Map<String, Object> movieHash = new HashMap<>();
        movieHash.put("latitude", this.latitude);
        movieHash.put("longitude", this.longitude);
        movieHash.put("place_id", this.place_id);
        movieHash.put("title", this.title);
        return movieHash;
    }

    Latlong(Map userMap){
        this.latitude = (Double) userMap.get("latitude");
        this.longitude = (Double) userMap.get("longitude");
        this.place_id = (String) userMap.get("place_id");
        this.title = (String) userMap.get("title");
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
