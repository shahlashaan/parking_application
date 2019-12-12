package com.example.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

import java.util.HashMap;
import java.util.Map;

public class PopUpActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PopUpActivity";

    private FirebaseAuth firebaseAuth;

    private TextView popUpText;
    private Button buttonConfirm;
    String message;
    String []positions;
    String parkingID;
    String lat;
    String lng;

    private ParkingSlot parkingSlot;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserStatusRef;
    private DatabaseReference mRef;
    private String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, com.example.smartpark.LoginActivity.class));
        }
        Intent intent = getIntent();
        message = intent.getStringExtra("confirmSlot");
        positions = message.split(",");
        lat=positions[0];
        lng=positions[1];
        UserId = firebaseAuth.getCurrentUser().getUid();
        popUpText=(TextView) findViewById(R.id.PopUpText);
        popUpText.setText("Confirm Slot Selection?");
        buttonConfirm = (Button) findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("parkingSlots");
        mUserStatusRef = mFirebaseDatabase.getReference("users");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                boolean connected = dataSnapshot.getValue(Boolean.class);
//                if(connected) {
//                    Log.d(TAG,"connected");
//                    toastMessage("connected:");

                parkingID=fetchData(dataSnapshot);
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
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.6));
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


            if(latitude.equals(lat) && longitude.equals(lng)){
                key = ds.getKey();
                parkingSlot = new ParkingSlot(Status, latitude, longitude, address, parking_area,price);
                parkingSlot.setStatus("1");

            }

//
//
//            String locLatLng = new String();
//            locLatLng = latitude + "," + longitude;



        }

        return key;
    }


    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(v==buttonConfirm){String dbParkingSlotKey = "/" + parkingID + "/";
            String dbUserStatus ="/" + UserId +"/";
            Map<String, Object> updatedValues = new HashMap<>();
            Map<String,Object> updateUser = new HashMap<>();
            updatedValues.put(dbParkingSlotKey + "parking_area", parkingSlot.getParkingArea());
            updatedValues.put(dbParkingSlotKey + "address", parkingSlot.getAddress());
            updatedValues.put(dbParkingSlotKey + "latitude", parkingSlot.getLatitude());
            updatedValues.put(dbParkingSlotKey + "longitude", parkingSlot.getLongitude());
            updatedValues.put(dbParkingSlotKey + "Status", parkingSlot.getStatus());
            updatedValues.put(dbParkingSlotKey + "Price", parkingSlot.getPrice());


            mRef.updateChildren(updatedValues);

            updateUser.put(dbUserStatus+"bookedStatus",parkingID);
            mUserStatusRef.updateChildren(updateUser);
            startActivity(new Intent(PopUpActivity.this,TimerActivity.class));
//            Intent intent =new Intent(PopUpActivity.this,ParkingConformation.class);
//            intent.putExtra("selected_parking_slot",message);
//            startActivity(intent);
        }
    }
}
