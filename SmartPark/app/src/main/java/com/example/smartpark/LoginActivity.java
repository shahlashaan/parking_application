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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;
    private TextView textViewForgetPassword;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() !=null){
            //profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));

        }

        progressDialog = new ProgressDialog(this);


        editTextEmail =  (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignin);
        textViewSignup = (TextView) findViewById(R.id.textViewSignUp);
        textViewForgetPassword = (TextView) findViewById(R.id.textViewForgetPassword);

        buttonSignIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
        textViewForgetPassword.setOnClickListener(this);

    }

    private void userLogin(){
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

        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if(!task.isSuccessful()){

                    //start the profile activity
                    Log.d("TAG","signInWithEmailed:failed"+task.getException());
//                    finish();
//                    startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                }
                else {
                    checkIfEmailVerified();
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(),ProfileActivity.class));


                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        if(view == buttonSignIn){
            userLogin();
        }
        if(view == textViewSignup){
            startActivity(new Intent(this,RegistrationActivity.class));
        }
        if(view == textViewForgetPassword){
            startActivity(new Intent(this,ForgetPassword.class));
        }
    }
    private void checkIfEmailVerified(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.isEmailVerified()){
            finish();
            Toast.makeText(LoginActivity.this,"Successfully Logged in",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed(){

        finish();
    }
}
