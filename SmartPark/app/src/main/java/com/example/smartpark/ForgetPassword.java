package com.example.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgetPassword extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ForgetPassword";

    private Button buttonSendEmail;
    private Button buttonGoToSignIn;

    private EditText editTextEmail;
    private TextView textViewResetEmail;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail =  (EditText) findViewById(R.id.editTextEmail2);

        buttonSendEmail = (Button) findViewById(R.id.buttonSentEmailLink);
        buttonGoToSignIn = (Button) findViewById(R.id.buttonGotoSignIn);
        textViewResetEmail = (TextView) findViewById(R.id.textViewPasswordReset);

        buttonSendEmail.setOnClickListener(this);
        buttonGoToSignIn.setOnClickListener(this);

    }

    public void SentEmail(){
        String email = editTextEmail.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG,"Email sent");
//                    buttonGoToSignIn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==buttonSendEmail){
            SentEmail();
        }
        if (v==buttonGoToSignIn){
            startActivity(new Intent(this,LoginActivity.class));

        }
    }
}
