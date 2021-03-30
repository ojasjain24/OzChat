package com.example.chatappv0;

import android.content.Intent;
import  android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
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
import java.util.Objects;

public class allGroupActivity extends AppCompatActivity {
    private allGroupsAdapter allGroupsadapter;
    private RecyclerView recyclerView;
    private LottieAnimationView empty,loading;
    private TextView noFriends,loadingText;
    ArrayList<groupDataModel> userList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allusers);
        noFriends=findViewById(R.id.noFriendsTextaau);
        empty=findViewById(R.id.emptyaau);
        loading=findViewById(R.id.loadingaau);
        loadingText=findViewById(R.id.loadingTextaau);
        recyclerView=findViewById(R.id.userslist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        readusers();
    }

    private void readusers() {
        loading.setSpeed(1);
        loading.playAnimation();
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("groups");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final groupDataModel group = snapshot.getValue(groupDataModel.class);
                    if(group.getType().startsWith("Public")) {
                        final DatabaseReference reference =databaseReference.child(group.getNodeid()).child("members");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.hasChild(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                                    userList.add(group);
                                }
                                loadingText.setVisibility(View.INVISIBLE);
                                loading.setVisibility(View.INVISIBLE);
                                allGroupsadapter = new allGroupsAdapter(getApplicationContext(), userList);
                                recyclerView.setLayoutManager(new LinearLayoutManager(allGroupActivity.this));
                                recyclerView.setAdapter(allGroupsadapter);
                                EmptyListAnimation();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
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
    private void EmptyListAnimation() {
        if (userList.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
            noFriends.setVisibility(View.VISIBLE);
            noFriends.setText("WOW! You are in every public group");
            empty.setSpeed(1);
            empty.playAnimation();
        } else {
            empty.setVisibility(View.GONE);
            noFriends.setVisibility(View.GONE);
        }
    }
}