package com.example.LineFollowerApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ParametersActivity extends Activity {
	
	Button buttonSend;
	EditText et1,et2,et3,et4,et5; // 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parameters);
		et1 = (EditText) findViewById(R.id.editText1);
		et2 = (EditText) findViewById(R.id.editText2);
	    et3 = (EditText) findViewById(R.id.editText3);
	    et4 = (EditText) findViewById(R.id.editText4);
	    et5 = (EditText) findViewById(R.id.editText5);
	    
		buttonSend = (Button) findViewById(R.id.buttonSend);
		buttonSend.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent();	
				intent.putExtra("et1",Byte.parseByte(et1.getText().toString()));
				intent.putExtra("et2",Byte.parseByte(et4.getText().toString()));
				intent.putExtra("et3",Byte.parseByte(et2.getText().toString()));
				intent.putExtra("et4",Byte.parseByte(et3.getText().toString()));
				intent.putExtra("et5",Byte.parseByte(et5.getText().toString()));
				setResult(RESULT_OK, intent);        
				finish();
			}
		});
	}
	

}
