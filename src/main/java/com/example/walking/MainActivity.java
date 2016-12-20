package com.example.walking;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

	private boolean state;
	public static Notice notice;
	public GoogleMap mMap;
	private Setting setting;
	public static NameSet nameSet;
	private ArrayList<Account> date = new ArrayList<>();
	private MapFragment mapFragment;
	private MyGPS myGPS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		state = sp.getBoolean("InitState", true);
		notice = new Notice(this);
		Explanation explanation = new Explanation(this);
		nameSet = new NameSet(this);
		myGPS = new MyGPS(this);
		if (state) {
			explanation.MysetContentView();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		state = sp.getBoolean("InitState", true);
		if (!state) {
			setContentView(R.layout.activity_map);
			// Obtain the SupportMapFragment and get notified when the map is ready to be used.
			mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
			mapFragment.getMapAsync(this);

			ArrayList<Bitmap> listBitmap = new ArrayList<>();
			Resources r = getResources();
			Bitmap bmp = BitmapFactory.decodeResource(r, R.mipmap.ic_launcher);
			listBitmap.add(bmp);
			listBitmap.add(bmp);
			ListView list = (ListView) findViewById(R.id.buttonList);
			ImageArrayAdapter adapter = new ImageArrayAdapter(this, R.layout.listchild, listBitmap);
			setting = new Setting(this);
			Account account = new Account(0, 1);
			date.add(account);
			account = new Account(1, 0);
			date.add(account);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					setting.MysetContentView(date.get(position));
					getFragmentManager().beginTransaction().remove(mapFragment).commit();
				}
			});
			list.setAdapter(adapter);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		myGPS.stopGPS();
		getFragmentManager().beginTransaction().remove(mapFragment).commit();
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
	}
}
