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
import com.affixchat.chatappv0.Adapter.groupFragmentAdapter;
import com.affixchat.chatappv0.Models.groupDataModel;
import com.affixchat.chatappv0.Models.groupMemberModel;
import com.affixchat.chatappv0.R;
import com.affixchat.chatappv0.allGroupActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.Empty;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link groupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class groupFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "groupfragment";
    private LottieAnimationView empty,loading;
    private TextView noFriends,loadingText;
    private groupFragmentAdapter groupfragmentAdapter;
    private RecyclerView recyclerView;
    final ArrayList<groupDataModel> userList = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public groupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment groupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static groupFragment newInstance(String param1, String param2) {
        groupFragment fragment = new groupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group,container,false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String text = sharedPreferences.getString("Theme", "Default");
        LottieAnimationView bgAnimation = view.findViewById(R.id.bgAnimation);
        if(!text.equals("Default")){
            bgAnimation.setVisibility(View.INVISIBLE);
        }else{
            bgAnimation.setVisibility(View.VISIBLE);
        }

        recyclerView= view.findViewById(R.id.recyclerViewgf);
        empty=view.findViewById(R.id.emptygf);
        noFriends=view.findViewById(R.id.noFriendsTextgf);
        loading=view.findViewById(R.id.loadinggf);
        loadingText=view.findViewById(R.id.loadingTextgf);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        readusers();
        return view;
    }
    private void readusers() {
        loading.setSpeed(1);
        loading.playAnimation();
        final FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("groups");
        Query query=databaseReference.orderByChild("lastmsg");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
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
                                loadingText.setVisibility(View.INVISIBLE);
                                loading.setVisibility(View.INVISIBLE);
                                groupfragmentAdapter = new groupFragmentAdapter(getContext(), userList);
                                LinearLayoutManager manager=new LinearLayoutManager(getContext());
                                manager.setStackFromEnd(true);
                                manager.setReverseLayout(true);
                                recyclerView.setLayoutManager(manager);
                                recyclerView.setAdapter(groupfragmentAdapter);
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
                Toast.makeText(getContext(), "check your network connection", Toast.LENGTH_SHORT).show();

            }
        });
        loadingText.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.INVISIBLE);
        EmptyListAnimation();
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
                    startActivity(new Intent(getContext(), allGroupActivity.class));
                }
            });
            noFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), allGroupActivity.class));
                }
            });
        } else {
            empty.setVisibility(View.GONE);
            noFriends.setVisibility(View.GONE);
        }
    }
}
