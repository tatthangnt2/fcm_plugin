import "package:flutter/services.dart";

class FCMPlugin {
  static const MethodChannel _channel = MethodChannel("flutter_tlt/fcm_plugin");

  static Future<void> initialize({
    required String apiKey,
    required String appId,
    required String messagingSenderId,
    required String projectId,
    ValueChanged<Map<String, dynamic>>? onMessage,
  }) async {
    _channel.setMethodCallHandler((call) async {
      if (call.method == "onMessage") {
        final data = Map<String, dynamic>.from(
          call.arguments as Map,
        );

        onMessage?.call(data);
      }
    });

    await _channel.invokeMethod<void>(
      "initialize",
      {
        "apiKey": apiKey,
        "appId": appId,
        "messagingSenderId": messagingSenderId,
        "projectId": projectId,
      },
    );
  }

  static Future<String?> getToken() {
    return _channel.invokeMethod<String>("getToken");
  }

  static Future<void> subscribeToTopic(String topic) async {
    await _channel.invokeMethod<void>("subscribeToTopic", {
      "topic": topic,
    });
  }

  static Future<void> unsubscribeFromTopic(String topic) async {
    await _channel.invokeMethod<void>("unsubscribeFromTopic", {
      "topic": topic,
    });
  }

  static Future<void> deleteToken() async {
    await _channel.invokeMethod<void>("deleteToken");
  }
}
