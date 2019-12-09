package com.example.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TimerActivity extends AppCompatActivity implements View.OnClickListener {
    private Chronometer chronometer;
    private boolean running;
    private FirebaseAuth firebaseAuth;
    private long pauseOffset;
    private boolean isPaused;
    private boolean wasRunning;
    private Button StopParkingHour;
    private Button GotTOProfile;
    private TextView textView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mUserStatusRef;
    private String UserId;
    private String parkingId;
    private String priceSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        firebaseAuth = FirebaseAuth.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        chronometer = findViewById(R.id.chronometer);
        StopParkingHour = findViewById(R.id.buttonFreeSlot);
        GotTOProfile = findViewById(R.id.buttonGotoProf2);
        textView = findViewById(R.id.TotalPrice);
        chronometer.setFormat("Time: %s");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("parkingSlots");
        mUserStatusRef = mFirebaseDatabase.getReference("users");
        if(savedInstanceState == null){
            chronometer.start();
            running=true;
            isPaused=false;
            StopParkingHour.setOnClickListener(this);
            GotTOProfile.setOnClickListener(this);

        }
        mUserStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parkingId = userParkingStatus(dataSnapshot);
//                String lotnPrice=userParkingStatus(dataSnapshot);
//                parkingId = lotnPrice.split(",")[0];
//                priceSlot = lotnPrice.split(",")[1];
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                priceSlot=parkingslotPrice(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private String userParkingStatus(DataSnapshot dataSnapshot) {
        String UserStatus="0";
//        String bookedStatus;
        for(DataSnapshot ds:dataSnapshot.getChildren()){
//            User uInfo = new User();
            String key = ds.getKey();
            //            uInfo.setEmail(ds.child("email").getValue(User.class).getEmail());
//            uInfo.setId(ds.child("id").getValue(User.class).getId());
            if(key.equals(UserId)) {
                String bookedStatus = ds.child("bookedStatus").getValue(String.class);
//                String price = ds.child("Price").getValue(String.class);
                if(!bookedStatus.equals("0")){
//                    priceSlot=price;
                    UserStatus=bookedStatus;

                }

                return UserStatus;

            }

        }
        return UserStatus;
    }

    private String parkingslotPrice(DataSnapshot dataSnapshot) {
        String Price="0";
//        String bookedStatus;
        for(DataSnapshot ds:dataSnapshot.getChildren()){
//            User uInfo = new User();
            String key = ds.getKey();
            //            uInfo.setEmail(ds.child("email").getValue(User.class).getEmail());
//            uInfo.setId(ds.child("id").getValue(User.class).getId());
            if(key.equals(parkingId)) {
                String slotStatus = ds.child("Status").getValue(String.class);
                String priceSlot = ds.child("Price").getValue(String.class);
                if(!slotStatus.equals("0")){
//                    priceSlot=price;
                    Price=priceSlot;

                }

                return Price;

            }

        }
        return Price;
    }




    @Override
    public void onClick(View v) {
        if(v==StopParkingHour){
            if(running){
                chronometer.stop();
                pauseOffset= SystemClock.elapsedRealtime()-chronometer.getBase();
                running=false;
                isPaused=true;
                double priceLotDouble = Double.parseDouble(priceSlot);
                int value = (int) ((pauseOffset/(1000*60*60)+1)*priceLotDouble);
                String dbParkingSlotKey = "/" + parkingId + "/";
                String dbUserStatus ="/" + UserId +"/";
                Map<String, Object> updatedValues = new HashMap<>();
                Map<String,Object> updateUser = new HashMap<>();

                updatedValues.put(dbParkingSlotKey + "Status","0");



                mRef.updateChildren(updatedValues);

                updateUser.put(dbUserStatus+"bookedStatus","0");
                mUserStatusRef.updateChildren(updateUser);

                textView.setText(Integer.toString(value));
//                textView.setText(parkingId);
                textView.setVisibility(View.VISIBLE);
            }
            else if(running==false && isPaused==true){
                chronometer.setBase(SystemClock.elapsedRealtime());
                pauseOffset=0;
                isPaused=false;

            }
        }
        if(v==GotTOProfile){
            startActivity(new Intent(this, ProfileActivity.class));

        }
    }
}
