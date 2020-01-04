/*
 * Copyright (c) 2019.
 */

package com.example.homework07;

import java.io.Serializable;

public class Tripogle implements Serializable {

    private String title, place_id, image_url;
    private Latlong loc;

    public Tripogle(String title, String place_id, String image_url, Latlong loc) {
        this.title = title;
        this.place_id = place_id;
        this.image_url = image_url;
        this.loc = loc;
    }

    public Tripogle() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Latlong getLoc() {
        return loc;
    }

    public void setLoc(Latlong loc) {
        this.loc = loc;
    }

    @Override
    public String toString() {
        return "Tripogle{" +
                "title='" + title + '\'' +
                ", place_id='" + place_id + '\'' +
                ", image_url='" + image_url + '\'' +
                ", loc=" + loc +
                '}';
    }
}
