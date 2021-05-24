package com.example.chatappv0;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chatappv0.Adapter.groupRequestAdapter;
import com.example.chatappv0.Adapter.membersAdapter;
import com.example.chatappv0.Models.groupDataModel;
import com.example.chatappv0.Models.groupMemberModel;
import com.example.chatappv0.Models.groupRequestsModel;
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

import java.util.ArrayList;
import java.util.HashMap;

public class groupVisit extends AppCompatActivity {
    private Uri imageUri;
    private DatabaseReference userdata;
    private FirebaseUser user;
    private AdView mAdView;
    private ImageView pic,settingsIcon,leaveIcon, addMember,changeName,changeDescription;
    private TextView name, description,leave,settings, requestText;
    ArrayList<usersModel> userList = new ArrayList<>();
    ArrayList<usersModel> userList2 = new ArrayList<>();
    ArrayList<groupRequestsModel> requestList= new ArrayList<>();
    ArrayList<groupMemberModel> membersList = new ArrayList<>();
    ArrayList<groupMemberModel> membersList2 = new ArrayList<>();
    private static final int imageRequest = 1;
    private RecyclerView recyclerView,recyclerView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_visit);
        MobileAds.initialize(groupVisit.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView4);
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

//            @Override
//            public void onAdLeftApplication() {
//                // Code to be executed when the user has left the app.
//            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
        final Intent i =getIntent();
        addMember=findViewById(R.id.addMember);
        name=findViewById(R.id.namegv);
        name.setText(i.getStringExtra("name"));
        description=findViewById(R.id.descriptiongv);
        description.setText(i.getStringExtra("description"));
        pic=findViewById(R.id.groupIcongv);
        setProfileImage(i.getStringExtra("nodeId"));
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
        recyclerView= findViewById(R.id.recyclerViewgv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(groupVisit.this));
        recyclerView2= findViewById(R.id.requestsRecyclerView);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(groupVisit.this));
        readusers(i.getStringExtra("nodeId"));
        readrequests(i.getStringExtra("nodeId"),i.getStringExtra("name"),i.getStringExtra("description"),i.getStringExtra("pic"));
        settings=findViewById(R.id.settingsgv);
        settingsIcon=findViewById(R.id.settingsIcongv);
        leave=findViewById(R.id.leaveGroupgv);
        requestText=findViewById(R.id.textView24);
        leaveIcon=findViewById(R.id.leaveGroupIcon);
        final FirebaseUser me =FirebaseAuth.getInstance().getCurrentUser();
        leaveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(groupVisit.this)
                        .setTitle("Exit?")
                        .setMessage("Are you sure you want to Leave the group?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final DatabaseReference reference =FirebaseDatabase.getInstance().getReference("groups").child(i.getStringExtra("nodeid")).child("members").child(me.getUid());
                                reference.setValue(null);
                                startActivity(new Intent(groupVisit.this, MainActivity.class));
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.logo)
                        .show();
            }
        });
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(groupVisit.this)
                        .setTitle("Exit?")
                        .setMessage("Are you sure you want to Leave the group?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final DatabaseReference reference =FirebaseDatabase.getInstance().getReference("groups").child(i.getStringExtra("nodeId")).child("members").child(me.getUid());
                                reference.setValue(null);
                                startActivity(new Intent(groupVisit.this, MainActivity.class));
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.logo)
                        .show();
            }
        });
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(groupVisit.this, "this feature is under development phase", Toast.LENGTH_SHORT).show();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(groupVisit.this, "this feature is under development phase", Toast.LENGTH_SHORT).show();
            }
        });
        changeName=findViewById(R.id.changeNamegv);
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog("Name",i.getStringExtra("nodeId"));
            }
        });
        changeDescription=findViewById(R.id.changeDescriptiongv);
        changeDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog("Description",i.getStringExtra("nodeId"));
            }
        });

    }

    private void readusers(final String nodeId) {
        final FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        final String[] myStatus = new String[1];
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("members");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                membersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final groupMemberModel user = snapshot.getValue(groupMemberModel.class);
                    membersList.add(user);
                    if(user.getUid().equals(me.getUid())){
                        myStatus[0] =user.getStatus();
                        if(myStatus[0].equals("creator")||myStatus[0].equals("admin")) {
                            addMember.setVisibility(View.VISIBLE);
                            addMember.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i =getIntent();
                                    Intent intent = new Intent(groupVisit.this, addMember.class);
                                    intent.putExtra("nodeId", i.getStringExtra("nodeId"));
                                    intent.putExtra("name", i.getStringExtra("name"));
                                    intent.putExtra("status", i.getStringExtra("status"));
                                    intent.putExtra("pic", i.getStringExtra("pic"));
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                    DatabaseReference reference =FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            usersModel model = snapshot.getValue(usersModel.class);
                            userList.add(model);
                            membersAdapter membersadapter = new membersAdapter(groupVisit.this, userList,membersList,nodeId, myStatus[0]);
                            recyclerView.setLayoutManager(new LinearLayoutManager(groupVisit.this));
                            recyclerView.setAdapter(membersadapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(groupVisit.this, "check your network connection", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void ShowDialog(final String line, final String nodeId){
        final AlertDialog.Builder alert= new AlertDialog.Builder(groupVisit.this);
        View view = getLayoutInflater().inflate(R.layout.status_dialog,null);
        Button save =view.findViewById(R.id.saveBtn);
        final EditText enterStatus = view.findViewById(R.id.enterStatus);
        enterStatus.setHint("Enter "+line+" ...");
        TextView heading = view.findViewById(R.id.textView11);
        heading.setText(line);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(nodeId);
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put(line.toLowerCase(),enterStatus.getText().toString());
                reference.updateChildren(hashMap);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void openImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,imageRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==imageRequest && resultCode==RESULT_OK){
            assert data != null;
            imageUri=data.getData();
            uploadImage();
        }
    }

    private String getfilextention(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
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
                                userdata= FirebaseDatabase.getInstance().getReference().child("groups").child(getIntent().getStringExtra("nodeId"));
                                HashMap<String ,Object> usermap=new HashMap<>();
                                usermap.put("groupicon", url);
                                userdata.updateChildren(usermap);
                                Toast.makeText(groupVisit.this, "upload successful", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(groupVisit.this, "Error : " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        Toast.makeText(groupVisit.this, "Error : " + task.getException().toString(), Toast.LENGTH_LONG).show();
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
                if (model.getGroupicon() != null) {
                    Picasso.get().load(Uri.parse(model.getGroupicon())).into(pic);
                }else{
                    pic.setImageResource(R.drawable.ic_launcher_foreground);
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
        Intent i = new Intent(groupVisit.this, groupChat.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("nodeId", getIntent().getStringExtra("nodeId"));
        i.putExtra("name",getIntent().getStringExtra("name"));
        i.putExtra("status", getIntent().getStringExtra("status"));
        i.putExtra("pic", getIntent().getStringExtra("pic"));
        startActivity(i);
    }

    private void readrequests(final String nodeId,final String name, final String description,final String Pic) {
        final FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        final String[] myStatus = new String[1];
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("requests");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList2.clear();
                requestList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final groupRequestsModel user = snapshot.getValue(groupRequestsModel.class);
                    requestList.add(user);
                    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("groups").child(nodeId).child("members");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                final groupMemberModel memberModel = snapshot1.getValue(groupMemberModel.class);
                                if (memberModel.getUid().equals(me.getUid())) {
                                    myStatus[0] = memberModel.getStatus();
                                    if (myStatus[0].equals("creator") || myStatus[0].equals("admin")) {
                                        requestText.setVisibility(View.VISIBLE);
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUserid());
                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                usersModel model = snapshot.getValue(usersModel.class);
                                                userList2.add(model);
                                                groupRequestAdapter groupRequestAdapter = new groupRequestAdapter(groupVisit.this, userList2, requestList, nodeId, myStatus[0],name,description,Pic);
                                                recyclerView2.setLayoutManager(new LinearLayoutManager(groupVisit.this));
                                                recyclerView2.setAdapter(groupRequestAdapter);
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(groupVisit.this, "check your network connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

}