package com.example.smartpark;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ViewProfileActivity  extends AppCompatActivity {
    private static final String TAG = "ViewProfileActivity";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRef;
    private DatabaseReference microRef;
    private DatabaseReference connectedRef;
    private String userID;
    private TextView textview_username,textView_mobileno,textView_emailID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_profile);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, com.example.smartpark.LoginActivity.class));
        }
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
//        mRef = mFirebaseDatabase.getReference().child("users");
        mRef = mFirebaseDatabase.getReference("users");
        textview_username = (TextView)findViewById(R.id.textview_username);
        textView_mobileno = (TextView)findViewById(R.id.textView_mobileno);
        textView_emailID = (TextView)findViewById(R.id.textView_emailID);
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
        String email="0";
        String name="0";
        String mobileNo="0";
        for(DataSnapshot ds:dataSnapshot.getChildren()){
            User uInfo = new User();
            String key = ds.getKey();
            if(key.equals(userID)){
                email = ds.child("email").getValue(String.class);
//                String ID = ds.child("id").getValue(String.class);
                name = ds.child("Name").getValue(String.class);
                mobileNo = ds.child("Mobile").getValue(String.class);
//                array.add(ID);

            }
//
            textView_emailID.setText("Email: "+email);
            textView_mobileno.setText("Mobile: "+mobileNo);
            textview_username.setText("Name: "+name);
        }

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

    public void buttonClickedEditName(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_edit_name, null);
        final EditText etUsername = alertLayout.findViewById(R.id.et_username);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Name Edit");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = etUsername.getText().toString();
                String dbUserStatus ="/" + userID +"/";
                Map<String,Object> updateUser = new HashMap<>();
                updateUser.put(dbUserStatus+"Name",name);
                mRef.updateChildren(updateUser);
                etUsername.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void buttonClickedEditMobileno(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_editmobileno, null);
        final EditText etMobileNo = alertLayout.findViewById(R.id.et_mobileNo);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("MobileNo Edit");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = etMobileNo.getText().toString();
                String dbUserStatus ="/" + userID +"/";
                Map<String,Object> updateUser = new HashMap<>();
                updateUser.put(dbUserStatus+"Mobile",name);
                mRef.updateChildren(updateUser);
                etMobileNo.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void buttonClickedHome(View view) {
        finish();
        startActivity(new Intent(this, ProfileActivity.class));

    }
}
