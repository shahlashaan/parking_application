package com.example.smartpark;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class FirebaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceSlots;
    private List<ParkingSlot> parkingSlots = new ArrayList<>();

    public interface DataStatus{
        void DataIsLoaded(List<ParkingSlot> parkingSlots,List<String> keys);
    }


    public FirebaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceSlots = mDatabase.getReference();
    }
    public void readSlots(final DataStatus dataStatus){
        mReferenceSlots.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parkingSlots.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    ParkingSlot slot = keyNode.getValue(ParkingSlot.class);
                    parkingSlots.add(slot);
                }
                dataStatus.DataIsLoaded(parkingSlots,keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}