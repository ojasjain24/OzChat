package com.example.chatappv0;

import android.content.Intent;
import  android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappv0.Adapter.AllUsersAdapter;
import com.example.chatappv0.Models.usersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class allusersActivity extends AppCompatActivity {
    private AllUsersAdapter allUsersAdapter;
    private RecyclerView recyclerView;

    ArrayList<usersModel> userList = new ArrayList<>();

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
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    usersModel user = snapshot.getValue(usersModel.class);
                    if(!currentUser.getUid().equals(user.getUserid())){
                        userList.add(new usersModel(user.getUsername(), user.getStatus(), user.getUserid(),user.getEmail(),user.getImageurl()));
                    }
                }
                allUsersAdapter = new AllUsersAdapter(getApplicationContext(), userList);
                recyclerView.setLayoutManager(new LinearLayoutManager(allusersActivity.this));
                recyclerView.setAdapter(allUsersAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(allusersActivity.this, "check your network connection", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(allusersActivity.this, MainActivity.class));
    }
}