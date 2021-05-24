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
import com.example.chatappv0.Models.usersModel;
import com.example.chatappv0.R;
import com.example.chatappv0.sendRequest;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;


public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.Holder> {

    Context context;
    ArrayList<usersModel> data;

    public AllUsersAdapter() {
    }

    public AllUsersAdapter(Context context, ArrayList<usersModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AllUsersAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_addapter,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        final usersModel userid =data.get(position);
        holder.name.setText(data.get(position).getUsername());
        holder.status.setText(data.get(position).getEmail());
        if(data.get(position).getImageurl() != null) {
            Picasso.get().load(Uri.parse(data.get(position).getImageurl())).into(holder.profile);
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
                Intent i = new Intent(context, sendRequest.class);
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
