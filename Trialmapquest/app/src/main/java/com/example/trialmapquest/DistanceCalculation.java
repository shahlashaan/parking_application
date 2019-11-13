package com.example.trialmapquest;

import android.graphics.Color;
import android.os.AsyncTask;

import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;

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

public class DistanceCalculation {
    double distanceValue;
    int minIndexVal;

    public String getMinimumDistance(){
        RouteMatrix routeMatrix = new RouteMatrix();
        List<LatLng> locationArray = new ArrayList<>();
        locationArray.add(new LatLng(23.792307,90.417769));
        locationArray.add(new LatLng(23.792342,90.418125));
        locationArray.add(new LatLng(23.792238,90.418295));
        routeMatrix.execute(
                "23.792307,90.417769",
                "23.792342,90.418125",
                "23.792238,90.48295"
        );
        LatLng minDisLocation = locationArray.get(getminIndexVal());
        Double l1 = minDisLocation.getLatitude();
        Double l2 = minDisLocation.getLongitude();
        String coOrdL1 = l1.toString();
        String coOrdL2 = l2.toString();

        return coOrdL1+","+coOrdL2;
    }

    private class RouteMatrix extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args){
            JSONObject postData = new JSONObject();


            try {
                // JSONArray of start and finish
                JSONArray locations = new JSONArray();
                locations.put(URLEncoder.encode("23.741434,90.390782", "UTF-8"));
                locations.put(URLEncoder.encode(args[0], "UTF-8"));
                locations.put(URLEncoder.encode(args[1], "UTF-8"));
                locations.put(URLEncoder.encode(args[0], "UTF-8"));
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
                List<Double> distances = new ArrayList<>();

                // fill list with every even value as lat and odd value as lng
                for (int point = 0; point < distanceCount; point = point + 1) {
                    distances.add((double) points.get(point));
                }
                double minimumDistance = Collections.min(distances);
                int minIndex = distances.indexOf(minimumDistance);
                setminDistanceVal(minimumDistance);
                setminIndexVal(minIndex);


            } catch (Exception e) {
                System.out.println("catch D: " + e.toString());
            }
        }
    }
    public void setminDistanceVal(double distanceValue){
        this.distanceValue = distanceValue;
    }
    public void setminIndexVal(int minIndexVal){
        this.minIndexVal = minIndexVal;
    }
    public double getminDistanceVal(){
        return distanceValue;
    }
    public int getminIndexVal(){
        return minIndexVal;
    }

}
