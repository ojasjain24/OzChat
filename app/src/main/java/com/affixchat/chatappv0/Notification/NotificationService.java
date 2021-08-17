package com.affixchat.chatappv0.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.MODE_PRIVATE;
import static com.affixchat.chatappv0.Notification.OreaNotification.CHANNELID;

public class NotificationService extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void onReceive(Context context, Intent intent) {
//
//        NotificationManager notification = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
//        if (remoteInput!=null) {

//            String replyText = remoteInput.getString("key_text_reply");
//
//            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//            String userid = firebaseAuth.getCurrentUser().getUid();
//
//
//            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//
//
//            SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", MODE_PRIVATE);
//            String friendid = sharedPreferences.getString("friendid", "");


//            SimpleDateFormat formatter= new SimpleDateFormat( "HH:mm:ss");
//            Date date = new Date(System.currentTimeMillis());
//            String currenttime = formatter.format(date);
//
////to be added
//            DatabaseReference fileRef= FirebaseDatabase.getInstance().getReference().child("chats").push();
//            HashMap<String,Object> hashMap = new HashMap();
//            hashMap.put("senderUid",userid);
//            hashMap.put("receiverUid", friendid);
//            hashMap.put("message",AESEncryptionMethod(replyText));
//            hashMap.put("isThisFile","false");
//            hashMap.put("time",""+System.currentTimeMillis());
//            hashMap.put("type","null");
//            hashMap.put("isseen","false");
//            hashMap.put("key",fileRef.getKey());
//            fileRef.setValue(hashMap);
//
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userid).child("friends").child(friendid);
//            HashMap<String , Object> hashMap1 = new HashMap<>();
//            hashMap1.put("lastmsg",""+System.currentTimeMillis());
//            databaseReference.updateChildren(hashMap1);
//
//            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("users").child(friendid).child("friends").child(userid);
//            HashMap<String , Object> map = new HashMap<>();
//            map.put("lastmsg",""+System.currentTimeMillis());
//            databaseReference1.updateChildren(map);
//
//            //was there
////            HashMap<String, Object> hashMap = new HashMap<>();
////            hashMap.put("sender", userid);
////            hashMap.put("receiver", friendid);
////            hashMap.put("message", replyText);
////            hashMap.put("time", currenttime);
////
////            firebaseFirestore.collection("Messages").document(currenttime).set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
////                @Override
////                public void onSuccess(Void aVoid) {
////
////                }
////            });


//            SharedPreferences shf = context.getSharedPreferences("NEWPREFS", MODE_PRIVATE);

//            int shit = shf.getInt("values", 0);

//            Notification repliedNotification =
//                    new NotificationCompat.Builder(context, CHANNELID)
//                            .setSmallIcon(
//                                    android.R.drawable.ic_dialog_info)
//                            .setContentText("Reply received")
//                            .build();
//
//            notification.notify(shit,
//                    repliedNotification);
//        }
    }

//    private String AESEncryptionMethod(String string){
//        SecretKeySpec secretKeySpec;
//        final byte[] encryptionKey ={5,15,-65,-56,3,45,-96,37,85,64,85,-92,-12,-5,64,-50};
//        Cipher cipher = null;
//        try {
//            cipher = Cipher.getInstance("AES");
//            } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        }
//        secretKeySpec = new SecretKeySpec(encryptionKey, "AES");
//
//        byte[] stringByte = string.getBytes();
//        byte[] encryptedByte = new byte[stringByte.length];
//
//        try {
//            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
//            encryptedByte = cipher.doFinal(stringByte);
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        }
//
//        String returnString = null;
//
//        try {
//            returnString = new String(encryptedByte, "ISO-8859-1");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return returnString;
//    }

}
