package com.example.smartpark;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class RemainingTimedata extends AppCompatActivity {

    private static final long START_TIME__N_MILLIS=600000;
    private TextView mTextViewCountdown;
    private Button mButtonStart;
    private Button mButtonExtend;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME__N_MILLIS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remaining_timedata);

        mTextViewCountdown = findViewById(R.id.textTimeCounter);

        mButtonStart = findViewById(R.id.button_start);

        mButtonExtend = findViewById(R.id.buttonExtend);

        startTimer();

    }

    private void startTimer(){
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
//        mButtonExtend.setVisibility(View.INVISIBLE);
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
}
