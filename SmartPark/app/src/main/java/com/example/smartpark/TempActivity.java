package com.example.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TempActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TempActivity";
    private FirebaseAuth firebaseAuth;

    private TextView textView;
    private TextView textView2;
    private TextView textView4;
    private Button buttonCheckConfirm;
    public Bundle bundle;
    String lat;
    String lng;
    double dlat;
    double dlng;

    ArrayList<LatLng> array;
    ArrayList<String> stringArrayList;
    String parkingID;
    private ParkingSlot parkingSlot;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, com.example.smartpark.LoginActivity.class));
        }
        Intent intent = getIntent();
        bundle = intent.getExtras();
        array = bundle.getParcelableArrayList("Parking_slots_latlng");
        stringArrayList = bundle.getStringArrayList("Parking_slots_string");
        dlat = array.get(0).getLatitude();
        dlng = array.get(0).getLongitude();

//        lat = String.valueOf(array.get(0).getLatitude());
//        lng = String.valueOf(array.get(0).getLongitude());
        String location = stringArrayList.get(0);
        String [] positions = location.split(",");
        lat=positions[0];
        lng=positions[1];

        textView=(TextView) findViewById(R.id.textView2);
        textView2=(TextView) findViewById(R.id.textView3);
        textView4=(TextView) findViewById(R.id.textView4);
        buttonCheckConfirm=(Button) findViewById(R.id.checkConfirm);

        textView2.setText(location);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("parkingSlots");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                boolean connected = dataSnapshot.getValue(Boolean.class);
//                if(connected) {
//                    Log.d(TAG,"connected");
//                    toastMessage("connected:");

                parkingID=fetchData(dataSnapshot);
                textView.setText(parkingID);
//                textView5.setText(parkingID);

//                }
//                else {
//                    Log.d(TAG,"not connected");
//                    toastMessage("not connected:");
//
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG,"Listener was cancelled");
                toastMessage("Listener was cancelled");

            }
        });
//        parkingSlot.setStatus("1");
//        mRef.child(parkingID).setValue(parkingSlot);
        buttonCheckConfirm.setOnClickListener(this);
    }

    private String fetchData(DataSnapshot dataSnapshot) {
        String key=new String();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
//            User uInfo = new User();
            //            uInfo.setEmail(ds.child("email").getValue(User.class).getEmail());
//            uInfo.setId(ds.child("id").getValue(User.class).getId());
            String Status = ds.child("Status").getValue(String.class);
            String address = ds.child("address").getValue(String.class);
            String latitude = ds.child("latitude").getValue(String.class);
            String longitude = ds.child("longitude").getValue(String.class);
            String parking_area = ds.child("parking_area").getValue(String.class);
            double DLAT = Double.parseDouble(latitude);
            double DLNG = Double.parseDouble(longitude);

            if(DLAT==dlat && DLNG==dlng){
                key = ds.getKey();
                parkingSlot = new ParkingSlot(Status, latitude, longitude, address, parking_area);
                parkingSlot.setStatus("1");

            }

//
//
//            String locLatLng = new String();
//            locLatLng = latitude + "," + longitude;



        }
        textView.setText(key);

        return key;
    }





    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(buttonCheckConfirm==v) {
            String dbParkingSlotKey = "/" + parkingID + "/";
            Map<String, Object> updatedValues = new HashMap<>();
            updatedValues.put(dbParkingSlotKey + "parking_area", parkingSlot.getParkingArea());
            updatedValues.put(dbParkingSlotKey + "address", parkingSlot.getAddress());
            updatedValues.put(dbParkingSlotKey + "latitude", parkingSlot.getLatitude());
            updatedValues.put(dbParkingSlotKey + "longitude", parkingSlot.getLongitude());
            updatedValues.put(dbParkingSlotKey + "Status", parkingSlot.getStatus());

            mRef.updateChildren(updatedValues);
            startActivity(new Intent(TempActivity.this,RemainingTimedata.class));
//        textView4.setText(updatedValues.get(dbParkingSlotKey));
        }
    }
}
