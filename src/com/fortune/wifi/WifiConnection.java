package com.fortune.wifi;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import com.fortune.device.ConnectWizardSelectFragment;
import com.fortune.device.DeviceActivity;
import com.fortune.device.MainActivity;
import com.fortune.device.ScanFragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

public class WifiConnection {
	Activity activity;
	SherlockFragment fragment;
	
	WifiManager wifiManager;
	ConnectivityManager connectManager;
	BroadcastReceiver receiver;

	ArrayList<APStatus> listAPStatus;

	Handler mHandler;
	public static final int strength = -100;

	final String TAG = "WifiConnection";

	public WifiConnection(Activity activity, SherlockFragment fragment) {
		this.activity = activity;
		this.fragment = fragment;
		
		wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		connectManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

		/* open wifi */
		if (!wifiManager.isWifiEnabled())
			wifiManager.setWifiEnabled(true);
		
		listAPStatus = new ArrayList<APStatus>();
	}
	
	public void setFragment(SherlockFragment fragment) {
		this.fragment = fragment;
	}

	public void startScan(final boolean isSearchDevice) {
		// show progress
		if (fragment != null) {
			if (fragment instanceof ScanFragment)
				((ScanFragment) fragment).setProgress(true);
			else if (fragment instanceof ConnectWizardSelectFragment)
				((ConnectWizardSelectFragment) fragment).setProgress(true);
		}

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// Code to execute when SCAN_RESULTS_AVAILABLE_ACTION event
				// occurs
				listAPStatus.clear();
				
				List<ScanResult> results = wifiManager.getScanResults();
				for (ScanResult result : results) {
					/* filter strength */
					if (result.level < strength)
						continue;
					else {
						if (isSearchDevice) {
							if (result.SSID.contains(MainActivity.DEVICE_KEYWORD)) 
								listAPStatus.add(new APStatus(result.SSID, result.BSSID, 
										result.capabilities, result.frequency, result.level, null));
							else
								continue;
							
						} else {
							if (result.SSID.contains(MainActivity.DEVICE_KEYWORD)) 
								continue;
							else
								listAPStatus.add(new APStatus(result.SSID, result.BSSID, 
										result.capabilities, result.frequency, result.level, null));
						}
						
//						Log.i(TAG, "SSID:" + result.SSID + "\n" +
//								"BSSID:" + result.BSSID + "\n" +
//								"Capabilities:" + result.capabilities + "\n" +
//								"Frequency:" + result.frequency + "\n" +
//								"Level:" + result.level);
					}
				}

				Collections.sort(listAPStatus,
						new Comparator<APStatus>() {
							public int compare(APStatus o1, APStatus o2) {
								return (int) (o2.getLevel() - o1.getLevel());
							}
						});

				// close progress
				if (fragment != null)
					if (fragment instanceof ScanFragment) {
						((ScanFragment) fragment).setProgress(false);
						((ScanFragment) fragment).setListView(listAPStatus);
					} else if (fragment instanceof ConnectWizardSelectFragment) {
						((ConnectWizardSelectFragment) fragment).setProgress(false);
						((ConnectWizardSelectFragment) fragment).setScanResult(listAPStatus);
					}
				
				try {
					activity.unregisterReceiver(receiver);
				} catch (Exception e) {
					Log.e(TAG, "The error of unregister receiver in wifi scan: " + e.toString());
				}
			}
		};
		activity.registerReceiver(receiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		// Now you can call this and it should execute the broadcastReceiver's
		// onReceive()
		wifiManager.startScan();
	}

	public void unregisterReceiver() {
		try {
			activity.unregisterReceiver(receiver);
		} catch (Exception e) {
			Log.e(TAG, "Unregister error: " + e.toString());
			return;
		}
	}

	public boolean connect(String SSID, String BSSID, String password) {
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.i(TAG, "Action: "+intent.getAction());
			}
		};
		activity.registerReceiver(receiver, new IntentFilter(
				WifiManager.NETWORK_STATE_CHANGED_ACTION));
		
		// show progress

		WifiConfiguration wifiConfig = new WifiConfiguration();

		wifiConfig.SSID = "\"" + SSID + "\"";
		wifiConfig.BSSID = BSSID;
		wifiConfig.status = WifiConfiguration.Status.ENABLED;
		wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		wifiConfig.allowedPairwiseCiphers
				.set(WifiConfiguration.PairwiseCipher.TKIP);
		wifiConfig.allowedPairwiseCiphers
				.set(WifiConfiguration.PairwiseCipher.CCMP);
		wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

		if (password.equals("")) {
			wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		} else {

			wifiConfig.allowedKeyManagement
					.set(WifiConfiguration.KeyMgmt.WPA_PSK);

			if (password.matches("[0-9A-Fa-f]{64}")) {
				wifiConfig.preSharedKey = password;
			} else {
				wifiConfig.preSharedKey = '"' + password + '"';
			}
		}

		int netId = wifiManager.addNetwork(wifiConfig);

		boolean isSuccessed = wifiManager.enableNetwork(netId, true);

		if (isSuccessed) {
			List<WifiConfiguration> configs = wifiManager
					.getConfiguredNetworks();
			for (WifiConfiguration config : configs) {
				if (config.networkId != netId
						&& config.SSID.equals('"' + SSID + '"')) {
					wifiManager.removeNetwork(config.networkId);
				}
			}
		} else {
			wifiManager.removeNetwork(netId);
		}

		return isSuccessed;
	}

	public String getSSID() {
		WifiInfo wInfo = wifiManager.getConnectionInfo();
		return wInfo.getSSID();
	}
	
	public String getBSSID() {
		WifiInfo wInfo = wifiManager.getConnectionInfo();
		return wInfo.getBSSID();
	}
	
	public String getMacAddress() {
	    WifiInfo wifiInf = wifiManager.getConnectionInfo();
	    return wifiInf.getMacAddress();
	}
	
	public InetAddress getSelfAddress() throws IOException {
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
          quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

	public final static int wifi = ConnectivityManager.TYPE_WIFI;
	public final static int mobile = ConnectivityManager.TYPE_MOBILE;
	public final static int noNetwork = -1;

	public int haveInternet() {
		NetworkInfo nInfo = connectManager.getActiveNetworkInfo();
		if (nInfo == null || !nInfo.isConnected()) {
			return noNetwork;
		} else {
			if (!nInfo.isAvailable()) {
				return noNetwork;
			} else {
				return nInfo.getType();
			}
		}
	}

	/* ¸õ¨ì wifi ³]©w */
	public static final int requestCodeForWifi = 0;

	public void showWifiSetting() {
//		AlertDialog.Builder alert = new AlertDialog.Builder(context);
//		alert.setIcon(context.getResources()
//				.getDrawable(R.drawable.ic_launcher));
//		alert.setTitle(context.getResources().getString(R.string.error));
//		alert.setMessage(context.getResources()
//				.getString(R.string.check_device));
//		alert.setPositiveButton(
//				context.getResources().getString(R.string.setting),
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialoginterface, int i) {
//						Intent intent = new Intent(
//								android.provider.Settings.ACTION_WIFI_SETTINGS);
//						((Activity) context).startActivityForResult(intent,
//								requestCodeForWifi);
//					}
//				});
//
//		alert.setNegativeButton(
//				context.getResources().getString(R.string.dialog_cancel),
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialoginterface, int i) {
//					}
//				});
//		alert.show();
	}
}
