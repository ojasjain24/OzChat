package com.affixchat.chatappv0;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.affixchat.chatappv0.Models.groupMeetingModel;
import com.affixchat.chatappv0.Models.usersModel;
import com.facebook.react.modules.core.PermissionListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.JitsiMeetViewListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class meetingActivity extends FragmentActivity implements JitsiMeetActivityInterface {
    private JitsiMeetView view;
    boolean doubleBackToExitPressedOnce = false;
    ArrayList<usersModel> userData = new ArrayList<>();
    final Boolean[] neverInside = {true};
    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JitsiMeetActivityDelegate.onActivityResult(
                this, requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            JitsiMeetActivityDelegate.onBackPressed();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        Intent i = getIntent();
        view = new JitsiMeetView(this);
        JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
        userInfo.setEmail(""+firebaseuser.getEmail());

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(firebaseuser.getUid());
        userData.clear();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final usersModel user = snapshot.getValue(usersModel.class);
                userInfo.setDisplayName(user.getUsername());
                String urlString = user.getImageurl();
                URL myURL = null;
                try {
                    myURL = new URL(urlString);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                userInfo.setAvatar(myURL);

                JitsiMeetConferenceOptions videoOptions = new JitsiMeetConferenceOptions.Builder()
                        .setRoom("https://meet.jit.si/Affix_"+i.getStringExtra("key"))
                        .setWelcomePageEnabled(false)
                        .setAudioOnly(false)
                        .setVideoMuted(false)
                        .setAudioMuted(false)
                        .setUserInfo(userInfo)
                        .build();

                JitsiMeetConferenceOptions audioOptions = new JitsiMeetConferenceOptions.Builder()
                        .setRoom("https://meet.jit.si/Affix_"+i.getStringExtra("key"))
                        .setWelcomePageEnabled(false)
                        .setAudioOnly(true)
                        .setVideoMuted(true)
                        .setAudioMuted(false)
                        .setUserInfo(userInfo)
                        .build();
                if(getIntent().getStringExtra("type").equals("video")) {
                    view.join(videoOptions);
                }else{
                    view.join(audioOptions);
                }
                setContentView(view);
                JitsiMeetViewListener listener = new JitsiMeetViewListener() {
                    @Override
                    public void onConferenceJoined(Map<String, Object> map) {

                    }

                    @Override
                    public void onConferenceTerminated(Map<String, Object> map) {
                        neverInside[0]=true;
                        Intent i =getIntent();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("meetings").child(i.getStringExtra("key"));
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                groupMeetingModel model = snapshot.getValue(groupMeetingModel.class);
                                if(user.getUid().equals(model.getHostUid())&& neverInside[0]){
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("endTime",System.currentTimeMillis()+"");
                                    neverInside[0] =false;
                                    reference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startActivity(new Intent(meetingActivity.this,MainActivity.class));
                                            android.os.Process.killProcess(android.os.Process.myPid());
                                        }
                                    });
                                }else{
                                    startActivity(new Intent(meetingActivity.this,MainActivity.class));
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                        );
                    }

                    @Override
                    public void onConferenceWillJoin(Map<String, Object> map) {

                    }
                };
                view.setListener(listener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view.dispose();
        view = null;
        JitsiMeetActivityDelegate.onHostDestroy(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        JitsiMeetActivityDelegate.onNewIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            final String[] permissions,
            final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JitsiMeetActivityDelegate.onHostResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void requestPermissions(String[] strings, int i, PermissionListener permissionListener) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}