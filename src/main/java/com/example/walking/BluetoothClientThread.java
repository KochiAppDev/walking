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
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class BluetoothClientThread extends Thread {
	private final BluetoothSocket clientSocket;
	private final BluetoothDevice mDevice;
	public static final UUID TECHBOOSTER_BTSAMPLE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private BluetoothAdapter myClientAdapter;
	private MainActivity context;
	private Boolean flag = true;

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

		String[] str = myClientAdapter.getName().split("_",0);
		for(Account account : MainActivity.group){
			String ID = String.valueOf(account.getID());
			if(ID.equals(str[1])) {
				return;
			}
		}

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle("グループに参加");
		alertDialog.setMessage(str[2]+"の仲間に加わりますか？");
		alertDialog.setPositiveButton("加わる", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					clientSocket.connect();
					byte[] buf = String.valueOf(MainActivity.user.getID() + "_" + MainActivity.user.getUsname()).getBytes("UTF-8");
					OutputStream outputStream = clientSocket.getOutputStream();
					outputStream.write(buf);
					outputStream.close();
					clientSocket.close();
				} catch (IOException e) {
					try {
						clientSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
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
	}
}
