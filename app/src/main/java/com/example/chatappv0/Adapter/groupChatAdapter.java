package com.example.chatappv0.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappv0.Models.groupChatModel;
import com.example.chatappv0.Models.usersModel;
import com.example.chatappv0.R;
import com.example.chatappv0.Models.chatModel;
import com.example.chatappv0.chatPage;
import com.example.chatappv0.forwardMessage;
import com.example.chatappv0.forwardMessageGroup;
import com.example.chatappv0.groupChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class groupChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<groupChatModel> mChat;
    FirebaseUser firebaseUser;
    TextView nameText;
    ImageView dp, delete, forward,copy;
    ClipboardManager clipboardManager;
    int count=0;
    String nodeId;
    final ArrayList<groupChatModel> list = new ArrayList<>();
    public static final int msgLeft = 0;
    public static final int msgRight = 1;
    public static final int fileLeft = 2;
    public static final int fileRight = 3;

    public groupChatAdapter() {
    }

    public groupChatAdapter(Context context, ArrayList<groupChatModel> mChat,String nodeId) {
        this.context = context;
        this.mChat = mChat;
        this.nodeId=nodeId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == msgRight) {
            return new msgHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.right_message_group_adapter, parent, false));
        } else if(viewType == msgLeft){
            return new msgHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.left_message_group_adapter, parent, false));
        }else if(viewType == fileRight){
            return new fileHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.right_file_group_adapter, parent, false));
        }else{
            return new fileHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.left_file_group_adapter, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final groupChatModel chat = mChat.get(position);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(chat.getSenderUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersModel usersModel = snapshot.getValue(com.example.chatappv0.Models.usersModel.class);
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(180), rnd.nextInt(180), rnd.nextInt(180));
                if (getItemViewType(position) == msgRight|| getItemViewType(position) == msgLeft) {
                    final msgHolder msgholder = (msgHolder) holder;
                    msgholder.message.setText(chat.getMessage());
                    String time = chat.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                    Date resultdate = new Date(Long.parseLong(time));
                    msgholder.time.setText(sdf.format(resultdate));
                    msgholder.name.setText(usersModel.getUsername());
                    msgholder.name.setTextColor(color);

                    msgholder.border.setOnLongClickListener(new View.OnLongClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public boolean onLongClick(View v) {
                            int color;
                            if(!list.contains(chat)) {
                                color = R.color.transpirent;
                                list.add(chat);
                            }else{
                                color = R.color.nullColor;
                                list.remove(chat);
                            }
                            msgholder.layout.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
                            if(list.size()!=0) {
                                nameText = ((groupChat) context).findViewById(R.id.chatPageName);
                                nameText.setVisibility(View.INVISIBLE);
                                dp = ((groupChat) context).findViewById(R.id.chatPageDp);
                                dp.setVisibility(View.INVISIBLE);
                                delete=((groupChat) context).findViewById(R.id.deleteIcong);
                                delete.setVisibility(View.VISIBLE);
                                forward=((groupChat) context).findViewById(R.id.forwardIcong);
                                forward.setVisibility(View.VISIBLE);
                                copy=((groupChat) context).findViewById(R.id.copyIcong);
                                if(count==0) {
                                    copy.setVisibility(View.VISIBLE);
                                }
                                Log.d("ojaslistoutside",list.size()+"");
                                final Boolean[] ok = {false};
                                delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        for(int i=list.size()-1;i>=0;i--){
                                            if(list.get(i).getSenderUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                final int finalI = i;
                                                Log.d("ojasok", ok[0].toString());
                                                if (!ok[0]) {
                                                    new AlertDialog.Builder(context)
                                                            .setTitle("Delete Messages?")
                                                            .setMessage("Only Messages sent by you will be deleted for everyone. do you want to delete?")
                                                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("chats").child(list.get(finalI).getKey());
                                                                    reference.setValue(null);
                                                                    ok[0] =true;
                                                                    nameText.setVisibility(View.VISIBLE);
                                                                    dp.setVisibility(View.VISIBLE);
                                                                    delete.setVisibility(View.INVISIBLE);
                                                                    forward.setVisibility(View.INVISIBLE);
                                                                    copy.setVisibility(View.INVISIBLE);
                                                                }
                                                            })
                                                            .setNegativeButton(android.R.string.no, null)
                                                            .setIcon(R.drawable.logo)
                                                            .show();
                                                }else{
                                                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("chats").child(list.get(finalI).getKey());
                                                    reference.setValue(null);
                                                }
                                            }
                                        }
                                    }
                                });

                                clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                copy.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String data="";
                                        for(int i=list.size()-1;i>=0;i--){
                                            data=data+list.get(i).getMessage()+"\n";
                                            ClipData clipData = ClipData.newPlainText("text"+i,data);
                                            clipboardManager.setPrimaryClip(clipData);
                                            msgholder.layout.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.nullColor)));
                                        }
                                        Toast.makeText(context, "Copied to Clipboard !", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(context,groupChat.class);
                                        i.putExtra("nodeId",nodeId);
                                        context.startActivity(i);
                                    }
                                });
                                forward.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i =new Intent(context, forwardMessageGroup.class);
                                        Bundle args = new Bundle();
                                        args.putSerializable("arrayList",list);
                                        i.putExtra("BUNDLE",args);
                                        context.startActivity(i);
                                    }
                                });
                            }else{
                                nameText = ((groupChat) context).findViewById(R.id.chatPageName);
                                nameText.setVisibility(View.VISIBLE);
                                dp = ((groupChat) context).findViewById(R.id.chatPageDp);
                                dp.setVisibility(View.VISIBLE);
                                delete=((groupChat) context).findViewById(R.id.deleteIcong);
                                delete.setVisibility(View.INVISIBLE);
                                forward=((groupChat) context).findViewById(R.id.forwardIcong);
                                forward.setVisibility(View.INVISIBLE);
                                copy=((groupChat) context).findViewById(R.id.copyIcong);
                                copy.setVisibility(View.INVISIBLE);

                            }
                            return false;
                        }
                    });

                }else{
                    final fileHolder fileholder = (fileHolder) holder;
                    fileholder.openBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent I = new Intent(Intent.ACTION_VIEW);
                            I.setData(Uri.parse(chat.getMessage()));
                            context.startActivity(I);
                        }
                    });
                    fileholder.name.setText(usersModel.getUsername());
                    String time = chat.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                    Date resultdate = new Date(Long.parseLong(time));
                    fileholder.time.setText(sdf.format(resultdate));
                    fileholder.name.setTextColor(color);
                    fileholder.type.setText(chat.getType());
                    fileholder.border.setOnLongClickListener(new View.OnLongClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public boolean onLongClick(View v) {
                            int color;
                            if(!list.contains(chat)) {
                                color = R.color.transpirent;
                                list.add(chat);
                                count+=1;
                            }else{
                                color = R.color.nullColor;
                                list.remove(chat);
                                count-=1;
                            }
                            fileholder.layout.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
                            if(list.size()!=0) {
                                nameText = ((groupChat) context).findViewById(R.id.chatPageName);
                                nameText.setVisibility(View.INVISIBLE);
                                dp = ((groupChat) context).findViewById(R.id.chatPageDp);
                                dp.setVisibility(View.INVISIBLE);
                                delete=((groupChat) context).findViewById(R.id.deleteIcong);
                                delete.setVisibility(View.VISIBLE);
                                forward=((groupChat) context).findViewById(R.id.forwardIcong);
                                forward.setVisibility(View.VISIBLE);
                                copy=((groupChat) context).findViewById(R.id.copyIcong);
                                copy.setVisibility(View.GONE);
                                Log.d("ojaslistoutside",list.size()+"");
                                final Boolean[] ok = {false};
                                delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        for(int i=list.size()-1;i>=0;i--){
                                            if(list.get(i).getSenderUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                final int finalI = i;
                                                if (!ok[0]) {
                                                    new AlertDialog.Builder(context)
                                                            .setTitle("Delete Messages?")
                                                            .setMessage("Only Messages sent by you will be deleted for everyone. do you want to delete?")
                                                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("chats").child(list.get(finalI).getKey());
                                                                    reference.setValue(null);
                                                                    ok[0] =true;
                                                                    Log.d("ojasokin", ok[0].toString());
                                                                    nameText.setVisibility(View.VISIBLE);
                                                                    dp.setVisibility(View.VISIBLE);
                                                                    delete.setVisibility(View.INVISIBLE);
                                                                    forward.setVisibility(View.INVISIBLE);
                                                                    copy.setVisibility(View.INVISIBLE);
                                                                }
                                                            })
                                                            .setNegativeButton(android.R.string.no, null)
                                                            .setIcon(R.drawable.logo)
                                                            .show();
                                                }else{
                                                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats").child(list.get(finalI).getKey());
                                                    reference.setValue(null);
                                                }
                                            }
                                        }
                                    }
                                });
                                forward.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i =new Intent(context, forwardMessage.class);
                                        Bundle args = new Bundle();
                                        args.putSerializable("arrayList",list);
                                        i.putExtra("BUNDLE",args);
                                        context.startActivity(i);
                                    }
                                });
                            }else{
                                nameText = ((groupChat) context).findViewById(R.id.chatPageName);
                                nameText.setVisibility(View.VISIBLE);
                                dp = ((groupChat) context).findViewById(R.id.chatPageDp);
                                dp.setVisibility(View.VISIBLE);
                                delete=((groupChat) context).findViewById(R.id.deleteIcong);
                                delete.setVisibility(View.INVISIBLE);
                                forward=((groupChat) context).findViewById(R.id.forwardIcong);
                                forward.setVisibility(View.INVISIBLE);
                                copy=((groupChat) context).findViewById(R.id.copyIcong);
                                copy.setVisibility(View.INVISIBLE);
                            }
                            return false;
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public static class msgHolder extends RecyclerView.ViewHolder {
        TextView message, name;
        TextView time;
        ConstraintLayout border;
        ConstraintLayout layout;

        public msgHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.textView13);
            message = itemView.findViewById(R.id.showMessageg);
            time = itemView.findViewById(R.id.timeg);
            border=itemView.findViewById(R.id.imageView3g);
            layout=itemView.findViewById(R.id.background);
        }
    }
    public static class fileHolder extends RecyclerView.ViewHolder {
        Button openBtn;
        TextView time,name,type;
        ImageView border;
        ConstraintLayout layout;
        public fileHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.textView13);
            openBtn=itemView.findViewById(R.id.openBtng);
            time=itemView.findViewById(R.id.timeg);
            type=itemView.findViewById(R.id.type);
            border=itemView.findViewById(R.id.imageView3g);
            layout=itemView.findViewById(R.id.background);
        }
    }
    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getIsThisFile().equals("true") && mChat.get(position).getSenderUid().equals(firebaseUser.getUid())) {
            return fileLeft;
        } else if (mChat.get(position).getIsThisFile().equals("true") && (!mChat.get(position).getSenderUid().equals(firebaseUser.getUid()))) {
            return fileRight;
        } else if ((mChat.get(position).getIsThisFile().equals("false") && mChat.get(position).getSenderUid().equals(firebaseUser.getUid()))) {
            return msgLeft;
        } else {
            return msgRight;
        }
    }
}
