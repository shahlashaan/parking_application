package com.example.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewDatabase extends AppCompatActivity {

    private static final String TAG = "ViewDatabase";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRef;
    private DatabaseReference microRef;
    private DatabaseReference connectedRef;
    private String userID;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_database);

        mListView = (ListView) findViewById(R.id.listview);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
//        mRef = mFirebaseDatabase.getReference().child("users");
        mRef = mFirebaseDatabase.getReference("users");
        FirebaseUser user = mAuth.getCurrentUser();
        userID=user.getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG,"onAuthStateChanged:signed_in:" + user.getUid());
//                    toastMessage("signed in:" + user.getEmail());
                }
                else {
                    Log.d(TAG,"onAuthStateChanged:signed_out:");
                    toastMessage("signed out:");
                }
            }
        };
        microRef = mRef.child(userID);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                boolean connected = dataSnapshot.getValue(Boolean.class);
//                if(connected) {
//                    Log.d(TAG,"connected");
//                    toastMessage("connected:");

                showData(dataSnapshot,userID);
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

    private void showData(DataSnapshot dataSnapshot,String userID) {
        ArrayList<String> array = new ArrayList<>();

        for(DataSnapshot ds:dataSnapshot.getChildren()){
            User uInfo = new User();
            String key = ds.getKey();
            if(key.equals(userID)){
                String email = ds.child("email").getValue(String.class);
                String ID = ds.child("id").getValue(String.class);
                array.add(ID);
                array.add(email);
            }
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
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,array);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
