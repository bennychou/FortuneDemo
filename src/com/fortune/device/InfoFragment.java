package com.fortune.device;

import org.holoeverywhere.widget.Switch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class InfoFragment extends SherlockFragment {
	DevicesInfoActivity activity;
	
	ImageView imageStatus;
	TextView textStatus;
	
	TextView textSSID;
	TextView textMac;
	TextView textIP;
	TextView textVolt;
	TextView textAmp;
	TextView textWatt;
	
	DeviceStatus deviceStatus;
	
	Handler handler;
	
	float oldAmp;
	float oldWatt;
	
	boolean isChecked;
	
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
		
		handler = new Handler();
		/* get arguments */
		deviceStatus = (DeviceStatus) getArguments().getSerializable("DeviceStatus");
		
		/* initialize parameter */
		oldAmp = 0.0f;
		oldWatt = 0.0f;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		findViews();
		
		if (deviceStatus.getStatus() < 0) {
			imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_configured));
			imageStatus.setEnabled(false);
		} else if (deviceStatus.getStatus() == 0) {
			imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_light_off));
			imageStatus.setEnabled(true);
			isChecked = false;
		} else if (deviceStatus.getStatus() == 1) {
			imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_light_on));
			imageStatus.setEnabled(true);
			isChecked = true;
		}
		
		textSSID.setText(deviceStatus.getSSID());
		textMac.setText(deviceStatus.getBSSID());
		textIP.setText(deviceStatus.getIP());
		textVolt.setText(Float.toString(deviceStatus.getVolt())+" V");
		textAmp.setText(Float.toString(deviceStatus.getAmp())+" mA");
		textWatt.setText(Float.toString(deviceStatus.getVolt()*deviceStatus.getAmp())+" mW");
		
		setListener();
	}
	
	private void findViews() {
		View view = getView();
		
		imageStatus = (ImageView) view.findViewById(R.id.image_status);
		textStatus = (TextView) view.findViewById(R.id.text_status);
		
		textSSID = (TextView) view.findViewById(R.id.text_ssid);
		textMac = (TextView) view.findViewById(R.id.text_mac);
		textIP = (TextView) view.findViewById(R.id.text_ip);
		textVolt = (TextView) view.findViewById(R.id.text_volt);
		textAmp = (TextView) view.findViewById(R.id.text_amp);
		textWatt = (TextView) view.findViewById(R.id.text_watt);
	}
	
	private void setListener() {
		imageStatus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.startAnimation(AnimationUtils.loadAnimation(getSherlockActivity(), R.anim.image_click));
				textStatus.setText("Processing...");
				imageStatus.setClickable(false);
				
				Runnable sendTCPRunnable = new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (activity != null) {
							if (isChecked) {
								activity.sendTCPCommand(deviceStatus.getIP(), "0\n10\n");
							} else {
								activity.sendTCPCommand(deviceStatus.getIP(), "1\n10\n");
							}
						}
					}
				};
				
				Thread thread = new Thread(sendTCPRunnable);
				thread.start();
			}
		});
	}
	
	public void updateOnOffStatus(final boolean isOn) {
		if (getSherlockActivity() == null) {
			Log.e(TAG, "activity is null");
			return;
		}
		
		getSherlockActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (isOn) {
					imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_light_on));
					imageStatus.setClickable(true);
					isChecked = true;
					textStatus.setText("");
					textAmp.setText(oldAmp+" mA");
					textWatt.setText(oldWatt+" mW");
				} else {
					imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_light_off));
					imageStatus.setClickable(true);
					isChecked = false;
					textStatus.setText("");
					textAmp.setText("0.0 mA");
					textWatt.setText("0.0 mW");
				}
			}
		});
	}
	
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
					imageStatus.setOnClickListener(null);
					if (deviceStatus.getStatus() < 0) {
//						imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_configured));
//						switchStatus.setEnabled(false);
					} else if (deviceStatus.getStatus() == 0) {
						imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_light_off));
						imageStatus.setClickable(true);
						isChecked = false;
						textStatus.setText("");
					} else if (deviceStatus.getStatus() == 1) {
						imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_light_on));
						imageStatus.setClickable(true);
						isChecked = true;
						textStatus.setText("");
					}
			
					if (deviceStatus.getStatus() < 0) {
						textSSID.setText(textSSID.getText().toString().replace(".", "")+".");
					} else {
						textSSID.setText(textSSID.getText().toString().replace(".", ""));
						textMac.setText(deviceStatus.getBSSID());
						textIP.setText(deviceStatus.getIP());
						textVolt.setText(Float.toString(deviceStatus.getVolt())+ " V");
						if (deviceStatus.getStatus() == 1) {
							oldAmp = deviceStatus.getAmp();
							textAmp.setText(Float.toString(deviceStatus.getAmp())+ " mA");
							
							if (deviceStatus.getVolt() == -1 &&
									deviceStatus.getAmp() == -1)
								textWatt.setText("-1"+ " mW");
							else {
								oldWatt = deviceStatus.getVolt()*deviceStatus.getAmp();
								textWatt.setText(Float.toString(deviceStatus.getVolt()*deviceStatus.getAmp())+ " mW");
							}
						} else {
							textAmp.setText("0.0 mA");
							textWatt.setText("0.0 mW");
						}
					}
					
					setListener();
				}
			});
			
//		}
	}

}
