package com.fortune.device;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.Spinner;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.fortune.wifi.APStatus;
import com.fortune.wifi.WifiConnection;

public class ScanFragment extends SherlockFragment {
	ArrayList<APStatus> listAPStatus;
	
	ProgressBar progressBar;
	TextView textNoDevice;
	
	ListView listView;
	ListAdapter adapter;
	
	WifiConnection connection;
	
	public static final int [] ChannelFrequency = {2412, 2417, 2422, 2427, 
		2432, 2437, 2442, 2447, 2452, 2457, 2462, 2467, 2472, 2484};
	
	public static final int [] APStrength = { -70, -80, -90 };
	
	public static ScanFragment newInstance() {
		ScanFragment sf = new ScanFragment();
		sf.setHasOptionsMenu(true);

//		Bundle args = new Bundle();
//		args.putBoolean("isWPD", isWPD);
//		args.putSerializable("listFileStatus", listFileStatus);
//
//		pf.setArguments(args);

		return sf;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		connection = new WifiConnection(activity, this);
		((DeviceActivity) activity).refreshFragment(this, DeviceActivity.SCAN);
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
		
		connection.startScan(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		
		menu.add(0, 0, 0, "Scan")
			.setIcon(R.drawable.ic_menu_scan)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 0:
			connection.startScan(true);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setProgress(boolean isEnable) {
		if (isEnable) {
			progressBar.setVisibility(View.VISIBLE);
			textNoDevice.setVisibility(View.GONE);
			listView.setVisibility(View.GONE);
		} else {
			progressBar.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
	}
	
	public void setListView(ArrayList<APStatus> listAPStatus) {
		if (this.listAPStatus == null) {
			this.listAPStatus = listAPStatus;
			
			if (this.listAPStatus.size() == 0) {
				// no ap
				textNoDevice.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
			} else {
				adapter = new ListAdapter(this.listAPStatus);
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(itemClickListener);
			}
		} else {
			this.listAPStatus = listAPStatus;
			
			if (this.listAPStatus.size() == 0) {
				// no ap
				textNoDevice.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
			} else {
				adapter = new ListAdapter(this.listAPStatus);
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, final int position,
				long arg3) {
			// TODO Auto-generated method stub
			Intent connectIntent = new Intent();
			connectIntent.setClass(getSherlockActivity(), ConnectWizardActivity.class);
			
			Bundle extras = new Bundle();
			extras.putSerializable("APStatus", listAPStatus.get(position));
			
			connectIntent.putExtras(extras);
			
			startActivityForResult(connectIntent, DeviceActivity.CONNECT_CONFIG);

//			View view = View.inflate(getSherlockActivity(),
//					R.layout.dialog_ap_detail, null);
//			// status
//			TextView txtStatusTitle = (TextView) view.findViewById(R.id.text_status_title);
//			TextView txtStatus = (TextView) view.findViewById(R.id.text_status);
//			View lineStatus = view.findViewById(R.id.line_status);
//			// ip
//			TextView txtIPTitle = (TextView) view.findViewById(R.id.text_ip_title);
//			TextView txtIP = (TextView) view.findViewById(R.id.text_ip);
//			View lineIP = view.findViewById(R.id.line_ip);
//			// other details
//			TextView txtStrength = (TextView) view.findViewById(R.id.text_strength);
//			TextView txtSecurity = (TextView) view.findViewById(R.id.text_security);
//			TextView txtMacAddress = (TextView) view.findViewById(R.id.text_mac_address);
//			TextView txtChannel = (TextView) view.findViewById(R.id.text_channel);
//			// password
//			TextView txtPasswordTitle = (TextView) view
//					.findViewById(R.id.text_password_title);
//			final EditText editPassword = (EditText) view
//					.findViewById(R.id.edit_password);
//			CheckBox cbxShow = (CheckBox) view.findViewById(R.id.cbx_show);
//			View linePassword = view.findViewById(R.id.line_password);
//			// wep
//			TextView txtKey = (TextView) view.findViewById(R.id.text_key);
//			final Spinner spinnerKey = (Spinner) view.findViewById(R.id.spinner_key);
//			
//			cbxShow.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
//				public void onCheckedChanged(CompoundButton buttonView,
//						boolean isChecked) {
//					if (isChecked) {
//						editPassword.setInputType(InputType.TYPE_CLASS_TEXT);
//					} else {
//						editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//					}
//				}
//			});
//			editPassword.setSingleLine(true);
//
//			// set the strength
//			if (listAPStatus.get(position).getLevel() > APStrength[0]) {
//				txtStrength.setText("Excellent");
//			} else if (listAPStatus.get(position).getLevel() > APStrength[1]) {
//				txtStrength.setText("Good");
//			} else if (listAPStatus.get(position).getLevel() > APStrength[2]) {
//				txtStrength.setText("Fair");
//			} else {
//				txtStrength.setText("Poor");
//			}
//
//			// set the security
//			if (listAPStatus.get(position).getCapabilities().contains("WPA2")) {
//				if (listAPStatus.get(position).getCapabilities().replace("WPA2", "").contains("WPA")) {
//					txtSecurity.setText("WPA/WPA2 PSK");
//				} else {
//					txtSecurity.setText("WPA2 PSK");
//				}
//			} else if (listAPStatus.get(position).getCapabilities().contains("WPA")) {
//				txtSecurity.setText("WPA PSK");
//			} else if (listAPStatus.get(position).getCapabilities().contains("WEP")) {
//				txtSecurity.setText("WEP");
//				txtKey.setVisibility(View.VISIBLE);
//				spinnerKey.setVisibility(View.VISIBLE);
//			} else if (listAPStatus.get(position).getCapabilities().equals("[ESS]")) {
//				txtSecurity.setText("NONE");
//				txtPasswordTitle.setVisibility(View.GONE);
//				editPassword.setVisibility(View.GONE);
//				cbxShow.setVisibility(View.GONE);
//				linePassword.setVisibility(View.GONE);
//			}
//			
//			// set mac address
//			txtMacAddress.setText(listAPStatus.get(position).getBSSID());
//			
//			// set channel
//			for (int i = 0; i < ChannelFrequency.length; i++) {
//				if (listAPStatus.get(position).getFrequency() == ChannelFrequency[i]) {
//					txtChannel.setText(Integer.toString(i+1));
//				}
//			}
//
//			// show the connected status
//			boolean isConnected = false;
//			if (connection.getSSID() != null && connection.getSSID().equals(listAPStatus.get(position).getSSID())) {
//				isConnected = true;
//				
//				txtStatus.setText("Connected");
//				
//				txtStatusTitle.setVisibility(View.VISIBLE);
//				txtStatus.setVisibility(View.VISIBLE);
//				lineStatus.setVisibility(View.VISIBLE);
//				
//				try {
//					txtIP.setText(connection.getSelfAddress().getHostAddress());
//					
//					txtIPTitle.setVisibility(View.VISIBLE);
//					txtIP.setVisibility(View.VISIBLE);
//					lineIP.setVisibility(View.VISIBLE);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				txtPasswordTitle.setVisibility(View.GONE);
//				editPassword.setVisibility(View.GONE);
//				cbxShow.setVisibility(View.GONE);
//				linePassword.setVisibility(View.GONE);
//			}
//
//			/* show alertdialog */
//			AlertDialog.Builder alert = new AlertDialog.Builder(getSherlockActivity());
//			alert.setTheme(R.style.Holo_Theme_Dialog_Alert_Light);
//			alert.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
//			alert.setTitle(listAPStatus.get(position).getSSID());
//			alert.setView(view);
//			
//			if (!isConnected) {
//				alert.setPositiveButton("Connect",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int whichButton) {
//								connection.connect(listAPStatus.get(position).getSSID(), 
//										listAPStatus.get(position).getBSSID(), editPassword.getText().toString());
//							}
//						});
//			}
//
//			alert.setNegativeButton(
//					"Cancel",
//					new DialogInterface.OnClickListener() {
//
//						public void onClick(DialogInterface dialog, int which) {
//							// TODO Auto-generated method stub
//						}
//					});
//
//			alert.show();
			
		}
	};
	
	class ListAdapter extends BaseAdapter {
		ArrayList<APStatus> listAPStatus;
		LayoutInflater myInflater;
		ViewHolder holder;
		public ListAdapter(ArrayList<APStatus> listAPStatus) {
			myInflater = LayoutInflater.from(getSherlockActivity());
			this.listAPStatus = listAPStatus;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listAPStatus.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listAPStatus.get(position);
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

				convertView = myInflater.inflate(R.layout.list_scan_item, null);
				
				holder.textName = (TextView) convertView.findViewById(R.id.text_name);
				holder.textSecurity = (TextView) convertView.findViewById(R.id.text_security);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.textName.setText(listAPStatus.get(position).getSSID());
			
			// set the security
			if (listAPStatus.get(position).getCapabilities().contains("WPA2")) {
				if (listAPStatus.get(position).getCapabilities().replace("WPA2", "").contains("WPA")) {
					holder.textSecurity.setText("WPA/WPA2 PSK");
				} else {
					holder.textSecurity.setText("WPA2 PSK");
				}
			} else if (listAPStatus.get(position).getCapabilities().contains("WPA")) {
				holder.textSecurity.setText("WPA PSK"); 
			} else if (listAPStatus.get(position).getCapabilities().contains("WEP")) {
				holder.textSecurity.setText("WEP");
			} else if (listAPStatus.get(position).getCapabilities().equals("[ESS]")) {
				holder.textSecurity.setText("NONE");
			}
			
			if (connection.getSSID() != null && connection.getSSID().equals(listAPStatus.get(position).getSSID()))
				holder.textSecurity.setText("Connected");
			
			return convertView;
		}
		
		public class ViewHolder {
			TextView textName;
			TextView textSecurity;
		}
		
	}
}
