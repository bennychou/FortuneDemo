package com.fortune.device;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.fortune.wifi.APStatus;

public class DevicesFragment extends SherlockFragment {
	DeviceActivity activity;
	ArrayList<DeviceStatus> listDeviceStatus;
	
	ProgressBar progressBar;
	TextView textNoDevice;
	
	ListView listView;
	ListAdapter adapter;
	
	final String TAG = "DevicesFragment";
	
	public static DevicesFragment newInstance() {
		DevicesFragment df = new DevicesFragment();

//		Bundle args = new Bundle();
//		args.putBoolean("isWPD", isWPD);
//		args.putSerializable("listFileStatus", listFileStatus);
//
//		pf.setArguments(args);

		return df;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = (DeviceActivity) activity;
		this.activity.refreshFragment(this, DeviceActivity.DEVICES);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_devices, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
		textNoDevice = (TextView) getView().findViewById(R.id.text_no_device);
		listView = (ListView) getView().findViewById(R.id.list_devices);
		
		listDeviceStatus = this.activity.getDeviesInfo();
		
		if (listDeviceStatus.size() == 0) {
			// no device
			textNoDevice.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
		} else {
			adapter = new ListAdapter(listDeviceStatus);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(itemClickListener);
			listView.setOnItemLongClickListener(itemLongClickListener);
			
			progressBar.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
	}
	
	public void refreshDevices(APStatus apStatus, DeviceStatus deviceStatus, boolean isAdd) {
		if (isAdd) {
			listDeviceStatus.add(new DeviceStatus(apStatus.getSSID(), 
					apStatus.getBSSID(), apStatus.getPassword(), null, -1, -1, -1, -1));
			
			adapter = new ListAdapter(listDeviceStatus);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(itemClickListener);
			listView.setOnItemLongClickListener(itemLongClickListener);
			
			progressBar.setVisibility(View.GONE);
			textNoDevice.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		} else {
			for (int i = 0; i < listDeviceStatus.size(); i++) {
				DeviceStatus oldDeviceStatus = listDeviceStatus.get(i);
				
				Log.i(TAG, oldDeviceStatus.getBSSID());
				
				if (oldDeviceStatus.getBSSID().equals(deviceStatus.getBSSID())) {
					oldDeviceStatus.setIP(deviceStatus.getIP());
					oldDeviceStatus.setStatus(deviceStatus.getStatus());
					oldDeviceStatus.setVolt(deviceStatus.getVolt());
					oldDeviceStatus.setAmp(deviceStatus.getAmp());
					
					listDeviceStatus.set(i, oldDeviceStatus);
					
					if (getSherlockActivity() == null) {
						Log.e(TAG, "activity is null");
						return;
					}

					getSherlockActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							adapter.notifyDataSetChanged();
						}
					});
					
				} else {
					oldDeviceStatus.setIP(deviceStatus.getIP());
					oldDeviceStatus.setStatus(deviceStatus.getStatus());
					oldDeviceStatus.setVolt(deviceStatus.getVolt());
					oldDeviceStatus.setAmp(deviceStatus.getAmp());
					
					listDeviceStatus.set(i, oldDeviceStatus);
					
					if (getSherlockActivity() == null) {
						Log.e(TAG, "activity is null");
						return;
					}

					getSherlockActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							adapter.notifyDataSetChanged();
						}
					});
				}
			}
		}
	}
	
	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			Intent infoIntent = new Intent();
			infoIntent.setClass(getSherlockActivity(), DevicesInfoActivity.class);
			infoIntent.putExtra("DeviceStatus", listDeviceStatus.get(position));
			
			startActivity(infoIntent);
		}
	};
	
	OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				final int position, long arg3) {
			// TODO Auto-generated method stub
			Builder builder = new AlertDialog.Builder(getSherlockActivity());
			builder.setTitle("Delete");
			builder.setMessage("Do you want to delete this device?");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					activity.removeDevice(listDeviceStatus.get(position));
					listDeviceStatus.remove(position);
					adapter.notifyDataSetChanged();
					
					if (listDeviceStatus.size() == 0) {
						textNoDevice.setVisibility(View.VISIBLE);
						listView.setVisibility(View.GONE);
					}
			    }
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    }
			});
			builder.show();
			
			return true;
		}
	};
	
	class ListAdapter extends BaseAdapter {
		ArrayList<DeviceStatus> listDeviceStatus;
		LayoutInflater myInflater;
		ViewHolder holder;
		public ListAdapter(ArrayList<DeviceStatus> listDeviceStatus) {
			myInflater = LayoutInflater.from(getSherlockActivity());
			this.listDeviceStatus = listDeviceStatus;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listDeviceStatus.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listDeviceStatus.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = myInflater.inflate(R.layout.list_devices_item, null);
				
				holder.textName = (TextView) convertView.findViewById(R.id.text_name);
				holder.imageStatus = (ImageView) convertView.findViewById(R.id.image_status);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.textName.setText(listDeviceStatus.get(position).getSSID());
			
			if (listDeviceStatus.get(position).getStatus() < 0) {
				holder.imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_configured));
			} else if (listDeviceStatus.get(position).getStatus() == 0) {
				holder.imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_light_off));
			} else if (listDeviceStatus.get(position).getStatus() == 1) {
				holder.imageStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_devices_light_on));
			}
			
			return convertView;
		}
		
		public class ViewHolder {
			TextView textName;
			ImageView imageStatus;
		}
		
	}
	
}
