package com.fortune.device;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
			
			progressBar.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
	}
	
	public void refreshDevices(APStatus apStatus) {
		listDeviceStatus.add(new DeviceStatus(apStatus.getSSID(), 
				apStatus.getBSSID(), apStatus.getPassword(), 1));
		
		adapter = new ListAdapter(listDeviceStatus);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);
		
		progressBar.setVisibility(View.GONE);
		textNoDevice.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
	}
	
	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			Intent infoIntent = new Intent();
			infoIntent.setClass(getSherlockActivity(), DevicesInfoActivity.class);
			
			startActivity(infoIntent);
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

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.textName.setText(listDeviceStatus.get(position).getSSID());
			
			return convertView;
		}
		
		public class ViewHolder {
			TextView textName;
		}
		
	}
	
}
