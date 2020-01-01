package com.example.LineFollowerApp;


import com.example.LineFollowerApp.R;
import com.example.LineFollowerApp.R.color;
import com.example.LineFollowerApp.R.string;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	ConnectThread mBluetooth;
	Button buttonConnect;
	Button buttonParameters;
	Handler btHandler;
	Button bUp,bLeft,bRight;
	ToggleButton tbManual;

	public void parametersActivity(){
		Intent i  = new Intent(this,ParametersActivity.class);
		startActivityForResult(i, 2);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1);
		}

		buttonConnect=(Button) findViewById(R.id.buttonConnect);
		buttonConnect.setOnClickListener(new OnClickListener(){
			public void onClick(View v){

				if(ConnectThread.connected == false){
					BluetoothAdapter mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
					BluetoothDevice serwer = mBluetoothAdapter.getRemoteDevice("98:D3:31:30:A7:60"); 
					mBluetoothAdapter.cancelDiscovery();	
					mBluetooth = new ConnectThread(serwer, btHandler);
					mBluetooth.start();

				}
				else if(ConnectThread.connected == true){
					mBluetooth.cancel();
				}

				buttonConnect.setText("Please Wait...");
				buttonConnect.setEnabled(false);


			}
		});

		Button buttonParameters = (Button) findViewById(R.id.buttonParameters);
		buttonParameters.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				parametersActivity();
			}

		});

		tbManual = (ToggleButton) findViewById(R.id.toggleButtonManual);
		tbManual.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(tbManual.isChecked()){
					byte[] data2send= new byte[2];
					data2send[0] = 121;
					data2send[1] = 122; 
					if(mBluetooth != null)
						mBluetooth.send(data2send);	
				}
				else{
					byte[] data2send= new byte[2];
					data2send[0] = 121;
					data2send[1] = 123; 
					if(mBluetooth != null)
						mBluetooth.send(data2send);	
				}
			}

		});

		bUp = (Button) findViewById(R.id.buttonUp);
		bUp.setOnTouchListener(new View.OnTouchListener() {

			private Handler mHandler;

			@Override public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (mHandler != null) return true;
					mHandler = new Handler();
					mHandler.postDelayed(mAction, 1);
					break;
				case MotionEvent.ACTION_UP:
					if (mHandler == null) return true;
					mHandler.removeCallbacks(mAction);
					mHandler = null;
					break;
				}
				return false;
			}

			Runnable mAction = new Runnable() {
				@Override public void run() {
					if(mBluetooth == null) return;
					byte[] data2send= new byte[2];
					data2send[0] = 121;
					data2send[1] = 8;
					mBluetooth.send(data2send);
					mHandler.postDelayed(this, 100);
				}
			};

		});

		bLeft = (Button) findViewById(R.id.buttonLeft);
		bLeft.setOnTouchListener(new View.OnTouchListener() {

			private Handler mHandler;

			@Override public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (mHandler != null) return true;
					mHandler = new Handler();
					mHandler.postDelayed(mAction, 1);
					break;
				case MotionEvent.ACTION_UP:
					if (mHandler == null) return true;
					mHandler.removeCallbacks(mAction);
					mHandler = null;
					break;
				}
				return false;
			}

			Runnable mAction = new Runnable() {
				@Override public void run() {
					if(mBluetooth == null) return;
					byte[] data2send= new byte[2];
					data2send[0] = 121;
					data2send[1] = 4;
					mBluetooth.send(data2send);
					mHandler.postDelayed(this, 100);
				}
			};

		});

		bRight = (Button) findViewById(R.id.buttonRight);
		bRight.setOnTouchListener(new View.OnTouchListener() {

			private Handler mHandler;

			@Override public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (mHandler != null) return true;
					mHandler = new Handler();
					mHandler.postDelayed(mAction, 1);
					break;
				case MotionEvent.ACTION_UP:
					if (mHandler == null) return true;
					mHandler.removeCallbacks(mAction);
					mHandler = null;
					break;
				}
				return false;
			}

			Runnable mAction = new Runnable() {
				@Override public void run() {
					if(mBluetooth == null) return;
					byte[] data2send= new byte[2];
					data2send[0] = 121;
					data2send[1] = 6;
					mBluetooth.send(data2send);
					mHandler.postDelayed(this, 100);
				}
			};

		});

		btHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				if(msg.what == 0){
					buttonConnect.setText(R.string.Connect);
					buttonConnect.setEnabled(true);
				}else if(msg.what == 1){
					buttonConnect.setText(R.string.Disconnect);
					buttonConnect.setEnabled(true);
				}

			}
		};


	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 2) {
			if(resultCode == RESULT_OK){
				byte[] data2send= new byte[6];
				data2send[0] = 111;
				data2send[1] = data.getByteExtra("et1", (byte) 0);
				data2send[2] = data.getByteExtra("et2", (byte) 0);
				data2send[3] = data.getByteExtra("et3", (byte) 0);
				data2send[4] = data.getByteExtra("et4", (byte) 0);
				data2send[5] = data.getByteExtra("et5", (byte) 0);
				if(mBluetooth != null)
					mBluetooth.send(data2send);	              
			}             	 
		}
	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		else if(id == R.id.connect){
			buttonConnect.callOnClick();
		}
		return super.onOptionsItemSelected(item);
	}

}
