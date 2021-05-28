package com.example.chatappv0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splashScreenA extends AppCompatActivity {

    ImageView logo;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logo=(ImageView) findViewById(R.id.logo_ecv);
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(splashScreenA.this,"ca-app-pub-1155879823920026/1490522289", adRequest, new InterstitialAdLoadCallback() {
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
        if (logo!=null){
            TranslateAnimation animate = new TranslateAnimation(0,0, -700, 0);
            animate.setDuration(1000);
            logo.startAnimation(animate);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Thread background = new Thread() {
                public void run() {
                    try {
                        // Thread will sleep for 3 seconds
                        sleep(2 * 1000);
                        // After 3 seconds redirect to another intent
                        if (user != null) {
                            if(auth.getCurrentUser().isEmailVerified()){
                                startActivity(new Intent(splashScreenA.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

                            }else{
                                Toast.makeText(splashScreenA.this, "Please Verify your mail", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(splashScreenA.this, loginActivity.class));
                            }
                        } else {
                            startActivity(new Intent(splashScreenA.this, loginActivity.class));
                        }
                        //Remove activity
                        finish();
                    } catch (Exception e) {
                    }
                }
            };
            // start thread
            background.start();
        }else{
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Thread background = new Thread() {
                public void run() {
                    try {
                        // Thread will sleep for 5 seconds
                        sleep(2 * 1000);
                        // After 3 seconds redirect to another intent
                        if (user != null) {
                            if (auth.getCurrentUser().isEmailVerified()) {
                                startActivity(new Intent(splashScreenA.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            }else{
                                Toast.makeText(splashScreenA.this, "Please Verify your mail", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(splashScreenA.this, loginActivity.class));
                            }
                        } else {
                            startActivity(new Intent(splashScreenA.this, loginActivity.class));
                        }
                        //Remove activity
                        finish();
                    } catch (Exception e) {
                    }
                }
            };
        }
    }
    public void playAd(Activity activity){
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
            Log.d("ojasadstart","yes");
        } else {
            Log.d("ojasnotreadysplash", "The interstitial ad wasn't ready yet.");
        }
    }
}