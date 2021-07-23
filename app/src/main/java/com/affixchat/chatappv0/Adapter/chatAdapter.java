package com.affixchat.chatappv0.Adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.affixchat.chatappv0.R;
import com.affixchat.chatappv0.Models.chatModel;
import com.affixchat.chatappv0.chatPage;
import com.affixchat.chatappv0.forwardMessage;
import com.affixchat.chatappv0.imageViewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class chatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<chatModel> mChat;
    FirebaseUser firebaseUser;
    String userId;
    TextView nameText;
    ArrayList<String> imageTypes = new ArrayList<>();
    ArrayList<String> videoTypes = new ArrayList<>();
    ArrayList<String> audioTypes = new ArrayList<>();
    ImageView dp, delete, forward,copy, videoCallBtn, callBtn;
    ClipboardManager clipboardManager;
    int count=0;
    final ArrayList<chatModel> list = new ArrayList<>();
    public static final int msgLeft = 0;
    public static final int msgRight = 1;
    public static final int fileLeft = 2;
    public static final int fileRight = 3;
    public static final int imgLeft = 4;
    public static final int imgRight = 5;

    public chatAdapter() {
    }

    public chatAdapter(Context context, ArrayList<chatModel> mChat,String userId) {
        this.context = context;
        this.mChat = mChat;
        this.userId=userId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == msgLeft) {
            return new msgHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.left_msg_adapter, parent, false));
        } else if(viewType == msgRight){
            return new msgHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.right_msg_adapter, parent, false));
        }else if(viewType == fileLeft){
            return new fileHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.left_file_adapter, parent, false));
        }else if(viewType==imgLeft){
            return new imgHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.left_img_adapter, parent, false));
        }else if(viewType==imgRight){
            return new imgHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.right_img_adapter, parent, false));
        }

        else{
            return new fileHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.right_file_adapter, parent, false));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final chatModel chat = mChat.get(position);
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
                        nameText = ((chatPage) context).findViewById(R.id.name);
                        nameText.setVisibility(View.INVISIBLE);
                        dp = ((chatPage) context).findViewById(R.id.DP);
                        dp.setVisibility(View.INVISIBLE);
                        delete=((chatPage) context).findViewById(R.id.deleteIcon);
                        delete.setVisibility(View.VISIBLE);
                        forward=((chatPage) context).findViewById(R.id.forwardIcon);
                        forward.setVisibility(View.VISIBLE);
                        copy=((chatPage) context).findViewById(R.id.copyIcon);
                        videoCallBtn = ((chatPage) context).findViewById(R.id.videoCall);
                        videoCallBtn.setVisibility(View.INVISIBLE);
                        callBtn = ((chatPage) context).findViewById(R.id.call);
                        callBtn.setVisibility(View.INVISIBLE);

                        if(count==0) {
                            copy.setVisibility(View.VISIBLE);
                        }
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AlertDialog.Builder(context)
                                    .setTitle("Delete Messages?")
                                    .setMessage("Only Messages sent by you will be deleted for everyone. do you want to delete?")
                                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            for(int i=list.size()-1;i>=0;i--) {
                                                if (list.get(i).getSenderUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats").child(list.get(i).getKey());
                                                    reference.setValue(null);
                                                    nameText.setVisibility(View.VISIBLE);
                                                    dp.setVisibility(View.VISIBLE);
                                                    videoCallBtn.setVisibility(View.VISIBLE);
                                                    callBtn.setVisibility(View.VISIBLE);
                                                    delete.setVisibility(View.INVISIBLE);
                                                    forward.setVisibility(View.INVISIBLE);
                                                    copy.setVisibility(View.INVISIBLE);
                                                }else {
                                                    Toast.makeText(context, "You can not delete messages sent by others", Toast.LENGTH_SHORT).show();
                                                }
                                            }
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
                                Intent i = new Intent(context,chatPage.class);
                                i.putExtra("userId",userId);
                                context.startActivity(i);
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
                        nameText = ((chatPage) context).findViewById(R.id.name);
                        nameText.setVisibility(View.VISIBLE);
                        dp = ((chatPage) context).findViewById(R.id.DP);
                        dp.setVisibility(View.VISIBLE);
                        videoCallBtn = ((chatPage) context).findViewById(R.id.videoCall);
                        videoCallBtn.setVisibility(View.VISIBLE);
                        callBtn = ((chatPage) context).findViewById(R.id.call);
                        callBtn.setVisibility(View.VISIBLE);
                        delete=((chatPage) context).findViewById(R.id.deleteIcon);
                        delete.setVisibility(View.INVISIBLE);
                        forward=((chatPage) context).findViewById(R.id.forwardIcon);
                        forward.setVisibility(View.INVISIBLE);
                        copy=((chatPage) context).findViewById(R.id.copyIcon);
                        copy.setVisibility(View.INVISIBLE);
                    }
                    return false;
                }
            });
        }
        else if(getItemViewType(position) == imgRight|| getItemViewType(position) == imgLeft){
            final imgHolder imgholder = (imgHolder) holder;
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
                        nameText = ((chatPage) context).findViewById(R.id.name);
                        nameText.setVisibility(View.INVISIBLE);
                        dp = ((chatPage) context).findViewById(R.id.DP);
                        dp.setVisibility(View.INVISIBLE);
                        videoCallBtn = ((chatPage) context).findViewById(R.id.videoCall);
                        videoCallBtn.setVisibility(View.INVISIBLE);
                        callBtn = ((chatPage) context).findViewById(R.id.call);
                        callBtn.setVisibility(View.INVISIBLE);
                        delete=((chatPage) context).findViewById(R.id.deleteIcon);
                        delete.setVisibility(View.VISIBLE);
                        forward=((chatPage) context).findViewById(R.id.forwardIcon);
                        forward.setVisibility(View.VISIBLE);
                        copy=((chatPage) context).findViewById(R.id.copyIcon);
                        copy.setVisibility(View.GONE);
                        Log.d("ojaslistoutside",list.size()+"");
                        final Boolean[] ok = {false};
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AlertDialog.Builder(context)
                                        .setTitle("Delete Messages?")
                                        .setMessage("Only Messages sent by you will be deleted for everyone. do you want to delete?")
                                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                for(int i=list.size()-1;i>=0;i--) {
                                                    if (list.get(i).getSenderUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats").child(list.get(i).getKey());
                                                        reference.setValue(null);
                                                        nameText.setVisibility(View.VISIBLE);
                                                        dp.setVisibility(View.VISIBLE);
                                                        videoCallBtn.setVisibility(View.VISIBLE);
                                                        callBtn.setVisibility(View.VISIBLE);
                                                        delete.setVisibility(View.INVISIBLE);
                                                        forward.setVisibility(View.INVISIBLE);
                                                        copy.setVisibility(View.INVISIBLE);
                                                    }else {
                                                        Toast.makeText(context, "You can not delete messages sent by others", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
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
                                Intent i =new Intent(context, forwardMessage.class);
                                Bundle args = new Bundle();
                                args.putSerializable("arrayList",list);
                                i.putExtra("BUNDLE",args);
                                context.startActivity(i);
                            }
                        });
                    }else{
                        nameText = ((chatPage) context).findViewById(R.id.name);
                        nameText.setVisibility(View.VISIBLE);
                        dp = ((chatPage) context).findViewById(R.id.DP);
                        dp.setVisibility(View.VISIBLE);
                        videoCallBtn = ((chatPage) context).findViewById(R.id.videoCall);
                        videoCallBtn.setVisibility(View.VISIBLE);
                        callBtn = ((chatPage) context).findViewById(R.id.call);
                        callBtn.setVisibility(View.VISIBLE);
                        delete=((chatPage) context).findViewById(R.id.deleteIcon);
                        delete.setVisibility(View.INVISIBLE);
                        forward=((chatPage) context).findViewById(R.id.forwardIcon);
                        forward.setVisibility(View.INVISIBLE);
                        copy=((chatPage) context).findViewById(R.id.copyIcon);
                        copy.setVisibility(View.INVISIBLE);
                    }
                    return false;
                }
            });

        }
        else{
            final fileHolder fileholder = (fileHolder) holder;
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
                        nameText = ((chatPage) context).findViewById(R.id.name);
                        nameText.setVisibility(View.INVISIBLE);
                        dp = ((chatPage) context).findViewById(R.id.DP);
                        dp.setVisibility(View.INVISIBLE);
                        videoCallBtn = ((chatPage) context).findViewById(R.id.videoCall);
                        videoCallBtn.setVisibility(View.INVISIBLE);
                        callBtn = ((chatPage) context).findViewById(R.id.call);
                        callBtn.setVisibility(View.INVISIBLE);
                        delete=((chatPage) context).findViewById(R.id.deleteIcon);
                        delete.setVisibility(View.VISIBLE);
                        forward=((chatPage) context).findViewById(R.id.forwardIcon);
                        forward.setVisibility(View.VISIBLE);
                        copy=((chatPage) context).findViewById(R.id.copyIcon);
                        copy.setVisibility(View.GONE);
                        Log.d("ojaslistoutside",list.size()+"");
                        final Boolean[] ok = {false};
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AlertDialog.Builder(context)
                                    .setTitle("Delete Messages?")
                                    .setMessage("Only Messages sent by you will be deleted for everyone. do you want to delete?")
                                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            for(int i=list.size()-1;i>=0;i--) {
                                                if (list.get(i).getSenderUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats").child(list.get(i).getKey());
                                                    reference.setValue(null);
                                                    nameText.setVisibility(View.VISIBLE);
                                                    dp.setVisibility(View.VISIBLE);
                                                    videoCallBtn.setVisibility(View.VISIBLE);
                                                    callBtn.setVisibility(View.VISIBLE);
                                                    delete.setVisibility(View.INVISIBLE);
                                                    forward.setVisibility(View.INVISIBLE);
                                                    copy.setVisibility(View.INVISIBLE);
                                                }else {
                                                    Toast.makeText(context, "You can not delete messages sent by others", Toast.LENGTH_SHORT).show();
                                                }
                                            }
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
                                Intent i =new Intent(context, forwardMessage.class);
                                Bundle args = new Bundle();
                                args.putSerializable("arrayList",list);
                                i.putExtra("BUNDLE",args);
                                context.startActivity(i);
                            }
                        });
                    }else{
                        nameText = ((chatPage) context).findViewById(R.id.name);
                        nameText.setVisibility(View.VISIBLE);
                        dp = ((chatPage) context).findViewById(R.id.DP);
                        dp.setVisibility(View.VISIBLE);
                        videoCallBtn = ((chatPage) context).findViewById(R.id.videoCall);
                        videoCallBtn.setVisibility(View.VISIBLE);
                        callBtn = ((chatPage) context).findViewById(R.id.call);
                        callBtn.setVisibility(View.VISIBLE);
                        delete=((chatPage) context).findViewById(R.id.deleteIcon);
                        delete.setVisibility(View.INVISIBLE);
                        forward=((chatPage) context).findViewById(R.id.forwardIcon);
                        forward.setVisibility(View.INVISIBLE);
                        copy=((chatPage) context).findViewById(R.id.copyIcon);
                        copy.setVisibility(View.INVISIBLE);
                    }
                    return false;
                }
            });
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public static class msgHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView time;
        ConstraintLayout border;
        ConstraintLayout layout;
        public msgHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.showMessage);
            time = itemView.findViewById(R.id.time);
            border=itemView.findViewById(R.id.imageView3);
            layout=itemView.findViewById(R.id.background);
            }
    }
    public static class fileHolder extends RecyclerView.ViewHolder {
        Button openBtn;
        TextView time,type;
        ImageView border;
        ConstraintLayout layout;
        public fileHolder(@NonNull View itemView) {
            super(itemView);
            openBtn=itemView.findViewById(R.id.openBtn);
            time=itemView.findViewById(R.id.time);
            type=itemView.findViewById(R.id.type);
            border=itemView.findViewById(R.id.imageView3);
            layout=itemView.findViewById(R.id.background);
        }
    }

    public static class imgHolder extends RecyclerView.ViewHolder {
        TextView time;
        ConstraintLayout border;
        ConstraintLayout layout;
        ImageView img;
        public imgHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            border=itemView.findViewById(R.id.imageView3);
            layout=itemView.findViewById(R.id.background);
            img=itemView.findViewById(R.id.showMessageFile);
        }
    }
    @Override
    public int getItemViewType(int position) {
//        //ArrayLists
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
            }
//            else if(videoTypes.contains(mChat.get(position).getType())){return fileLeft;}
//            else if(audioTypes.contains(mChat.get(position).getType())){return fileLeft;}
            else {
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

}