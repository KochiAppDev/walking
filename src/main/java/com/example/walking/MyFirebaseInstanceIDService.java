package com.example.walking;


import android.provider.Settings;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

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
		String udid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
	}
}
