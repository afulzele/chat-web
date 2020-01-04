package com.example.homework07;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class CreateTripFragment extends Fragment {

    //    private OnFragmentInteractionListener mListener;
    private LocationManager lManager;
    private String googlePlaceApi = "*your-api-key*";

    private EditText create_trip_search_bar;
    private RecyclerView create_trip_rv;
    private ProgressBar create_trip_pb;
    private Button btn_search_trip;

    private String this_trip_id_if_from_places_search;

    public CreateTripFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_trip, container, false);

        create_trip_search_bar = view.findViewById(R.id.create_trip_search_bar);
        create_trip_rv = view.findViewById(R.id.create_trip_rv);
        create_trip_pb = view.findViewById(R.id.create_trip_pb);
        btn_search_trip = view.findViewById(R.id.btn_search_trip);

        create_trip_rv.setVisibility(View.INVISIBLE);
        create_trip_pb.setVisibility(View.INVISIBLE);

        lManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);

//        create_trip_search_bar.setText("Charlotte");

        Bundle arguments = getArguments();

        if (arguments != null && arguments.containsKey("loc_id")) {
//            create_trip_search_bar.setVisibility(View.INVISIBLE);
            create_trip_search_bar.setEnabled(false);
            btn_search_trip.setVisibility(View.INVISIBLE);
            btn_search_trip.setEnabled(false);

            final Latlong this_trip_loc_info = (Latlong) arguments.getSerializable("loc_id");
            this_trip_id_if_from_places_search = arguments.getString("this_trip_id");

            create_trip_search_bar.setText("Places near " + this_trip_loc_info.getTitle());

            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + this_trip_loc_info.getLatitude() + "," + this_trip_loc_info.getLongitude() + "&radius=5000&key=*your-api-key*";
            new GetNearbyResultFromGoogle().execute(url);
        }

        btn_search_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (create_trip_search_bar.getText().toString().equals("")) {
                    create_trip_search_bar.setError("Required");
                } else {
                    String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?types=(cities)&input=" + create_trip_search_bar.getText().toString() + "&key=" + googlePlaceApi;
                    new GetTripResultFromGoogle().execute(url);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("GPS not Enabled")
                    .setMessage("Would you like to enable the GPS setting?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    getActivity().finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {

        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    public interface OnFragmentInteractionListener {
//        void onFragmentInteraction(Uri uri);
//    }

    private class GetNearbyResultFromGoogle extends AsyncTask<String, Void, ArrayList<Latlong>> {

        @Override
        protected ArrayList<Latlong> doInBackground(String... strings) {


            HttpURLConnection connection = null;
            ArrayList<Latlong> result = new ArrayList<>();
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF-8");
                    JSONObject root = new JSONObject(json);
                    JSONArray results = root.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject track = results.getJSONObject(i);


                        JSONObject geometry = track.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        Double latitude = location.getDouble("lat");
                        Double longitude = location.getDouble("lng");
                        String name = track.getString("name");
                        String place_id = track.getString("place_id");

                        Latlong llp = new Latlong(latitude, longitude, place_id, name);

                        result.add(llp);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            create_trip_pb.setVisibility(View.VISIBLE);
            create_trip_rv.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<Latlong> latlongs) {
            super.onPostExecute(latlongs);
            create_trip_pb.setVisibility(View.INVISIBLE);
            create_trip_rv.setVisibility(View.VISIBLE);
            CreatePlaceAdapter adapter = new CreatePlaceAdapter(getContext(), latlongs, this_trip_id_if_from_places_search);
            create_trip_rv.setAdapter(adapter);
            create_trip_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    private class GetTripResultFromGoogle extends AsyncTask<String, Void, ArrayList<Tripogle>> {

        @Override
        protected ArrayList<Tripogle> doInBackground(String... strings) {
            HttpURLConnection connection = null;
            ArrayList<Tripogle> result = new ArrayList<>();
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF-8");
                    JSONObject root = new JSONObject(json);
                    JSONArray results = root.getJSONArray("predictions");
                    if (results.length() == 0 || results.length() < 1) {
                        Tripogle emptyTripres = new Tripogle();
                        emptyTripres.setTitle("No results found");
                        emptyTripres.setPlace_id("00");
                        result.add(emptyTripres);
                    }
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject w = results.getJSONObject(i);
                        Tripogle trip = new Tripogle();
                        trip.setTitle(w.getString("description"));
                        trip.setPlace_id(w.getString("place_id"));
                        result.add(trip);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            create_trip_pb.setVisibility(View.VISIBLE);
            create_trip_rv.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<Tripogle> trips) {
            super.onPostExecute(trips);
            create_trip_pb.setVisibility(View.INVISIBLE);
            create_trip_rv.setVisibility(View.VISIBLE);
            CreateTripAdapter adapter = new CreateTripAdapter(getContext(), trips);
            create_trip_rv.setAdapter(adapter);
            create_trip_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }
}
