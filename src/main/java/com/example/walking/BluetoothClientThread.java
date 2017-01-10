package com.example.walking;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothClientThread extends Thread {
	private final BluetoothSocket clientSocket;
	private final BluetoothDevice mDevice;
	public static final UUID TECHBOOSTER_BTSAMPLE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	static BluetoothAdapter myClientAdapter;
	private MainActivity context;

	public BluetoothClientThread(MainActivity context, BluetoothDevice device, BluetoothAdapter btAdapter){
		this.context = context;
		BluetoothSocket tmpSock = null;
		mDevice = device;
		myClientAdapter = btAdapter;

		try{
			tmpSock = device.createRfcommSocketToServiceRecord(TECHBOOSTER_BTSAMPLE_UUID);
		}catch(IOException e){
			e.printStackTrace();
		}
		clientSocket = tmpSock;
	}

	public void run(){
		if(myClientAdapter.isDiscovering()){
			myClientAdapter.cancelDiscovery();
		}

		try{
			clientSocket.connect();
			InputStream inputStream = clientSocket.getInputStream();
			final int userID = inputStream.read();
			inputStream.close();
			clientSocket.close();
			String url = "https://kochi-app-dev-walking.herokuapp.com/info";
			String req = "id=" + userID;
			JSONObject json = HttpPost.exec_post(url,req);
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
			alertDialog.setTitle("グループの追加");
			alertDialog.setMessage(json.getString("name")+"をグループに追加しますか？");
			alertDialog.setPositiveButton("追加する", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String url = "https://kochi-app-dev-walking.herokuapp.com/add";
					String req = "u1=" + context.user.getID() + "&u1=" + userID;
					JSONObject json = HttpPost.exec_post(url,req);
					try {
						context.user.setGroupID(json.getInt("group_id"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			alertDialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			alertDialog.create().show();
		}catch(IOException e){
			try {
				clientSocket.close();
			} catch (IOException closeException) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
