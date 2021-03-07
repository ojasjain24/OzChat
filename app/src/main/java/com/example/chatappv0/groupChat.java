package com.example.chatappv0;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatappv0.Adapter.chatAdapter;
import com.example.chatappv0.Adapter.groupChatAdapter;
import com.example.chatappv0.Models.chatModel;
import com.example.chatappv0.Models.groupChatModel;
import com.example.chatappv0.Models.groupDataModel;
import com.example.chatappv0.Models.usersModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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

public class groupChat extends AppCompatActivity {
    private TextView name;
    private FirebaseUser user;
    private RecyclerView messageList;
    private DatabaseReference reference;
    private ArrayList<groupChatModel> chatList;
    private ImageView attach;
    private static final int imageRequest = 1;
    private Uri imageUri;
    private AdView mAdView;
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;
    private final byte[] encryptionKey ={5,15,-65,-56,3,45,-96,37,85,64,85,-92,-12,-5,64,-50};
    static String LastMessageTime;
    ImageView delete, copy,forward;
    public groupChat(){
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null) {
            this.getSupportActionBar().hide();
        }
        try {
            cipher = Cipher.getInstance("AES");
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        secretKeySpec = new SecretKeySpec(encryptionKey, "AES");
        setContentView(R.layout.activity_group_chat);
        mAdView = findViewById(R.id.adView3);
        final AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                super.onAdFailedToLoad(adError);
                mAdView.loadAd(adRequest);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
        final ImageView DP = findViewById(R.id.chatPageDp);
        attach=findViewById(R.id.imageView2g);
        name = findViewById(R.id.chatPageName);
        delete=findViewById(R.id.deleteIcong);
        copy=findViewById(R.id.copyIcong);
        forward=findViewById(R.id.forwardIcong);
        messageList = findViewById(R.id.chatPageMessageList);
        messageList.setHasFixedSize(true);
        final TextView message = findViewById(R.id.chatPageMessage);
        ImageView send = findViewById(R.id.sendMSGg);
        user= FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        final String nodeId= intent.getStringExtra("nodeId");
        reference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    final groupDataModel user = dataSnapshot.getValue(groupDataModel.class);
                    name.setText(user.getName());
                    if (user.getGroupicon() != null) {
                        Picasso.get().load(Uri.parse(user.getGroupicon())).into(DP);
                    }else{
                        DP.setImageResource(R.drawable.ic_launcher_foreground);
                    }
                    name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(groupChat.this,groupVisit.class);
                            i.putExtra("name",user.getName());
                            i.putExtra("description",user.getDescription());
                            i.putExtra("pic",user.getGroupicon());
                            i.putExtra("nodeId",user.getNodeid());
                            startActivity(i);
                        }
                    });
                    DP.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(groupChat.this,groupVisit.class);
                            i.putExtra("name",user.getName());
                            i.putExtra("description",user.getDescription());
                            i.putExtra("pic",user.getGroupicon());
                            i.putExtra("nodeId",user.getNodeid());
                            startActivity(i);
                        }
                    });
                }
                readMsg(user.getUid(),nodeId);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg= message.getText().toString();
                if(msg.equals("")){
                    Toast.makeText(groupChat.this, "no text entered", Toast.LENGTH_SHORT).show();
                }else {
                    sendMsg(user.getUid(),msg,nodeId);
                }
                message.setText("");
            }
        });
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==imageRequest && resultCode==RESULT_OK){
            imageUri=data.getData();
            uploadImage();

        }
    }

    private void sendMsg(String me, String message,String nodeId){
        DatabaseReference fileRef= FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("chats").push();
        HashMap<String,Object> hashMap = new HashMap();
        hashMap.put("senderUid",me);
        hashMap.put("message",AESEncryptionMethod(message));
        hashMap.put("isThisFile","false");
        hashMap.put("time",""+System.currentTimeMillis());
        hashMap.put("type","null");
        hashMap.put("key",fileRef.getKey());
        fileRef.setValue(hashMap);
    }
    private void readMsg(final String myuid, final String nodeId){
        chatList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("groups").child(nodeId).child("chats");
        Query query= (reference.orderByChild("time"));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for(DataSnapshot Snapshot : snapshot.getChildren()) {
                    groupChatModel chat = Snapshot.getValue(groupChatModel.class);
                    if (chat != null) {
                        try {
                            chatList.add(new groupChatModel(chat.getSenderUid(),chat.getIsThisFile(),chat.getKey(),AESDecryptionMethod(chat.getMessage()),chat.getTime(),chat.getType()));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(chatList.size()!=0){
                    LastMessageTime = chatList.get(chatList.size()-1).getTime();
                }else{
                    LastMessageTime = "0";
                }
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId);
                HashMap<String , Object> hashMap = new HashMap<>();
                hashMap.put("lastmsg",LastMessageTime);
                databaseReference.updateChildren(hashMap);
                groupChatAdapter adapter = new groupChatAdapter(groupChat.this, chatList,nodeId);
                LinearLayoutManager manager=new LinearLayoutManager(getApplicationContext());
                manager.setStackFromEnd(true);
                messageList.setLayoutManager(manager);
                messageList.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    //files sending
    private void sendFile(String me, String nodeId, String fileAddr){
        DatabaseReference fileRef= FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("chats").push();
        HashMap<String,Object> hashMap = new HashMap();
        hashMap.put("senderUid",me);
        hashMap.put("message",AESEncryptionMethod(fileAddr));
        hashMap.put("isThisFile","true");
        hashMap.put("time",""+System.currentTimeMillis());
        hashMap.put("type",""+getFileExtension(imageUri));
        hashMap.put("key",fileRef.getKey());
        fileRef.setValue(hashMap);
    }
    private void uploadImage(){
        final Intent intent = getIntent();
        final ProgressDialog pd= new ProgressDialog(this);
        pd.setMessage("uploading");
        pd.show();
        if(imageUri != null){
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("uploads").child(System.currentTimeMillis()+"."+ getFileExtension(imageUri));
            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                pd.dismiss();
                                Toast.makeText(groupChat.this, "upload successful", Toast.LENGTH_LONG).show();
                                StorageReference filePath= FirebaseStorage.getInstance().getReference().child("uploads");
                                sendFile(user.getUid(),intent.getStringExtra("nodeId"),url);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(groupChat.this, "Error : " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        pd.dismiss();
                        Toast.makeText(groupChat.this, "Error : " + task.getException().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void openImage() {
        Intent intent=new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,imageRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(groupChat.this,MainActivity.class));

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