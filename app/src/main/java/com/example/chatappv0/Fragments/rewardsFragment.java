package com.example.chatappv0.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatappv0.Models.usersModel;
import com.example.chatappv0.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static java.lang.Float.floatToIntBits;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link rewardsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class rewardsFragment extends Fragment implements OnUserEarnedRewardListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private AdView mAdViewup;
    private AdView mAdViewdown;
    private RewardedInterstitialAd rewardedInterstitialAd;
    private String TAG = "RewardsFragment";
    Button seeAdBtn;
    TextView pointsText;
    DatabaseReference reference;
    String points;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public rewardsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment rewardsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static rewardsFragment newInstance(String param1, String param2) {
        rewardsFragment fragment = new rewardsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reward,container,false);
        mAdViewup = view.findViewById(R.id.adViewaf2);
        mAdViewdown = view.findViewById(R.id.adViewaf);
        seeAdBtn=view.findViewById(R.id.adbutton);
        pointsText=view.findViewById(R.id.pointsText);
        reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                points = snapshot.getValue(usersModel.class).getPoints();
                pointsText.setText(points);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final AdRequest adRequest = new AdRequest.Builder().build();
        final AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdViewup.loadAd(adRequest);
        mAdViewup.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                super.onAdFailedToLoad(adError);
                mAdViewup.loadAd(adRequest);
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
        mAdViewdown.loadAd(adRequest2);
        mAdViewdown.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                super.onAdFailedToLoad(adError);
                mAdViewdown.loadAd(adRequest2);
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
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadAd();
            }
        });
        seeAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAd();
            }
        });
        return view;
    }
    public void loadAd() {
        RewardedInterstitialAd.load(getContext(), "ca-app-pub-1155879823920026/1940447997",
        new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(RewardedInterstitialAd ad) {
                rewardedInterstitialAd = ad;
                Log.e(TAG, "onAdLoaded");
                rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    /** Called when the ad failed to show full screen content. */
                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.i(TAG, "onAdFailedToShowFullScreenContent");
                    }

                    /** Called when ad showed the full screen content. */
                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.i(TAG, "onAdShowedFullScreenContent");
                        loadAd();
                    }

                    /** Called when full screen content is dismissed. */
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.i(TAG, "onAdDismissedFullScreenContent");
                        loadAd();
                    }
                });
            }
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                Log.e(TAG, "onAdFailedToLoad");
            }
        });
    }

    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
        Log.i(TAG, "onUserEarnedReward");
        float pointsNew = parseFloat(points) + 25.00f;
        reference.child("points").setValue(pointsNew+"");
    }

    public void showAd(){
        if(rewardedInterstitialAd!=null) {
            rewardedInterstitialAd.show( getActivity(),rewardsFragment.this);
        }
        else{
            Toast.makeText(getContext(), "Try again", Toast.LENGTH_SHORT).show();
            loadAd();
        }
    }
}