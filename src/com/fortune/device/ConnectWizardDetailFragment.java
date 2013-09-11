package com.fortune.device;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.fortune.wifi.APStatus;
import com.fortune.wifi.WifiConnection;

public class ConnectWizardDetailFragment extends SherlockFragment {
	ConnectWizardActivity activity;
	APStatus apStatus;
	
	Button buttonCancel;
	Button buttonNext;
	TextView textSSID;
	TextView textMac;
	EditText editPassword;
	CheckBox cbxShow;
	
	public static ConnectWizardDetailFragment newInstance(APStatus apStatus) {
		ConnectWizardDetailFragment cwdf = new ConnectWizardDetailFragment();

		Bundle args = new Bundle();
		args.putSerializable("APStatus", apStatus);
		
		cwdf.setArguments(args);

		return cwdf;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		this.activity = (ConnectWizardActivity) activity;
		this.activity.refreshFragment(this, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_connect_wizard_detail, container, false);
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
		
		textSSID.setText(apStatus.getSSID());
		textMac.setText(apStatus.getBSSID());
		editPassword.setText(MainActivity.DEVICE_PASSWORD);
		
		setListeners();
	}
	
	private void findViews() {
		View view = getView();
		
		buttonCancel = (Button) view.findViewById(R.id.button_cancel);
		buttonNext = (Button) view.findViewById(R.id.button_next);
		
		textSSID = (TextView) view.findViewById(R.id.text_ssid);
		textMac = (TextView) view.findViewById(R.id.text_mac);
		editPassword = (EditText) view.findViewById(R.id.edit_password);
		
		cbxShow = (CheckBox) view.findViewById(R.id.cbx_show);
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
				activity.connect(editPassword.getText().toString());
				activity.setCurrentItem(1);
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
}
