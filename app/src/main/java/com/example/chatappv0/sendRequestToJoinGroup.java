package com.example.chatappv0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chatappv0.Models.groupRequestsModel;
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
import java.util.Objects;

public class sendRequestToJoinGroup extends AppCompatActivity {
    private ImageView profilePic;
    private TextView status,requestSent;
    private Button send;
    groupRequestsModel groupModel;
    ArrayList<groupRequestsModel> userList = new ArrayList<>();
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources.Theme theme = super.getTheme();
        new ThemeSetter().aSetTheme(this,theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request_to_join_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        profilePic = findViewById(R.id.groupDP);
        status = findViewById(R.id.groupDescription);
        requestSent=findViewById(R.id.requestSentDisplay);
        TextView name = findViewById(R.id.groupName);
        Intent intent = getIntent();
        final String nodeId= intent.getStringExtra("nodeId");
        final String userName= intent.getStringExtra("name");
        final String userStatus= intent.getStringExtra("status");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        name.setText(userName);
        status.setText(userStatus);
        if(intent.getStringExtra("pic") != null) {
            Picasso.get().load(Uri.parse(intent.getStringExtra("pic"))).into(profilePic);
        }else {
            profilePic.setImageResource(R.drawable.ic_launcher_foreground);
        }
        send=findViewById(R.id.sendRequest);
        final FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        if(Objects.equals(intent.getStringExtra("type"), "Public-join without asking")){
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("members").child(me.getUid());
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("status","member");
                    hashMap.put("uid",me.getUid());
                    databaseReference.setValue(hashMap);
                    startActivity(new Intent(sendRequestToJoinGroup.this,allGroupActivity.class));
                }
            });
        }else{
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("requests");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        groupModel = dataSnapshot.getValue(groupRequestsModel.class);
                        if (reference.child(me.getUid())!=null) {
                            userList.add(groupModel);
                        }
                    }
                    if(userList.size()==0) {
                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference userdata = FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("requests").child(user.getUid());
                                HashMap<String, String> usermap = new HashMap<>();
                                usermap.put("userid",user.getUid());
                                userdata.setValue(usermap);
                            }
                        });
                    } else {
                        send.setText("Cancel request");
                        send.setBackgroundResource(R.drawable.cancel_btn);
                        requestSent.setVisibility(View.VISIBLE);
                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reference.child(user.getUid()).setValue(null);
                                startActivity(new Intent(sendRequestToJoinGroup.this,allGroupActivity.class));
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(sendRequestToJoinGroup.this, "" + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(sendRequestToJoinGroup.this,allGroupActivity.class));
    }
}