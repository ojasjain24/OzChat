package com.example.chatappv0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class profileVisit extends AppCompatActivity {
    private TextView name,status,profession,country,language,gender;
    private ImageView dp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_visit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i =getIntent();
        name=findViewById(R.id.textView14pv);
        name.setText(i.getStringExtra("name"));
        status=findViewById(R.id.STATUSpv);
        status.setText(i.getStringExtra("status"));
        profession=findViewById(R.id.textView5pv);
        profession.setText(i.getStringExtra("profession"));
        country=findViewById(R.id.textView6pv);
        country.setText(i.getStringExtra("country"));
        language=findViewById(R.id.textView7pv);
        language.setText(i.getStringExtra("language"));
        gender=findViewById(R.id.textView9pv);
        gender.setText(i.getStringExtra("gender"));
        dp=findViewById(R.id.DPpv);
        if(i.getStringExtra("pic") != null) {
            Picasso.get().load(Uri.parse(i.getStringExtra("pic"))).into(dp);
        }else{
            dp.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}