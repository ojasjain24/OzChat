package com.example.chatappv0;

import android.content.Intent;
import  android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatappv0.Adapter.selectUserForGroupAdapter;
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

public class displayUserForGroup extends AppCompatActivity {
    private selectUserForGroupAdapter selectUserForGroupAdapter;
    private RecyclerView recyclerView;
    FloatingActionButton nextBtn;
    ArrayList<usersModel> userList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        nextBtn=findViewById(R.id.floatingActionButton);
        recyclerView=findViewById(R.id.making_group_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        readusers();

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUserForGroupAdapter.uploadOnDataBase();
                Intent i =new Intent(displayUserForGroup.this,groupNameActivity.class);
                i.putExtra("nodeId",selectUserForGroupAdapter.dataBaseKey());
                startActivity(i);
            }
        });
    }
    private void readusers() {
        final FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    usersModel user = snapshot.getValue(usersModel.class);
                    if(!user.getUserid().equals(me.getUid())) {
                        userList.add(new usersModel(user.getUsername(), user.getStatus(), user.getUserid(), user.getEmail(), user.getImageurl()));
                    }
                }
                selectUserForGroupAdapter = new selectUserForGroupAdapter(getApplicationContext(), userList);
                recyclerView.setLayoutManager(new LinearLayoutManager(displayUserForGroup.this));
                recyclerView.setAdapter(selectUserForGroupAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(displayUserForGroup.this, "check your network connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}