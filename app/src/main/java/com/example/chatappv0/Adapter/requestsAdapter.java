package com.example.chatappv0.Adapter;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatappv0.Models.acceptRequestModel;
import com.example.chatappv0.Models.usersModel;
import com.example.chatappv0.R;
import com.example.chatappv0.acceptRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
public class requestsAdapter extends RecyclerView.Adapter<requestsAdapter.Holder> {
    Context context;
    ArrayList<acceptRequestModel> data;
    ArrayList<usersModel> usersModelArrayList = new ArrayList<>();
    usersModel usersModel;
    public requestsAdapter() {
    }
    public requestsAdapter(Context context, ArrayList<acceptRequestModel> data) {
        this.context = context;
        this.data = data;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new requestsAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_addapter,parent,false));

    }
    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer size=data.size();
                for(Integer i=0; i<size;i++){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        usersModel=dataSnapshot.getValue(usersModel.class);
                        if(data.get(i).getSenderuid().equals(usersModel.getUserid())){
                            usersModelArrayList.add(usersModel);
                        }
                    }
                }
                holder.name.setText(usersModelArrayList.get(position).getUsername());
                holder.status.setText(usersModelArrayList.get(position).getStatus());
                if(usersModelArrayList.get(position).getImageurl()!= null) {
                    Picasso.get().load(Uri.parse(usersModelArrayList.get(position).getImageurl())).into(holder.profile);
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
                        final acceptRequestModel userid =data.get(position);
                        Intent i = new Intent(context, acceptRequest.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("userId", usersModelArrayList.get(position).getUserid());
                        i.putExtra("name",usersModelArrayList.get(position).getUsername());
                        i.putExtra("status", usersModelArrayList.get(position).getStatus());
                        i.putExtra("profession", usersModelArrayList.get(position).getProfession());
                        i.putExtra("gender", usersModelArrayList.get(position).getGender());
                        i.putExtra("language", usersModelArrayList.get(position).getLanguage());
                        i.putExtra("country", usersModelArrayList.get(position).getCountry());
                        i.putExtra("pic", usersModelArrayList.get(position).getImageurl());
                        i.putExtra("nodeId",userid.getRequestid());
                        context.startActivity(i);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    public class Holder extends RecyclerView.ViewHolder {
        TextView name, status;
        CircleImageView profile;
        RelativeLayout card;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.chat_profile_name);
            status = itemView.findViewById(R.id.chat_profile_status);
            profile = itemView.findViewById(R.id.chat_profile_pic);
            card = itemView.findViewById(R.id.chat_card);
        }
    }
}