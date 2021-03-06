package com.fortune.device;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.fortune.device.DeviceActivity.UdpThread;
import com.viewpagerindicator.TabPageIndicator;

@SuppressLint("NewApi")
public class DevicesInfoActivity extends SherlockFragmentActivity {
	ViewPager pager;
	TabPageIndicator indicator;
	FragmentPagerAdapter adapter;
	
	DeviceStatus deviceStatus;
	
	UdpThread udpThread;
	
	public InfoFragment infof;
	public AlarmsFragment af;
	public TimerFragment tf;
	
	public static final int INFO = 0;
	public static final int ALARM = 1;
	public static final int TIMER = 2;
	
	private static final String[] CONTENT = new String[] {"Info.", "Schedule", "Timer"};

	final String TAG = "DevicesInfoActivity";
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_devices_info);
		
		/* get extras */
		deviceStatus = (DeviceStatus) getIntent().getSerializableExtra("DeviceStatus");
		
		/* set action bar */
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		findViews();
		
		adapter = new ClusterFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
        
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		MulticastLock multicastlock = wifiManager
				.createMulticastLock("test-udp");
		multicastlock.acquire();
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
	}
	
	final int refreshDeviceStatus = 1;
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case refreshDeviceStatus:
				deviceStatus = (DeviceStatus) msg.getData().getSerializable("DeviceStatus");
				
				if (infof != null) {
					infof.updateDeviceStatus(deviceStatus);
				}
				break;
			}
		}
		
	};
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (udpThread == null) {
			udpThread = new UdpThread(DeviceActivity.UDP_PORT);
			udpThread.start();
		} else {
			udpThread.stopThread();
			udpThread = new UdpThread(DeviceActivity.UDP_PORT);
			udpThread.start();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (udpThread != null) {
			udpThread.stopThread();
		}
	}

	private void findViews() {
		pager = (ViewPager) findViewById(R.id.viewPager);
		indicator = (TabPageIndicator) findViewById(R.id.indicator);
	}
	
	public void refreshFragment(SherlockFragment fragment, int index) {
		switch(index) {
		case INFO:
			this.infof = (InfoFragment) fragment;
			break;
			
		case ALARM:
			this.af = (AlarmsFragment) fragment;
			break;
			
		case TIMER:
			this.tf = (TimerFragment) fragment;
			break;
		}
	}
	
	public void sendTCPCommand(String IP, String command) {
		if (udpThread != null) {
			udpThread.stopThread();
		}
		Socket socket = null;
		try {
			InetAddress serverAddr = InetAddress.getByName(IP);
			socket = new Socket(serverAddr, 9998);
			socket.setReuseAddress(true);
//			socket.bind(new InetSocketAddress(IP, 9998));
			socket.setSoTimeout(1000);

			// -----發送socket--------
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())),
					true);
			
			Log.i(TAG, command);
			
			out.println(command);
			out.flush();
			// -----/發送socket/--------

			// -----接收socket--------
			BufferedReader br = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));

			char[] m = new char[100];
			br.read(m);
			String rec_msg = new String(m);

			Log.i(TAG, rec_msg);
			
			if (rec_msg.contains("OK")) {
				if (infof != null) {
					if (command.substring(0, 1).equals("1")) {
						deviceStatus.setStatus(1);
						infof.updateOnOffStatus(true);
					} else {
						deviceStatus.setStatus(0);
						infof.updateOnOffStatus(false);
					}
				}
			}
			// -----/接收socket/--------

		} catch (Exception e) {
			Log.e("TCP", "S: Error PrintWrite", e);
		} finally {
			if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		if (udpThread == null) {
			udpThread = new UdpThread(DeviceActivity.UDP_PORT);
			udpThread.start();
		} else {
			udpThread.stopThread();
			udpThread = new UdpThread(DeviceActivity.UDP_PORT);
			udpThread.start();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent data = new Intent();
			
			Bundle extras = new Bundle();
			extras.putSerializable("DeviceStatus", deviceStatus);
			
			data.putExtras(extras);
			setResult(DeviceActivity.UPDATE_DEVICE_STATUS, data);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent data = new Intent();
			
			Bundle extras = new Bundle();
			extras.putSerializable("DeviceStatus", deviceStatus);
			
			data.putExtras(extras);
			setResult(DeviceActivity.UPDATE_DEVICE_STATUS, data);
		}
		return super.onKeyDown(keyCode, event);
	}

	private static final int MAX_UDP_DATAGRAM_LEN = 1500;
	class UdpThread extends Thread {
		int udpPort;
		boolean isRunning;
		DatagramSocket ds;

		public UdpThread(int udpPort) {
			this.udpPort = udpPort;
			isRunning = true;
			ds = null;
		}

		public void stopThread() {
			isRunning = false;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			String response;
			
			try {
				ds = new DatagramSocket(null);
				ds.setBroadcast(true);
				ds.setReuseAddress(true);
				ds.bind(new InetSocketAddress(udpPort));
				ds.setSoTimeout(20000);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			while (isRunning) {
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

				try {
					// disable timeout for testing
					byte[] msg = new byte[MAX_UDP_DATAGRAM_LEN];
					DatagramPacket dp = new DatagramPacket(msg, msg.length);
					
					if (!ds.isClosed())
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
					e.printStackTrace();
					Log.e(TAG, "UDP socket error: "+e.toString());
					
					DeviceStatus deviceStatus = new DeviceStatus(null, 
							"", null, "", 0.0f, 0.0f, -1, -1);
					
					Message message = new Message();
					message.what = refreshDeviceStatus;
					
					Bundle data = new Bundle();
					data.putSerializable("DeviceStatus", deviceStatus);
					
					message.setData(data);
					mHandler.handleMessage(message);
				} catch (IOException e) {
					e.printStackTrace();
					Log.e(TAG, "UDP IO error: "+e.toString());
					
					DeviceStatus deviceStatus = new DeviceStatus(null, 
							"", null, "", 0.0f, 0.0f, -1, -1);
					
					Message message = new Message();
					message.what = refreshDeviceStatus;
					
					Bundle data = new Bundle();
					data.putSerializable("DeviceStatus", deviceStatus);
					
					message.setData(data);
					mHandler.handleMessage(message);
				} finally {
//					if (ds != null) {
//						ds.close();
//					}
				}
			}
			
			if (ds != null) {
				ds.close();
			}
		}
	}
	
	class ClusterFragmentPagerAdapter extends FragmentPagerAdapter {
		public ClusterFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return InfoFragment.newInstance(deviceStatus);
			} else if (position == 1) {
				return AlarmsFragment.newInstance();
			} else {
				return TimerFragment.newInstance();
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
