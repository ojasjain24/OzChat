package com.affixchat.chatappv0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class acceptRequest extends AppCompatActivity {
    private Button acceptBtn, declineBtn;
    private TextView status,profession,country,languages,gender;
    private ImageView dp;
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         Resources.Theme theme = super.getTheme();
         new ThemeSetter().aSetTheme(this,theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_request);
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        acceptBtn=findViewById(R.id.accept);
        dp=findViewById(R.id.DPshow);
        final Intent intent = getIntent();
        final String userId=intent.getStringExtra("userId");
        status=findViewById(R.id.STATUSshow);
        status.setText(intent.getStringExtra("status"));
         profession=findViewById(R.id.professionShow);
         profession.setText(intent.getStringExtra("profession"));
         country=findViewById(R.id.countryShow);
         country.setText(intent.getStringExtra("country"));
         languages=findViewById(R.id.languagesShow);
         languages.setText(intent.getStringExtra("language"));
         gender=findViewById(R.id.genderShow);
         gender.setText(intent.getStringExtra("gender"));

         if(intent.getStringExtra("pic") != null) {
             Picasso.get().load(Uri.parse(intent.getStringExtra("pic"))).into(dp);
         }else {
             dp.setImageResource(R.drawable.ic_launcher_foreground);
         }
         final FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        declineBtn=findViewById(R.id.reject);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd= new ProgressDialog(acceptRequest.this);
                pd.setMessage("accepting");
                pd.show();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(intent.getStringExtra("userId")).child("friends").child(me.getUid());
                HashMap<String ,String> usermap=new HashMap<>();
                usermap.put("uid",me.getUid());
                databaseReference.setValue(usermap);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(me.getUid()).child("friends").child(intent.getStringExtra("userId"));
                HashMap<String ,String> map=new HashMap<>();
                map.put("uid",intent.getStringExtra("userId"));
                reference.setValue(map);

                final DatabaseReference requests = FirebaseDatabase.getInstance().getReference("requests");
                requests.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        requests.child(intent.getStringExtra("nodeId")).setValue(null);
                        Intent i = new Intent(acceptRequest.this,allRequest.class);
                        startActivity(i);
                        pd.dismiss();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(acceptRequest.this, ""+error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("requests");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reference.child(intent.getStringExtra("nodeId")).setValue(null);
                        Intent i = new Intent(acceptRequest.this,allRequest.class);
                        startActivity(i);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}