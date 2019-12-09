package com.example.smartpark;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class DistanceCalculationActivity extends AppCompatActivity {
    private MapView mapView;
    private MapboxMap map;
    public Bundle bundle;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    ArrayList<SlotPositionPrice> positionPrices = new ArrayList<>();
    ArrayList<Marker> markerArray = new ArrayList<>();
    ArrayList<LatLng> locationArray = new ArrayList<>();
    ArrayList<String> stringLocations = new ArrayList<>();
    ArrayList<MapPoints> locationsWithDistances = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_distance_calculation);
        Intent intent = getIntent();
        bundle = intent.getExtras();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("parkingSlots");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getParkingPrice(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.w(TAG,"Listener was cancelled");
//                toastMessage("Listener was cancelled");

            }
        });
        MapQuest.start(getApplicationContext());
        mapView = (MapView) findViewById(R.id.mapquestMapView);
        mapView.onCreate(savedInstanceState);


//        locationArray.add(new LatLng(23.7937704,90.4130481));
//        locationArray.add(new LatLng(23.792342,90.418125));
//        locationArray.add(new LatLng(23.792307,90.417769));
//        locationArray.add(new LatLng(23.792238,90.418295));

//        for(ParkingSlot Slot:array){
//            String LNG = Slot.getLongitude();
//            String LAT = Slot.getLatitude();
//            String locLatLng = new String();
//            locLatLng = LAT+","+LNG;
//            double DLNG = Double.parseDouble(LNG);
//            double DLAT = Double.parseDouble(LAT);
//
//            locationArray.add(new LatLng(DLAT,DLNG));
//            stringLocations.add(locLatLng);
//        }
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                mapView.setStreetMode();
                locationArray = bundle.getParcelableArrayList("Parking_slots_latlng");
                stringLocations = bundle.getStringArrayList("Parking_slots_string");
//                Intent intent = getIntent();
//                ArrayList<ParkingSlot> array = intent.getParcelableArrayListExtra("Parking_slots");

//                for(ParkingSlot Slot:array){
//                    String LNG = Slot.getLongitude();
//                    String LAT = Slot.getLatitude();
//                    String locLatLng = new String();
//                    locLatLng = LAT+","+LNG;
//                    double DLNG = Double.parseDouble(LNG);
//                    double DLAT = Double.parseDouble(LAT);
//
//                    locationArray.add(new LatLng(DLAT,DLNG));
//                    stringLocations.add(locLatLng);
//                }
//

                RouteMatrix routeMatrix = new RouteMatrix();
                routeMatrix.execute(
                        "23.792342,90.418125",
                        "23.800385,90.409126",
                        "23.792238,90.418295"
                );
                map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        for (Marker pinpoint:markerArray){
                            if(pinpoint.equals(marker)){
                                Intent intent = new Intent(DistanceCalculationActivity.this,SlotSelectionActivity.class);
                                LatLng selected = pinpoint.getPosition();
                                String lat = String.valueOf(selected.getLatitude());
                                String lng = String.valueOf(selected.getLongitude());
                                intent.putExtra("selectedSlot", lat+","+lng);
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
                for(String sLoc:stringLocations){
                    locations.put(URLEncoder.encode(sLoc, "UTF-8"));

                }

//                locations.put(URLEncoder.encode(args[0], "UTF-8"));
//                locations.put(URLEncoder.encode(args[1], "UTF-8"));
//                locations.put(URLEncoder.encode(args[2], "UTF-8"));
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
                    String locVal = getStrLatLng(locationArray.get(i));
                    MapPoints m = new MapPoints(distancesDouble.get(i),locVal);
                    locationsWithDistances.add(m);

                }
                SortDistances sort = new SortDistances();
                ArrayList<String> sortedLocations = sort.sortLocations(locationsWithDistances);

                for(int i = 1;i < 5;i++) {

//                    map.addMarker(new MarkerOptions().position(locationArray.get(i)));
//                    addmarker(map,distances.get(0),locationArray.get(0));
                    LatLng valueOnMap = convertoLatLng(sortedLocations.get(i));
                    Marker pinMarker=addmarker(map,distances.get(i),valueOnMap);
                    markerArray.add(pinMarker);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(valueOnMap,11));
                }


//                for(int i = 0;i < locationArray.size();i++) {
//
////                    map.addMarker(new MarkerOptions().position(locationArray.get(i)));
////                    addmarker(map,distances.get(0),locationArray.get(0));
//                    Marker pinMarker=addmarker(map,distances.get(i),locationArray.get(i));
//                    markerArray.add(pinMarker);
//                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationArray.get(i),11));
//                }
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




    private void getParkingPrice(DataSnapshot dataSnapshot){

        for(DataSnapshot ds:dataSnapshot.getChildren()) {

            String latitude = ds.child("latitude").getValue(String.class);
            String longitude = ds.child("longitude").getValue(String.class);
            String price = ds.child("Price").getValue(String.class);
            String Status = ds.child("Status").getValue(String.class);


            SlotPositionPrice parkingSlotPrice = new SlotPositionPrice(latitude,longitude,price) ;




            if(Status.equals("0")) {
                positionPrices.add(parkingSlotPrice);

            }
        }

    }

    private Marker addmarker(MapboxMap map,String distance,LatLng parkSlotGeoLoc){
//        String distanceSnippet = Double.toString(distance);
        MarkerOptions markerOptions = new MarkerOptions();
//        LatLng parkSlotGeoLoc = new LatLng();
//        parkSlotGeoLoc = getParkingSlot();
        String parkingPrice = "0";
        String lonLatPos = getStrLatLng(parkSlotGeoLoc);
        String stLat = lonLatPos.split(",")[0];
        String stLng = lonLatPos.split(",")[1];

        for(SlotPositionPrice slotPrice:positionPrices){
            if(stLat.equals(slotPrice.slotLatitude) && stLng.equals(slotPrice.slotLongitude)){
                parkingPrice=slotPrice.slotPrice;
            }
        }


        markerOptions.position(parkSlotGeoLoc);
        markerOptions.snippet(distance+",Tk "+parkingPrice);
        Marker mapPin = map.addMarker(markerOptions);
        return mapPin;
    }

    public String getStrLatLng(LatLng locValue){
        String lat = String.valueOf(locValue.getLatitude());
        String lng = String.valueOf(locValue.getLongitude());
        return lat+","+lng;
    }

    public LatLng convertoLatLng(String Loc){
        String[] positions=Loc.split(",");
        double latitude = Double.parseDouble(positions[0]);
        double longitude = Double.parseDouble(positions[1]);
        LatLng location = new LatLng(latitude,longitude);
        return location;
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
