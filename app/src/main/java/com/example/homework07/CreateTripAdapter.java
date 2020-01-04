/*
 * Copyright (c) 2019.
 */

package com.example.homework07;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CreateTripAdapter extends RecyclerView.Adapter<CreateTripAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView search_trip_rv_name;
        private ImageView search_trip_rv_add_del;

        public ViewHolder(View itemView) {
            super(itemView);
            search_trip_rv_name = (TextView) itemView.findViewById(R.id.search_trip_rv_name);
            search_trip_rv_add_del = (ImageView) itemView.findViewById(R.id.search_trip_rv_add_del);
        }
    }

    private ArrayList<Tripogle> mtrips;
    private Context mc;

    // Pass in the contact array into the constructor
    public CreateTripAdapter(Context c, ArrayList<Tripogle> trips) {
        mtrips = trips;
        mc = c;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public CreateTripAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.search_trips_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(CreateTripAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        Tripogle trips = mtrips.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.search_trip_rv_name;
        ImageView img = viewHolder.search_trip_rv_add_del;

        textView.setText(trips.getTitle());
        if (trips.getPlace_id().toString().equals("00")) {
            img.setImageResource(0);
            img.setEnabled(false);
            img.setVisibility(View.INVISIBLE);
        } else {
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new getTripDetailsAsync().execute(mtrips.get(position).getPlace_id());

                }
            });
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mtrips.size();
    }

    public class getTripDetailsAsync extends AsyncTask<String, Void, Trip> {

        @Override
        protected Trip doInBackground(String... strings) {
            String url_placedetails = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + strings[0] + "&key=*your-api-key*";

            HttpURLConnection connection = null;
            Trip main_trip = new Trip();

            try {
                URL url = new URL(url_placedetails);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF-8");
                    JSONObject root = new JSONObject(json);
                    JSONObject result = root.getJSONObject("result");


                    JSONObject geometry = result.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    Double lat = location.getDouble("lat");
                    Double lng = location.getDouble("lng");


                    String place_id = result.getString("place_id");



                    main_trip.setLoc_uid(place_id);

                    String desc = result.getString("formatted_address");
                    main_trip.setTitle(desc);

                    Latlong latlong = new Latlong(lat, lng, place_id, desc);
                    main_trip.setMainloc(latlong);

                    JSONArray photos = result.getJSONArray("photos");
                    JSONObject w = photos.getJSONObject(0);
                    String photo_ref = w.getString("photo_reference");
                    main_trip.setCover_image_url("https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference="+photo_ref+"&key=*your-api-key*");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return main_trip;
        }

        @Override
        protected void onPostExecute(Trip trip) {
            super.onPostExecute(trip);
            Bundle bundle = new Bundle();
            bundle.putSerializable("trip_info", trip);
//            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            EditTripFragment f_frag = new EditTripFragment();
            FragmentTransaction transaction = ((AppCompatActivity) mc).getSupportFragmentManager().beginTransaction();
            f_frag.setArguments(bundle);
            transaction.replace(R.id.main_container, f_frag);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
