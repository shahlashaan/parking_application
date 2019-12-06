package com.example.smartpark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

public class TimerActivity extends AppCompatActivity implements View.OnClickListener {
    private Chronometer chronometer;
    private boolean running;
    private long pauseOffset;
    private boolean isPaused;
    private boolean wasRunning;
    private Button StopParkingHour;
    private Button GotTOProfile;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        chronometer = findViewById(R.id.chronometer);
        StopParkingHour = findViewById(R.id.buttonFreeSlot);
        GotTOProfile = findViewById(R.id.buttonGotoProf2);
        textView = findViewById(R.id.TotalPrice);
        chronometer.setFormat("Time: %s");

        if(savedInstanceState == null){
            chronometer.start();
            running=true;
            isPaused=false;
            StopParkingHour.setOnClickListener(this);
            GotTOProfile.setOnClickListener(this);

        }



    }


    @Override
    public void onClick(View v) {
        if(v==StopParkingHour){
            if(running){
                chronometer.stop();
                pauseOffset= SystemClock.elapsedRealtime()-chronometer.getBase();
                running=false;
                isPaused=true;
                int value = (int) (pauseOffset/(1000*60*60));

                textView.setText(Integer.toString(value));
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
