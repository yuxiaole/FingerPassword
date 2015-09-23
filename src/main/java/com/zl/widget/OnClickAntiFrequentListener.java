package com.zl.widget;

import android.view.View;
import android.view.View.OnClickListener;



public abstract class OnClickAntiFrequentListener implements OnClickListener {

	/**
	 * milliseconds
	 */
	public static long lastClickTime = 0;
	
	/**
	 * milliseconds
	 */
	public static final int minInterval = 500;
	
	@Override
	public void onClick(View v) {
		long time = System.currentTimeMillis();
		if (time - lastClickTime > minInterval ) {
			lastClickTime = time;
			onClickAntiFrequent(v);
		}
	}
	
	public abstract void onClickAntiFrequent(View v);

}
