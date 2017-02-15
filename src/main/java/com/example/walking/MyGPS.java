package com.example.walking;


import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

//GPS用のクラス
class MyGPS implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest locationRequest;
	private FusedLocationProviderApi fusedLocationProviderApi;

	private MainActivity mainActivity;
	public Marker marker;
	private double latitude = 0;
	private double longitude = 0;

	MyGPS(MainActivity context) {
		mainActivity = context;
	}

	//パーミッションの設定
	void setting() {
		if (Build.VERSION.SDK_INT >= 23) {
			// 拒否していた場合
			if (ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				int REQUEST_PERMISSION = 1000;
				if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
					ActivityCompat.requestPermissions(mainActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
				} else {
					Toast toast = Toast.makeText(mainActivity, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
					toast.show();
					ActivityCompat.requestPermissions(mainActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
				}
			}
		}
	}

	//LocationManagerの設定
	void startGPS() {
		// LocationRequest を生成して精度、インターバルを設定
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(1000);
		locationRequest.setFastestInterval(10);

		fusedLocationProviderApi = LocationServices.FusedLocationApi;

		mGoogleApiClient = new GoogleApiClient.Builder(mainActivity)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

		mGoogleApiClient.connect();

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
		Location lastLocation = fusedLocationProviderApi.getLastLocation(mGoogleApiClient);
		if(lastLocation != null){
			LatLng sydney = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
			marker = MainActivity.mMap.addMarker(new MarkerOptions()
													.position(sydney)
													.icon(BitmapDescriptorFactory.fromResource(MainActivity.color[MainActivity.user.getIcon()])));
		}
	}

	//GPSの停止
	void stopGPS() {
		mGoogleApiClient.disconnect();
	}

	public void rootSet() {
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
		Location lastLocation = fusedLocationProviderApi.getLastLocation(mGoogleApiClient);
		double[] location = {lastLocation.getLatitude(),lastLocation.getLongitude()};
		mainActivity.root.add(location);
	}

	//位置情報の設定
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
		int result=0;
		try {
			result = json.getInt("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if(mainActivity.rFlag){
			double[] location = {latitude,longitude};
			mainActivity.root.add(location);
		}
		if(result == -1){
			LocationSet();
		}
	}

	//位置情報の取得
	@Override
	public void onLocationChanged(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		LatLng sydney = new LatLng(latitude, longitude);
		if(marker == null){
			marker = MainActivity.mMap.addMarker(new MarkerOptions()
					.position(sydney)
					.icon(BitmapDescriptorFactory.fromResource(MainActivity.color[MainActivity.user.getIcon()])));
		}else{
			marker.setPosition(sydney);
		}
		MainActivity.mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
		LocationSet();
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
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
		fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		fusedLocationProviderApi.removeLocationUpdates(mGoogleApiClient, this);
	}
}
