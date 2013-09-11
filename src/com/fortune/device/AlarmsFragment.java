package com.fortune.device;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.TimePicker;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class AlarmsFragment extends SherlockFragment{
	public static AlarmsFragment newInstance() {
		AlarmsFragment af = new AlarmsFragment();
		af.setHasOptionsMenu(true);

//		Bundle args = new Bundle();
//		args.putBoolean("isWPD", isWPD);
//		args.putSerializable("listFileStatus", listFileStatus);
//
//		pf.setArguments(args);

		return af;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_alarms, container, false);
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		
		menu.add(0, 0, 0, "Add")
			.setIcon(R.drawable.ic_menu_add)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 0:
			addAlarm();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void addAlarm() {
		View view = View.inflate(getSherlockActivity(),
				R.layout.dialog_add_alarm, null);
		TimePicker timerPicker = (TimePicker) view.findViewById(R.id.timePicker);
		EditText editSubject = (EditText) view.findViewById(R.id.edit_subject);
		

		/* show alertdialog */
		AlertDialog.Builder alert = new AlertDialog.Builder(getSherlockActivity());
		alert.setTheme(R.style.Holo_Theme_Dialog_Alert_Light);
		alert.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
		alert.setTitle("Alarm");
		alert.setView(view);
		

		alert.setPositiveButton("Add",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						
					}
				});	

		alert.setNegativeButton(
				"Cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				});

		alert.show();
	}
}
