package com.example.walking;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;


public class BluetoothServerThread extends Thread {
	private final BluetoothServerSocket servSock;
	public static final UUID TECHBOOSTER_BTSAMPLE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	static BluetoothAdapter myServerAdapter;
	private MainActivity context;
	private boolean flag = true;

	public BluetoothServerThread(MainActivity context, BluetoothAdapter btAdapter){
		BluetoothServerSocket tmpServSock = null;
		myServerAdapter = btAdapter;
		this.context = context;

		try{
			tmpServSock = myServerAdapter.listenUsingRfcommWithServiceRecord("walking", TECHBOOSTER_BTSAMPLE_UUID);
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
					byte[] buf = String.valueOf(context.user.getID()).getBytes("UTF-8");
					OutputStream outputStream = receivedSocket.getOutputStream();
					outputStream.write(buf);
					outputStream.close();
					servSock.close();
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
