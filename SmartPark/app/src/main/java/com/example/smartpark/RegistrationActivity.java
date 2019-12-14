package com.example.smartpark;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editName;
    private EditText editMobileNumber;
    private TextView textViewSignin;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private FirebaseUser firebaseUser;
    DatabaseReference reff = FirebaseDatabase.getInstance().getReference();
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() !=null){
            //profile activity here
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));

        }

        progressDialog = new ProgressDialog(this);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        textViewSignin = (TextView) findViewById(R.id.textViewSignin);

        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }

    private void registerUser(){

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();




        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // Toast.makeText(MainActivity.this, "Registered Successfully",Toast.LENGTH_SHORT).show();
                    Log.d("TAG","User Created"+task.isSuccessful());

//                    sendEmailVerificationEmail();
                    firebaseUser = firebaseAuth.getInstance().getCurrentUser();

                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
//                                reff.push().setValue(user);
                                AddUserInfor(email,firebaseUser.getUid());



                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
//                                finish();
                            }
                            else{
                                overridePendingTransition(0,0);
                                finish();
                                overridePendingTransition(0,0);
                                startActivity(getIntent());
                            }
                        }
                    });

//                    reff.child(firebaseUser.getUid()).setValue(user);
//                    finish();
//                    startActivity(new Intent(getApplicationContext(),ProfileActivity.class));

                }else{
                    Toast.makeText(getApplicationContext(), "Could not register. please try again",Toast.LENGTH_SHORT).show();

                }
//                progressDialog.dismiss();
            }
        });

    }
    private void AddUserInfor(String email,String id){
        user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setMobileNo("0");
        user.setName("0");
        user.setBoookedStatus("0");
        reff.child("users").child(id).setValue(user);
    }

//    private void sendEmailVerificationEmail(){
//
//    }



    @Override
    public void onClick(View view) {
        if(view == buttonRegister){
            registerUser();
        }
        if(view == textViewSignin){
            //will open log in activity here
            startActivity(new Intent(this,LoginActivity.class));
        }
    }
}
