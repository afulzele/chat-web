/*
 * Copyright (c) 2019.
 */

package com.example.homework07;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return rootView;


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        assert this.getArguments() != null;
        final String this_trip_uid = this.getArguments().getString("this_trip_uid");

        db.collection("trips").document(this_trip_uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final Trip trip = documentSnapshot.toObject(Trip.class);

                        PolylineOptions po = new PolylineOptions();

                        ArrayList<Latlong> lat_lng = trip.getGeometry();
                        ArrayList<Marker> markers = new ArrayList<>();
                        ArrayList<LatLng> points = new ArrayList<>();
                        lat_lng.add(lat_lng.get(0));

                        for (int i = 0; i < lat_lng.size(); i++) {
                            LatLng position = new LatLng(lat_lng.get(i).getLatitude(), lat_lng.get(i).getLongitude());
                            points.add(position);
                            markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(lat_lng.get(i).getLatitude(), lat_lng.get(i).getLongitude())).title(lat_lng.get(i).getTitle())));
                        }

                        po.clickable(false);
                        po.addAll(points);
                        po.width(5);
                        po.color(Color.BLUE);


//                        mMap.clear();
//
//                        MarkerOptions markerOptions = new MarkerOptions();
//
//                        Double flat = lat_lng.get(0).getLatitude();
//                        Double flng = lat_lng.get(0).getLongitude();
//
//                        // Setting the position for the marker
//                        markerOptions.position(new LatLng(flat, flng));
//                        markerOptions.title(flat + " : " + flng);
//                        mMap.addMarker(markerOptions);
//
//                        markerOptions.position(new LatLng(lat_lng.get(lat_lng.size() - 1).getLatitude(), lat_lng.get(lat_lng.size() - 1).getLongitude()));
//                        markerOptions.title(lat_lng.get(0).getLatitude() + " : " + lat_lng.get(0).getLongitude());
//                        mMap.addMarker(markerOptions);

                        mMap.addPolyline(po);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat_lng.get(0).getLatitude(), lat_lng.get(0).getLongitude())));

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (Marker marker : markers) {
                            builder.include(marker.getPosition());
                        }
                        LatLngBounds bounds = builder.build();
                        int padding = 50; // offset from edges of the map in pixels
                        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                            @Override
                            public void onMapLoaded() {
                                mMap.animateCamera(cu);
                            }
                        });


                    }
                });


    }
}
