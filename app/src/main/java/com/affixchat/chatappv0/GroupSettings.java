package com.affixchat.chatappv0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.react.modules.toast.ToastModule;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class GroupSettings extends AppCompatActivity {
    Button saveBtn, cancelBtn;
    TextView groupTypeDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources.Theme theme = super.getTheme();
        new ThemeSetter().aSetTheme(this,theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Spinner spinner = findViewById(R.id.spinnerChangeType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.Type, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setGravity(Gravity.CENTER);
        spinner.setAdapter(adapter);
        groupTypeDisplay=findViewById(R.id.groupTypeDisplay);
        Intent i = getIntent();
        groupTypeDisplay.setText(i.getStringExtra("Type"));
        saveBtn = findViewById(R.id.button3);
        cancelBtn = findViewById(R.id.button4);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(""+i.getStringExtra("nodeId"));
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("type",spinner.getSelectedItem().toString());
                reference.updateChildren(hashMap);
                Toast.makeText(GroupSettings.this, "Successfully changed group Type to " + spinner.getSelectedItem(), Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }
}