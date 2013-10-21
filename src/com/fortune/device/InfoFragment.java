package com.fortune.device;

import org.holoeverywhere.widget.Switch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class InfoFragment extends SherlockFragment {
	DevicesInfoActivity activity;
	
	ImageView imageStatus;
	Switch switchStatus;
	
	TextView textSSID;
	TextView textMac;
	TextView textIP;
	TextView textVolt;
	TextView textAmp;
	TextView textWatt;
	
	DeviceStatus deviceStatus;
	
	Handler handler;
	
	boolean isSwitchByUser;
	boolean isSwitchByCode;
	
	final String TAG = "InfoFragment";
	
	public static InfoFragment newInstance(DeviceStatus deviceStatus) {
		InfoFragment infof = new InfoFragment();

		Bundle args = new Bundle();
		args.putSerializable("DeviceStatus", deviceStatus);
		infof.setArguments(args);

		return infof;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = (DevicesInfoActivity) activity;
		
		this.activity.refreshFragment(this, DevicesInfoActivity.INFO);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_info, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		isSwitchByUser = false;
		isSwitchByCode = false;
		handler = new Handler();
		/* get arguments */
		deviceStatus = (DeviceStatus) getArguments().getSerializable("DeviceStatus");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		findViews();
		
		if (deviceStatus.getStatus() < 0) {
			imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_configured));
			switchStatus.setEnabled(false);
		} else if (deviceStatus.getStatus() == 0) {
			imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_light_off));
			switchStatus.setEnabled(true);
			switchStatus.setChecked(false);
		} else if (deviceStatus.getStatus() == 1) {
			imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_light_on));
			switchStatus.setEnabled(true);
			switchStatus.setChecked(true);
		}
		
		textSSID.setText(deviceStatus.getSSID());
		textMac.setText(deviceStatus.getBSSID());
		textIP.setText(deviceStatus.getIP());
		textVolt.setText(Float.toString(deviceStatus.getVolt()));
		textAmp.setText(Float.toString(deviceStatus.getAmp()));
		textWatt.setText(Float.toString(deviceStatus.getVolt()*deviceStatus.getAmp()));
		
		setListener();
	}
	
	private void findViews() {
		View view = getView();
		
		imageStatus = (ImageView) view.findViewById(R.id.image_status);
		switchStatus = (Switch) view.findViewById(R.id.switch_sataus);
		
		textSSID = (TextView) view.findViewById(R.id.text_ssid);
		textMac = (TextView) view.findViewById(R.id.text_mac);
		textIP = (TextView) view.findViewById(R.id.text_ip);
		textVolt = (TextView) view.findViewById(R.id.text_volt);
		textAmp = (TextView) view.findViewById(R.id.text_amp);
		textWatt = (TextView) view.findViewById(R.id.text_watt);
	}
	
	private void setListener() {
		switchStatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
				// TODO Auto-generated method stub
				if (isSwitchByCode) {
					isSwitchByCode = false;
					return;
				}
				
				switchStatus.setEnabled(false);
				isSwitchByUser = true;
				
				Runnable sendTCPRunnable = new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (activity != null) {
							if (isChecked) {
								activity.sendTCPCommand(deviceStatus.getIP(), "1\n10\n");
							} else {
								activity.sendTCPCommand(deviceStatus.getIP(), "0\n10\n");
							}
						}
					}
				};
				
				Thread thread = new Thread(sendTCPRunnable);
				thread.start();
			}
		});
	}
	
	int countFailed = 0;
	public void updateDeviceStatus(final DeviceStatus deviceStatus) {
//		if (deviceStatus.getBSSID().equals(this.deviceStatus.getBSSID())) {
			if (getSherlockActivity() == null) {
				Log.e(TAG, "activity is null");
				return;
			}
			
			getSherlockActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (deviceStatus.getStatus() < 0) {
//						imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_configured));
//						switchStatus.setEnabled(false);
					} else if (deviceStatus.getStatus() == 0) {
						imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_light_off));
						
						if (isSwitchByUser) {
							if (!switchStatus.isChecked()) {
								isSwitchByUser = false;
								switchStatus.setEnabled(true);
							} else {
								countFailed++;
							}
							
							if (countFailed > 5) {
								countFailed = 0;
								isSwitchByCode = true;
								isSwitchByUser = false;
								switchStatus.setEnabled(true);
								switchStatus.setChecked(false);
							}
						}
					} else if (deviceStatus.getStatus() == 1) {
						imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_light_on));
						switchStatus.setEnabled(true);
						
						if (isSwitchByUser) {
							if (switchStatus.isChecked()) {
								isSwitchByUser = false;
								switchStatus.setEnabled(true);
							} else {
								countFailed++;
							}
							
							if (countFailed > 5) {
								countFailed = 0;
								isSwitchByCode = true;
								isSwitchByUser = false;
								switchStatus.setEnabled(true);
								switchStatus.setChecked(true);
							}
						}
					}
			
					if (deviceStatus.getStatus() < 0) {
						textMac.setText(deviceStatus.getBSSID()+".");
					} else {
						textMac.setText(deviceStatus.getBSSID());
						textIP.setText(deviceStatus.getIP());
						textVolt.setText(Float.toString(deviceStatus.getVolt()));
						textAmp.setText(Float.toString(deviceStatus.getAmp()));
						textWatt.setText(Float.toString(deviceStatus.getVolt()*deviceStatus.getAmp()));
					}
				}
			});
			
//		}
	}

}
