package com.affixchat.chatappv0.Notification;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.affixchat.chatappv0.OnGetObjectListener;
import com.affixchat.chatappv0.R;
import com.affixchat.chatappv0.chatPage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class sendNotificationFunction {
    APISERVICESHIT apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APISERVICESHIT.class);

    public void sendNotification(String receiver, String me, Context context, String Message, String Title, OnGetObjectListener<Boolean> callback){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Token");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(me, R.drawable.logo_noti, Message, Title,
                            receiver);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if(callback != null) callback.onGetObject(true);
                                    }else{
                                        if(callback != null) callback.onGetObject(false);
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    if(callback != null) callback.onGetObject(false);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if(callback != null) callback.onGetObject(false);
            }
        });
    }
}
