package com.example.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ParkingConformation extends AppCompatActivity {
    private static final String TAG = "ParkingConformation";
    String []positions;
    String message;
    String parkingID;
    private ParkingSlot parkingSlot;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private TextView textView5;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_conformation);
        Intent intent = getIntent();
        message = intent.getStringExtra("selected_parking_slot");
        textView5 = (TextView) findViewById(R.id.textView5);
        positions = message.split(",");
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
                textView5.setText(parkingID);

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
            String price = ds.child("Price").getValue(String.class);


            if(latitude.equals(positions[0]) && longitude.equals(positions[1])){
                key = ds.getKey();
                parkingSlot = new ParkingSlot(Status, latitude, longitude, address, parking_area,price);


            }

//
//
//            String locLatLng = new String();
//            locLatLng = latitude + "," + longitude;
//            double DLAT = Double.parseDouble(latitude);
//            double DLNG = Double.parseDouble(longitude);


        }
        return key;
    }




            private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
