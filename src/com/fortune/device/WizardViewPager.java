package com.fortune.device;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class WizardViewPager extends ViewPager {
	private boolean isSwiped = true;

	public WizardViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public WizardViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
	
	public void setSwiped(boolean isSwiped) {
		this.isSwiped = isSwiped;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if (isSwiped)
			return super.onInterceptTouchEvent(arg0);
		
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if (isSwiped)
			return super.onTouchEvent(arg0);
		
		return false;
	}
	
}
