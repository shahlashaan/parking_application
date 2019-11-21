package com.example.smartpark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PopUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView popUpText;
    private Button buttonConfirm;
    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);
        Intent intent = getIntent();
        message = intent.getStringExtra("confirmSlot");
        popUpText=(TextView) findViewById(R.id.PopUpText);
        popUpText.setText("Confirm Slot Selection?");
        buttonConfirm = (Button) findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.6));
    }

    @Override
    public void onClick(View v) {
        if(v==buttonConfirm){
            Intent intent =new Intent(PopUpActivity.this,ParkingConformation.class);
            intent.putExtra("selected_parking_slot",message);
            startActivity(intent);
        }
    }
}
