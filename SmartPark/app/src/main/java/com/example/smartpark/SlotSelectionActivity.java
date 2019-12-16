package com.example.smartpark;


import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapquest.mapping.maps.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SlotSelectionActivity extends AppCompatActivity {
    private MapView mapView;
    private MapboxMap map;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, com.example.smartpark.LoginActivity.class));
        }
        setContentView(R.layout.activity_slot_selection);
        Intent intent = getIntent();
        String message = intent.getStringExtra("selectedSlot");
//        TextView textView = findViewById(R.id.textView);
//        textView.setText(message);
        mapView = (MapView) findViewById(R.id.mapquestMapView);
        mapView.onCreate(savedInstanceState);
//        DistanceCalculation distanceCalculation = new DistanceCalculation();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                mapView.setStreetMode();
                String targetLatLng=message;
                String[] positions=targetLatLng.split(",");
                double latitude = Double.parseDouble(positions[0]);
                double longitude = Double.parseDouble(positions[1]);
                LatLng location = new LatLng(latitude,longitude);
//                targetLatLng=distanceCalculation.getMinimumDistance();
//                List<LatLng> locationArray = new ArrayList<>();
////                locationArray.add(new LatLng(23.792380,90.418295));
//                locationArray.add(new LatLng(23.792342,90.418125));
////                locationArray.add(new LatLng(23.792307,90.417769));
//                locationArray.add(new LatLng(23.792238,90.418295));
//                for(int i = 0;i < locationArray.size();i++) {
//
//                    map.addMarker(new MarkerOptions().position(locationArray.get(i)));
//
//                }
                Marker slotPosition=map.addMarker(new MarkerOptions().position(location));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(location,11));
                Route route = new Route();
                route.execute(
                        "23.7937704,90.4130481", // origin
                        targetLatLng, // destination
                        "Fastest" // type

                );
                map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker slotPosition) {


                                Intent intent = new Intent(SlotSelectionActivity.this,PopUpActivity.class);
                                LatLng selected = location;
                                String lat = String.valueOf(selected.getLatitude());
                                String lng = String.valueOf(selected.getLongitude());
                                intent.putExtra("confirmSlot", lat+","+lng);
//                                finish();

                        startActivity(intent);

                        return false;
                    }
                });
            }
        });
    }
    private class Route extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... args){
            JSONObject postData = new JSONObject();


            try {
                // JSONArray of start and finish
                JSONArray locations = new JSONArray();
                locations.put(URLEncoder.encode(args[0], "UTF-8"));
                locations.put(URLEncoder.encode(args[1], "UTF-8"));
                postData.put("locations", locations); // put array inside main object

                // JSONObject options
                JSONObject options = new JSONObject();
                options.put("routeType", args[2]);
                options.put("generalize", "0");
                postData.put("options", options);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // create the api request string
            String urlstring = "http://www.mapquestapi.com/directions/v2/route" +
                    "?key=naGG6n1HL3R5UHDkRYIBipB3HV8R4cvv&json=" +
                    postData.toString();

            // make the GET request and prep the response string
            StringBuilder json = new StringBuilder();
            try {
                URL url = new URL(urlstring);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        json.append(line);
                    }
                } catch (Exception e) {
                    System.out.println("catch B: " + e.toString());
                } finally {
                    urlConn.disconnect();
                }
            } catch (Exception e) {
                System.out.println("catch C: " + e.toString());
            }
            return json.toString();
        }

        protected void onPostExecute(String json){
            try {
                // get shape points from response
                JSONArray points = new JSONObject(json)
                        .getJSONObject("route")
                        .getJSONObject("shape")
                        .getJSONArray("shapePoints");

                // get every other shape point
                int pointcount = points.length() / 2;

                // create a shape point list
                List<LatLng> shapePoints = new ArrayList<>();

                // fill list with every even value as lat and odd value as lng
                for (int point = 0; point < pointcount; point = point + 1) {
                    shapePoints.add(new LatLng(
                            (double) points.get(point * 2),
                            (double) points.get(point * 2 + 1)
                    ));
                }

                // create polyline options
                PolylineOptions polyline = new PolylineOptions();
                polyline.addAll(shapePoints)
                        .width(5)
                        .color(Color.GRAY)
                        .alpha((float)0.75);

                // add the polyline to the map
                map.addPolyline(polyline);

                JSONObject distance = new JSONObject(json).getJSONObject("route");
                double distanceValue = (double) distance.get("distance");
                // get map bounds
                JSONObject bounds = new JSONObject(json)
                        .getJSONObject("route")
                        .getJSONObject("boundingBox");

                // create bounds for animating map
                LatLngBounds latLngBounds = new LatLngBounds.Builder()
                        .include(new LatLng(
                                (double) bounds.getJSONObject("ul").get("lat"),
                                (double) bounds.getJSONObject("ul").get("lng")
                        ))
                        .include(new LatLng(
                                (double) bounds.getJSONObject("lr").get("lat"),
                                (double) bounds.getJSONObject("lr").get("lng")
                        ))
                        .build();

                // animate to map bounds
//                addmarker(map,distanceValue);
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);
            } catch (Exception e) {
                System.out.println("catch D: " + e.toString());
            }
        }
    }
//    private void addmarker(MapboxMap map,double distance){
//        String distanceSnippet = Double.toString(distance);
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(new LatLng(23.792307,90.417769));
//        markerOptions.snippet(distanceSnippet);
//        map.addMarker(markerOptions);
//    }

    @Override
    public void onResume()
    { super.onResume(); mapView.onResume(); }

    @Override
    public void onPause()
    { super.onPause(); mapView.onPause(); }

    @Override
    protected void onDestroy()
    { super.onDestroy(); mapView.onDestroy(); }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    { super.onSaveInstanceState(outState); mapView.onSaveInstanceState(outState); }
}
