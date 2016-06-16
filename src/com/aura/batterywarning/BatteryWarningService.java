package com.aura.batterywarning;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.aura.batterywarning.activity.AppManager;
import com.aura.batterywarning.activity.BatteryWarningActivity;

public class BatteryWarningService extends Service {

	private Timer checkBatteryTimer;
	private CheckBatteryTask checkBatteryTask;

	class CheckBatteryTask extends TimerTask {

		@Override
		public void run() {
			openWarningDialog();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Utils.BATTERY_CHECKING_TIMER);
		registerReceiver(mBroadcastReceiver, intentFilter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		openWarningDialog();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopTimer();
		unregisterReceiver(mBroadcastReceiver);
	}

	private void startTimer() {
		stopTimer();

		checkBatteryTimer = new Timer();
		checkBatteryTask = new CheckBatteryTask();
		checkBatteryTimer.schedule(checkBatteryTask, Utils.BATTERY_DIALOG_DELAY, Utils.BATTERY_DIALOG_PERIOD);
	}

	private void stopTimer() {
		if (checkBatteryTask != null) {
			checkBatteryTask.cancel();
		}

		if (checkBatteryTimer != null) {
			checkBatteryTimer.cancel();
			checkBatteryTimer.purge();
		}
	}
	
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d("TAG", "BATTERY_CHECKING_TIMER:" + action);
			if (Utils.BATTERY_CHECKING_TIMER.equals(action)) {
				startTimer();
			}			
		}
	};

	public static void openSettings(Context contxt) {
		Intent intent = new Intent();
		ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(cn);
		contxt.startActivity(intent);
	}

	public static void startCheckTimer(Context context) {
		Intent intent = new Intent(Utils.BATTERY_CHECKING_TIMER);
		context.getApplicationContext().sendBroadcast(intent);
	}

	public boolean isTopActivity(String pkg, String cls) {
		boolean ret = false;
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE) ;
		List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1) ;
		if(runningTaskInfos != null && runningTaskInfos.size() > 0) {
			ComponentName componentName = runningTaskInfos.get(0).topActivity;
			if (componentName != null && componentName.getPackageName().equals(pkg) && componentName.getClassName().equals(cls)) {
				ret = true;
			}
		}
		
		return ret;
	}

	private void openWarningDialog() {
		boolean isTop = isTopActivity(getPackageName(), BatteryWarningActivity.class.getName());
		Log.d("TAG", "CheckBatteryTask:" + MyApp.readRemovedBatteryFlag()+",isTop:"+isTop);
		if (!isTop && !MyApp.readRemovedBatteryFlag()) {
			Intent intent = new Intent(getApplicationContext(), BatteryWarningActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else if (MyApp.readRemovedBatteryFlag()) {
			stopSelf();
	        AppManager.getAppManager().finishAllActivity();
		}
	}
}
