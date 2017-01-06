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
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String vr = Build.VERSION.RELEASE;
		String id;
		String url;
		if(sp.getBoolean("tokenset", false)) {
			id = sp.getString("userID","-1");
			if(id.equals("-1")){return;}
			url = "https://kochi-app-dev-walking.herokuapp.com/token";
		}else{
			id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
			url = "https://kochi-app-dev-walking.herokuapp.com/device";
		}
		String req = "id=" + id + "&tk=" + token + "&os=0&vr=" + vr;
		JSONObject json = HttpPost.exec_post(url,req);
		try {
			sp.edit().putString("userID", json.getString("user_id")).apply();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
