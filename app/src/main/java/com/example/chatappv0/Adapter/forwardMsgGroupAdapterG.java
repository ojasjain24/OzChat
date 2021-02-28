package com.example.chatappv0.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappv0.Models.chatModel;
import com.example.chatappv0.Models.friendsModel;
import com.example.chatappv0.Models.groupChatModel;
import com.example.chatappv0.Models.groupDataModel;
import com.example.chatappv0.Models.usersModel;
import com.example.chatappv0.R;
import com.example.chatappv0.chatPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;


public class forwardMsgGroupAdapterG extends RecyclerView.Adapter<forwardMsgGroupAdapterG.Holder> {

    Context context;
    ArrayList<groupDataModel> data;
    ArrayList arrayList = new ArrayList();
    ArrayList<groupChatModel> chatModel;
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;
    private final byte[] encryptionKey ={5,15,-65,-56,3,45,-96,37,85,64,85,-92,-12,-5,64,-50};
    ArrayList<usersModel> usersModelArrayList = new ArrayList<>();

    public forwardMsgGroupAdapterG() {
    }

    public forwardMsgGroupAdapterG(Context context, ArrayList<groupDataModel> data,ArrayList<groupChatModel> chatModel) {
        this.context = context;
        this.data = data;
        this.chatModel=chatModel;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.select_user_for_group,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        holder.name.setText(data.get(position).getName());
        try {
            cipher = Cipher.getInstance("AES");
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        secretKeySpec = new SecretKeySpec(encryptionKey, "AES");
        if(data.get(position).getGroupicon() != null) {
            Picasso.get().load(Uri.parse(data.get(position).getGroupicon())).into(holder.profile);
        }else{
            holder.profile.setImageResource(R.drawable.ic_launcher_foreground);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBox.isChecked()) {
                    arrayList.add(data.get(position).getNodeid());
                }
                if(arrayList.contains(data.get(position).getNodeid())&&(!holder.checkBox.isChecked())){
                    arrayList.remove(data.get(position).getNodeid());
                }
            }
        });

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkBox.setChecked(!holder.checkBox.isChecked());
                if(holder.checkBox.isChecked()) {
                    arrayList.add(data.get(position).getNodeid());
                }
                if(arrayList.contains(data.get(position).getNodeid())&&(!holder.checkBox.isChecked())){
                    arrayList.remove(data.get(position).getNodeid());
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView name;
        CircleImageView profile;
        ConstraintLayout card;
        CheckBox checkBox;

        public Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textView10);
            profile = itemView.findViewById(R.id.imageView);
            card=itemView.findViewById(R.id.select_for_group);
            checkBox=itemView.findViewById(R.id.checkBox);
        }
    }
    public void uploadOnDataBase() {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < chatModel.size(); j++) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(arrayList.get(i).toString()).child("chats").push();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("senderUid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("message",AESEncryptionMethod(chatModel.get(j).getMessage()));
                hashMap.put("isThisFile",chatModel.get(j).getIsThisFile());
                hashMap.put("time",""+System.currentTimeMillis());
                hashMap.put("type",chatModel.get(j).getType());
                hashMap.put("isseen","false");
                hashMap.put("key",databaseReference.getKey());
                databaseReference.setValue(hashMap);
                Log.d("ojasinsidefunction","inside");
            }
        }
    }
    private String AESEncryptionMethod(String string){

        byte[] stringByte = string.getBytes();
        byte[] encryptedByte = new byte[stringByte.length];

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedByte = cipher.doFinal(stringByte);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        String returnString = null;

        try {
            returnString = new String(encryptedByte, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnString;
    }

    private String AESDecryptionMethod(String string) throws UnsupportedEncodingException {
        byte[] EncryptedByte = string.getBytes("ISO-8859-1");
        String decryptedString = string;

        byte[] decryption;

        try {
            decipher.init(cipher.DECRYPT_MODE, secretKeySpec);
            decryption = decipher.doFinal(EncryptedByte);
            decryptedString = new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptedString;
    }
}