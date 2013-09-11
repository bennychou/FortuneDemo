package com.fortune.device;

import com.fortune.wifi.WifiConnection;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class MainActivity extends Activity {
	
	Button btnLogin;
	EditText editPassword;
	CheckBox cbxShow;
	
	WifiConnection connection;
	
	public static final String DEVICE_KEYWORD = "FTS_";
	public static final String DEVICE_PASSWORD = "27992589";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setTheme(R.style.Holo_Theme_Light_DarkActionBar); // set the theme
		setContentView(R.layout.activity_main);
		
		findViews();
		
		setListeners();
	}
	
	private void findViews() {
		btnLogin = (Button) findViewById(R.id.btn_login);
		editPassword = (EditText) findViewById(R.id.edit_pwd);
		cbxShow = (CheckBox) findViewById(R.id.cbx_show);
	}
	
	private void setListeners() {
		btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent devicesIntent = new Intent();
				devicesIntent.setClass(MainActivity.this, DeviceActivity.class);
				startActivity(devicesIntent);
			}
		});
		
		cbxShow.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					editPassword.setInputType(InputType.TYPE_CLASS_TEXT);
				} else {
					editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
