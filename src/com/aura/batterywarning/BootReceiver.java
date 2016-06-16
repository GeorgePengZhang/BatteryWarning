package com.aura.batterywarning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
			
		String action = intent.getAction();
		boolean readRemovedBatteryFlag = MyApp.readRemovedBatteryFlag();
		Log.d("TAG", "readRemovedBatteryFlag:"+readRemovedBatteryFlag+",action:"+action);
		if(!readRemovedBatteryFlag){
			if ("android.intent.action.BOOT_COMPLETED".equals(action) || "android.intent.action.USER_PRESENT".equals(action)) {
				Intent i = new Intent(context,BatteryWarningService.class);
				context.startService(i);
			}else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
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
