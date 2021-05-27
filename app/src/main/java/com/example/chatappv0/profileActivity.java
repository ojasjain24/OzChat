package com.example.chatappv0;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chatappv0.Models.usersModel;
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
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;


public class profileActivity extends AppCompatActivity {
    private Uri imageUri;
    private static final int imageRequest = 1;
    private CircleImageView profilePic;
    private TextView statusEditor, professionEditor, countryEditor, genderEditor, languagesEditor , name;
    private ImageButton statusBtn, professionBtn, countryBtn, genderBtn, languagesBtn;
    private DatabaseReference userdata;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profilePic = findViewById(R.id.imageView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        statusEditor = findViewById(R.id.statusEditor);
        professionEditor = findViewById(R.id.professionEditor);
        countryEditor = findViewById(R.id.countryEditor);
        genderEditor = findViewById(R.id.genderEditor);
        languagesEditor = findViewById(R.id.languagesEditor);
        statusBtn=findViewById(R.id.statusBtn);
        name=findViewById(R.id.namepa);
        user=FirebaseAuth.getInstance().getCurrentUser();
        setProfileImage();
        statusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog("Status");
            }
        });
        professionBtn=findViewById(R.id.professionBtn);
        professionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog("Profession");
            }
        });
        countryBtn=findViewById(R.id.countryBtn);
        countryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog("Country");
            }
        });
        genderBtn=findViewById(R.id.genderBtn);
        genderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog("Gender");
            }
        });
        languagesBtn=findViewById(R.id.languageBtn);
        languagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog("Language");
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
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
                                userdata= FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                                HashMap<String ,Object> usermap=new HashMap<>();
                                usermap.put("imageurl", url);
                                userdata.updateChildren(usermap);
                                Toast.makeText(profileActivity.this, "upload successful", Toast.LENGTH_LONG).show();
                                }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(profileActivity.this, "Error : " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        Toast.makeText(profileActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

    public void setProfileImage(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersModel model = dataSnapshot.getValue(usersModel.class);
                if (model.getImageurl() != null) {
                    Picasso.get().load(Uri.parse(model.getImageurl())).into(profilePic);
                }else{
                    profilePic.setImageResource(R.drawable.ic_launcher_foreground);
                }
                statusEditor.setText(model.getStatus());
                professionEditor.setText(model.getProfession());
                countryEditor.setText(model.getCountry());
                genderEditor.setText(model.getGender());
                languagesEditor.setText(model.getLanguage());
                name.setText((model.getUsername()).substring(0,1).toUpperCase()+(model.getUsername()).substring(1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void ShowDialog(final String line){
        final AlertDialog.Builder alert= new AlertDialog.Builder(profileActivity.this);
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
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put(line.toLowerCase(),enterStatus.getText().toString());
                reference.updateChildren(hashMap);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(profileActivity.this,MainActivity.class));
    }
}