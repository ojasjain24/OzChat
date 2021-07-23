package com.affixchat.chatappv0.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.affixchat.chatappv0.Models.friendsModel;
import com.affixchat.chatappv0.Models.usersModel;
import com.affixchat.chatappv0.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;

public class selectUserForGroupAdapter extends RecyclerView.Adapter<selectUserForGroupAdapter.Holder> {

    Context context;
    ArrayList<friendsModel> data;
    ArrayList<usersModel> usersList = new ArrayList<>();
    ArrayList arrayList = new ArrayList();
    public selectUserForGroupAdapter() {
    }

    public selectUserForGroupAdapter(Context context, ArrayList<friendsModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new selectUserForGroupAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.select_user_for_group,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size=data.size();
                final friendsModel userid =data.get(position);
                for(int i = 0; i <size; i++){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        usersModel usersModel=dataSnapshot.getValue(usersModel.class);
                        if(data.get(i).getUid().equals(usersModel.getUserid())){
                            usersList.add(usersModel);
                        }
                    }
                }
        holder.name.setText(usersList.get(position).getUsername());
        if(usersList.get(position).getImageurl() != null) {
            Picasso.get().load(Uri.parse(usersList.get(position).getImageurl())).fit().centerCrop().into(holder.profile);
        }else{
            holder.profile.setImageResource(R.drawable.ic_launcher_foreground);
        }
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBox.isChecked()) {
                    arrayList.add(usersList.get(position).getUserid());
                }
                if(arrayList.contains(usersList.get(position).getUserid())&&(!holder.checkBox.isChecked())){
                    arrayList.remove(usersList.get(position).getUserid());
                }
            }
        });

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkBox.setChecked(!holder.checkBox.isChecked());
                if(holder.checkBox.isChecked()) {
                    arrayList.add(usersList.get(position).getUserid());
                }
                if(arrayList.contains(usersList.get(position).getUserid())&&(!holder.checkBox.isChecked())){
                    arrayList.remove(usersList.get(position).getUserid());
                }
            }
        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView name;
        CircleImageView profile;
        ConstraintLayout card;
        CheckBox checkBox;

        public Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textView10);
            profile = itemView.findViewById(R.id.imageView);
            card=itemView.findViewById(R.id.select_for_group);
            checkBox=itemView.findViewById(R.id.checkBox);
        }
    }
    FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("groups").push();
    public void uploadOnDataBase(){
        int size=arrayList.size();
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("status","member");
        for(int i=0;i<size;i++){
            hashMap.put("uid",arrayList.get(i).toString());
            databaseReference.child("members").child(""+arrayList.get(i).toString()).setValue(hashMap);
        }
        HashMap<String,String> map = new HashMap<>();
        map.put("status","creator");
        map.put("uid",me.getUid());
        databaseReference.child("members").child(me.getUid()).setValue(map);
    }
    public String dataBaseKey(){
        return databaseReference.getKey();
    }
}