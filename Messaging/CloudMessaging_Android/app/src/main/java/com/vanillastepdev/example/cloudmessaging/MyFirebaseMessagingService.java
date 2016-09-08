package com.vanillastepdev.example.cloudmessaging;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "CloudMessage-Example";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // 데이터 파트
        Map<String, String> data = remoteMessage.getData();
        if ( data != null ) {
            Log.d(TAG, "== Data Payload ==");
            for ( String key : data.keySet() ) {
                Log.d(TAG, key + " : " + data.get(key));
            }
        }

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if ( notification != null ) {
            Log.d(TAG, "== Notification Payload ==");
            String title = notification.getTitle();
            String body = notification.getBody();
            Log.d(TAG, "title : " + title);
            Log.d(TAG, "body : " + body);
        }
    }
}