package com.example.walking;


import android.os.Handler;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

public class MyTimerTask extends java.util.TimerTask {

	private Handler mHandler = new Handler();
	private final String url = "https://kochi-app-dev-walking.herokuapp.com/position";

	@Override
	public void run() {
		mHandler.post(new Runnable() {
			public void run() {
				String req;
				MainActivity.groupMarker.clear();
				for(int i=0; i<MainActivity.group.size(); i++){
					req = "id=" + MainActivity.group.get(i).getID();
					HttpPost httpPost = new HttpPost();
					httpPost.execute(url, req);
					MainActivity.mDone = new CountDownLatch(1);
					try {
						MainActivity.mDone.await();
					} catch (InterruptedException e) {
					}
					JSONObject jsonObject = httpPost.jsonObject;
					if(jsonObject != null){
						long lat = 0;
						long lon = 0;
						String ts = "";
						try {
							lat = jsonObject.getLong("lat");
							lon = jsonObject.getLong("lon");
							ts = jsonObject.getString("ts");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						MainActivity.group.get(i).setLat(lat);
						MainActivity.group.get(i).setLon(lon);
						MainActivity.group.get(i).setTs(ts);
						LatLng sydney = new LatLng(lat, lon);
						Marker marker = MainActivity.mMap.addMarker(new MarkerOptions().position(sydney));
						MainActivity.groupMarker.add(marker);
					}
				}
			}
		});
	}
}
