package com.example.walking;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

class MyGPS implements LocationListener {
	LocationManager locationManager;
	private MainActivity mainActivity;
	private Marker marker;
	String provider = null;
	private double latitude = 0;
	private double longitude = 0;

	MyGPS(MainActivity context) {
		mainActivity = context;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	void setting(){
		if(Build.VERSION.SDK_INT >= 23){
			// 拒否していた場合
			if (ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
				int REQUEST_PERMISSION = 1000;
				if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION)){
					ActivityCompat.requestPermissions(mainActivity,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
				} else {
					Toast toast = Toast.makeText(mainActivity, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
					toast.show();
					ActivityCompat.requestPermissions(mainActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
				}
			}
		}
	}

	void startGPS() {
		// ロケーションマネージャーのインスタンスを取得
		locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
		if (ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
			if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
				//GPSが利用可能
				provider = LocationManager.GPS_PROVIDER;
			}else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
				//ネットワークが利用可能
				provider = LocationManager.NETWORK_PROVIDER;
			}else{
				//位置情報の利用不可能
				return;
			}
		}else{
			return;
		}
		locationManager.requestLocationUpdates(provider, 0, 10, this);
	}

	void stopGPS(){
		if (ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		locationManager.removeUpdates(this);
	}

	void setMarker(Location location, String title){
		if(location != null){
			LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
			marker = mainActivity.mMap.addMarker(new MarkerOptions().position(sydney).title(title));
			mainActivity.mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
		}
	}

	private void setMP(Location location){
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		LatLng sydney = new LatLng(latitude, longitude);
		marker.setPosition(sydney);
		mainActivity.mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
	}

	private void LocationSet(){
		String url = "https://kochi-app-dev-walking.herokuapp.com/position";
		String req = "id=" + MainActivity.sp.getString("userID","-1") + "&lat=" + latitude + "&lon=" + longitude;
		HttpPost httpPost = new HttpPost();
		httpPost.execute(url,req);
		MainActivity.mDone = new CountDownLatch(1);
		try {
			MainActivity.mDone.await();
		} catch (InterruptedException e) {}
		JSONObject json = httpPost.jsonObject;
		try {
			int result = json.getInt("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onLocationChanged(Location location) {
		setMP(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		if (provider.equals(LocationManager.GPS_PROVIDER)) {
			stopGPS();
			startGPS();
		}
	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if (status == LocationProvider.AVAILABLE && provider.equals(LocationManager.GPS_PROVIDER)) {
			stopGPS();
			startGPS();
		}
	}
}
