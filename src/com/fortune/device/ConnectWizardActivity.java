package com.fortune.device;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.fortune.wifi.APStatus;
import com.fortune.wifi.WifiConnection;
import com.viewpagerindicator.LinePageIndicator;

public class ConnectWizardActivity extends SherlockFragmentActivity {
	APStatus apStatus;
	
	WizardViewPager pager;
	LinePageIndicator indicator;
	FragmentPagerAdapter adapter;
	
	WifiConnection connection;
	
	CheckConnectedThread ccThread;
	CheckConfiguredThread ccfThread;
	
	public ConnectWizardDetailFragment cwdf;
	public ConnectWizardConnectingFragment cwcf;
	public ConnectWizardSelectFragment cwsf;
	public ConnectWizardConfiguringFragment cwcof;
	
	private static final String[] CONTENT = new String[] {"Detail", "Connecting", "Select", "Configuring"};
	public static final int DETAIL = 0;
	public static final int CONNECTING = 1;
	public static final int SELECT = 2;
	public static final int CONFIGURING = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTheme(R.style.Holo_Theme_Light_DarkActionBar_NoActionBar);
		setContentView(R.layout.activity_connect_wizard);
		
		/* get extras */
		Bundle extras = getIntent().getExtras();
		apStatus = (APStatus) extras.getSerializable("APStatus");
		
		findViews();
		
		connection = new WifiConnection(this, null);
		
		adapter = new ClusterFragmentPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		pager.setSwiped(false);
		indicator.setViewPager(pager);
		
		indicator.setOnPageChangeListener(pageChangeListener);
	}
	
	final int switchPage = 0;
	final int configuredSuccessed = 1;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case switchPage:
				setCurrentItem(msg.getData().getInt("pageIndex"));
				break;
				
			case configuredSuccessed:
				if (cwcof != null) {
					cwcof.setConfiguredResult(true);
				}
				break;
			}
		}
	};
	
	private void findViews() {
		pager = (WizardViewPager) findViewById(R.id.pager);
		indicator = (LinePageIndicator) findViewById(R.id.indicator);
	}
	
	public void setCurrentItem(int index) {
		indicator.setCurrentItem(index);
	}
	
	public void startScan(boolean isSearchDevice) {
		connection.startScan(isSearchDevice);
	}
	
	public void connect(String password) {
		connection.connect(apStatus.getSSID(), apStatus.getBSSID(), password);
	}
	
	public void unregisterReceiver() {
		connection.unregisterReceiver();
	}
	
	public void startCheckConnection() {
		if (ccThread == null || ccThread.isInterrupted()) {
			ccThread = new CheckConnectedThread();
			ccThread.start();
		}
	}
	
	public void startCheckConfiguration() {
		if (ccfThread == null || ccfThread.isInterrupted()) {
			ccfThread = new CheckConfiguredThread();
			ccfThread.start();
		}
	}
	
	public void refreshFragment(SherlockFragment fragment, int index) {
		switch(index) {
		case DETAIL:
			this.cwdf = (ConnectWizardDetailFragment) fragment;
			break;
		case CONNECTING:
			this.cwcf = (ConnectWizardConnectingFragment) fragment;
			break;
		case SELECT:
			this.cwsf = (ConnectWizardSelectFragment) fragment;
			break;
		case CONFIGURING:
			this.cwcof = (ConnectWizardConfiguringFragment) fragment;
			break;
		}
	}
	
	OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			switch(position) {
			case DETAIL: // detail
				
				break;
			case CONNECTING: // connecting
				startCheckConnection();
				break;
				
			case SELECT: // select
				if (ccThread != null && ccThread.isAlive()) {
					ccThread.stopThread();
				}
				if (cwsf != null) {
					connection.setFragment(cwsf);
					connection.startScan(false);
				}
				break;
				
			case CONFIGURING: // configuring
				if (ccThread != null && ccThread.isAlive()) {
					ccThread.stopThread();
				}
				startCheckConfiguration();
				break;
			}
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	class CheckConnectedThread extends Thread {
		boolean isRunning;
		int count;
		
		public CheckConnectedThread() {
			isRunning = true;
			count = 0;
		}
		
		public void stopThread() {
			isRunning = false;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			while(isRunning) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				count++;
				
				if (apStatus.getBSSID().equals(connection.getBSSID())) {
					// successed
					isRunning = false;
					unregisterReceiver();
					
					Message msg = new Message();
					msg.what = switchPage;
					
					Bundle data = new Bundle();
					data.putInt("pageIndex", 2);
					
					msg.setData(data);
					mHandler.sendMessage(msg);
				}
				
				if (count >= 20) {
					// failed
					isRunning = false;
					unregisterReceiver();
				}
			}
		}
	}
	
	class CheckConfiguredThread extends Thread {
		boolean isRunning;
		int count;
		
		public CheckConfiguredThread() {
			isRunning = true;
			count = 0;
		}
		
		public void stopThread() {
			isRunning = false;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			while(isRunning) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				count++;
				
				if (count >= 4) {
					isRunning = false;
					Message msg = new Message();
					msg.what = configuredSuccessed;
					mHandler.sendMessage(msg);
				}
			}
		}
	}
	
	class ClusterFragmentPagerAdapter extends FragmentPagerAdapter {
		public ConnectWizardDetailFragment cwdf;
		public ConnectWizardConnectingFragment cwcf;
		public ConnectWizardSelectFragment cwsf;
		public ConnectWizardConfiguringFragment cwcof;
		
		public ClusterFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				cwdf = ConnectWizardDetailFragment.newInstance(apStatus);
				cwdf.setRetainInstance(true);
				return cwdf;
			} else if (position == 1) {
				cwcf = ConnectWizardConnectingFragment.newInstance(apStatus);
				cwcf.setRetainInstance(true);
				return cwcf;
			} else if (position == 2) {
				cwsf = ConnectWizardSelectFragment.newInstance();
				cwsf.setRetainInstance(true);
				return cwsf;
			} else {
				cwcof = ConnectWizardConfiguringFragment.newInstance(apStatus);
				cwcof.setRetainInstance(true);
				return cwcof;
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