package com.example.smartpark;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private TextView textViewUserEmail;
    private Button buttonLogout;
    private Button buttonMapview;



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

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonMapview = (Button) findViewById(R.id.buttonMap);
        textViewUserEmail.setText("Welcome "+user.getEmail()  );
        buttonLogout.setOnClickListener(this);
        new FirebaseHelper().readSlots(new FirebaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<ParkingSlot> parkingSlots, List<String> keys) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == buttonMapview){
            startActivity(new Intent(this, DistanceCalculationActivity.class));

        }
        if(view == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));

        }
    }
}
