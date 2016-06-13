package com.aura.batterywarning;


import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class MyApp extends Application{


	public static MyApp mApp;
	public static Context mContext;
	
	/**保存是否开启过服务 */
	private final static String REMOVED_BATTERY = "removedBattery";
		
	@Override
	public void onCreate() {
		super.onCreate();

		mApp = this;
		mContext = getApplicationContext();
		
	}
	
	/**
	 * 读取电池记录结果
	 * @return
	 */	
	public static boolean readRemovedBatteryFlag() {
		SharedPreferences spf = MyApp.mApp.getSharedPreferences(REMOVED_BATTERY, Context.MODE_PRIVATE);
		return spf.getBoolean("removedBattery", false);
	}
	
	/**
	 * 记录电池移除结果
	 * @return
	 */	
	public static void saveRemovedBatteryFlag(Boolean removedBatteryFlag){
		SharedPreferences spf = MyApp.mApp.getSharedPreferences(REMOVED_BATTERY, Context.MODE_PRIVATE);
		Editor editor = spf.edit();
		editor.putBoolean("removedBattery", removedBatteryFlag);
		editor.commit();
	}


	/**
	 * 网络是否连接 add by donsen
	 * 
	 * @param mContext
	 */
	public static boolean isNetConnect() {
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	
	/**
	* �?��app是否启动
	*/
	public static boolean isClsRunning() {
		ActivityManager am = (ActivityManager)MyApp.mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		boolean isAppRunning = false;
		String MY_PKG_NAME = "com.auratech.order.activity";
		for (RunningTaskInfo info : list) {
		    if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
		        isAppRunning = true;
		        break;
		    }
		}
			     
        return isAppRunning;
    }	
	
	
}
