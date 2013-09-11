package com.fortune.device;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.fortune.wifi.APStatus;
import com.fortune.wifi.WifiConnection;

public class ConnectWizardConnectingFragment extends SherlockFragment {
	ConnectWizardActivity activity;
	APStatus apStatus;
	
	Button buttonCancel;
	Button buttonNext;
	ProgressBar progressBar;
	TextView textConnecting;
	TextView textRetry;
	public static ConnectWizardConnectingFragment newInstance(APStatus apStatus) {
		ConnectWizardConnectingFragment cwcf = new ConnectWizardConnectingFragment();

		Bundle args = new Bundle();
		args.putSerializable("APStatus", apStatus);

		cwcf.setArguments(args);

		return cwcf;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		this.activity = (ConnectWizardActivity) activity;
		this.activity.refreshFragment(this, 1);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_connect_wizard_connecting, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		/* get arguments */
		Bundle args = getArguments();
		apStatus = (APStatus) args.getSerializable("APStatus");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		findViews();
	}
	
	private void findViews() {
		View view = getView();
		
		buttonCancel = (Button) view.findViewById(R.id.button_cancel);
		buttonNext = (Button) view.findViewById(R.id.button_next);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		textConnecting = (TextView) view.findViewById(R.id.text_connecting);
		textRetry = (TextView) view.findViewById(R.id.text_retry);
	}
	
	public void setFailedLayout() {
		buttonNext.setText("Retry");
		buttonNext.setEnabled(true);
		buttonCancel.setEnabled(true);
		progressBar.setVisibility(View.GONE);
		textConnecting.setVisibility(View.GONE);
		textRetry.setText("Do you want to connect to "+ apStatus.getSSID() +" again?");
		textRetry.setVisibility(View.VISIBLE);
		
		buttonNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				activity.connect(MainActivity.DEVICE_PASSWORD);
				setConnectingLayout();
			}
		});
		
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activity.finish();
			}
		});
	}
	
	public void setConnectingLayout() {
		buttonNext.setText("Next");
		buttonNext.setEnabled(false);
		buttonCancel.setEnabled(false);
		progressBar.setVisibility(View.VISIBLE);
		textConnecting.setVisibility(View.VISIBLE);
		textRetry.setVisibility(View.GONE);
	}
}
