package com.example.walking;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

class MyGPS implements LocationListener {
	private LocationManager locationManager;
	private MainActivity mainActivity;
	private double latitude = 0;
	private double longitude = 0;

	MyGPS(MainActivity context) {
		mainActivity = context;
		String provider = null;
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
		startGPS(provider);
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	private void startGPS(String provider) {
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
		locationManager.requestLocationUpdates(provider, 0, 0, this);
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

	@Override
	public void onLocationChanged(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		LatLng sydney = new LatLng(latitude, longitude);
		mainActivity.mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
		mainActivity.mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
	}
	@Override
	public void onProviderDisabled(String provider) {
		if (provider == LocationManager.GPS_PROVIDER) {
			stopGPS();
			startGPS(provider);
		}
	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if (status == LocationProvider.AVAILABLE && provider == LocationManager.GPS_PROVIDER) {
			stopGPS();
			startGPS(provider);
		}
	}
}
