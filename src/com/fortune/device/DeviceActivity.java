package com.fortune.device;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
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
	
	UdpThread udpThread;
	
	private static final String[] CONTENT = new String[] {"Devices", "Scan"};
	public static final String CONFIG_DEVICES = "CONFIG_DEVICES";
	public static final int CONNECT_CONFIG = 0;
	public static final int CONNECT_CONFIG_SUCCESSED = 1;
	
	public static final int DEVICES = 0;
	public static final int SCAN = 1;
	
	public static final int UDP_PORT = 6666;
	
	final String TAG = "DeviceActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		setTheme(R.style.Holo_Theme_Light_DarkActionBar); // set the theme
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_device);
		
		setProgressBarIndeterminateVisibility(false);
		
//		devicesPrefs = getSharedPreferences(TAG, MODE_PRIVATE);
		deviceDBHelper = new DeviceDBHelper(this);
		
		findViews();
		
		adapter = new ClusterFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
        
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		MulticastLock multicastlock = wifiManager
				.createMulticastLock("test-udp");
		multicastlock.acquire();
	}
	
	final int refreshDeviceStatus = 1;
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case refreshDeviceStatus:
				if (df != null) {
					df.refreshDevices(null, 
							(DeviceStatus) msg.getData().getSerializable("DeviceStatus"), 
							false);
				}
				break;
			}
		}
		
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(resultCode) {
		case CONNECT_CONFIG_SUCCESSED:
			indicator.setCurrentItem(DEVICES);
			
			APStatus apStatus = (APStatus) data.getExtras().getSerializable("APStatus");
			
			SQLiteDatabase db = deviceDBHelper.getWritableDatabase();
			
			// check if the ap exists in db
			Cursor cursor = db.query(DeviceDBHelper.TableName, new String[]{DeviceDBHelper.ColumnBSSID}, 
					DeviceDBHelper.ColumnBSSID+"=?", new String[]{apStatus.getBSSID()}, null, null, null);
			
			int count = 0;
			
			if (cursor.moveToFirst()) {
				do {
					count++;
				} while(cursor.moveToNext()); 
			}
			
			if (count == 0) {
				ContentValues values = new ContentValues();
				values.put(DeviceDBHelper.ColumnSSID, apStatus.getSSID());
				values.put(DeviceDBHelper.ColumnBSSID, apStatus.getBSSID());
				values.put(DeviceDBHelper.ColumnPassword, apStatus.getPassword());
				values.put(DeviceDBHelper.ColumnStatus, "1");
				db.insert(DeviceDBHelper.TableName, null, values);
				
				if (df != null) {
					df.refreshDevices(apStatus, null, true);
				}
			}
			
			if (udpThread == null) {
				udpThread = new UdpThread(UDP_PORT);
				udpThread.start();
			} else {
				udpThread.stopThread();
				udpThread = new UdpThread(UDP_PORT);
				udpThread.start();
			}
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if (udpThread == null) {
			udpThread = new UdpThread(UDP_PORT);
			udpThread.start();
		} else {
			udpThread.stopThread();
			udpThread = new UdpThread(UDP_PORT);
			udpThread.start();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		if (udpThread != null) {
			udpThread.stopThread();
			udpThread.interrupted();
			udpThread = null;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		deviceDBHelper.close();
		
		if (udpThread != null) {
			udpThread.stopThread();
			udpThread.interrupted();
			udpThread = null;
		}
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
            			null,
            			-1,
            			-1,
            			-1,
            			-1));
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
	
	private static final int MAX_UDP_DATAGRAM_LEN = 1500;
	class UdpThread extends Thread {
		int udpPort;
		boolean isRunning;

		public UdpThread(int udpPort) {
			this.udpPort = udpPort;
			isRunning = true;
		}

		public void stopThread() {
			isRunning = false;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			while (isRunning) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String response;
				byte[] msg = new byte[MAX_UDP_DATAGRAM_LEN];
				DatagramPacket dp = new DatagramPacket(msg, msg.length);
				DatagramSocket ds = null;
				try {
					ds = new DatagramSocket(6666);
					// disable timeout for testing
					ds.setSoTimeout(5000);
					ds.receive(dp);
					response = new String(msg, 0, dp.getLength());
					
					String [] splitResponse = response.split("\n");
					
					if (splitResponse.length >= 5) {
						String [] splitStr = splitResponse[4].split("\r");
						
						String mac = splitResponse[0].toLowerCase();
						String ip = splitResponse[1];
						int state = Integer.parseInt(splitResponse[2]);
						float volt = Float.parseFloat(splitResponse[3]);
						float amp = Float.parseFloat(splitStr[0]);
						
						mac = mac.substring(0, 2)+":"+
								mac.substring(2, 4)+":"+
								mac.substring(4, 6)+":"+
								mac.substring(6, 8)+":"+
								mac.substring(8, 10)+":"+
								mac.substring(10);
						
						DeviceStatus deviceStatus = new DeviceStatus(null, 
								mac, null, ip, volt, amp, state, -1);
						
						Message message = new Message();
						message.what = refreshDeviceStatus;
						
						Bundle data = new Bundle();
						data.putSerializable("DeviceStatus", deviceStatus);
						
						message.setData(data);
						mHandler.handleMessage(message);
						
						Log.i(TAG, "mac:"+mac+";ip:"+ip+
								";state:"+state+";volt:"+volt+";amp:"+amp);
					}

				} catch (SocketException e) {
//					e.printStackTrace();
					Log.e(TAG, "UDP socket error: "+e.toString());
				} catch (IOException e) {
//					e.printStackTrace();
					Log.e(TAG, "UDP IO error: "+e.toString());
				} finally {
					if (ds != null) {
						ds.close();
					}
				}
			}
		}
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
