package com.affixchat.chatappv0;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.affixchat.chatappv0.Adapter.SectionPagerAdapter;
import com.affixchat.chatappv0.Adapter.requestsAdapter;
import com.affixchat.chatappv0.Models.acceptRequestModel;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private InterstitialAd mInterstitialAd, mInterstitialAds;
    boolean doubleBackToExitPressedOnce = false;
    private int CAMERA_PERMISSION_CODE = 1, MIC_PERMISSION_CODE = 2;
    public static final String SHARED_PREFS = "sharedPrefs";
    ArrayList<acceptRequestModel> list =new ArrayList<>();
    //    public static final String CHANNEL_MEET = "channelMeet";
    //    public static final String CHANNEL_MSG = "channelMsg";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Resources.Theme theme = super.getTheme();
        new ThemeSetter().aSetTheme(this,theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.viewpager);

        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        getSupportActionBar().setTitle("Affix");



        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-1155879823920026/9612039594", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("TAG", "The ad was dismissed.");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
            }
        });
        AdRequest adRequests = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-1155879823920026/3689946305", adRequests, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAds = interstitialAd;
                mInterstitialAds.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("TAG", "The ad was dismissed.");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAds = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAds = null;
            }
        });
//        notificationServices notificationServices = new notificationServices();
//        NotificationManager manager = getSystemService(NotificationManager.class);
//        notificationServices.createNotificationChannels(MainActivity.this,CHANNEL_MEET,manager,"","");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ) {
            requestPermissionsAudio();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            requestPermissionsCamera();
        }
        splashScreenA splashScreenA = new splashScreenA();
        splashScreenA.playAd(MainActivity.this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        GenerateToken(token);

                    }
                });}
    @SuppressLint("SetTextI18n")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.requests);
        View actionView = menuItem.getActionView();
        allRequest allrequest = new allRequest();
        allrequest.readusers(new OnGetObjectListener<ArrayList<acceptRequestModel>>() {
            @Override
            public void onGetObject(ArrayList<acceptRequestModel> object) {
                list=object;
                TextView count = actionView.findViewById(R.id.requestCount);
                count.setText(list.size()+"");
            }

            @Override
            public void onFail(Exception e) {
                Toast.makeText(MainActivity.this, "check your network connection", Toast.LENGTH_SHORT).show();
            }
        });


        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.loguotmenu){
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(MainActivity.this,loginActivity.class));
        }
        if(item.getItemId() == R.id.profilemenu){
            startActivity(new Intent(MainActivity.this,profileActivity.class));
        }
        if(item.getItemId() == R.id.allusers){
            startActivity(new Intent(MainActivity.this,allusersActivity.class));
        }
        if(item.getItemId() == R.id.requests){
            Intent i = new Intent(MainActivity.this, allRequest.class);
//            Bundle args = new Bundle();
//            args.putSerializable("ARRAYLIST", list);
            i.putExtra("BUNDLE",list);
            startActivity(i);
        }
        if(item.getItemId()==R.id.groupmenu){
            startActivity(new Intent(MainActivity.this, displayUserForGroup.class));
        }
        if(item.getItemId()==R.id.allgroups){
            startActivity(new Intent(MainActivity.this,allGroupActivity.class));
        }
        if(item.getItemId()==R.id.aboutus){
            startActivity(new Intent(MainActivity.this,AboutUsActivity.class));
        }
        if(item.getItemId()==R.id.appThemes){
            ThemeChangeDialogBox();
        }
        if(item.getItemId()==R.id.otherappsmenu){
            startActivity(new Intent(MainActivity.this,OtherApps.class));
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MainActivity.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
            finishAffinity();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mInterstitialAds != null) {
            mInterstitialAds.show(MainActivity.this);
        }
    }
    private void requestPermissionsCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)  ) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Camera Permissions are required for call feature.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }
    private void requestPermissionsAudio() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO) ) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Mic Permissions are required for call feature.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, MIC_PERMISSION_CODE);

                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, MIC_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Camera Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == MIC_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Mic Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Mic Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void ThemeChangeDialogBox() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.theme_dialog, null);
        Spinner spinner = view.findViewById(R.id.spinnert);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(alert.getContext(),R.array.Theme,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setGravity(Gravity.CENTER);
        spinner.setAdapter(adapter);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        Button save =view.findViewById(R.id.saveBtn);
        alertDialog.setCanceledOnTouchOutside(true);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Theme", spinner.getSelectedItem()+"").apply();
                Toast.makeText(MainActivity.this, ""+spinner.getSelectedItem(), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void GenerateToken(String token) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", token);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Token").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"");
        databaseReference.setValue(hashMap);
    }
}