package com.aura.batterywarning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	
	private static boolean isBootCompleted = false;

	@Override
	public void onReceive(Context context, Intent intent) {
			
		String action = intent.getAction();
		boolean readRemovedBatteryFlag = MyApp.readRemovedBatteryFlag();
		
		String msg = "readRemovedBatteryFlag:"+readRemovedBatteryFlag+",action:"+action+",time:"+Utils.getCurTime()+",launcher:"+Utils.isClsRunning()+",isBootCompleted:"+isBootCompleted+"\n";
		Log.d("TAG", msg);
		Utils.writeLogToSdcard(msg);
		
		if(!readRemovedBatteryFlag) {
			if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
				Intent i = new Intent(context,BatteryWarningService.class);
				context.startService(i);
				
				isBootCompleted = true;
			} else if (isBootCompleted && "android.intent.action.USER_PRESENT".equals(action)) {
				Intent i = new Intent(context,BatteryWarningService.class);
				context.startService(i);
			} else if (isBootCompleted && "android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
				ConnectivityManager cmanger = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        	NetworkInfo netInfo = cmanger.getActiveNetworkInfo();
	        	if(netInfo != null) {
	        		if(netInfo.getType() == ConnectivityManager.TYPE_WIFI && netInfo.isConnected()) {
	        			Intent i = new Intent(context,BatteryWarningService.class);
						context.startService(i);
	        		}
	        	}
			}
		}
	}

}
