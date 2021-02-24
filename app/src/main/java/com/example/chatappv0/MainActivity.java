package com.example.chatappv0;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chatappv0.Adapter.SectionPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.viewpager);

        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        getSupportActionBar().setTitle("OzChat");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.loguotmenu){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,loginActivity.class));
        }
        if(item.getItemId() == R.id.profilemenu){
            startActivity(new Intent(MainActivity.this,profileActivity.class));
        }
        if(item.getItemId() == R.id.allusers){
            startActivity(new Intent(MainActivity.this,allusersActivity.class));
        }
        if(item.getItemId() == R.id.requests){
            startActivity(new Intent(MainActivity.this, allRequest.class));
        }
        if(item.getItemId()==R.id.groupmenu){
            startActivity(new Intent(MainActivity.this, displayUserForGroup.class));
        }
        if(item.getItemId()==R.id.allgroups){
            startActivity(new Intent(MainActivity.this,allGroupActivity.class));
        }
        if(item.getItemId()==R.id.settingsmenu){
            Toast.makeText(this, "This feature is under development phase", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}