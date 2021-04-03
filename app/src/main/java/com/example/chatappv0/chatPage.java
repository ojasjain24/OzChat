package com.example.chatappv0;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatappv0.Adapter.chatAdapter;
import com.example.chatappv0.Models.chatModel;
import com.example.chatappv0.Models.usersModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.io.FileNotFoundException;
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
import static java.lang.Float.parseFloat;

public class chatPage extends AppCompatActivity {
    private TextView name;
    private FirebaseUser user;
    private RecyclerView messageList;
    private DatabaseReference reference;
    private ArrayList<chatModel> chatList;
    private ImageView attach;
    private static final int imageRequest = 1;
    private Uri imageUri;
    private ImageView DP;
    private Cipher cipher, decipher;
    private AdView mAdView;
    private SecretKeySpec secretKeySpec;
    private final byte[] encryptionKey ={5,15,-65,-56,3,45,-96,37,85,64,85,-92,-12,-5,64,-50};
    static String LastMessageTime;
    ImageView delete, copy,forward;

    public chatPage(){
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
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        setContentView(R.layout.activity_chat_page);
        mAdView = findViewById(R.id.adView2);
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
        delete=findViewById(R.id.deleteIcon);
        copy=findViewById(R.id.copyIcon);
        forward=findViewById(R.id.forwardIcon);
        DP = findViewById(R.id.DP);
        attach=findViewById(R.id.imageView2);
        name = findViewById(R.id.name);
        messageList = findViewById(R.id.messageList);
        messageList.setHasFixedSize(true);
        final TextView message = findViewById(R.id.message);
        ImageView send = findViewById(R.id.sendMSG);
        user= FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        final String userId= intent.getStringExtra("userId");
        reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    final usersModel usermodel = dataSnapshot.getValue(usersModel.class);
                    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                    if(usermodel.getUserid().equals(firebaseUser.getUid())){
                        name.setText("Me");
                    }else{
                        name.setText((usermodel.getUsername()).substring(0,1).toUpperCase()+(usermodel.getUsername()).substring(1));
                    }
                    if (usermodel.getImageurl() != null) {
                        Picasso.get().load(Uri.parse(usermodel.getImageurl())).into(DP);
                    }else{
                        DP.setImageResource(R.drawable.ic_launcher_foreground);
                    }
                    name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(chatPage.this,profileVisit.class);
                            i.putExtra("name",usermodel.getUsername());
                            i.putExtra("status",usermodel.getStatus());
                            i.putExtra("pic",usermodel.getImageurl());
                            i.putExtra("gender",usermodel.getGender());
                            i.putExtra("profession",usermodel.getProfession());
                            i.putExtra("country",usermodel.getCountry());
                            i.putExtra("language",usermodel.getLanguage());
                            startActivity(i);
                        }
                    });
                    DP.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(chatPage.this,profileVisit.class);
                            i.putExtra("name",usermodel.getUsername());
                            i.putExtra("status",usermodel.getStatus());
                            i.putExtra("pic",usermodel.getImageurl());
                            i.putExtra("gender",usermodel.getGender());
                            i.putExtra("profession",usermodel.getProfession());
                            i.putExtra("country",usermodel.getCountry());
                            i.putExtra("language",usermodel.getLanguage());
                            startActivity(i);
                        }
                    });
                }
                readMsg(user.getUid(),userId);
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
                    Toast.makeText(chatPage.this, "no text entered", Toast.LENGTH_SHORT).show();
                }else {
                    sendMsg(user.getUid(),userId,msg);
                }
                message.setText("");
            }
        });
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
//                final AlertDialog.Builder alert= new AlertDialog.Builder(chatPage.this);
//                View view = LayoutInflater.from(chatPage.this).inflate(R.layout.file_type_dialog_box,null);
//                FloatingActionButton photo, video, file, audio;
//                photo=view.findViewById(R.id.imgfb);
//                video=view.findViewById(R.id.vidfb);
//                file=view.findViewById(R.id.docfb);
//                audio=view.findViewById(R.id.audiofb);
//                alert.setView(view);
//                final AlertDialog alertDialog = alert.create();
//                alertDialog.setCanceledOnTouchOutside(true);
//                photo.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        openImage();
//                    }
//                });
//                video.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        openVideo();
//                    }
//                });
//                file.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        openFile();
//                    }
//                });
//                audio.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        openAudio();
//                    }
//                });
//                alertDialog.show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
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
                        Toast.makeText(chatPage.this, "You have used all your data. Go to reward section to earn more", Toast.LENGTH_SHORT).show();
                    }
                    out[0] =1;
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void sendMsg(String me, String receiver, String message){
        name.setVisibility(View.VISIBLE);
        DP.setVisibility(View.VISIBLE);
        forward.setVisibility(View.INVISIBLE);
        copy.setVisibility(View.INVISIBLE);
        delete.setVisibility(View.INVISIBLE);
        DatabaseReference fileRef= FirebaseDatabase.getInstance().getReference().child("chats").push();
        HashMap<String,Object> hashMap = new HashMap();
        hashMap.put("senderUid",me);
        hashMap.put("receiverUid", receiver);
        hashMap.put("message",AESEncryptionMethod(message));
        hashMap.put("isThisFile","false");
        hashMap.put("time",""+System.currentTimeMillis());
        hashMap.put("type","null");
        hashMap.put("isseen","false");
        hashMap.put("key",fileRef.getKey());
        fileRef.setValue(hashMap);
    }

    private void readMsg(final String myuid, final String receiveruid){
        chatList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("chats");
        Query query= (reference.orderByChild("time"));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for(DataSnapshot Snapshot : snapshot.getChildren()) {
                    chatModel chat = Snapshot.getValue(chatModel.class);
                    if (chat != null) {
                        if ((chat.getReceiverUid().equals(myuid)&&chat.getSenderUid().equals(receiveruid))||(chat.getReceiverUid().equals(receiveruid)&&
                                chat.getSenderUid().equals(myuid))) {
                            try {
                                chatList.add(new chatModel(chat.getSenderUid(),chat.getReceiverUid(),AESDecryptionMethod(chat.getMessage()),chat.getIsThisFile(),chat.getTime(),chat.getType(),chat.getIsseen(),chat.getKey()));
//                                String fileType = MimeTypeMap.getFileExtensionFromUrl(AESDecryptionMethod(chat.getMessage()));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        if ((chat.getReceiverUid().equals(myuid)&&chat.getSenderUid().equals(receiveruid))){
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("isseen","true");
                            Snapshot.getRef().updateChildren(hashMap);
                        }
                    }
                }
                if(chatList.size()!=0){
                    LastMessageTime = chatList.get(chatList.size()-1).getTime();
                }else{
                    LastMessageTime = "0";
                }
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(myuid).child("friends").child(receiveruid);
                HashMap<String , Object> hashMap = new HashMap<>();
                hashMap.put("lastmsg",LastMessageTime);
                databaseReference.updateChildren(hashMap);

                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("users").child(receiveruid).child("friends").child(myuid);
                HashMap<String , Object> map = new HashMap<>();
                map.put("lastmsg",LastMessageTime);
                databaseReference1.updateChildren(map);

                chatAdapter adapter = new chatAdapter(chatPage.this, chatList, getIntent().getStringExtra("userId"));
                LinearLayoutManager manager =new LinearLayoutManager(getApplicationContext());
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
    private void sendFile(String me, String receiver, String fileAddr){
        name.setVisibility(View.VISIBLE);
        DP.setVisibility(View.VISIBLE);
        forward.setVisibility(View.INVISIBLE);
        copy.setVisibility(View.INVISIBLE);
        delete.setVisibility(View.INVISIBLE);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("chats").push();
        HashMap<String,Object> hashMap = new HashMap();
        hashMap.put("senderUid",me);
        hashMap.put("receiverUid", receiver);
        hashMap.put("message",AESEncryptionMethod(fileAddr));
        hashMap.put("isThisFile","true");
        hashMap.put("time",""+System.currentTimeMillis());
        hashMap.put("type",""+getFileExtension(imageUri));
        hashMap.put("isseen","false");
        hashMap.put("key",reference.getKey());
        reference.setValue(hashMap);
    }

    private void uploadImage(){
        Intent intent = getIntent();
        final String userId= intent.getStringExtra("userId");
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
                                Toast.makeText(chatPage.this, "upload successful", Toast.LENGTH_LONG).show();
                                StorageReference filePath= FirebaseStorage.getInstance().getReference().child("uploads");
                                sendFile(user.getUid(),userId,url);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(chatPage.this, "Error : " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        pd.dismiss();
                        Toast.makeText(chatPage.this, "Error : " + task.getException().toString(), Toast.LENGTH_LONG).show();
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

//    private void openImage() {
//        Intent intent=new Intent();
//        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent,imageRequest);
//    }
//    private void openVideo() {
//        Intent intent=new Intent();
//        intent.setType("video/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent,imageRequest);
//    }
//    private void openAudio() {
//        Intent intent=new Intent();
//        intent.setType("audio/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent,imageRequest);
//    }
private void openFile() {
    Intent intent=new Intent();
    intent.setType("*/*");
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(intent,imageRequest);
}


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(chatPage.this,MainActivity.class));
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
            decipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
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