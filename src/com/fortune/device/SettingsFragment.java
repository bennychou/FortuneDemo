package com.fortune.device;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class SettingsFragment extends SherlockFragment {

	public static SettingsFragment newInstance() {
		SettingsFragment sf = new SettingsFragment();

//		Bundle args = new Bundle();
//		args.putBoolean("isWPD", isWPD);
//		args.putSerializable("listFileStatus", listFileStatus);
//
//		pf.setArguments(args);

		return sf;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_settings, container, false);
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
	}
}
