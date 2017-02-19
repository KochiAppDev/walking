package com.example.walking;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener, View.OnClickListener {

	public static SharedPreferences sp;
	public static CountDownLatch mDone;

	public static Notice notice;
	public static GoogleMap mMap;
	private Setting setting;
	public static NameSet nameSet;
	public MyGPS myGPS;

	private MapFragment mapFragment;

	private SensorManager manager;
	private Sensor sensor;
	private long lastUpdate = 0;
	private static final int SHAKE_THRESHOLD = 800;
	private int last_x = 0;
	private int last_y = 0;
	private int last_z = 0;

	public boolean rFlag = false;
	public ArrayList<double[]> root = new ArrayList<>();

	private BluetoothAdapter mBluetoothAdapter;
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// 見つけたデバイス情報の取得
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(device.getName().startsWith("walking")){
					BluetoothClientThread bct = new BluetoothClientThread(MainActivity.this, device, mBluetoothAdapter);
					bct.start();
				}
			}
			if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				startDetect();
			}
		}
	};
	private BluetoothServerThread bst;

	private MyTimerTask timerTask;

	public static Account user;
	public Marker[] markers;
	public static ArrayList<Account> group = new ArrayList<Account>();
	public static ArrayList<Marker> groupMarker = new ArrayList<>();
	public static final int[] color = new int[8];
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	private String BluetoothAdapterName ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		boolean state = sp.getBoolean("InitState", true);

		//各種クラスの生成
		setting = new Setting(this);
		notice = new Notice(this);
		Explanation explanation = new Explanation(this);
		nameSet = new NameSet(this);
		myGPS = new MyGPS(this);

		//画像を取得
		color[0] = R.mipmap.blue;
		color[1] = R.mipmap.green;
		color[2] = R.mipmap.ltblue;
		color[3] = R.mipmap.orange;
		color[4] = R.mipmap.purple;
		color[5] = R.mipmap.pink;
		color[6] = R.mipmap.red;
		color[7] = R.mipmap.yellow;

		//Bluetoothの登録
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast toast = Toast.makeText(this, "この端末はグループの追加ができません", Toast.LENGTH_SHORT);
			toast.show();
		}else{
			//各種マネージャーやサービスの登録
			manager = (SensorManager) getSystemService(SENSOR_SERVICE);
			sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
		}

		//初回起動かの判定
		if (state) {
			explanation.MysetContentView();
		}
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	@Override
	protected void onResume() {
		super.onResume();
		boolean state = sp.getBoolean("InitState", true);
		if (!state) {
			setContentView(R.layout.activity_map);
			// Obtain the SupportMapFragment and get notified when the map is ready to be used.
			mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
			mapFragment.getMapAsync(this);

			setUser();

			if (mBluetoothAdapter != null) {
				if (!mBluetoothAdapter.isEnabled()) {
					//OFFだった場合、ONにすることを促すダイアログを表示する画面に遷移
					Intent btOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(btOn, 0);
				} else {
					startDetect();
					bst = new BluetoothServerThread(this, mBluetoothAdapter);
					bst.start();
				}
				BluetoothAdapterName = mBluetoothAdapter.getName();
				mBluetoothAdapter.setName("walking_" + user.getID() + "_" + user.getUsname());
			}

			ArrayList<Integer> iconlist = new ArrayList<>();
			ListView list = (ListView) findViewById(R.id.buttonList);
			try {
				setGroup(user.getGroupID());
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
					LatLng sydney;
					Marker marker = null;
					if (position == 0) {
						marker = myGPS.marker;
					} else {
						try{
							marker = groupMarker.get(position - 1);
						}catch (IndexOutOfBoundsException e){}
					}
					if(marker != null){
						sydney = marker.getPosition();
						mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
					}
				}
			});

			list.setAdapter(adapter);

			Timer timer = new Timer();
			timerTask = new MyTimerTask();
			timer.schedule(timerTask, 0, 60000);

			myGPS.setting();
			myGPS.startGPS();

			if(!user.isType()){
				findViewById(R.id.root).setOnClickListener(this);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int ResultCode, Intent date) {
		switch (requestCode) {
			case 0:
				if (ResultCode != Activity.RESULT_OK) {
					Toast toast = Toast.makeText(this, "オンにしないとグループの追加はできません", Toast.LENGTH_SHORT);
					toast.show();
				}
				break;
			default:
		}
	}

	private void startDetect() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter);
		//接続可能なデバイスを検出
		if(mBluetoothAdapter.isDiscovering()){
			//検索中の場合は検出をキャンセルする
			mBluetoothAdapter.cancelDiscovery();
		}
		//デバイスを検索する
		mBluetoothAdapter.startDiscovery();
	}

	//ユーザー情報の登録
	public static void setUser() {
		try {
			String url = "https://kochi-app-dev-walking.herokuapp.com/info";
			String req = "id=" + sp.getString("userID", "-1");
			HttpPost httpPost = new HttpPost();

			httpPost.execute(url, req);
			mDone = new CountDownLatch(1);
			try {
				mDone.await();
			} catch (InterruptedException e) {
			}
			JSONObject json = httpPost.jsonObject;
			int id = json.getInt("id");
			if (id == -1) {
				return;
			}
			String name = json.getString("name");
			boolean type = false;
			if (json.getInt("type") == 0) {
				type = true;
			}
			int icon = json.getInt("icon");
			int groupID = json.getInt("group");
			String str = json.getString("rt");

			user = new Account(id, name, type, icon);
			user.setGroupID(groupID);

			JSONArray jsonArray = new JSONArray(str);
			int count = jsonArray.length();
			double[][] rt = new double[count][2];
			for(int i=0; i<count; i++){
				String string = jsonArray.getString(i);
				JSONArray Array = new JSONArray(string);
				rt[i][0] = Array.getDouble(0);
				rt[i][1] = Array.getDouble(1);
			}
			user.setRt(rt);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	//グループの取得
	public static void setGroup(int groupID) throws JSONException {
		String url;
		String req;
		url = "https://kochi-app-dev-walking.herokuapp.com/group";
		req = "gp=" + groupID;
		HttpPost httpPost = new HttpPost();
		httpPost.execute(url, req);
		mDone = new CountDownLatch(1);
		try {
			mDone.await();
		} catch (InterruptedException e) {
		}
		JSONArray jsonArray = httpPost.jsonArray;
		group.clear();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			int id = jsonObject.getInt("id");
			if (id != user.getID()) {
				String name = jsonObject.getString("name");
				boolean type = false;
				if (jsonObject.getInt("type") == 0) {
					type = true;
				}
				int icon = jsonObject.getInt("icon");
				group.add(new Account(id, name, type, icon));
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		myGPS.stopGPS();
		if(mapFragment != null){
			getFragmentManager().beginTransaction().remove(mapFragment).commit();
		}
		if (mBluetoothAdapter != null) {
			if (mBluetoothAdapter.isDiscovering()) {
				mBluetoothAdapter.cancelDiscovery();
				unregisterReceiver(mReceiver);
			}
			if(mBluetoothAdapter.isEnabled()){
				bst.runStop();
			}
			manager.unregisterListener(this);
			mBluetoothAdapter.setName(BluetoothAdapterName);
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;

		mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				if(user.isType()){
					int id = Integer.valueOf(marker.getId().split("m")[1]);
					if(id == 0){
						setting.MysetContentView(user);
					}else{
						setting.MysetContentView(group.get(id + 1));
					}
					myGPS.stopGPS();
					myGPS.marker = null;
					getFragmentManager().beginTransaction().remove(mapFragment).commit();
				}

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

	@Override
	public void onClick(View v) {
		double[][] rt = user.getRt();
		if(rt != null){
			markers = new Marker[rt.length];
			for(int i=0; i<rt.length; i++){
				LatLng sydney = new LatLng(rt[i][0],rt[i][1]);
				markers[i] = mMap.addMarker(new MarkerOptions().position(sydney));
			}
		}
	}

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	public Action getIndexApiAction() {
		Thing object = new Thing.Builder()
				.setName("Main Page") // TODO: Define a title for the content shown.
				// TODO: Make sure this auto-generated URL is correct.
				.setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
				.build();
		return new Action.Builder(Action.TYPE_VIEW)
				.setObject(object)
				.setActionStatus(Action.STATUS_TYPE_COMPLETED)
				.build();
	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		AppIndex.AppIndexApi.start(client, getIndexApiAction());
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		AppIndex.AppIndexApi.end(client, getIndexApiAction());
		client.disconnect();
	}
}
