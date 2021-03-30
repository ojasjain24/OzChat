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
import com.example.chatappv0.Adapter.requestsAdapter;
import com.example.chatappv0.Models.acceptRequestModel;
import com.example.chatappv0.Models.usersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class allRequest extends AppCompatActivity {
    private com.example.chatappv0.Adapter.requestsAdapter requestsAdapter;
    private RecyclerView recyclerView;
    private LottieAnimationView empty,loading;
    private TextView noFriends,loadingText;
    ArrayList<acceptRequestModel> userList = new ArrayList<>();
    String senderUid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_request);
        noFriends=findViewById(R.id.noFriendsTextaur);
        empty=findViewById(R.id.emptyaur);
        loading=findViewById(R.id.loadingaur);
        loadingText=findViewById(R.id.loadingTextaur);
        recyclerView=findViewById(R.id.requestList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        readusers();
    }
    private void readusers() {
        loading.setSpeed(1);
        loading.playAnimation();
        final FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("requests");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final acceptRequestModel user = snapshot.getValue(acceptRequestModel.class);
                    user.setRequestid(snapshot.getKey());
                    senderUid=user.getSenderuid();
                    if (user.getReceveruid().equals(me.getUid())){
                        userList.add(user);
                    }
                }
                loadingText.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.INVISIBLE);
                setAdapter();
                EmptyListAnimation();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(allRequest.this, "check your network connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setAdapter(){
        requestsAdapter = new requestsAdapter(getApplicationContext(), userList);
        recyclerView.setLayoutManager(new LinearLayoutManager(allRequest.this));
        recyclerView.setAdapter(requestsAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(allRequest.this,MainActivity.class));
    }
    private void EmptyListAnimation() {
        if (userList.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
            noFriends.setVisibility(View.VISIBLE);
            empty.setSpeed(1);
            empty.playAnimation();
        } else {
            empty.setVisibility(View.GONE);
            noFriends.setVisibility(View.GONE);
        }
    }
}