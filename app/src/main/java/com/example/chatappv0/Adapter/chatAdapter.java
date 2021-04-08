package com.example.chatappv0.Adapter;

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
import android.provider.ContactsContract;
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

import com.example.chatappv0.R;
import com.example.chatappv0.Models.chatModel;
import com.example.chatappv0.chatPage;
import com.example.chatappv0.forwardMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
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
    ImageView dp, delete, forward,copy;
    ClipboardManager clipboardManager;
    private SecretKeySpec secretKeySpec;
    private final byte[] encryptionKey ={5,15,-65,-56,3,45,-96,37,85,64,85,-92,-12,-5,64,-50};
    private Cipher cipher, decipher;
    int count=0;
    final ArrayList<chatModel> list = new ArrayList<>();
    public static final int msgLeft = 0;
    public static final int msgRight = 1;
    public static final int fileLeft = 2;
    public static final int fileRight = 3;

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
        if (viewType == msgRight) {
            return new msgHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.right_message_adapter, parent, false));
        } else if(viewType == msgLeft){
            return new msgHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.left_message_adapter, parent, false));
        }else if(viewType == fileRight){
            return new fileHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.right_file_adapter, parent, false));
        }else{
            return new fileHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.left_file_adapter, parent, false));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final chatModel chat = mChat.get(position);
        try {
            cipher = Cipher.getInstance("AES");
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        secretKeySpec = new SecretKeySpec(encryptionKey, "AES");

        if (getItemViewType(position) == msgRight|| getItemViewType(position) == msgLeft) {
            final msgHolder msgholder = (msgHolder) holder;
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
    @Override
    public int getItemViewType(int position) {
//        //ArrayLists
//        imageTypes.add("bmp");
//        imageTypes.add("gif");
//        imageTypes.add("jpg");
//        imageTypes.add("png");
//        imageTypes.add("webp");
//        videoTypes.add("mp4");
//        videoTypes.add("mkv");
//        videoTypes.add("webm");
//        audioTypes.add("3gp");
//        audioTypes.add("mp3");
//        audioTypes.add("wav");
//        audioTypes.add("ogg");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getIsThisFile().equals("true") && mChat.get(position).getSenderUid().equals(firebaseUser.getUid())) {
//            if(imageTypes.contains(mChat.get(position).getType())){return fileLeft;}
//            else if(videoTypes.contains(mChat.get(position).getType())){return fileLeft;}
//            else if(audioTypes.contains(mChat.get(position).getType())){return fileLeft;}
//            else {
                return fileLeft;
//            }
        } else if (mChat.get(position).getIsThisFile().equals("true") && (!mChat.get(position).getSenderUid().equals(firebaseUser.getUid()))) {
            return fileRight;
        } else if ((mChat.get(position).getIsThisFile().equals("false") && mChat.get(position).getSenderUid().equals(firebaseUser.getUid()))) {
            return msgLeft;
        } else {
            return msgRight;
        }
    }

    private String AESDecryptionMethod(String string) throws UnsupportedEncodingException {
        byte[] EncryptedByte = string.getBytes(StandardCharsets.ISO_8859_1);
        String decryptedString = string;

        byte[] decryption;

        try {
            decipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            decryption = decipher.doFinal(EncryptedByte);
            decryptedString = new String(decryption);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return decryptedString;
    }
}