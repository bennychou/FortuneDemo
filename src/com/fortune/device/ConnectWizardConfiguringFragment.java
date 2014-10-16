package com.fortune.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.fortune.wifi.APStatus;

public class ConnectWizardConfiguringFragment extends SherlockFragment {
	ConnectWizardActivity activity;
	
	Button buttonCancel;
	Button buttonNext;
	
	ProgressBar progressBar;
	TextView textConfiguring;
	
	Bundle args;
	public static ConnectWizardConfiguringFragment newInstance(APStatus apStatus) {
		ConnectWizardConfiguringFragment cwcf = new ConnectWizardConfiguringFragment();

		Bundle args = new Bundle();
		args.putSerializable("APStatus", apStatus);
		
		cwcf.setArguments(args);
//		Bundle args = new Bundle();
//		args.putBoolean("isWPD", isWPD);
//		args.putSerializable("listFileStatus", listFileStatus);
//
//		pf.setArguments(args);

		return cwcf;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		this.activity = (ConnectWizardActivity) activity;
		this.activity.refreshFragment(this, ConnectWizardActivity.CONFIGURING);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_connect_wizard_configuring, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		args = getArguments();
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
		
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		textConfiguring = (TextView) view.findViewById(R.id.text_configuring);
	}
	
	private void setListeners() {
		buttonNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent data = new Intent();
				data.putExtras(args);
				activity.setResult(DeviceActivity.CONNECT_CONFIG_SUCCESSED, data);
				activity.finish();
			}
		});
	}
	
	public void setConfiguredResult(boolean isSucessed) {
		if (isSucessed) {
			buttonCancel.setVisibility(View.GONE);
			buttonNext.setVisibility(View.VISIBLE);
			buttonNext.setEnabled(true);
			progressBar.setVisibility(View.GONE);
			textConfiguring.setText("Succeed!");
		} else {
			
		}
	}
}
