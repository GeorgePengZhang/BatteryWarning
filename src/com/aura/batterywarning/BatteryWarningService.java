package com.aura.batterywarning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class BatteryWarningService extends Service {
	
	private int dialogType = Utils.DIALOG_TYPE_NONE;
	
	private Handler mHandler = new Handler();

	private AlertDialog myDialog = null;

	private Timer checkBatteryTimer;
	private CheckBatteryTask checkBatteryTask;

	class CheckBatteryTask extends TimerTask {

		@Override
		public void run() {
			Log.d("TAG", "CheckBatteryTask:"+MyApp.readRemovedBatteryFlag());
			if (!myDialog.isShowing()&& !MyApp.readRemovedBatteryFlag()) {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						showDialog(Utils.DIALOG_TYPE_0);
					}
				});
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		showDialog(Utils.DIALOG_TYPE_0);
		startTimer();
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopTimer();
	}
	
	private void startTimer() {
		stopTimer();
		
		checkBatteryTimer = new Timer();
		checkBatteryTask = new CheckBatteryTask();
		checkBatteryTimer.schedule(checkBatteryTask, Utils.BATTERY_DIALOG_DELAY, Utils.BATTERY_DIALOG_PERIOD);
	}
	
	private void stopTimer() {
		if (checkBatteryTimer != null) {
			checkBatteryTimer.cancel();
		}
		
		if (checkBatteryTask != null) {
			checkBatteryTask.cancel();
		}
	}
	
	private void showDialog(int type) {
		dialogType = type;
		
		if (myDialog == null) {
			myDialog = new AlertDialog.Builder(this).create();
			myDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		}
		
		myDialog.show();
		myDialog.setCancelable(false);
		myDialog.getWindow().setContentView(R.layout.alertdialog_layout);
		
		TextView messageText = (TextView) myDialog.findViewById(R.id.message);
		Button positiveButton = (Button) myDialog.findViewById(R.id.positiveButton);
		Button negativeButton = (Button) myDialog.findViewById(R.id.negativeButton);
		positiveButton.setOnClickListener(myClick);
		negativeButton.setOnClickListener(myClick);
		
		switch (type) {
		case Utils.DIALOG_TYPE_0:
			
			if (MyApp.isNetConnect()) {
				messageText.setText(getResources().getString(R.string.dialog3_text));
			} else {
				messageText.setText(getResources().getString(R.string.connectTheInternetTip));
			}
			
			positiveButton.setText(R.string.cancel);
			negativeButton.setText(R.string.next);
			break;
		case Utils.DIALOG_TYPE_1:
			messageText.setText(getResources().getString(R.string.dialog4_text));
			positiveButton.setText(R.string.back);
			negativeButton.setText(R.string.next);
			break;
		case Utils.DIALOG_TYPE_2:
			messageText.setText(getResources().getString(R.string.dialog5_text));
			positiveButton.setText(R.string.back);
			negativeButton.setText(R.string.confirm);	
			break;

		default:
			break;
		}
	}
	
	
	
	Click myClick = new Click();
	
	class Click implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			
			if (myDialog != null) {
				myDialog.dismiss();
			}
			
			switch (dialogType) {
			case Utils.DIALOG_TYPE_0:
				if (R.id.positiveButton == id) {
					startTimer();
				} else if (R.id.negativeButton == id) {
					if (MyApp.isNetConnect()) {
						showDialog(Utils.DIALOG_TYPE_1);
					} else {
						startTimer();
						openSettings();
					}
				}
				break;
			case Utils.DIALOG_TYPE_1:
				if (R.id.positiveButton == id) {
					showDialog(Utils.DIALOG_TYPE_0);
				} else if (R.id.negativeButton == id) {
					showDialog(Utils.DIALOG_TYPE_2);
				}
				break;
			case Utils.DIALOG_TYPE_2:
				if (R.id.positiveButton == id) {
					showDialog(Utils.DIALOG_TYPE_1);
				} else if (R.id.negativeButton == id) {
					sendDialogResult();
				}
				break;

			default:
				break;
			}
		}
	}

	private void openSettings() {
		Intent intent = new Intent();
		ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(cn);
		startActivity(intent);
	}

	private void sendDialogResult() {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
//		String deviceId2 = Secure.getString(getContentResolver(), Secure.ANDROID_ID);// 序列号
		String deviceId2 = getRKSerial();
		System.out.println("设备的序列号为：" + deviceId2);

		String deviceModel = android.os.Build.MODEL;// 设置型号
		System.out.println("设备的型号为：" + deviceModel);

		params.addBodyParameter("Model", deviceModel);
		params.addBodyParameter("SerialNum", deviceId2);
		params.addBodyParameter("pickStatus", "1"); // 1：已经摘取  0：未摘取
		startTimer();
		http.send(HttpRequest.HttpMethod.POST, Utils.BATTERY_URL,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException error, String msg) {
						Log.d("TAG", "onFailure:"+msg);
						Toast.makeText(BatteryWarningService.this, getString(R.string.falied), Toast.LENGTH_SHORT).show();
						startTimer();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						Log.d("TAG", "onSuccess");
						MyApp.saveRemovedBatteryFlag(true);
						Toast.makeText(BatteryWarningService.this,getString(R.string.success), Toast.LENGTH_SHORT).show();
						stopSelf();
					}
				});
	}
	
	private static String getRKSerial() {
    	
        File sectorName = new File("/proc/rknand_sector");
        char buf[] = new char[32];
        String sn = "unknown";

        if (sectorName.exists()) {
            FileReader fread = null;
            BufferedReader buffer = null;
            try {
                fread = new FileReader(sectorName);
                buffer = new BufferedReader(fread);
                if (buffer.read(buf, 0, 32) == 32) {
                    int len = buf[0] | (buf[1] << 8);
                    if (len > 0)
                        sn = new String(buf, 2, len);
                    Log.d("Build", "len=" + len + ", sn=" + sn);
                }

            } catch (Exception e) {
            } finally {
            	if ( buffer != null ) {
            		try {
						buffer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
            		buffer = null;
            	}
            	
            	if ( fread != null ) {
            		try {
						fread.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
            		fread = null;
            	}
            
            }
        }
		
        return sn.substring(sn.indexOf(':')+1);
        
    }

}
