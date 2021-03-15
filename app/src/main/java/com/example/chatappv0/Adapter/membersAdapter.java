package com.example.chatappv0.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappv0.MainActivity;
import com.example.chatappv0.Models.groupMemberModel;
import com.example.chatappv0.Models.usersModel;
import com.example.chatappv0.R;
import com.example.chatappv0.groupVisit;
import com.example.chatappv0.profileActivity;
import com.example.chatappv0.profileVisit;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class membersAdapter extends RecyclerView.Adapter<membersAdapter.Holder> {

    Context context;
    ArrayList<usersModel> data;
    ArrayList<groupMemberModel> model;
    String nodeId;
    String myStatus;
    public membersAdapter() {
    }

    public membersAdapter(Context context, ArrayList<usersModel> data,ArrayList<groupMemberModel> model,String nodeId,String myStatus) {
        this.context = context;
        this.data = data;
        this.model=model;
        this.nodeId=nodeId;
        this.myStatus=myStatus;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new membersAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_addapter,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        final usersModel userid =data.get(position);
        holder.name.setText(data.get(position).getUsername());
        holder.status.setText(data.get(position).getStatus());
        if(data.get(position).getImageurl() != null) {
            Picasso.get().load(Uri.parse(data.get(position).getImageurl())).into(holder.profile);
        }else{
            holder.profile.setImageResource(R.drawable.ic_launcher_foreground);
        }
        if(model.get(position).getStatus().equals("admin")){
            holder.admin.setText("Admin");
            holder.admin.setVisibility(View.VISIBLE);
        }
        if(model.get(position).getStatus().equals("creator")){
            holder.admin.setText("Creator");
            holder.admin.setVisibility(View.VISIBLE);
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
                final AlertDialog.Builder alert= new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.members_dialog,null);
                TextView profile, message, admin, remove;
                profile=view.findViewById(R.id.textView14);
                message=view.findViewById(R.id.textView17);
                admin=view.findViewById(R.id.textView18);
                remove=view.findViewById(R.id.textView19);
                alert.setView(view);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(true);

                profile.setOnClickListener(new View.OnClickListener() {
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
                if(myStatus.equals("creator")&&(!model.get(position).getStatus().equals("creator"))) {
                    remove.setVisibility(View.VISIBLE);
                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(context)
                                    .setMessage("Remove " + data.get(position).getUsername() + " from the group?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("members").child(model.get(position).getUid());
                                            reference.setValue(null);
                                            alertDialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null)
                                    .show();
                        }
                    });
                }
            if((myStatus.equals("creator")||myStatus.equals("admin"))&&(!model.get(position).getStatus().equals("creator"))) {
                admin.setVisibility(View.VISIBLE);
                if(model.get(position).getStatus().equals("admin")){
                    admin.setText("    Remove Admin");
                    admin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(context)
                                    .setTitle("Remove from Admin?")
                                    .setMessage("Are you sure you want to Remove participant from Admin?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("members").child(model.get(position).getUid());
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("status", "member");
                                            reference.updateChildren(hashMap);
                                            alertDialog.dismiss();
                                            }
                                    })
                                    .setNegativeButton(android.R.string.no, null)
                                    .setIcon(R.drawable.logo)
                                    .show();
                        }
                    });
                }else {
                    admin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("members").child(model.get(position).getUid());
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("status", "admin");
                            reference.updateChildren(hashMap);
                            alertDialog.dismiss();
                        }
                    });
                }

            }

                message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "This feature is under Development Phase", Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialog.show();
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

        public Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.chat_profile_name);
            status = itemView.findViewById(R.id.chat_profile_status);
            profile = itemView.findViewById(R.id.chat_profile_pic);
            card = itemView.findViewById(R.id.chat_card);
            admin=itemView.findViewById(R.id.textView15);
        }
    }
}
