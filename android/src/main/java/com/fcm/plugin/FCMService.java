package com.fcm.plugin;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMPlugin";

    public static MethodChannel channel;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Message received: " + remoteMessage.getData());

        if (channel == null) {
            return;
        }

        Map<String, Object> message = new HashMap<>();

        message.put("messageId", remoteMessage.getMessageId());

        if (remoteMessage.getFrom() != null) {
            message.put("from", remoteMessage.getFrom());
        }

        if (remoteMessage.getData() != null) {
            message.put("data", remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification notification =
                    remoteMessage.getNotification();

            Map<String, Object> notificationData = new HashMap<>();

            notificationData.put("title", notification.getTitle());
            notificationData.put("body", notification.getBody());

            message.put("notification", notificationData);
        }

        channel.invokeMethod("onMessage", message);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.d(TAG, "Release FCM token: " + token);
    }
}