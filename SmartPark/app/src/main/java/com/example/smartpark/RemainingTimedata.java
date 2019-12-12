package com.example.smartpark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class RemainingTimedata extends AppCompatActivity implements View.OnClickListener {

    private static final long START_TIME__N_MILLIS=600000;
    private TextView mTextViewCountdown;
    private Button mButtonStart;
    private Button mButtonExtend;
    private Button mButtonProfile;
    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;
    private long mTimeLeftInMillis=START_TIME__N_MILLIS ;
    private long mEndTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remaining_timedata);

        mTextViewCountdown = findViewById(R.id.textTimeCounter);

        mButtonStart = findViewById(R.id.button_start);

        mButtonExtend = findViewById(R.id.buttonExtend);
        mButtonProfile = findViewById(R.id.buttonToProfile);

        mButtonProfile.setOnClickListener(this);
        startTimer();


//        mButtonStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startTimer();
//
//            }
//        });

    }

    private void startTimer(){
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis=millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;

            }
        }.start();

        mTimerRunning = true;
        mButtonStart.setVisibility(View.INVISIBLE);
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis/1000)/60;
        int seconds = (int) (mTimeLeftInMillis/1000)%60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%2d:%2d",minutes,seconds);
        mTextViewCountdown.setText(timeLeftFormatted);
    }

    private void extendTime(){
        mTimeLeftInMillis = START_TIME__N_MILLIS;
        updateCountDownText();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("millisLeft",mTimeLeftInMillis);
        outState.putBoolean("timeRunning",mTimerRunning);
        outState.putLong("endTime",mEndTime);
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//        mTimeLeftInMillis = savedInstanceState.getLong("millisLeft");
//        mTimerRunning = savedInstanceState.getBoolean("timerRunning");
//        updateCountDownText();
//        if (mTimerRunning){
//            mEndTime = savedInstanceState.getLong("endTime");
//            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
//            startTimer();
//        }
//    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("millisLeft",mTimeLeftInMillis);
        editor.putBoolean("timerRunning",mTimerRunning);
        editor.putLong("endTime",mEndTime);

        editor.apply();
//
//        if (mCountDownTimer != null){
//            mCountDownTimer.cancel();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        mTimeLeftInMillis = prefs.getLong("millisLeft",START_TIME__N_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning",false);

        updateCountDownText();

        if(mTimerRunning){
            mEndTime = prefs.getLong("endTime",0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            if (mTimeLeftInMillis < 0){
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
            }else {
                startTimer();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v == mButtonProfile){
            startActivity(new Intent(this, ProfileActivity.class));

        }
    }
    //    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
//            setContentView(R.layout.activity_remaining_timedata);
//        }
//        else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            setContentView(R.layout.activity_remaining_timedata);
//        }
//    }
}
