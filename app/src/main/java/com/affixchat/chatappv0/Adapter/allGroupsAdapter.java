package com.affixchat.chatappv0.Adapter;

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
import com.affixchat.chatappv0.Models.groupDataModel;
import com.affixchat.chatappv0.R;
import com.affixchat.chatappv0.sendRequestToJoinGroup;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;


public class allGroupsAdapter extends RecyclerView.Adapter<allGroupsAdapter.Holder> {

    Context context;
    ArrayList<groupDataModel> data;

    public allGroupsAdapter() {
    }

    public allGroupsAdapter(Context context, ArrayList<groupDataModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new allGroupsAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_addapter,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        holder.name.setText(data.get(position).getName());
        holder.status.setText(data.get(position).getDescription());
        if(data.get(position).getGroupicon() != null) {
            Picasso.get().load(Uri.parse(data.get(position).getGroupicon())).fit().centerCrop().into(holder.profile);
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
                Intent i = new Intent(context, sendRequestToJoinGroup.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("nodeId", data.get(position).getNodeid());
                i.putExtra("name",data.get(position).getName());
                i.putExtra("status", data.get(position).getDescription());
                i.putExtra("pic", data.get(position).getGroupicon());
                i.putExtra("type",data.get(position).getType());
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

        public Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.chat_profile_name);
            status = itemView.findViewById(R.id.chat_profile_status);
            profile = itemView.findViewById(R.id.chat_profile_pic);
            card = itemView.findViewById(R.id.chat_card);

        }
    }
}