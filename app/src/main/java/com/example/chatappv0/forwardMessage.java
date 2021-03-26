package com.example.chatappv0;

import android.content.Intent;
import  android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappv0.Adapter.forwardMessageAdapter;
import com.example.chatappv0.Adapter.forwardMsgGroupAdapter;
import com.example.chatappv0.Adapter.groupFragmentAdapter;
import com.example.chatappv0.Adapter.selectUserForGroupAdapter;
import com.example.chatappv0.Models.chatModel;
import com.example.chatappv0.Models.friendsModel;
import com.example.chatappv0.Models.groupDataModel;
import com.example.chatappv0.Models.groupMemberModel;
import com.example.chatappv0.Models.usersModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class forwardMessage extends AppCompatActivity {
    private forwardMessageAdapter forwardMessageAdapter;
    private forwardMsgGroupAdapter forwardMsgGroupAdapter;
    private RecyclerView recyclerView, recyclerView2;
    FloatingActionButton nextBtn;
    ArrayList<friendsModel> userList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forward_message_actvity);
        nextBtn=findViewById(R.id.floatingActionButtonfma);
        recyclerView=findViewById(R.id.making_group_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView2=findViewById(R.id.recyclerView2);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        readusers();
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardMessageAdapter.uploadOnDataBase();
                forwardMsgGroupAdapter.uploadOnDataBase();
                Intent i =new Intent(forwardMessage.this,MainActivity.class);
                startActivity(i);
            }
        });
    }
    private void readusers() {
        final FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users").child(me.getUid()).child("friends");
        Query query  = databaseReference.orderByChild("lastmsg");
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        final ArrayList<chatModel> list = (ArrayList<chatModel>) args.getSerializable("arrayList");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    friendsModel user = snapshot.getValue(friendsModel.class);
                        userList.add(user);
                }
                forwardMessageAdapter = new forwardMessageAdapter(getApplicationContext(), userList,list);
                LinearLayoutManager manager=new LinearLayoutManager(forwardMessage.this);
                manager.setReverseLayout(true);
                manager.setStackFromEnd(true);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(forwardMessageAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(forwardMessage.this, "check your network connection", Toast.LENGTH_SHORT).show();
            }
        });


        DatabaseReference databaseReference1= FirebaseDatabase.getInstance().getReference("groups");
        Query query1=databaseReference1.orderByChild("lastmsg");
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                final ArrayList<groupDataModel> userList = new ArrayList<>();
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final groupDataModel user = snapshot.getValue(groupDataModel.class);
                    if(user.getNodeid() != null){
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(user.getNodeid()).child("members");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    groupMemberModel model = snapshot1.getValue(groupMemberModel.class);
                                    if(model.getUid().equals(me.getUid())){
                                        userList.add(user);
                                    }
                                }
                                forwardMsgGroupAdapter = new forwardMsgGroupAdapter(getApplicationContext(), userList,list);
                                LinearLayoutManager manager=new LinearLayoutManager(forwardMessage.this);
                                manager.setStackFromEnd(true);
                                manager.setReverseLayout(true);
                                recyclerView2.setLayoutManager(manager);
                                recyclerView2.setAdapter(forwardMsgGroupAdapter);
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
                Toast.makeText(forwardMessage.this, "check your network connection", Toast.LENGTH_SHORT).show();

            }
        });

    }
}