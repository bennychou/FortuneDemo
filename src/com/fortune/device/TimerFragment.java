package com.fortune.device;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.NumberPicker;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.TimePicker;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class TimerFragment extends SherlockFragment {
	
	NumberPicker hourPicker;
	NumberPicker minutePicker;
	NumberPicker secondPicker;
	
	public static TimerFragment newInstance() {
		TimerFragment tf = new TimerFragment();

//		Bundle args = new Bundle();
//		args.putBoolean("isWPD", isWPD);
//		args.putSerializable("listFileStatus", listFileStatus);
//
//		pf.setArguments(args);

		return tf;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_timer, container, false);
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
		
		hourPicker = (NumberPicker) getView().findViewById(R.id.number_picker_hour);
		minutePicker = (NumberPicker) getView().findViewById(R.id.number_picker_minute);
		secondPicker = (NumberPicker) getView().findViewById(R.id.number_picker_second);
		
		hourPicker.setMaxValue(12);
		hourPicker.setMinValue(0);
		
		minutePicker.setMaxValue(59);
		minutePicker.setMinValue(0);
		
		secondPicker.setMaxValue(59);
		secondPicker.setMinValue(0);
	}
}
