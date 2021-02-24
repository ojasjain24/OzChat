package com.example.chatappv0;

import android.content.Intent;
import  android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappv0.Adapter.AllUsersAdapter;
import com.example.chatappv0.Adapter.allGroupsAdapter;
import com.example.chatappv0.Models.groupDataModel;
import com.example.chatappv0.Models.usersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class allGroupActivity extends AppCompatActivity {
    private allGroupsAdapter allGroupsadapter;
    private RecyclerView recyclerView;

    ArrayList<groupDataModel> userList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allusers);
        recyclerView=findViewById(R.id.userslist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        readusers();
    }




    private void readusers() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("groups");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    groupDataModel group = snapshot.getValue(groupDataModel.class);
                        userList.add(group);

                }
                allGroupsadapter = new allGroupsAdapter(getApplicationContext(), userList);
                recyclerView.setLayoutManager(new LinearLayoutManager(allGroupActivity.this));
                recyclerView.setAdapter(allGroupsadapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(allGroupActivity.this, "check your network connection", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(allGroupActivity.this,MainActivity.class));
    }
}