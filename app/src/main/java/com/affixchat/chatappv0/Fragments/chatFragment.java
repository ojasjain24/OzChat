package com.affixchat.chatappv0.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.affixchat.chatappv0.Models.friendsModel;
import com.affixchat.chatappv0.R;
import com.affixchat.chatappv0.Adapter.acceptedUserAdapter;
import com.affixchat.chatappv0.allusersActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link rewardsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class chatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private com.affixchat.chatappv0.Adapter.acceptedUserAdapter acceptedUserAdapter;
    private RecyclerView recyclerView;
    private LottieAnimationView empty, loading;
    private TextView noFriends, loadingText;
    ArrayList<friendsModel> userList = new ArrayList<>();

    public chatFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment rewardsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static chatFragment newInstance(String param1, String param2) {
        chatFragment fragment = new chatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String text = sharedPreferences.getString("Theme", "Default");
        LottieAnimationView bgAnimation = view.findViewById(R.id.bgAnimation);
        if (!text.equals("Default")) {
            bgAnimation.setVisibility(View.INVISIBLE);
        } else {
            bgAnimation.setVisibility(View.VISIBLE);
        }

        noFriends = view.findViewById(R.id.noFriendsText);
        empty = view.findViewById(R.id.empty);
        loading = view.findViewById(R.id.loading);
        loadingText = view.findViewById(R.id.loadingText);
        readusers();
        return view;
    }

    public void readusers() {
        loading.setSpeed(1);
        loading.playAnimation();
        FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(me.getUid()).child("friends");
//        databaseReference.keepSynced(true);
        Query query = databaseReference.orderByChild("lastmsg");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    friendsModel user = snapshot.getValue(friendsModel.class);
                    userList.add(user);
                }
                loadingText.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.INVISIBLE);
                acceptedUserAdapter = new acceptedUserAdapter(getContext(), userList);
                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                manager.setReverseLayout(true);
                manager.setStackFromEnd(true);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(acceptedUserAdapter);
                EmptyListAnimation();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "check your network connection", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void EmptyListAnimation() {
        if (userList.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
            noFriends.setVisibility(View.VISIBLE);
            empty.setSpeed(1);
            empty.playAnimation();
            empty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), allusersActivity.class));
                }
            });
            noFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), allusersActivity.class));
                }
            });
        } else {
            empty.setVisibility(View.GONE);
            noFriends.setVisibility(View.GONE);
        }
    }


}