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
import com.airbnb.lottie.LottieAnimationView;
import com.example.chatappv0.Models.groupDataModel;
import com.example.chatappv0.Models.groupMeetingModel;
import com.example.chatappv0.R;
import com.example.chatappv0.groupChat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class groupFragmentAdapter extends RecyclerView.Adapter<groupFragmentAdapter.Holder> {

    Context context;
    ArrayList<groupDataModel> data;

    public groupFragmentAdapter() {
    }

    public groupFragmentAdapter(Context context, ArrayList<groupDataModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new groupFragmentAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_addapter,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        holder.name.setText((data.get(position).getName()).substring(0,1).toUpperCase()+(data.get(position).getName()).substring(1));
        holder.status.setText(data.get(position).getDescription());
        if(data.get(position).getGroupicon() != null) {
            Picasso.get().load(Uri.parse(data.get(position).getGroupicon())).into(holder.profile);
        }else{
            holder.profile.setImageResource(R.drawable.ic_launcher_foreground);
        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "" + holder.name.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(data.get(position).getNodeid()).child("meetings");
        Query query = reference.orderByChild("endTime");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    groupMeetingModel model = dataSnapshot.getValue(groupMeetingModel.class);
                    if (Long.parseLong(model.getEndTime())==0)
                        holder.live.setVisibility(View.VISIBLE);
                    else
                        holder.live.setVisibility(View.INVISIBLE);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(context, groupChat.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("nodeId", data.get(position).getNodeid());
                            i.putExtra("name",data.get(position).getName());
                            i.putExtra("status", data.get(position).getDescription());
                            i.putExtra("pic", data.get(position).getGroupicon());
                            i.putExtra("endTime",model.getEndTime()+"");
                            i.putExtra("key",model.getKey());
                            i.putExtra("type",model.getType());
                            i.putExtra("host",model.getHostUid());
                            context.startActivity(i);
                        }
                    });
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, groupChat.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("nodeId", data.get(position).getNodeid());
                i.putExtra("name",data.get(position).getName());
                i.putExtra("status", data.get(position).getDescription());
                i.putExtra("pic", data.get(position).getGroupicon());
                context.startActivity(i);
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
        LottieAnimationView live;

        public Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.chat_profile_name);
            status = itemView.findViewById(R.id.chat_profile_status);
            profile = itemView.findViewById(R.id.chat_profile_pic);
            card = itemView.findViewById(R.id.chat_card);
            live=itemView.findViewById(R.id.liveAnimation);
        }
    }
}