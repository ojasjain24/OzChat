package com.affixchat.chatappv0.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APISERVICESHIT {


    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA4T3j3is:APA91bHRVs-WIxHTZvvVdJUksPWQth_WOZEJIwnEhpUXvveFz5__c643iSTy_sPajvlgLov2P4EYaFNqu4UNfDeF5Z4sE7TzHsbhGyQRDaGEdJYXMelcRFrR76ihMq73y9B7jsFPCfoa"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);


}
