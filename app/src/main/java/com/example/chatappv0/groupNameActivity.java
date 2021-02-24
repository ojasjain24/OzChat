package com.example.chatappv0;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatappv0.Models.groupDataModel;
import com.example.chatappv0.Models.usersModel;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class groupNameActivity extends AppCompatActivity {
    EditText groupName, groupDescription;
    FloatingActionButton nextBtn;
    private static final int imageRequest = 1;
    private Uri imageUri;
    private DatabaseReference userdata;
    private FirebaseUser user;
    ImageView profilePic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_name);
        groupName=findViewById(R.id.editGroupName);
        groupDescription=findViewById(R.id.editGroupDescription);
        profilePic=findViewById(R.id.groupIcon);
        final Intent i =getIntent();
        setProfileImage(i.getStringExtra("nodeId"));
        nextBtn=findViewById(R.id.floatingActionButton2);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String description = groupDescription.getText().toString();
                final String name = groupName.getText().toString();
                if((name.trim().equals(""))&&(description.trim().equals(""))){
                    Toast.makeText(groupNameActivity.this, "Please enter full details", Toast.LENGTH_SHORT).show();
                }else{
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(""+i.getStringExtra("nodeId"));
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("name",name);
                    hashMap.put("description",description);
                    hashMap.put("nodeid",""+i.getStringExtra("nodeId"));
                    reference.updateChildren(hashMap);
                    startActivity(new Intent(groupNameActivity.this,MainActivity.class));
                }
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
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
            assert data != null;
            imageUri=data.getData();
            Intent i =getIntent();
            uploadImage(i.getStringExtra("nodeId"));
        }
    }
    private void openImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,imageRequest);
    }
    private String getfilextention(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(final String nodeId){
        final ProgressDialog pd= new ProgressDialog(this);
        pd.setMessage("uploading");
        pd.show();
        if(imageUri != null){
            final StorageReference fileref = FirebaseStorage.getInstance().getReference().child("uploads").child(System.currentTimeMillis()+"."+getfilextention(imageUri));
            fileref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                pd.dismiss();
                                usersModel model = new usersModel();
                                user= FirebaseAuth.getInstance().getCurrentUser();
                                userdata= FirebaseDatabase.getInstance().getReference().child("groups").child(nodeId);
                                HashMap<String ,Object> usermap=new HashMap<>();
                                usermap.put("groupicon", url);
                                userdata.updateChildren(usermap);
                                Toast.makeText(groupNameActivity.this, "upload successful", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(groupNameActivity.this, "Error : " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        Toast.makeText(groupNameActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }
    public void setProfileImage(String nodeId){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("groups").child(nodeId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupDataModel model = dataSnapshot.getValue(groupDataModel.class);
                if (model != null) {
                    if (model.getGroupicon() != null) {
                        Picasso.get().load(Uri.parse(model.getGroupicon())).into(profilePic);
                    }else{
                        profilePic.setImageResource(R.drawable.ic_launcher_foreground);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final Intent i= getIntent();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(i.getStringExtra("nodeId"));
        reference.setValue(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        final Intent i= getIntent();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(i.getStringExtra("nodeId"));
        reference.setValue(null);
    }
}