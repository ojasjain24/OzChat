package com.example.chatappv0;

import android.content.Intent;
import android.content.res.Resources;
import  android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.chatappv0.Adapter.allGroupsAdapter;
import com.example.chatappv0.Models.groupDataModel;
import com.google.firebase.auth.FirebaseAuth;
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
        Resources.Theme theme = super.getTheme();
        new ThemeSetter().aSetTheme(this,theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allusers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                    if(group.getType() !=null && group.getType().toLowerCase().startsWith("public")) {
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem item = menu.findItem(R.id.search);
        final android.widget.SearchView searchView = (android.widget.SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchEmployee(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchView.getQuery().toString().trim().length() != 0) {
                    searchEmployee(newText);
                }
                else{
                    searchEmployee("");
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void searchEmployee(String query) {
        ArrayList<groupDataModel> searchList = new ArrayList<>();
        loading.setSpeed(1);
        loading.playAnimation();
        for(int i = 0; i < userList.size(); i++) {
            if(userList.get(i).getName().toLowerCase().startsWith(query.toLowerCase().trim())){
                searchList.add(userList.get(i));
            }
            loadingText.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.INVISIBLE);
            allGroupsadapter = new allGroupsAdapter(getApplicationContext(), searchList);
            recyclerView.setLayoutManager(new LinearLayoutManager(allGroupActivity.this));
            recyclerView.setAdapter(allGroupsadapter);
            EmptyListAnimation();
            if(query.equals("")){
                allGroupsadapter = new allGroupsAdapter(getApplicationContext(), userList);
                recyclerView.setLayoutManager(new LinearLayoutManager(allGroupActivity.this));
                recyclerView.setAdapter(allGroupsadapter);
            }
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}