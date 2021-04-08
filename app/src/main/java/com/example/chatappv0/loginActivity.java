package com.example.chatappv0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {
    private TextView username;
    private TextView password;
    private FirebaseAuth auth;
    private TextView forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username=findViewById(R.id.emailInput);
        password=findViewById(R.id.passwordInput);
        forgetPassword=findViewById(R.id.forgetPassword);

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
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgot_password(v);
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
    private Boolean validateEmailDialogBox(EditText reset,String val) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

        if (val.isEmpty()) {
            Toast.makeText(loginActivity.this, "Email not be empty",Toast.LENGTH_SHORT).show();
            return false;
        } else if (!val.matches(emailPattern)) {
            Toast.makeText(loginActivity.this, "Invalid Email",Toast.LENGTH_SHORT).show();
            return false;
        } else {
            reset.setError(null);
            return true;
        }
    }
    @SuppressLint("SetTextI18n")
    private void forgot_password(View v) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(loginActivity.this);
        View view = getLayoutInflater().inflate(R.layout.change_password_dialog, null);
        Button yes = view.findViewById(R.id.yes);
        Button no = view.findViewById(R.id.no);
        final EditText enterEmail = view.findViewById(R.id.enterEmail);
        enterEmail.setHint("Enter Your Email...");
        TextView heading = view.findViewById(R.id.textchangepassword);
        heading.setText("Change Password");
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = enterEmail.getText().toString().trim();
                if (validateEmailDialogBox(enterEmail, mail)) {
                    auth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(loginActivity.this, "Reset Link Has been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(loginActivity.this, "Error! Reset Link is Not Sent. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(loginActivity.this, "Not Sent", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}