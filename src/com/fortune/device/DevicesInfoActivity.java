package com.fortune.device;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TabPageIndicator;

public class DevicesInfoActivity extends SherlockFragmentActivity {
	ViewPager pager;
	TabPageIndicator indicator;
	FragmentPagerAdapter adapter;
	
	private static final String[] CONTENT = new String[] {"Info.", "Alarms", "Timer"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_devices_info);
		
		/* set action bar */
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		findViews();
		
		adapter = new ClusterFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
	}
	
	private void findViews() {
		pager = (ViewPager) findViewById(R.id.viewPager);
		indicator = (TabPageIndicator) findViewById(R.id.indicator);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class ClusterFragmentPagerAdapter extends FragmentPagerAdapter {
		public ClusterFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return InfoFragment.newInstance();
			} else if (position == 1) {
				return AlarmsFragment.newInstance();
			} else {
				return TimerFragment.newInstance();
			}
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return POSITION_NONE;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position];
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
	}

}
