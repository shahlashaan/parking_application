package com.example.trialmapquest;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapquest.mapping.MapQuest;
import com.mapquest.mapping.maps.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private MapboxMap map;
    List<Marker> markerArray = new ArrayList<>();
    List<LatLng> locationArray = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapQuest.start(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapquestMapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                mapView.setStreetMode();


                locationArray.add(new LatLng(23.7937704,90.4130481));
                locationArray.add(new LatLng(23.792342,90.418125));
                locationArray.add(new LatLng(23.792307,90.417769));
                locationArray.add(new LatLng(23.792238,90.418295));

                RouteMatrix routeMatrix = new RouteMatrix();
                routeMatrix.execute(
                        "23.792342,90.418125",
                        "23.792307,90.417769",
                        "23.792238,90.418295"
                );
                map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        for (Marker pinpoint:markerArray){
                            if(pinpoint.equals(marker)){
                                Intent intent = new Intent(MainActivity.this,SelectParking.class);
                                startActivity(intent);
                            }
                        }
                        return false;
                    }
                });
            }
        });
    }
    private class RouteMatrix extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args){
            JSONObject postData = new JSONObject();


            try {
                // JSONArray of start and finish
                JSONArray locations = new JSONArray();
                locations.put(URLEncoder.encode("23.7937704,90.4130481", "UTF-8"));
                locations.put(URLEncoder.encode(args[0], "UTF-8"));
                locations.put(URLEncoder.encode(args[1], "UTF-8"));
                locations.put(URLEncoder.encode(args[2], "UTF-8"));
                postData.put("locations", locations); // put array inside main object

                // JSONObject options
                JSONObject options = new JSONObject();
                options.put("allToAll", "false");
                postData.put("options", options);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // create the api request string
            String urlstring = "http://www.mapquestapi.com/directions/v2/routematrix" +
                    "?key=ZfjUbJ4Ewa9hrGhJiwrJM8JDt40FfUZH&json=" +
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
                        .getJSONArray("distance");

                // get every other shape point
                int distanceCount = points.length();

                // create a shape point list
                ArrayList<String> distances = new ArrayList<String>();
//
                ArrayList<Double> distancesDouble = new ArrayList<Double>();
                // fill list with every even value as lat and odd value as lng
                for (int point = 0; point < distanceCount; point = point + 1) {
                    distances.add(points.get(point).toString());
                }
                for (int point = 0; point < distanceCount; point = point + 1) {
                    distancesDouble.add(Double.parseDouble(distances.get(point)));
                }
                double minimumDistance = Collections.min(distancesDouble);
                int minIndex = distances.indexOf(minimumDistance);
//
////                JSONArray locationsArr = new JSONObject(json).getJSONArray("locations");
////                List<LatLng> mapPositions = new ArrayList<>();
////                int arraylength = locationsArr.length();
                for(int i = 0;i < locationArray.size();i++) {

//                    map.addMarker(new MarkerOptions().position(locationArray.get(i)));
//                    addmarker(map,distances.get(0),locationArray.get(0));
                    Marker pinMarker=addmarker(map,distances.get(i),locationArray.get(i));
                    markerArray.add(pinMarker);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationArray.get(i),11));
                }
//                for(int loc = 0; loc < arraylength;loc++){
//                    JSONObject position = locationsArr.getJSONObject(loc).getJSONObject("latLng");
//                    double lng = (double) position.get("lng");
//                    double lat = (double) position.get("lat");
//                    LatLng positionLatLng =  new LatLng(lat,lng);
//                    mapPositions.add(positionLatLng);
//                }





            } catch (Exception e) {
                System.out.println("catch D: " + e.toString());
            }
        }
    }
    private Marker addmarker(MapboxMap map,String distance,LatLng parkSlotGeoLoc){
//        String distanceSnippet = Double.toString(distance);
        MarkerOptions markerOptions = new MarkerOptions();
//        LatLng parkSlotGeoLoc = new LatLng();
//        parkSlotGeoLoc = getParkingSlot();
//        String val = new String();
//        val = parkSlotGeoLoc.toString();
        markerOptions.position(parkSlotGeoLoc);
        markerOptions.snippet(distance);
        Marker mapPin = map.addMarker(markerOptions);
        return mapPin;
    }

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
