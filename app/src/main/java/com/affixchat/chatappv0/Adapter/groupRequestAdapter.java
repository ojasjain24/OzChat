package com.affixchat.chatappv0.Adapter;


import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;
import com.affixchat.chatappv0.Models.groupRequestsModel;
import com.affixchat.chatappv0.Models.usersModel;
import com.affixchat.chatappv0.R;
import com.affixchat.chatappv0.groupVisit;
import com.affixchat.chatappv0.profileVisit;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class groupRequestAdapter extends RecyclerView.Adapter<groupRequestAdapter.Holder> {

    Context context;
    ArrayList<usersModel> data;
    ArrayList<groupRequestsModel> model;
    String nodeId;
    groupRequestsModel uidPos;
    String myStatus;
    String groupName,groupDescrption,groupPic;
    public groupRequestAdapter() {
    }

    public groupRequestAdapter(Context context, ArrayList<usersModel> data,ArrayList<groupRequestsModel> model,String nodeId,String myStatus, String groupName,String groupDescrption,String groupPic) {
        this.context = context;
        this.data = data;
        this.model=model;
        this.nodeId=nodeId;
        this.myStatus=myStatus;
        this.groupDescrption=groupDescrption;
        this.groupName=groupName;
        this.groupPic=groupPic;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new groupRequestAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.request_to_join_group_adapter,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        final usersModel userid =data.get(position);
        holder.name.setText(data.get(position).getUsername());
        holder.status.setText(data.get(position).getStatus());

        if(data.get(position).getImageurl() != null) {
            Picasso.get().load(Uri.parse(data.get(position).getImageurl())).fit().centerCrop().into(holder.profile);
        }else{
            holder.profile.setImageResource(R.drawable.ic_launcher_foreground);
        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "" + holder.name.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, profileVisit.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("userId", userid.getUserid());
                i.putExtra("name",userid.getUsername());
                i.putExtra("status", userid.getStatus());
                i.putExtra("profession", userid.getProfession());
                i.putExtra("gender", userid.getGender());
                i.putExtra("language", userid.getLanguage());
                i.putExtra("country", userid.getCountry());
                i.putExtra("pic", userid.getImageurl());
                context.startActivity(i);
            }
        });
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("members").child(userid.getUserid());
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("status","member");
                hashMap.put("uid",userid.getUserid());
                databaseReference.setValue(hashMap);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("requests");
                reference.child(userid.getUserid()).setValue(null);
                Intent i = new Intent(context, groupVisit.class);
                i.putExtra("name",groupName);
                i.putExtra("description",groupDescrption);
                i.putExtra("pic",groupPic);
                i.putExtra("nodeId",nodeId);
                context.startActivity(i);
            }
        });
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("requests");
                reference.child(userid.getUserid()).setValue(null);
                Intent i = new Intent(context, groupVisit.class);
                i.putExtra("name",groupName);
                i.putExtra("description",groupDescrption);
                i.putExtra("pic",groupPic);
                i.putExtra("nodeId",nodeId);
                context.startActivity(i);
            }
        });
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView name, status, admin;
        CircleImageView profile;
        RelativeLayout card;
        Button accept, reject;

        public Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.chat_profile_namerga);
            status = itemView.findViewById(R.id.chat_profile_statusrga);
            profile = itemView.findViewById(R.id.chat_profile_picrga);
            card = itemView.findViewById(R.id.chat_cardrga);
            admin=itemView.findViewById(R.id.textView15rga);
            accept=itemView.findViewById(R.id.button);
            reject=itemView.findViewById(R.id.button2);
        }
    }
}

