package com.example.walking;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;


public class BluetoothServerThread extends Thread {
	private final BluetoothServerSocket servSock;
	public static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	static BluetoothAdapter myServerAdapter;
	private MainActivity context;
	private boolean flag = true;

	public BluetoothServerThread(MainActivity context, BluetoothAdapter btAdapter){
		BluetoothServerSocket tmpServSock = null;
		myServerAdapter = btAdapter;
		this.context = context;

		try{
			tmpServSock = myServerAdapter.listenUsingRfcommWithServiceRecord("walking", mUUID);
		}catch(IOException e){
			e.printStackTrace();
		}
		servSock = tmpServSock;
	}

	public void run(){
		BluetoothSocket receivedSocket = null;
		while(flag) {
			try {
				receivedSocket = servSock.accept();
			} catch (IOException e) {
				break;
			}
			if(receivedSocket != null){
				try {
					InputStream inputStream = receivedSocket.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
					br.close();
					inputStream.close();
					servSock.close();

					String userIDName = sb.toString();
					final String[] user = userIDName.split("_",0);
					new AlertDialog.Builder(context)
						.setTitle("申請がきています")
						.setMessage(user[1]+"を仲間に加わえますか？")
						.setPositiveButton("加える", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String url = "https://kochi-app-dev-walking.herokuapp.com/add";
								String req = "u1=" + MainActivity.user.getID() + "&u1=" + user[0];
								HttpPost httpPost = new HttpPost();
								httpPost.execute(url,req);
								MainActivity.mDone = new CountDownLatch(1);
								try {
									MainActivity.mDone.await();
								} catch (InterruptedException e) {}
								JSONObject json = httpPost.jsonObject;
								try {
									MainActivity.user.setGroupID(json.getInt("group_id"));
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						})
						.setNegativeButton("キャンセル", null)
						.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void runStop(){
		flag = false;
	}
}
