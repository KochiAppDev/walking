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
		if(order == null){
			return;
		}
		switch (order){
			case "config":
			case "route":
			case "setting":
				MainActivity.mainActivity.setUser();
				break;
			case "group":
				int groupID = Integer.valueOf(remoteMessage.getData().get("group"));
				MainActivity.user.setGroupID(groupID);
				MainActivity.mainActivity.settingList();
				break;
			case "message":
				break;
			default:
		}
	}
}
