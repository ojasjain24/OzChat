package com.example.chatappv0;

import android.content.Intent;
import  android.os.Bundle;

import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappv0.Adapter.addMemberAdapter;

import com.example.chatappv0.Models.groupMemberModel;
import com.example.chatappv0.Models.usersModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class addMember extends AppCompatActivity {
    private addMemberAdapter addMemberAdapter;
    private RecyclerView recyclerView;
    FloatingActionButton nextBtn;
    ArrayList<usersModel> userList = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        nextBtn=findViewById(R.id.floatingActionButtonam);
        recyclerView=findViewById(R.id.newMemberList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        readusers();
        final Intent intent=getIntent();
        final String nodeId=intent.getStringExtra("nodeId");
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMemberAdapter.uploadOnDataBase(nodeId);
                Intent i = new Intent(addMember.this, groupChat.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("nodeId", intent.getStringExtra("nodeId"));
                i.putExtra("name",intent.getStringExtra("name"));
                i.putExtra("status", intent.getStringExtra("status"));
                i.putExtra("pic", intent.getStringExtra("pic"));
                startActivity(i);
            }
        });
    }
    private void readusers() {
        final Intent intent=getIntent();
        final FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users");
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(intent.getStringExtra("nodeId")).child("members");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final usersModel user = snapshot.getValue(usersModel.class);
                    userList.add(user);
                }
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            groupMemberModel groupMemberModel=snapshot1.getValue(groupMemberModel.class);
                            int size= userList.size();
                            for(int i = size - 1; i >= 0; i--) {
                                if(userList.get(i).getUserid().equals(groupMemberModel.getUid())){
                                    userList.remove(i);
                                }
                            }
                        }
                        addMemberAdapter = new addMemberAdapter(getApplicationContext(), userList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(addMember.this));
                        recyclerView.setAdapter(addMemberAdapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(addMember.this, "check your network connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}