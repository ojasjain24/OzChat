package com.affixchat.chatappv0.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.affixchat.chatappv0.Models.chatModel;
import com.affixchat.chatappv0.Models.friendsModel;
import com.affixchat.chatappv0.Models.meetModel;
import com.affixchat.chatappv0.Models.usersModel;
import com.affixchat.chatappv0.R;
import com.affixchat.chatappv0.chatPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class acceptedUserAdapter extends RecyclerView.Adapter<acceptedUserAdapter.Holder> {

    Context context;
    ArrayList<friendsModel> data;
    ArrayList<usersModel> usersModelArrayList = new ArrayList<>();

    public acceptedUserAdapter() {
    }

    public acceptedUserAdapter(Context context, ArrayList<friendsModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new acceptedUserAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_addapter,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size=data.size();
                final friendsModel userid =data.get(position);
                for(int i = 0; i <size; i++){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        usersModel usersModel=dataSnapshot.getValue(usersModel.class);
                        if(data.get(i).getUid().equals(usersModel.getUserid())){
                            usersModelArrayList.add(usersModel);
                        }
                    }
                }
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

                if(userid.getUid().equals(user.getUid())){
                    holder.name.setText("Me");
                }else{
                    holder.name.setText((usersModelArrayList.get(position).getUsername()).substring(0,1).toUpperCase()+(usersModelArrayList.get(position).getUsername()).substring(1));
                }
                holder.status.setText(usersModelArrayList.get(position).getStatus());
                if(usersModelArrayList.get(position).getImageurl() != null) {
                    Picasso.get().load(Uri.parse(usersModelArrayList.get(position).getImageurl())).fit().centerCrop().into(holder.profile);
                }else{
                    holder.profile.setImageResource(R.drawable.ic_launcher_foreground);
                }
                holder.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "" + holder.name.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("meetings");
                Query query = databaseReference.orderByChild("endTime");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            meetModel model = dataSnapshot.getValue(meetModel.class);
                            if (Long.parseLong(model.getEndTime()) == 0 && ((model.getHostUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && model.getPartnerUid().equals(userid.getUid())) || (model.getPartnerUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && model.getHostUid().equals(userid.getUid())))) {
                                holder.live.setVisibility(View.VISIBLE);
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(context, chatPage.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.putExtra("userId", userid.getUid());
                                        i.putExtra("endTime",model.getEndTime()+"");
                                        i.putExtra("key",model.getKey());
                                        i.putExtra("type",model.getType());
                                        i.putExtra("host",model.getHostUid());
                                        i.putExtra("partner", model.getPartnerUid());

                                        i.putExtra("name",usersModelArrayList.get(position).getUsername());
                                        i.putExtra("dp",usersModelArrayList.get(position).getImageurl());
                                        i.putExtra("country",usersModelArrayList.get(position).getCountry());
                                        i.putExtra("gender",usersModelArrayList.get(position).getGender());
                                        i.putExtra("lang",usersModelArrayList.get(position).getLanguage());
                                        i.putExtra("status",usersModelArrayList.get(position).getStatus());
                                        i.putExtra("prof",usersModelArrayList.get(position).getProfession());
                                        Log.d("ojasaccepted","in");
                                        context.startActivity(i);
                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled (@NonNull DatabaseError error){

                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, chatPage.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("userId", userid.getUid());
                        i.putExtra("name",usersModelArrayList.get(position).getUsername());
                        i.putExtra("dp",usersModelArrayList.get(position).getImageurl());
                        i.putExtra("country",usersModelArrayList.get(position).getCountry());
                        i.putExtra("gender",usersModelArrayList.get(position).getGender());
                        i.putExtra("lang",usersModelArrayList.get(position).getLanguage());
                        i.putExtra("status",usersModelArrayList.get(position).getStatus());
                        i.putExtra("prof",usersModelArrayList.get(position).getProfession());
                        context.startActivity(i);
                        Log.d("ojasaccepted","out");
                    }
                });

                //Improve this one
                final FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
                final int[] count = {0};
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chats");
                reference.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot Snapshot : snapshot.getChildren()) {
                            chatModel chat = Snapshot.getValue(chatModel.class);
                            if (chat != null) {
                                if (chat.getReceiverUid().equals(me.getUid())&&chat.getSenderUid().equals(userid.getUid())){
                                    if(chat.getIsseen().equals("false")){
                                        count[0] = count[0] +1;
                                    }
                                }
                            }
                        }
                        if(count[0] !=0){
                            holder.countcard.setVisibility(View.VISIBLE);
                        }
                        holder.messageCount.setText(count[0] +"");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
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

        TextView name, status,messageCount;
        CircleImageView profile;
        RelativeLayout card;
        CardView countcard;
        LottieAnimationView live;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.chat_profile_name);
            status = itemView.findViewById(R.id.chat_profile_status);
            profile = itemView.findViewById(R.id.chat_profile_pic);
            card = itemView.findViewById(R.id.chat_card);
            messageCount=itemView.findViewById(R.id.messageCount);
            countcard=itemView.findViewById(R.id.countCard);
            live=itemView.findViewById(R.id.liveAnimation);
        }
    }

//    public static void setBadge(Context context, int count) {
//        String launcherClassName = getLauncherClassName(context);
//        if (launcherClassName == null) {
//            return;
//        }
//        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
//        intent.putExtra("badge_count", count);
//        intent.putExtra("badge_count_package_name", context.getPackageName());
//        intent.putExtra("badge_count_class_name", launcherClassName);
//        context.sendBroadcast(intent);
//    }
//
//    public static String getLauncherClassName(Context context) {
//
//        PackageManager pm = context.getPackageManager();
//
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//
//        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
//        for (ResolveInfo resolveInfo : resolveInfos) {
//            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
//            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
//                String className = resolveInfo.activityInfo.name;
//                return className;
//            }
//        }
//        return null;
//    }


}
