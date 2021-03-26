package com.example.chatappv0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {
    private TextView username;
    private TextView password;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username=findViewById(R.id.emailInput);
        password=findViewById(R.id.passwordInput);

        Button login = findViewById(R.id.login);
        Button signup = findViewById(R.id.signupcheck);
        auth = FirebaseAuth.getInstance();
        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               startActivity(new Intent(loginActivity.this,signupActivity.class));
               finish();
           }

        });
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String usernameInput=username.getText().toString();
                String passwordInput=password.getText().toString();
                if(!(usernameInput.trim().equals("")||passwordInput.trim().equals(""))) {
                    loginUser(usernameInput, passwordInput);
                }else{
                    Toast.makeText(loginActivity.this, "please fill all the information", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void loginUser(String username,String password) {
        auth.signInWithEmailAndPassword(username , password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                if(auth.getCurrentUser().isEmailVerified()){
                    Toast.makeText(loginActivity.this, "log-in successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(loginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),"Please Verify Your Email",Toast.LENGTH_SHORT).show();
                }
            }
        });
        auth.signInWithEmailAndPassword(username,password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(loginActivity.this, "entered email or password not valid", Toast.LENGTH_SHORT).show();
            }
        });
    }
    protected void onStart(){
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            if(auth.getCurrentUser().isEmailVerified()){
                startActivity(new Intent(loginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }

        }

    }
}