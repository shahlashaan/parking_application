package com.example.smartpark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class TempActivity extends AppCompatActivity {
    private TextView textView;
    private TextView textView2;
    public Bundle bundle;
    ArrayList<LatLng> array;
    ArrayList<String> stringArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        Intent intent = getIntent();
        bundle = intent.getExtras();
        array = bundle.getParcelableArrayList("Parking_slots_latlng");
        stringArrayList = bundle.getStringArrayList("Parking_slots_string");
        String lat = String.valueOf(array.get(1).getLatitude());
        String location = stringArrayList.get(1);
        textView=(TextView) findViewById(R.id.textView2);
        textView2=(TextView) findViewById(R.id.textView3);
        textView.setText(lat);
        textView2.setText(location);
    }
}
