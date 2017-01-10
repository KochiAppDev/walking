package com.example.walking;


import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
	@Override
	public void onTokenRefresh() {
		// Get updated InstanceID token.
		String refreshedToken = FirebaseInstanceId.getInstance().getToken();

		// TODO: Implement this method to send any registration to your app's servers.
		sendRegistrationToServer(refreshedToken);
	}
	private void sendRegistrationToServer(String token) {
		// TODO: Implement this method to send token to your app server.
		String vr = Build.VERSION.RELEASE;
		String id;
		String url;
		if(MainActivity.sp.getBoolean("tokenSet", false)) {
			id = MainActivity.sp.getString("userID","-1");
			if(id.equals("-1")){return;}
			url = "https://kochi-app-dev-walking.herokuapp.com/token";
		}else{
			id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
			url = "https://kochi-app-dev-walking.herokuapp.com/device";
		}
		String req = "id=" + id + "&tk=" + token + "&os=0&vr=" + vr;
		HttpPost httpPost = new HttpPost();
		httpPost.execute(url,req);
		MainActivity.mDone = new CountDownLatch(1);
		try {
			MainActivity.mDone.await();
		} catch (InterruptedException e) {}
		JSONObject json = httpPost.jsonObject;
		try {
			String user_id = json.getString("user_id");
			MainActivity.sp.edit().putString("userID", user_id).apply();
			MainActivity.sp.edit().putBoolean("tokenSet", true).apply();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
