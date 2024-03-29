package com.example.jsoncheck;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.MapQuestAccountManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Route;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private MapboxMap map;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        MapQuestAccountManager.start(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapquestMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                Route route = new Route();
                route.execute(
                        "1555 Blake St, Denver, CO", // origin
                        "39.744979,-104.989506", // destination
                        "pedestrian" // type
                );
            }
        });


        getJSON("http://10.0.2.2/android/getdata.php");
    }
    private void getJSON(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                try {
                    loadIntoListView(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        class Route extends AsyncTask <String, Void, String> {
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
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                // create the api request string
                String urlstring = "http://www.mapquestapi.com/directions/v2/route" +
                        "?key=brK53YAgFqbRjCvs7rlH65HqS1GGVAlK&json=" +
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
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);
                } catch (Exception e) {
                    System.out.println("catch D: " + e.toString());
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void loadIntoListView(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String[] heroes = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            heroes[i] = obj.getString("parking_area");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, heroes);
        listView.setAdapter(arrayAdapter);
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
