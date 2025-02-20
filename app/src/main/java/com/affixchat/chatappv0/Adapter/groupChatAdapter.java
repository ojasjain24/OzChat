package com.affixchat.chatappv0.Adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

import com.affixchat.chatappv0.Models.chatModel;
import com.affixchat.chatappv0.Models.groupChatModel;
import com.affixchat.chatappv0.Models.usersModel;
import com.affixchat.chatappv0.R;
import com.affixchat.chatappv0.forwardMessageGrp;
import com.affixchat.chatappv0.groupChat;
import com.affixchat.chatappv0.imageViewActivity;
import com.affixchat.chatappv0.profileVisit;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class groupChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<groupChatModel> mChat;
    FirebaseUser firebaseUser;
    TextView nameText;
    ArrayList<String> imageTypes = new ArrayList<>();
    ImageView dp, delete, forward,copy, videoCallBtn, callBtn;
    ClipboardManager clipboardManager;
    int count=0;
    String nodeId;
    private SecretKeySpec secretKeySpec;
    private final byte[] encryptionKey ={5,15,-65,-56,3,45,-96,37,85,64,85,-92,-12,-5,64,-50};
    private Cipher cipher, decipher;
    final ArrayList<groupChatModel> list = new ArrayList<>();
    public static final int msgLeft = 0;
    public static final int msgRight = 1;
    public static final int fileLeft = 2;
    public static final int fileRight = 3;
    public static final int imgLeft = 4;
    public static final int imgRight = 5;
    public groupChatAdapter() {
    }

    public groupChatAdapter(Context context,String nodeId) {
        this.context = context;
        this.mChat = new ArrayList<>();
        this.nodeId=nodeId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == msgRight) {
            return new msgHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.right_msg_grp_adapter, parent, false));
        } else if(viewType == msgLeft){
            return new msgHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.left_msg_grp_adapter, parent, false));
        }else if(viewType == fileRight){
            return new fileHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.right_file_grp_adapter, parent, false));
        }else if(viewType==imgLeft){
            return new imgHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.left_img_grp_adapter, parent, false));
        }else if(viewType==imgRight){
            return new imgHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.right_img_grp_adapter, parent, false));
        }
        else{
            return new fileHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.left_file_grp_adapter, parent, false));
        }
    }

    @SuppressLint("GetInstance")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final groupChatModel chat = mChat.get(position);
        nameText = ((groupChat) context).findViewById(R.id.chatPageName);
        dp = ((groupChat) context).findViewById(R.id.chatPageDp);
        delete=((groupChat) context).findViewById(R.id.deleteIcong);
        forward=((groupChat) context).findViewById(R.id.forwardIcong);
        videoCallBtn = ((groupChat) context).findViewById(R.id.videoCall);
        callBtn = ((groupChat) context).findViewById(R.id.call);
        copy=((groupChat) context).findViewById(R.id.copyIcong);

        try {
            cipher = Cipher.getInstance("AES");
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        secretKeySpec = new SecretKeySpec(encryptionKey, "AES");

//      names of msg sender
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(chat.getSenderUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final usersModel usersModel = snapshot.getValue(com.affixchat.chatappv0.Models.usersModel.class);
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(180), rnd.nextInt(180), rnd.nextInt(180));
                if (getItemViewType(position) == msgRight|| getItemViewType(position) == msgLeft) {
                    final msgHolder msgholder = (msgHolder) holder;
                    msgholder.name.setText(usersModel.getUsername());
                    msgholder.name.setTextColor(color);
                    msgholder.name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(context, profileVisit.class);
                            i.putExtra("name",usersModel.getUsername());
                            i.putExtra("status",usersModel.getStatus());
                            i.putExtra("pic",usersModel.getImageurl());
                            i.putExtra("gender",usersModel.getGender());
                            i.putExtra("profession",usersModel.getProfession());
                            i.putExtra("country",usersModel.getCountry());
                            i.putExtra("language",usersModel.getLanguage());
                            context.startActivity(i);
                        }
                    });
                }
                if (getItemViewType(position) == fileRight|| getItemViewType(position) == fileLeft) {
                    final fileHolder fileholder = (fileHolder) holder;
                    fileholder.name.setText(usersModel.getUsername());
                    fileholder.name.setTextColor(color);
                    fileholder.name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(context, profileVisit.class);
                            i.putExtra("name",usersModel.getUsername());
                            i.putExtra("status",usersModel.getStatus());
                            i.putExtra("pic",usersModel.getImageurl());
                            i.putExtra("gender",usersModel.getGender());
                            i.putExtra("profession",usersModel.getProfession());
                            i.putExtra("country",usersModel.getCountry());
                            i.putExtra("language",usersModel.getLanguage());
                            context.startActivity(i);
                        }
                    });
                }
                if (getItemViewType(position) == imgRight|| getItemViewType(position) == imgLeft) {
                    final imgHolder imgholder = (imgHolder) holder;
                    imgholder.name.setText(usersModel.getUsername());
                    imgholder.name.setTextColor(color);
                    imgholder.name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(context, profileVisit.class);
                            i.putExtra("name",usersModel.getUsername());
                            i.putExtra("status",usersModel.getStatus());
                            i.putExtra("pic",usersModel.getImageurl());
                            i.putExtra("gender",usersModel.getGender());
                            i.putExtra("profession",usersModel.getProfession());
                            i.putExtra("country",usersModel.getCountry());
                            i.putExtra("language",usersModel.getLanguage());
                            context.startActivity(i);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (getItemViewType(position) == msgRight|| getItemViewType(position) == msgLeft) {
            final msgHolder msgholder = (msgHolder) holder;
            int color;
            if(list.contains(chat)) {
                color = R.color.transpirent;
               }else{
                color = R.color.nullColor;
               }
            msgholder.layout.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
            msgholder.message.setText(chat.getMessage());
            String time = chat.getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date resultdate = new Date(Long.parseLong(time));
            msgholder.time.setText(sdf.format(resultdate));
            msgholder.layout.setOnLongClickListener(new View.OnLongClickListener() {
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
                        dp.setVisibility(View.INVISIBLE);
                        nameText.setVisibility(View.INVISIBLE);
                        delete.setVisibility(View.VISIBLE);
                        forward.setVisibility(View.VISIBLE);
                        videoCallBtn.setVisibility(View.INVISIBLE);
                        callBtn.setVisibility(View.INVISIBLE);
                        if(count==0) {
                            copy.setVisibility(View.VISIBLE);
                        }
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ProgressDialog pd= new ProgressDialog(context);
                                pd.setMessage("deleting");
                                new AlertDialog.Builder(context)
                                .setTitle("Delete Messages?")
                                .setMessage("Only Messages sent by you will be deleted for everyone. do you want to delete?")
                                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        pd.show();
                                        for(int i=list.size()-1;i>=0;i--) {
                                            if (list.get(i).getSenderUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("chats").child(list.get(i).getKey());
                                                reference.setValue(null);
                                                mChat.remove(list.get(i));
                                                notifyDataSetChanged();
                                                nameText.setVisibility(View.VISIBLE);
                                                dp.setVisibility(View.VISIBLE);
                                                delete.setVisibility(View.INVISIBLE);
                                                forward.setVisibility(View.INVISIBLE);
                                                copy.setVisibility(View.INVISIBLE);
                                                videoCallBtn.setVisibility(View.VISIBLE);
                                                callBtn.setVisibility(View.VISIBLE);
                                            }else {
                                                Toast.makeText(context, "You can not delete messages sent by others", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        pd.dismiss();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(R.drawable.logo)
                                .show();
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
                                Intent i =new Intent(context, forwardMessageGrp.class);
                                Bundle args = new Bundle();
                                args.putSerializable("arrayList",list);
                                i.putExtra("BUNDLE",args);
                                context.startActivity(i);
                            }
                        });
                    }else{
                        nameText.setVisibility(View.VISIBLE);
                        dp.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.INVISIBLE);
                        forward.setVisibility(View.INVISIBLE);
                        copy.setVisibility(View.INVISIBLE);
                        videoCallBtn.setVisibility(View.VISIBLE);
                        callBtn.setVisibility(View.VISIBLE);
                    }
                    return false;
                }
            });
        }
        else if (getItemViewType(position) == imgRight|| getItemViewType(position) == imgLeft){
            final groupChatAdapter.imgHolder imgholder = (groupChatAdapter.imgHolder) holder;
            int color;
            if(list.contains(chat)) {
                color = R.color.transpirent;
            }else{
                color = R.color.nullColor;
            }
            imgholder.layout.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
            imgholder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, imageViewActivity.class);
                    intent.putExtra("image", chat.getMessage());
                    intent.putExtra("Type",chat.getType());
                    context.startActivity(intent);
                }
            });
            try {
                if(chat.getMessage() != null) {
                    Picasso.get().load(Uri.parse(chat.getMessage())).fit().centerCrop().into(imgholder.img);
                }else{
                    imgholder.img.setImageResource(R.drawable.ic_launcher_foreground);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Too Large to Load", Toast.LENGTH_SHORT).show();
                Log.d("ojaserror",""+e);
            }
            String time = chat.getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date resultdate = new Date(Long.parseLong(time));
            imgholder.time.setText(sdf.format(resultdate));
            imgholder.layout.setOnLongClickListener(new View.OnLongClickListener() {
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
                    imgholder.layout.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
                    if(list.size()!=0) {
                        nameText.setVisibility(View.INVISIBLE);
                        dp.setVisibility(View.INVISIBLE);
                        delete.setVisibility(View.VISIBLE);
                        forward.setVisibility(View.VISIBLE);
                        copy.setVisibility(View.GONE);
                        videoCallBtn.setVisibility(View.INVISIBLE);
                        callBtn.setVisibility(View.INVISIBLE);
                        Log.d("ojaslistoutside",list.size()+"");
                        final Boolean[] ok = {false};
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ProgressDialog pd= new ProgressDialog(context);
                                pd.setMessage("deleting");
                                new AlertDialog.Builder(context)
                                        .setTitle("Delete Messages?")
                                        .setMessage("Only Messages sent by you will be deleted for everyone. do you want to delete?")
                                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                pd.show();
                                                for(int i=list.size()-1;i>=0;i--) {
                                                    if (list.get(i).getSenderUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("chats").child(list.get(i).getKey());
                                                        reference.setValue(null);
                                                        mChat.remove(list.get(i));
                                                        notifyDataSetChanged();
                                                        nameText.setVisibility(View.VISIBLE);
                                                        dp.setVisibility(View.VISIBLE);
                                                        delete.setVisibility(View.INVISIBLE);
                                                        forward.setVisibility(View.INVISIBLE);
                                                        copy.setVisibility(View.INVISIBLE);
                                                        videoCallBtn.setVisibility(View.VISIBLE);
                                                        callBtn.setVisibility(View.VISIBLE);

                                                    }else {
                                                        Toast.makeText(context, "You can not delete messages sent by others", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                pd.dismiss();
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, null)
                                        .setIcon(R.drawable.logo)
                                        .show();
                            }
                        });
                        forward.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i =new Intent(context, forwardMessageGrp.class);
                                Bundle args = new Bundle();
                                args.putSerializable("arrayList",list);
                                i.putExtra("BUNDLE",args);
                                context.startActivity(i);
                            }
                        });
                    }else{
                        nameText.setVisibility(View.VISIBLE);
                        dp.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.INVISIBLE);
                        forward.setVisibility(View.INVISIBLE);
                        copy.setVisibility(View.INVISIBLE);
                        videoCallBtn.setVisibility(View.VISIBLE);
                        callBtn.setVisibility(View.VISIBLE);}
                    return false;
                }
            });
        }
        else {
            final groupChatAdapter.fileHolder fileholder = (groupChatAdapter.fileHolder) holder;
            int color;
            if(list.contains(chat)) {
                color = R.color.transpirent;
            }else{
                color = R.color.nullColor;
            }
            fileholder.layout.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
            fileholder.openBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(Intent.ACTION_VIEW);
                    I.setData(Uri.parse(chat.getMessage()));
                    context.startActivity(I);
                }
            });
            fileholder.type.setText(chat.getType());
            String time = chat.getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date resultdate = new Date(Long.parseLong(time));
            fileholder.time.setText(sdf.format(resultdate));
            fileholder.layout.setOnLongClickListener(new View.OnLongClickListener() {
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
                        nameText.setVisibility(View.INVISIBLE);
                        dp.setVisibility(View.INVISIBLE);
                        delete.setVisibility(View.VISIBLE);
                        forward.setVisibility(View.VISIBLE);
                        copy.setVisibility(View.GONE);
                        videoCallBtn.setVisibility(View.INVISIBLE);
                        callBtn.setVisibility(View.INVISIBLE);
                        final Boolean[] ok = {false};
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ProgressDialog pd= new ProgressDialog(context);
                                pd.setMessage("deleting");
                                new AlertDialog.Builder(context)
                                        .setTitle("Delete Messages?")
                                        .setMessage("Only Messages sent by you will be deleted for everyone. do you want to delete?")
                                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                pd.show();
                                                for(int i=list.size()-1;i>=0;i--) {
                                                    if (list.get(i).getSenderUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("chats").child(list.get(i).getKey());
                                                        reference.setValue(null);
                                                        mChat.remove(list.get(i));
                                                        notifyDataSetChanged();
                                                        nameText.setVisibility(View.VISIBLE);
                                                        dp.setVisibility(View.VISIBLE);
                                                        delete.setVisibility(View.INVISIBLE);
                                                        forward.setVisibility(View.INVISIBLE);
                                                        copy.setVisibility(View.INVISIBLE);
                                                        videoCallBtn.setVisibility(View.VISIBLE);
                                                        callBtn.setVisibility(View.VISIBLE);        }else {
                                                        Toast.makeText(context, "You can not delete messages sent by others", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                pd.dismiss();
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, null)
                                        .setIcon(R.drawable.logo)
                                        .show();
                            }
                        });

                        forward.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i =new Intent(context, forwardMessageGrp.class);
                                Bundle args = new Bundle();
                                args.putSerializable("arrayList",list);
                                i.putExtra("BUNDLE",args);
                                context.startActivity(i);
                            }
                        });
                    }else{
                        nameText.setVisibility(View.VISIBLE);
                        dp.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.INVISIBLE);
                        forward.setVisibility(View.INVISIBLE);
                        copy.setVisibility(View.INVISIBLE);
                        videoCallBtn.setVisibility(View.VISIBLE);
                        callBtn.setVisibility(View.VISIBLE);
                    }
                    return false;
                }
            });
        }
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
    public static class imgHolder extends RecyclerView.ViewHolder {
        TextView time, name;
        ConstraintLayout border;
        ConstraintLayout layout;
        ImageView img;
        public imgHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            border=itemView.findViewById(R.id.imageView3);
            layout=itemView.findViewById(R.id.background);
            img=itemView.findViewById(R.id.showMessageFile);
            name=itemView.findViewById(R.id.textView13);
        }
    }

    @Override
    public int getItemViewType(int position) {
//        ArrayLists
        imageTypes.add("bmp");
        imageTypes.add("gif");
        imageTypes.add("jpg");
        imageTypes.add("png");
        imageTypes.add("webp");
//        videoTypes.add("mp4");
//        videoTypes.add("mkv");
//        videoTypes.add("webm");
//        audioTypes.add("3gp");
//        audioTypes.add("mp3");
//        audioTypes.add("wav");
//        audioTypes.add("ogg");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getIsThisFile().equals("true") && mChat.get(position).getSenderUid().equals(firebaseUser.getUid())) {
            if(imageTypes.contains(mChat.get(position).getType())){
                return imgRight;
            }else {
                return fileRight;
            }
        } else if (mChat.get(position).getIsThisFile().equals("true") && (!mChat.get(position).getSenderUid().equals(firebaseUser.getUid()))) {
            if(imageTypes.contains(mChat.get(position).getType())){
                return imgLeft;
            }else {
                return fileLeft;
            }
        } else if ((mChat.get(position).getIsThisFile().equals("false") && mChat.get(position).getSenderUid().equals(firebaseUser.getUid()))) {
            return msgRight;
        } else {
            return msgLeft;
        }
    }
    public void addMessageG (groupChatModel model){
        mChat.add(model);
        notifyDataSetChanged();
    }
}
