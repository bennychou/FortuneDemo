package com.fortune.device;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.fortune.wifi.APStatus;

public class ConnectWizardSelectFragment extends SherlockFragment {
	ConnectWizardActivity activity;
	ArrayList<APStatus> listAPStatus;
	ArrayAdapter<String> adapter;
	
	Button buttonCancel;
	Button buttonNext;
	
	RelativeLayout relativeSelect;
	ProgressBar progressBar;
	TextView textScanning;
	
	Spinner spinnerSSID;
	TextView textMac;
	TextView textSecurity;
	Spinner spinnerWep;
	EditText editPassword;
	CheckBox cbxShow;
	
	TableRow tableRowWep;
	TableRow tableRowPassword;
	public static ConnectWizardSelectFragment newInstance() {
		ConnectWizardSelectFragment cwsf = new ConnectWizardSelectFragment();

//		Bundle args = new Bundle();
//		args.putBoolean("isWPD", isWPD);
//		args.putSerializable("listFileStatus", listFileStatus);
//
//		pf.setArguments(args);

		return cwsf;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		this.activity = (ConnectWizardActivity) activity;
		this.activity.refreshFragment(this, 2);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_connect_wizard_select, container, false);
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
		
		findViews();
		
		setListeners();
	}
	
	private void findViews() {
		View view = getView();
		
		buttonCancel = (Button) view.findViewById(R.id.button_cancel);
		buttonNext = (Button) view.findViewById(R.id.button_next);
		
		relativeSelect = (RelativeLayout) view.findViewById(R.id.relative_select);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		textScanning = (TextView) view.findViewById(R.id.text_scanning);
		
		spinnerSSID = (Spinner) view.findViewById(R.id.spinner_ssid);
		textMac = (TextView) view.findViewById(R.id.text_mac);
		textSecurity = (TextView) view.findViewById(R.id.text_security);
		spinnerWep = (Spinner) view.findViewById(R.id.spinner_wep);
		editPassword = (EditText) view.findViewById(R.id.edit_password);
		cbxShow = (CheckBox) view.findViewById(R.id.cbx_show);
		
		tableRowWep = (TableRow) view.findViewById(R.id.table_row_wep);
		tableRowPassword = (TableRow) view.findViewById(R.id.table_row_password);
	}
	
	private void setListeners() {
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activity.finish();
			}
		});
		
		buttonNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activity.setCurrentItem(ConnectWizardActivity.CONFIGURING);
			}
		});
		
		cbxShow.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					editPassword.setInputType(InputType.TYPE_CLASS_TEXT);
				} else {
					editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});
	}
	
	public void setProgress(boolean isEnable) {
		if (isEnable) {
			buttonCancel.setEnabled(false);
			buttonNext.setEnabled(false);
			
			relativeSelect.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
			textScanning.setVisibility(View.VISIBLE);
		} else {
			buttonCancel.setEnabled(true);
			buttonNext.setEnabled(true);
			
			relativeSelect.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			textScanning.setVisibility(View.GONE);
		}
	}
	
	public void setScanResult(ArrayList<APStatus> listAPStatus) {
		ArrayList<String> listSSID = new ArrayList<String>();
		this.listAPStatus = listAPStatus;
		
		if (listAPStatus.size() == 0) {
			// no ap
		} else {
			for (int i = 0; i < listAPStatus.size(); i++) {
				listSSID.add(listAPStatus.get(i).getSSID());
			}
			adapter = new ArrayAdapter<String>(getSherlockActivity(),
	                android.R.layout.simple_spinner_item, listSSID);
			adapter
            	.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerSSID.setAdapter(adapter);
			spinnerSSID.setOnItemSelectedListener(itemSSIDSelectedListener);
		}
	}
	
	OnItemSelectedListener itemSSIDSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			textMac.setText(listAPStatus.get(position).getBSSID());
			// set the security
			if (listAPStatus.get(position).getCapabilities().contains("WPA2")) {
				if (listAPStatus.get(position).getCapabilities().replace("WPA2", "").contains("WPA")) {
					textSecurity.setText("WPA/WPA2 PSK");
					tableRowWep.setVisibility(View.GONE);
					tableRowPassword.setVisibility(View.VISIBLE);
					cbxShow.setVisibility(View.VISIBLE);
				} else {
					textSecurity.setText("WPA2 PSK");
					tableRowWep.setVisibility(View.GONE);
					tableRowPassword.setVisibility(View.VISIBLE);
					cbxShow.setVisibility(View.VISIBLE);
				}
			} else if (listAPStatus.get(position).getCapabilities().contains("WPA")) {
				textSecurity.setText("WPA PSK");
				tableRowWep.setVisibility(View.GONE);
				tableRowPassword.setVisibility(View.VISIBLE);
				cbxShow.setVisibility(View.VISIBLE);
			} else if (listAPStatus.get(position).getCapabilities().contains("WEP")) {
				textSecurity.setText("WEP");
				tableRowWep.setVisibility(View.VISIBLE);
				tableRowPassword.setVisibility(View.VISIBLE);
				cbxShow.setVisibility(View.VISIBLE);
			} else if (listAPStatus.get(position).getCapabilities().equals("[ESS]")) {
				textSecurity.setText("NONE");
				tableRowPassword.setVisibility(View.GONE);
				cbxShow.setVisibility(View.GONE);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};
}
