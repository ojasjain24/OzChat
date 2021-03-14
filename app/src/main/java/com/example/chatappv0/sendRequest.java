package com.example.chatappv0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chatappv0.Models.acceptRequestModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;

public class sendRequest extends AppCompatActivity {
    private ImageView profilePic;
    private TextView status,requestSent;
    private TextView profession,country,languages,gender;
    private Button send;
    acceptRequestModel userModel;
    ArrayList<acceptRequestModel> userList = new ArrayList<>();
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);
        profilePic = findViewById(R.id.DP);
        requestSent=findViewById(R.id.textView12);
        status=findViewById(R.id.STATUS);
        profession=findViewById(R.id.textView5);
        country=findViewById(R.id.textView6);
        languages=findViewById(R.id.textView7);
        gender=findViewById(R.id.textView9);
        Intent intent = getIntent();
        final String userId= intent.getStringExtra("userId");
        status.setText(intent.getStringExtra("status"));
        profession.setText(intent.getStringExtra("profession"));
        country.setText(intent.getStringExtra("country"));
        languages.setText(intent.getStringExtra("language"));
        gender.setText(intent.getStringExtra("gender"));
        if(intent.getStringExtra("pic") != null) {
            Picasso.get().load(Uri.parse(intent.getStringExtra("pic"))).into(profilePic);
        }else {
            profilePic.setImageResource(R.drawable.ic_launcher_foreground);
        }
        send=findViewById(R.id.sendRequest);
        final FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("requests");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        userModel = dataSnapshot.getValue(acceptRequestModel.class);
                        if (userModel.getSenderuid().equals(me.getUid()) && userModel.getReceveruid().equals(userId)) {
                            userModel.setRequestid(dataSnapshot.getKey());
                            userList.add(userModel);
                        }
                        if (userModel.getSenderuid().equals(me.getUid()) && userModel.getReceveruid().equals(userId)) {
                            userModel.setRequestid(dataSnapshot.getKey());
                            if(userModel.getRequestid()!=null){
                                break;
                            }
                        }
                    }
                    if(userList.size()==0) {
                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference userdata = FirebaseDatabase.getInstance().getReference().child("requests");
                                HashMap<String, String> usermap = new HashMap<>();
                                usermap.put("senderuid", user.getUid());
                                usermap.put("receveruid", userId);
                                userdata.push().setValue(usermap);
                            }
                        });
                    } else {
                        send.setText("Cancel request");
                        send.setBackgroundResource(R.drawable.cancel_btn);
                        requestSent.setVisibility(View.VISIBLE);
                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reference.child(userModel.getRequestid()).setValue(null);
                                startActivity(new Intent(sendRequest.this,allusersActivity.class));
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(sendRequest.this, "" + error, Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(sendRequest.this,allusersActivity.class));
    }
}