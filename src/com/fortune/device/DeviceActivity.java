package com.fortune.device;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.fortune.wifi.APStatus;
import com.viewpagerindicator.TabPageIndicator;

public class DeviceActivity extends SherlockFragmentActivity {
	ViewPager pager;
	TabPageIndicator indicator;
	FragmentPagerAdapter adapter;
	
	public DevicesFragment df;
	public ScanFragment sf;
	
//	SharedPreferences devicesPrefs;
	DeviceDBHelper deviceDBHelper;
	
	private static final String[] CONTENT = new String[] {"Devices", "Scan"};
	public static final String CONFIG_DEVICES = "CONFIG_DEVICES";
	public static final int CONNECT_CONFIG = 0;
	public static final int CONNECT_CONFIG_SUCCESSED = 1;
	
	public static final int DEVICES = 0;
	public static final int SCAN = 1;
	
	final String TAG = "DeviceActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		setTheme(R.style.Holo_Theme_Light_DarkActionBar); // set the theme
		setContentView(R.layout.activity_device);
		
//		devicesPrefs = getSharedPreferences(TAG, MODE_PRIVATE);
		deviceDBHelper = new DeviceDBHelper(this);
		
		findViews();
		
		adapter = new ClusterFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(resultCode) {
		case CONNECT_CONFIG_SUCCESSED:
			indicator.setCurrentItem(DEVICES);
			
			APStatus apStatus = (APStatus) data.getExtras().getSerializable("APStatus");
			
			SQLiteDatabase db = deviceDBHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(DeviceDBHelper.ColumnSSID, apStatus.getSSID());
			values.put(DeviceDBHelper.ColumnBSSID, apStatus.getBSSID());
			values.put(DeviceDBHelper.ColumnPassword, apStatus.getPassword());
			values.put(DeviceDBHelper.ColumnStatus, "1");
			db.insert(DeviceDBHelper.TableName, null, values);
			
			if (df != null) {
				df.refreshDevices(apStatus);
			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		deviceDBHelper.close();
	}

	private void findViews() {
		pager = (ViewPager) findViewById(R.id.viewPager);
		indicator = (TabPageIndicator) findViewById(R.id.indicator);
	}
	
	public void refreshFragment(SherlockFragment fragment, int index) {
		switch(index) {
		case DEVICES:
			this.df = (DevicesFragment) fragment;
			break;
			
		case SCAN:
			this.sf = (ScanFragment) fragment;
			break;
		}
	}
	
	public ArrayList<DeviceStatus> getDeviesInfo() {
		ArrayList<DeviceStatus> listDeviceStatus = new ArrayList<DeviceStatus>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DeviceDBHelper.TableName;
 
        SQLiteDatabase db = deviceDBHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	listDeviceStatus.add(new DeviceStatus(cursor.getString(1), 
            			cursor.getString(2), 
            			cursor.getString(3), 
            			Integer.parseInt(cursor.getString(4))));
            } while (cursor.moveToNext());
        }
		return listDeviceStatus;
	}
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	class ClusterFragmentPagerAdapter extends FragmentPagerAdapter {
		public DevicesFragment df;
		public ScanFragment sf;
		
		public ClusterFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				df = DevicesFragment.newInstance();
				df.setRetainInstance(true);
				return df;
			} else {
				sf = ScanFragment.newInstance();
				sf.setRetainInstance(true);
				return sf;
			}
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return POSITION_NONE;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position];
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
	}
}
