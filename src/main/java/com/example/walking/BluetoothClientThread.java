package com.example.walking;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothClientThread extends Thread {
	private final BluetoothSocket clientSocket;
	private final BluetoothDevice mDevice;
	public static final UUID TECHBOOSTER_BTSAMPLE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	static BluetoothAdapter myClientAdapter;

	public BluetoothClientThread(BluetoothDevice device, BluetoothAdapter btAdapter){
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
			int userID = inputStream.read();
			inputStream.close();
			clientSocket.close();
		}catch(IOException e){
			try {
				clientSocket.close();
			} catch (IOException closeException) {
				e.printStackTrace();
			}
		}
	}
}
