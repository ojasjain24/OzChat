//package com.example.chatappv0;
//
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.example.chatappv0.Adapter.acceptedUserAdapter;
//import com.example.chatappv0.Models.friendsModel;
//import com.example.chatappv0.Models.usersModel;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//
//public class usersDataSuperModel {
//    ArrayList<usersModel> userDataList = new ArrayList<>();
//    public void readusers() {
//        FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
//        Query query = databaseReference.orderByChild("lastmsg");
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    usersModel user = snapshot.getValue(usersModel.class);
//                    userDataList.add(user);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                }
//        });
//    }
//    public ArrayList<usersModel> getUserList(){
//        readusers();
//        return userDataList;
//    }
//}