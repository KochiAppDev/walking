package com.example.walking;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		// TODO(developer): Handle FCM messages here.
		// If the application is in the foreground handle both data and notification messages here.
		// Also if you intend on generating your own notifications as a result of a received FCM
		// message, here is where that should be initiated. See sendNotification method below.
		String order = remoteMessage.getData().get("order");
		switch (order){
			case "config":
			case "route":
			case "setting":
				MainActivity.setUser();
				break;
			case "group":
				try {
					MainActivity.setGroup(Integer.valueOf(remoteMessage.getData().get("group")));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case "message":
				break;
			default:
		}
	}
}
