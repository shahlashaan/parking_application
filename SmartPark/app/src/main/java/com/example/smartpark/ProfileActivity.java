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
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProfileActivity";

    private FirebaseAuth firebaseAuth;
    private TextView textViewUserBookId;
    private TextView textViewUserEmail;
    private Button buttonLogout;
    private Button buttonMapview;
    private Button buttonViewData;
    private Button buttonRemainingTime;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mUserStatusRef;
    private Button buttonTemp;
    private String userId;
    private boolean alreadyParked;

    String valval = new String();
//    private List<ParkingSlot> parkingSlots = new ArrayList<>();
    ArrayList<ParkingSlot> array = new ArrayList<>();
    ArrayList<LatLng> locationArray = new ArrayList<>();
    ArrayList<String> stringLocationArray = new ArrayList<>();



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
        userId = firebaseAuth.getCurrentUser().getUid();
//        textViewData = (TextView) findViewById(R.id.textViewData);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserBookId = (TextView) findViewById(R.id.textViewUserBookId);

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonMapview = (Button) findViewById(R.id.buttonMap);
        buttonViewData = (Button) findViewById(R.id.buttonData);
        buttonTemp = (Button) findViewById(R.id.checkData);
        buttonRemainingTime = (Button) findViewById(R.id.buttonTime);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("parkingSlots");
        mUserStatusRef = mFirebaseDatabase.getReference("users");




        mUserStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                alreadyParked=userParkingStatus(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

//        if(alreadyParked==true){
//            buttonMapview.setEnabled(false);
//            buttonTemp.setEnabled(false);
//        }
//        else if(alreadyParked==false){
//            buttonRemainingTime.setEnabled(false);
//
//        }
        textViewUserEmail.setText("Welcome "+user.getEmail()  );
        buttonLogout.setOnClickListener(this);
        buttonMapview.setOnClickListener(this);
        buttonViewData.setOnClickListener(this);
        buttonTemp.setOnClickListener(this);
        buttonRemainingTime.setOnClickListener(this);
    }

    private void fetchData(DataSnapshot dataSnapshot) {

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
            String price = ds.child("Price").getValue(String.class);

            ParkingSlot parkingSlot = new ParkingSlot(Status,latitude,longitude,address,parking_area,price) ;


            String locLatLng = new String();
            locLatLng = latitude+","+longitude;
            double DLAT = Double.parseDouble(latitude);
            double DLNG = Double.parseDouble(longitude);


            if(Status.equals("0")) {
                array.add(parkingSlot);
                locationArray.add(new LatLng(DLAT,DLNG));
                stringLocationArray.add(locLatLng);
            }



        }

    }

    private boolean userParkingStatus(DataSnapshot dataSnapshot) {
        boolean checkUserStatus=false;
//        String bookedStatus;
        for(DataSnapshot ds:dataSnapshot.getChildren()){
//            User uInfo = new User();
            String key = ds.getKey();
            //            uInfo.setEmail(ds.child("email").getValue(User.class).getEmail());
//            uInfo.setId(ds.child("id").getValue(User.class).getId());
            if(key.equals(userId)) {
                String bookedStatus = ds.child("bookedStatus").getValue(String.class);
                if(!bookedStatus.equals("0")){
                    checkUserStatus=true;
                    buttonMapview.setEnabled(false);
                    buttonTemp.setEnabled(false);

                }
                else if(bookedStatus.equals("0")) {
                    buttonRemainingTime.setEnabled(false);

                }
                textViewUserBookId.setText(Boolean.toString(checkUserStatus));

                return checkUserStatus;

            }

        }
        return checkUserStatus;
    }

    @Override
    public void onClick(View view) {
        if(view == buttonMapview){
            Intent intent = new Intent(ProfileActivity.this,DistanceCalculationActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putParcelableArrayList("Parking_slots",array);
//            intent.putParcelableArrayListExtra("Parking_slots",array);

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("Parking_slots_latlng",locationArray);
            bundle.putStringArrayList("Parking_slots_string",stringLocationArray);
            intent.putExtras(bundle);

            startActivity(intent);
//            startActivity(new Intent(this, DistanceCalculationActivity.class));

        }
        if(view == buttonViewData){
            startActivity(new Intent(this, ViewDatabase.class));

        }

        if(view == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));

        }
        if(view == buttonTemp){
            Intent intent = new Intent(ProfileActivity.this,TempActivity.class);
            Bundle bundle = new Bundle();
//            bundle.putParcelableArrayList("Parking_slots",array);

            bundle.putParcelableArrayList("Parking_slots_latlng",locationArray);
            bundle.putStringArrayList("Parking_slots_string",stringLocationArray);
            intent.putExtras(bundle);
            startActivity(intent);
//            startActivity(new Intent(this, DistanceCalculationActivity.class));

        }
        if(view == buttonRemainingTime){
            startActivity(new Intent(this, TimerActivity.class));

        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
