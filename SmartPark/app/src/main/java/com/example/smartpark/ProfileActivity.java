package com.example.smartpark;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProfileActivity";

    private FirebaseAuth firebaseAuth;
    private TextView textViewData;
    private TextView textViewUserEmail;
    private Button buttonLogout;
    private Button buttonMapview;
    private Button buttonViewData;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    String valval = new String();
    private List<ParkingSlot> parkingSlots = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, com.example.smartpark.LoginActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
//        textViewData = (TextView) findViewById(R.id.textViewData);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonMapview = (Button) findViewById(R.id.buttonMap);
        buttonViewData = (Button) findViewById(R.id.buttonData);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("parkingSlots");

        textViewUserEmail.setText("Welcome "+user.getEmail()  );
        buttonLogout.setOnClickListener(this);
        buttonMapview.setOnClickListener(this);
        buttonViewData.setOnClickListener(this);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                boolean connected = dataSnapshot.getValue(Boolean.class);
//                if(connected) {
//                    Log.d(TAG,"connected");
//                    toastMessage("connected:");

                fetchData(dataSnapshot);
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

    }

    private void fetchData(DataSnapshot dataSnapshot) {
        ArrayList<ParkingSlot> array = new ArrayList<>();

        for(DataSnapshot ds:dataSnapshot.getChildren()){
//            User uInfo = new User();
            String key = ds.getKey();
            //            uInfo.setEmail(ds.child("email").getValue(User.class).getEmail());
//            uInfo.setId(ds.child("id").getValue(User.class).getId());
            String Status = ds.child("Status").getValue(String.class);
            String address = ds.child("address").getValue(String.class);
            String latitude = ds.child("latitude").getValue(String.class);
            String longitude = ds.child("longitude").getValue(String.class);
            String parking_area = ds.child("parking_area").getValue(String.class);
            ParkingSlot parkingSlot = new ParkingSlot(Status,latitude,longitude,address,parking_area) ;



//            parkingSlot.setStatus(ds.child("Status").getValue(ParkingSlot.class).getStatus());
//            parkingSlot.setAddress(ds.child("address").getValue(ParkingSlot.class).getAddress());
//            parkingSlot.setLatitude(ds.child("latitude").getValue(ParkingSlot.class).getLatitude());
//            parkingSlot.setLongitude(ds.child("longitude").getValue(ParkingSlot.class).getLongitude());
//            parkingSlot.setParkingArea(ds.child("parking_area").getValue(ParkingSlot.class).getParkingArea());

            array.add(parkingSlot);




//            String email = ds.child("Status").getValue(String.class);
//            String ID = ds.child("address").getValue(String.class);
//            array.add(ID);
//            array.add(email);

//            String email = ds.child("email").getValue(String.class);
//            String ID = ds.child("id").getValue(String.class);

//            uInfo.setEmail(ds.child("email").getValue(User.class).getEmail());
//            uInfo.setId(ds.child("id").getValue(User.class).getId());
//
//            Log.d(TAG,"showdata:email:" + uInfo.getEmail());
//            Log.d(TAG,"showdata:uid:" + uInfo.getId());


//            Log.d(TAG,"showdata:email:" + email);
//            Log.d(TAG,"showdata:uid:" + ID);
//            ArrayList<String> array = new ArrayList<>();
//            array.add(ID);
//            array.add(email);
//            array.add(key);

        }

    }

    @Override
    public void onClick(View view) {
        if(view == buttonMapview){
            startActivity(new Intent(this, DistanceCalculationActivity.class));

        }
        if(view == buttonViewData){
            startActivity(new Intent(this, ViewDatabase.class));

        }

        if(view == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));

        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
