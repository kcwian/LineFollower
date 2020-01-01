package com.example.LineFollowerApp;

import java.io.IOException;
import java.util.UUID;

import android.R.string;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ConnectThread extends Thread {
	
	public static boolean connected = false;
	private final BluetoothSocket mmSocket;
	private final BluetoothDevice mmDevice;	
	ConnectedThread bt2;
	Handler h2;

	public ConnectThread(BluetoothDevice device, Handler btHandler) {
		// Use a temporary object that is later assigned to mmSocket, because mmSocket is final
		BluetoothSocket tmp = null;
		mmDevice = device;
		h2 = btHandler;
		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			// MY_UUID is the app's UUID string, also used by the server code
			UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
			tmp = device.createRfcommSocketToServiceRecord(uuid);
		} catch (IOException e) { }
		mmSocket = tmp;
	}

	public void run() {
		// Cancel discovery because it will slow down the connection

		try {
			// Connect the device through the socket. This will block until it succeeds or throws an exception
			Log.d("INFO","Pr�ba polaczenia....");	
			mmSocket.connect();			
			h2.sendEmptyMessage(1);
			connected = true;
			Log.d("INFO","Polaczono z serwerem!");
			bt2 = new ConnectedThread(mmSocket);

		} catch (IOException connectException) {
			// Unable to connect; close the socket and get out
			connected = false;
			Log.d("INFO","Nie udane");

		}
		if(connected == true) 
			return;
		else
			run();

		// Do work to manage the connection (in a separate thread)
		// manageConnectedSocket(mmSocket);
	}

	public void send(byte[] text) {
		if(connected == true)
		bt2.write(text);
	}
	
	public void send(byte databyte) {
		if(connected == true)
			bt2.write(databyte);	
	}

	/** Will cancel an in-progress connection, and close the socket */
	public void cancel() {
		try {		
			bt2.cancel();
			mmSocket.close();		
			connected = false;
			h2.sendEmptyMessage(0);
			Log.d("INFO","Cancel");
		} catch (IOException e) { }
	}




}
