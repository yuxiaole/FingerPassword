package com.zl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ScreenObserver{
	private static String TAG = "ScreenObserver"; 
	private Context context;
	private ScreenStateListener mScreenStateListener;
	private ScreenBroadcastReceiver mScreenBroadcastReceiver;
	
	public ScreenObserver(Context context){
		this.context=context;
		mScreenBroadcastReceiver=new ScreenBroadcastReceiver();
	}
	
	private class ScreenBroadcastReceiver extends BroadcastReceiver{
		private String action=null;
		@Override
		public void onReceive(Context context, Intent intent) {
			action=intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				mScreenStateListener.onScreenOn();
			}else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				mScreenStateListener.onScreenOff();
			}
		}
	}
	
	public void requestScreenStateUpdate(ScreenStateListener listener){
		mScreenStateListener=listener;
		startScreenBroadcastReceiver();
	}
	
	public void stopScreenStateUpdate(){
		context.unregisterReceiver(mScreenBroadcastReceiver);
	}
	
	public void startScreenBroadcastReceiver(){
		IntentFilter filter=new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		context.registerReceiver(mScreenBroadcastReceiver, filter);
	}
	
	
	public interface ScreenStateListener{
		public void onScreenOn();
		public void onScreenOff();
	}
}
