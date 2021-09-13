package com.affixchat.chatappv0;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.affixchat.chatappv0.Models.groupMemberModel;
import com.affixchat.chatappv0.Notification.sendNotificationFunction;
import com.airbnb.lottie.LottieAnimationView;
import com.affixchat.chatappv0.Adapter.groupChatAdapter;
import com.affixchat.chatappv0.Models.groupChatModel;
import com.affixchat.chatappv0.Models.groupDataModel;
import com.affixchat.chatappv0.Models.usersModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static java.lang.Float.parseFloat;

public class groupChat extends AppCompatActivity {
    private TextView name;
    private FirebaseUser user;
    private RecyclerView messageList;
    private DatabaseReference reference;
    private ArrayList<groupChatModel> chatList;
    private ImageView attach,back;
    private static final int imageRequest = 1;
    private Uri imageUri;
    private CardView meetingCard;
    private Button joinMeetBtn;
    private TextView meetHostName, meetType;
    private ImageView meetIcon;
    private ImageView videoCall,audioCall;
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;
    String groupName="";
    private final byte[] encryptionKey ={5,15,-65,-56,3,45,-96,37,85,64,85,-92,-12,-5,64,-50};
    static String LastMessageTime;
    ImageView delete, copy,forward;
    groupChatAdapter adapter;
    ArrayList<groupMemberModel> membersList = new ArrayList<>();
    public groupChat(){
    }
    @SuppressLint("GetInstance")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources.Theme theme = super.getTheme();
        new ThemeSetter().aSetTheme(this,theme);
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null) {
            this.getSupportActionBar().hide();
        }
        try {
            cipher = Cipher.getInstance("AES");
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        secretKeySpec = new SecretKeySpec(encryptionKey, "AES");
        setContentView(R.layout.activity_group_chat);
        Intent intent = getIntent();
        final String nodeId= intent.getStringExtra("nodeId");

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String text = sharedPreferences.getString("Theme", "Default");
        LottieAnimationView bgAnimation = findViewById(R.id.bgAnimation);
        if(!text.equals("Default")){
            bgAnimation.setVisibility(View.INVISIBLE);
        }else{
            bgAnimation.setVisibility(View.VISIBLE);
        }
        membersDetail(new OnGetObjectListener<ArrayList<groupMemberModel>>() {
            @Override
            public void onGetObject(ArrayList<groupMemberModel> object) {
                membersList=object;
            }

            @Override
            public void onFail(Exception e) {
                Toast.makeText(groupChat.this, "check your network connection", Toast.LENGTH_SHORT).show();
            }
        });final ImageView DP = findViewById(R.id.chatPageDp);
        attach=findViewById(R.id.imageView2g);
        name = findViewById(R.id.chatPageName);
        delete=findViewById(R.id.deleteIcong);
        copy=findViewById(R.id.copyIcong);
        forward=findViewById(R.id.forwardIcong);
        meetingCard = findViewById(R.id.include);
        joinMeetBtn=findViewById(R.id.joinMeetBtn);
        meetHostName=findViewById(R.id.meetCreatorName);
        meetType=findViewById(R.id.meetType);
        meetIcon=findViewById(R.id.meetLogo);
        videoCall=findViewById(R.id.videoCall);
        audioCall=findViewById(R.id.call);
        back=findViewById(R.id.imageView6g);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (Objects.equals(intent.getStringExtra("endTime"), "0")){
            videoCall.setVisibility(View.INVISIBLE);
            audioCall.setVisibility(View.INVISIBLE);
            meetingCard.setVisibility(View.VISIBLE);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(intent.getStringExtra("host"));
            databaseReference.addValueEventListener(new ValueEventListener() {
                @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    meetHostName.setText(snapshot.child("username").getValue().toString());
                    if(intent.getStringExtra("type").equals("video")){
                        meetType.setText("Video");
                        meetIcon.setImageDrawable(getDrawable(R.drawable.video_call));
                    }else{
                        meetType.setText("Audio");
                        meetIcon.setImageDrawable(getDrawable(R.drawable.call));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            joinMeetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(groupChat.this, groupMeetingActivity.class);
                    i.putExtra("key",intent.getStringExtra("key"));
                    i.putExtra("groupUid", nodeId);
                    i.putExtra("type", intent.getStringExtra("type"));
                    startActivity(i);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            });
        }else{
            videoCall.setVisibility(View.VISIBLE);
            audioCall.setVisibility(View.VISIBLE);
            meetingCard.setVisibility(View.GONE);
        }

        messageList = findViewById(R.id.chatPageMessageList);
        messageList.setHasFixedSize(true);
        final TextView message = findViewById(R.id.chatPageMessage);
        ImageView send = findViewById(R.id.sendMSGg);
        user= FirebaseAuth.getInstance().getCurrentUser();
        readMsg(nodeId);
        reference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    final groupDataModel groupDetails = dataSnapshot.getValue(groupDataModel.class);
                    groupName=(groupDetails.getName()).substring(0,1).toUpperCase()+(groupDetails.getName()).substring(1);
                    name.setText((groupDetails.getName()).substring(0,1).toUpperCase()+(groupDetails.getName()).substring(1));
                    if (groupDetails.getGroupicon() != null) {
                        Picasso.get().load(Uri.parse(groupDetails.getGroupicon())).into(DP);
                    }else{
                        DP.setImageResource(R.drawable.ic_launcher_foreground);
                    }
                    name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(groupChat.this,groupVisit.class);
                            i.putExtra("name",groupDetails.getName());
                            i.putExtra("description",groupDetails.getDescription());
                            i.putExtra("pic",groupDetails.getGroupicon());
                            i.putExtra("nodeId",groupDetails.getNodeid());
                            i.putExtra("Type", groupDetails.getType());
                            startActivity(i);
                        }
                    });
                    DP.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(groupChat.this,groupVisit.class);
                            i.putExtra("name",groupDetails.getName());
                            i.putExtra("description",groupDetails.getDescription());
                            i.putExtra("pic",groupDetails.getGroupicon());
                            i.putExtra("nodeId",groupDetails.getNodeid());
                            i.putExtra("Type", groupDetails.getType());
                            startActivity(i);
                        }
                    });
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendNotificationFunction notificationFunction = new sendNotificationFunction();
                notificationFunction.sendNotification(membersList.get(0).getUid(), user.getUid(), groupChat.this, "Join the "+groupName+" group Video call", "Group Video Call", new OnGetObjectListener<Boolean>() {
                    @Override
                    public void onGetObject(Boolean object) {
                        onVideoCallClicked(nodeId);
                    }

                    @Override
                    public void onFail(Exception e) {
                        onVideoCallClicked(nodeId);
                    }
                });
                int j=1;
                while (j<= membersList.size()-1) {
                    if (!membersList.get(j).getUid().equals(user.getUid())) {
                        notificationFunction.sendNotification(membersList.get(j).getUid(), user.getUid(), groupChat.this, "Join the "+groupName+" group Video call", "Group Video Call", new OnGetObjectListener<Boolean>() {
                            @Override
                            public void onGetObject(Boolean object) {
                            }

                            @Override
                            public void onFail(Exception e) {
                            }
                        });
                    }
                    j+=1;
                }
            }
        });

        audioCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendNotificationFunction notificationFunction = new sendNotificationFunction();
                notificationFunction.sendNotification(membersList.get(0).getUid(), user.getUid(), groupChat.this, "Join the "+groupName+" group Audio call", "Group Audio Call", new OnGetObjectListener<Boolean>() {
                    @Override
                    public void onGetObject(Boolean object) {
                        onAudioCallClicked(nodeId);
                    }

                    @Override
                    public void onFail(Exception e) {
                        onAudioCallClicked(nodeId);

                    }
                });
                int j=1;
                while (j<= membersList.size()-1) {
                    if (!membersList.get(j).getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        notificationFunction.sendNotification(membersList.get(j).getUid(), user.getUid(), groupChat.this, "Join the "+groupName+" group Audio call", "Group Audio Call", new OnGetObjectListener<Boolean>() {
                            @Override
                            public void onGetObject(Boolean object) {
                            }

                            @Override
                            public void onFail(Exception e) {
                            }
                        });
                    }
                    j+=1;
                }


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

        adapter = new groupChatAdapter(groupChat.this,nodeId);
        LinearLayoutManager manager=new LinearLayoutManager(getApplicationContext());
        manager.setStackFromEnd(true);
        messageList.setLayoutManager(manager);
        messageList.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==imageRequest && resultCode==RESULT_OK){
            imageUri=data.getData();
            final int[] out = {0};
            AssetFileDescriptor fileDescriptor = null;
            try {
                fileDescriptor = getApplicationContext().getContentResolver().openAssetFileDescriptor(imageUri , "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final float fileSize = fileDescriptor.getLength()/(1024.00f*1024.00f);
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String points =snapshot.getValue(usersModel.class).getPoints();
                    if(parseFloat(points)>=fileSize && out[0] ==0){
                        uploadImage();
                        float pointsnew = parseFloat(points) - fileSize;
                        reference.child("points").setValue(pointsnew+"");
                    }else if(parseFloat(points)<fileSize){
                        Toast.makeText(groupChat.this, "Not Enough Points! Go to reward section to get the Points.", Toast.LENGTH_SHORT).show();
                    }
                    out[0] =1;
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void onAudioCallClicked(String nodeId){

        Intent i =new Intent(groupChat.this, groupMeetingActivity.class);
        DatabaseReference meetData = FirebaseDatabase.getInstance().getReference().child("groups").child(nodeId).child("meetings").push();
        HashMap<String ,String>usermap=new HashMap<>();
        usermap.put("key",meetData.getKey());
        usermap.put("endTime","0");
        usermap.put("hostUid",user.getUid());
        usermap.put("startTime",System.currentTimeMillis()+"");
        usermap.put("type","audio");
        meetData.setValue(usermap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                i.putExtra("key", meetData.getKey()+"");
                i.putExtra("groupUid", nodeId);
                i.putExtra("type", "audio");
                startActivity(i);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }
    private void onVideoCallClicked(String nodeId){
        Intent i =new Intent(groupChat.this, groupMeetingActivity.class);
        DatabaseReference meetData = FirebaseDatabase.getInstance().getReference().child("groups").child(nodeId).child("meetings").push();
        HashMap<String ,String>usermap=new HashMap<>();
        usermap.put("key",meetData.getKey());
        usermap.put("endTime","0");
        usermap.put("hostUid",user.getUid());
        usermap.put("startTime",System.currentTimeMillis()+"");
        usermap.put("type","video");
        meetData.setValue(usermap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                i.putExtra("key", meetData.getKey()+"");
                i.putExtra("groupUid", nodeId);
                i.putExtra("type", "video");
                startActivity(i);
            }
        });
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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId);
        HashMap<String , Object> hashMap1 = new HashMap<>();
        hashMap1.put("lastmsg",""+LastMessageTime);
        databaseReference.updateChildren(hashMap1);

        sendNotificationFunction notificationFunction = new sendNotificationFunction();
        int j=0;
        while (j<= membersList.size()-1) {
            if (!membersList.get(j).getUid().equals(me)) {
                notificationFunction.sendNotification(membersList.get(j).getUid(), me, groupChat.this, "New message in "+groupName+" group", "New Group Message", null);
            }
            j+=1;
        }
    }

//    private void readMsgm(final String myuid, final String nodeId){
//        chatList = new ArrayList<>();
//        reference = FirebaseDatabase.getInstance().getReference().child("groups").child(nodeId).child("chats");
//        Query query= (reference.orderByChild("time"));
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                chatList.clear();
//                for(DataSnapshot Snapshot : snapshot.getChildren()) {
//                    groupChatModel chat = Snapshot.getValue(groupChatModel.class);
//                    if (chat != null) {
//                        try {
//                            chatList.add(new groupChatModel(chat.getSenderUid(),chat.getIsThisFile(),chat.getKey(),AESDecryptionMethod(chat.getMessage()),chat.getTime(),chat.getType()));
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                if(chatList.size()!=0){
//                    LastMessageTime = chatList.get(chatList.size()-1).getTime();
//                }else{
//                    LastMessageTime = "0";
//                }
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//    }

    private void readMsg(final String nodeId) {
        chatList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("groups").child(nodeId).child("chats");
        Query query = (reference.orderByChild("time"));
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    groupChatModel chat = snapshot.getValue(groupChatModel.class);
                    if (chat != null) {
                        try {
                            adapter.addMessageG(new groupChatModel(chat.getSenderUid(),chat.getIsThisFile(),chat.getKey(),AESDecryptionMethod(chat.getMessage()),chat.getTime(),chat.getType()));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    messageList.smoothScrollToPosition(adapter.getItemCount());
                }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId);
        HashMap<String , Object> hashMap1 = new HashMap<>();
        hashMap1.put("lastmsg",""+LastMessageTime);
        databaseReference.updateChildren(hashMap1);
        sendNotificationFunction notificationFunction = new sendNotificationFunction();
        int j=0;
        while (j<= membersList.size()-1) {
            if (!membersList.get(j).getUid().equals(me)) {
                notificationFunction.sendNotification(membersList.get(j).getUid(), me, groupChat.this, "New message in "+groupName+" group", "New Group Message", null);
            }
            j+=1;
        }
    }

    private void uploadImage(){
        final Intent intent = getIntent();
        final ProgressDialog pd= new ProgressDialog(this);
        pd.setMessage("uploading");
        pd.setCanceledOnTouchOutside(false);
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
                        Toast.makeText(groupChat.this, "Error : " + Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_LONG).show();
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
        startActivity(new Intent(groupChat.this, MainActivity.class));
        finishAffinity();
    }

    private String AESEncryptionMethod(String string){

        byte[] stringByte = string.getBytes();
        byte[] encryptedByte = new byte[stringByte.length];

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedByte = cipher.doFinal(stringByte);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
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
            decipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            decryption = decipher.doFinal(EncryptedByte);
            decryptedString = new String(decryption);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptedString;
    }

    private void membersDetail(OnGetObjectListener<ArrayList<groupMemberModel>> callback){
        ArrayList<groupMemberModel> groupMembers = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("groups").child(getIntent().getStringExtra("nodeId")).child("members");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    groupMemberModel memberModel = dataSnapshot.getValue(groupMemberModel.class);
                    groupMembers.add(memberModel);
                }
                callback.onGetObject(groupMembers);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFail(error.toException());
            }
        });
    }
}