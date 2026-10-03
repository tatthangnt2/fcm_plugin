package com.fcm.plugin;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class FCMPlugin
        implements FlutterPlugin, MethodChannel.MethodCallHandler {

    private static final String CHANNEL =
            "flutter_tlt/fcm_plugin";

    private static final String APP_NAME =
            "fcm_plugin";

    private Context context;
    private MethodChannel channel;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        channel = new MethodChannel(
                binding.getBinaryMessenger(),CHANNEL
        );

        channel.setMethodCallHandler(this);

        FCMService.channel = channel;
    }

    @Override
    public void onDetachedFromEngine(
            @NonNull FlutterPlugin.FlutterPluginBinding binding) {

        if (channel != null) {
            channel.setMethodCallHandler(null);
        }

        FCMService.channel = null;

        channel = null;
        context = null;
    }

    @Override
    public void onMethodCall(
            @NonNull MethodCall call,
            @NonNull MethodChannel.Result result) {

        switch (call.method) {

            case "initialize":
                initialize(call, result);
                break;

            case "getToken":
                getToken(result);
                break;

            case "subscribeToTopic":
                subscribeToTopic(call, result);
                break;

            case "unsubscribeFromTopic":
                unsubscribeFromTopic(call, result);
                break;

            case "deleteToken":
                deleteToken(result);
                break;

            default:
                result.notImplemented();
        }
    }

    private void initialize(
            MethodCall call,
            MethodChannel.Result result) {

        try {
            String apiKey = call.argument("apiKey");
            String appId = call.argument("appId");
            String messagingSenderId =
                    call.argument("messagingSenderId");
            String projectId =
                    call.argument("projectId");

            if (apiKey == null ||
                    appId == null ||
                    messagingSenderId == null ||
                    projectId == null) {

                result.error(
                        "INVALID_ARGUMENT",
                        "Firebase configuration is incomplete",
                        null
                );

                return;
            }

            FirebaseApp firebaseApp;

            try {
                firebaseApp = FirebaseApp.getInstance(APP_NAME);
            } catch (IllegalStateException e) {

                FirebaseOptions options =
                        new FirebaseOptions.Builder()
                                .setApiKey(apiKey)
                                .setApplicationId(appId)
                                .setGcmSenderId(
                                        messagingSenderId
                                )
                                .setProjectId(projectId)
                                .build();

                firebaseApp =
                        FirebaseApp.initializeApp(
                                context,
                                options,
                                APP_NAME
                        );
            }

            result.success(null);

        } catch (Exception e) {

            result.error(
                    "INITIALIZE_ERROR",
                    e.getMessage(),
                    null
            );
        }
    }

    private FirebaseMessaging getMessaging() {

        FirebaseApp app =
                FirebaseApp.getInstance(APP_NAME);

        return FirebaseMessaging.getInstance(app);
    }

    private void getToken(
            MethodChannel.Result result) {

        try {

            getMessaging()
                    .getToken()
                    .addOnSuccessListener(token ->
                            result.success(token)
                    )
                    .addOnFailureListener(exception ->
                            result.error(
                                    "TOKEN_ERROR",
                                    exception.getMessage(),
                                    null
                            )
                    );

        } catch (Exception e) {

            result.error(
                    "TOKEN_ERROR",
                    e.getMessage(),
                    null
            );
        }
    }

    private void subscribeToTopic(
            MethodCall call,
            MethodChannel.Result result) {

        String topic = call.argument("topic");

        if (topic == null || topic.isEmpty()) {

            result.error(
                    "INVALID_TOPIC",
                    "Topic is required",
                    null
            );

            return;
        }

        try {

            getMessaging()
                    .subscribeToTopic(topic)
                    .addOnSuccessListener(unused ->
                            result.success(null)
                    )
                    .addOnFailureListener(exception ->
                            result.error(
                                    "SUBSCRIBE_ERROR",
                                    exception.getMessage(),
                                    null
                            )
                    );

        } catch (Exception e) {

            result.error(
                    "SUBSCRIBE_ERROR",
                    e.getMessage(),
                    null
            );
        }
    }

    private void unsubscribeFromTopic(
            MethodCall call,
            MethodChannel.Result result) {

        String topic = call.argument("topic");

        if (topic == null || topic.isEmpty()) {

            result.error(
                    "INVALID_TOPIC",
                    "Topic is required",
                    null
            );

            return;
        }

        try {

            getMessaging()
                    .unsubscribeFromTopic(topic)
                    .addOnSuccessListener(unused ->
                            result.success(null)
                    )
                    .addOnFailureListener(exception ->
                            result.error(
                                    "UNSUBSCRIBE_ERROR",
                                    exception.getMessage(),
                                    null
                            )
                    );

        } catch (Exception e) {

            result.error(
                    "UNSUBSCRIBE_ERROR",
                    e.getMessage(),
                    null
            );
        }
    }

    private void deleteToken(
            MethodChannel.Result result) {

        try {

            getMessaging()
                    .deleteToken()
                    .addOnSuccessListener(unused ->
                            result.success(null)
                    )
                    .addOnFailureListener(exception ->
                            result.error(
                                    "DELETE_TOKEN_ERROR",
                                    exception.getMessage(),
                                    null
                            )
                    );

        } catch (Exception e) {

            result.error(
                    "DELETE_TOKEN_ERROR",
                    e.getMessage(),
                    null
            );
        }
    }
}