package com.affixchat.chatappv0;

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
import com.affixchat.chatappv0.Adapter.AllUsersAdapter;
import com.affixchat.chatappv0.Models.usersModel;
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
    private LottieAnimationView empty,loading;
    private TextView noFriends,loadingText;
    ArrayList<usersModel> userList = new ArrayList<>();

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
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final usersModel user = snapshot.getValue(usersModel.class);
                    if(!currentUser.getUid().equals(user.getUserid())){
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("friends");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.hasChild(user.getUserid())) {
                                    userList.add(user);
                                }
                                loadingText.setVisibility(View.INVISIBLE);
                                loading.setVisibility(View.INVISIBLE);
                                allUsersAdapter = new AllUsersAdapter(getApplicationContext(), userList);
                                recyclerView.setLayoutManager(new LinearLayoutManager(allusersActivity.this));
                                recyclerView.setAdapter(allUsersAdapter);
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
                Toast.makeText(allusersActivity.this, "check your network connection", Toast.LENGTH_SHORT).show();

            }
        });

        loadingText.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.INVISIBLE);
        EmptyListAnimation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(allusersActivity.this, MainActivity.class));
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
        ArrayList<usersModel> searchList = new ArrayList<>();
        loading.setSpeed(1);
        loading.playAnimation();
        for(int i = 0; i < userList.size(); i++) {
            if(userList.get(i).getUsername().toLowerCase().startsWith(query.toLowerCase().trim())){
                searchList.add(userList.get(i));
            }
            loadingText.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.INVISIBLE);
            allUsersAdapter = new AllUsersAdapter(getApplicationContext(), searchList);
            recyclerView.setLayoutManager(new LinearLayoutManager(allusersActivity.this));
            recyclerView.setAdapter(allUsersAdapter);
            EmptyListAnimation();
            if(query.equals("")){
                allUsersAdapter = new AllUsersAdapter(getApplicationContext(), userList);
                recyclerView.setLayoutManager(new LinearLayoutManager(allusersActivity.this));
                recyclerView.setAdapter(allUsersAdapter);
            }
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}