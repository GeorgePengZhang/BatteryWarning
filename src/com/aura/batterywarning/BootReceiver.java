package com.aura.batterywarning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
			
		String action = intent.getAction();
		boolean readRemovedBatteryFlag = MyApp.readRemovedBatteryFlag();
		
		if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
			
			if(!readRemovedBatteryFlag){
				Intent i = new Intent(context,BatteryWarningService.class);
				context.startService(i);
			}
		}else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
			State wifiState = null;
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			if (wifiState != null && State.CONNECTED != wifiState) {
				
				//网络断开
				
			}else if (wifiState != null && State.CONNECTED == wifiState) {
				if(!readRemovedBatteryFlag){
					Intent i = new Intent(context,BatteryWarningService.class);
					context.startService(i);
				}
			}
		}
		
		
		
//		if(!BatteryWarningService.removedBatteryFlag){
//			Intent i = new Intent(context,BatteryWarningService.class);
//			context.startService(i);
//		}
		
	}

}
