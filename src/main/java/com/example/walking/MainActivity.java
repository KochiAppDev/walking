package com.example.walking;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {

	public static Notice notice;
	public GoogleMap mMap;
	private Setting setting;
	public static NameSet nameSet;
	private MyGPS myGPS;

	private MapFragment mapFragment;

	private SensorManager manager;
	private Sensor sensor;
	private long lastUpdate = 0;
	private static final int SHAKE_THRESHOLD = 800;
	private int last_x = 0;
	private int last_y = 0;
	private int last_z = 0;

	private BluetoothAdapter mBluetoothAdapter;
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)){
				// 見つけたデバイス情報の取得
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				BluetoothClientThread bct = new BluetoothClientThread(MainActivity.this, device, mBluetoothAdapter);
				bct.start();
				startDetect();
			}
		}
	};
	private BluetoothServerThread bst;

	public Account user;
	private ArrayList<Account> group = new ArrayList<Account>();
	public static final Bitmap[] color =new Bitmap[8];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean state = sp.getBoolean("InitState", true);
		notice = new Notice(this);
		Explanation explanation = new Explanation(this);
		nameSet = new NameSet(this);
		myGPS = new MyGPS(this);
		Resources r = getResources();
		color[0] = BitmapFactory.decodeResource(r, R.mipmap.ic_launcher);
		color[1] = BitmapFactory.decodeResource(r, R.mipmap.ic_launcher);
		color[2] = BitmapFactory.decodeResource(r, R.mipmap.ic_launcher);
		color[3] = BitmapFactory.decodeResource(r, R.mipmap.ic_launcher);
		color[4] = BitmapFactory.decodeResource(r, R.mipmap.ic_launcher);
		color[5] = BitmapFactory.decodeResource(r, R.mipmap.ic_launcher);
		color[6] = BitmapFactory.decodeResource(r, R.mipmap.ic_launcher);
		color[7] = BitmapFactory.decodeResource(r, R.mipmap.ic_launcher);
		manager = (SensorManager)getSystemService(SENSOR_SERVICE);
		sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast toast = Toast.makeText(this, "この端末はグループの追加ができません", Toast.LENGTH_SHORT);
			toast.show();
		}
		if (state) {
			explanation.MysetContentView();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean state = sp.getBoolean("InitState", true);
		if (!state) {
			setContentView(R.layout.activity_map);
			// Obtain the SupportMapFragment and get notified when the map is ready to be used.
			mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
			mapFragment.getMapAsync(this);

			if (!mBluetoothAdapter.isEnabled()) {
				//OFFだった場合、ONにすることを促すダイアログを表示する画面に遷移
				Intent btOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(btOn, 0);
			}else{
				startDetect();
				bst = new BluetoothServerThread(this, mBluetoothAdapter);
				bst.start();
			}

			ArrayList<Integer> iconlist = new ArrayList<>();
			ListView list = (ListView) findViewById(R.id.buttonList);
			try {
				setGroup();
				iconlist.add(user.getIcon());
				for (Account account : group) {
					iconlist.add(account.getIcon());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			ImageArrayAdapter adapter = new ImageArrayAdapter(this, R.layout.listchild, iconlist);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					setting.MysetContentView(group.get(position));
					getFragmentManager().beginTransaction().remove(mapFragment).commit();
				}
			});
			list.setAdapter(adapter);
			myGPS.setting();
			myGPS.startGPS();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int ResultCode, Intent date){
		switch (requestCode){
			case 0:
				if (ResultCode == Activity.RESULT_OK) {
					startDetect();
					bst = new BluetoothServerThread(this, mBluetoothAdapter);
					bst.start();
				} else {
					Toast toast = Toast.makeText(this, "オンにしないとグループの追加はできません", Toast.LENGTH_SHORT);
					toast.show();
				}
				break;
			default:
		}
	}

	private void startDetect(){
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);

		mBluetoothAdapter.startDiscovery();
	}

	private void setGroup() throws JSONException {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String userID = sp.getString("userID","-1");
		if(userID.equals("-1")){return;}
		String url;
		String req;
		url = "https://kochi-app-dev-walking.herokuapp.com/group";
		req = "id=" + userID;
		JSONObject json = HttpPost.exec_post(url,req);
		JSONArray jsonArray = json.getJSONArray("group");
		for(int i=0; i<jsonArray.length(); i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			int id = jsonObject.getInt("id");
			String name = jsonObject.getString("name");
			boolean type = false;
			if(jsonObject.getInt("type") == 0){
				type = true;
			}
			int icon = jsonObject.getInt("icon");
			group.add(new Account(id, name, type, icon));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		myGPS.stopGPS();
		getFragmentManager().beginTransaction().remove(mapFragment).commit();
		manager.unregisterListener(this);
		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}
		unregisterReceiver(mReceiver);
		bst.runStop();
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		if (myGPS.provider != null) {
			if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			myGPS.setMarker(myGPS.locationManager.getLastKnownLocation(myGPS.provider), "name");
		}
		mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener () {
			@Override
			public boolean onMarkerClick(Marker marker) {
				int id = Integer.valueOf(marker.getId().split("m")[1]);
				setting.MysetContentView(group.get(id));
				getFragmentManager().beginTransaction().remove(mapFragment).commit();
				return true;
			}
		});
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		long curTime = System.currentTimeMillis();
		// only allow one update every 100ms.
		if ((curTime - lastUpdate) > 100) {
			long diffTime = (curTime - lastUpdate);
			lastUpdate = curTime;
			int x = (int) event.values[0];
			int y = (int) event.values[1];
			int z = (int) event.values[2];
			float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
			if (speed > SHAKE_THRESHOLD) {
				Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				startActivityForResult(intent, 1);
			}
			last_x = x;
			last_y = y;
			last_z = z;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
}
