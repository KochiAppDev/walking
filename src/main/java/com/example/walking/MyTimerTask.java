package com.example.walking;


import android.os.Handler;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
				int index = 1;
				for(Marker marker : MainActivity.groupMarker){
					marker.remove();
				}
				MainActivity.groupMarker.clear();
				for(Account account : MainActivity.group){
					req = "id=" + account.getID();
					HttpPost httpPost = new HttpPost();
					httpPost.execute(url, req);
					MainActivity.mDone = new CountDownLatch(1);
					try {
						MainActivity.mDone.await();
					} catch (InterruptedException e) {
					}
					JSONObject jsonObject = httpPost.jsonObject;
					if(jsonObject != null){
						double lat = 0;
						double lon = 0;
						String ts = "";
						try {
							lat = jsonObject.getDouble("lat");
							lon = jsonObject.getDouble("lon");
							ts = jsonObject.getString("ts");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						account.setLat(lat);
						account.setLon(lon);
						account.setTs(ts);
						LatLng sydney = new LatLng(lat, lon);
						Marker marker = MainActivity.mMap.addMarker(new MarkerOptions()
								.position(sydney)
								.icon(BitmapDescriptorFactory.fromResource(MainActivity.color[account.getIcon()])));
						marker.setTag(index);
						MainActivity.groupMarker.add(marker);
						index++;
					}
				}
			}
		});
	}
}
