package com.aura.batterywarning.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.aura.batterywarning.BatteryWarningService;
import com.aura.batterywarning.MyApp;
import com.aura.batterywarning.R;
import com.aura.batterywarning.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class BatteryWarningActivity extends BaseActivity {

	private Context mContext;
	private TextView txt;
	private int mType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentLayout(R.layout.notify_layout);
		
		txt = (TextView)this.findViewById(R.id.notify);
        
		setContentMsg(Utils.DIALOG_TYPE_0);
	}
	
	
	@Override
	protected void OnOK() {
		
		switch (mType) {
		case Utils.DIALOG_TYPE_0:
			if (MyApp.isNetConnect()) {
				setContentMsg(Utils.DIALOG_TYPE_1);
			} else {
				BatteryWarningService.openSettings(mContext);
				AppManager.getAppManager().finishActivity(this);
			}
			break;
		case Utils.DIALOG_TYPE_1:
			setContentMsg(Utils.DIALOG_TYPE_2);
			break;
		case Utils.DIALOG_TYPE_2:
			setContentMsg(Utils.DIALOG_TYPE_3);
			break;
		case Utils.DIALOG_TYPE_3:
			sendDialogResult(1);
			AppManager.getAppManager().finishActivity(this);
			break;

		default:
			break;
		}
		
	}

	@Override
	protected void OnCancel() {
		
		if (MyApp.isNetConnect()) {
			switch (mType) {
			case Utils.DIALOG_TYPE_0:
			case Utils.DIALOG_TYPE_1:
			case Utils.DIALOG_TYPE_2:
			case Utils.DIALOG_TYPE_3:
				sendDialogResult(0);
				break;
			default:
				break;
			}
		}
		
		AppManager.getAppManager().finishActivity(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BatteryWarningService.startCheckTimer(mContext);
	}
	
	
	public void setContentMsg(int type) {
		mType = type;
		
		switch (type) {
		case Utils.DIALOG_TYPE_0:
			if (MyApp.isNetConnect()) {
	        	txt.setText(getString(R.string.batterywarning_hint));
	        	setStateTVText(getString(R.string.state_step_a));
	        	setOKText(getString(R.string.read_now));
	        	setCancelText(getString(R.string.read_later));
			} else {
				txt.setText(getString(R.string.connect_wifi_hint));
				setStateGone();
				setOKText(getString(R.string.next));
	        	setCancelText(getString(R.string.cancel));
			}
			break;
		case Utils.DIALOG_TYPE_1:
			txt.setText(getString(R.string.instructions1_hint)+"\n\n"+getString(R.string.instructions2_hint));
	    	setStateCBText(getString(R.string.state_step_b));
	    	setOKText(getString(R.string.remove_now));
	    	setCancelText(getString(R.string.remove_later));
			break;
			
		case Utils.DIALOG_TYPE_2:
			txt.setText(R.string.battery_remove_hint);
	    	setStateGone();
	    	setOKText(getString(R.string.yes));
	    	setCancelText(getString(R.string.not_yet));
			break;
			
		case Utils.DIALOG_TYPE_3:
			txt.setText(R.string.legal_notices);
	    	setStateCBText(getString(R.string.state_step_c));
	    	setOKText(getString(R.string.agree));
	    	setCancelText(getString(R.string.reject));
			break;

		default:
			break;
		}
	}
	
	private void sendDialogResult(final int pickStatus) {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		String deviceId2 = Utils.getModelNumber();
		Log.d("TAG","sendDialogResult deviceId2:" + deviceId2);

		String deviceModel = android.os.Build.MODEL;// 设置型号
		Log.d("TAG","sendDialogResult deviceModel:" + deviceModel);

		params.addBodyParameter("Model", deviceModel);
		params.addBodyParameter("SerialNum", deviceId2);
		params.addBodyParameter("pickStatus", pickStatus+""); // 1：已经摘取  0：未摘取
		http.send(HttpRequest.HttpMethod.POST, Utils.BATTERY_URL_USEING, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException error, String msg) {
				Log.d("TAG", "onFailure:"+msg);
				if (pickStatus == 1) {
					Toast.makeText(BatteryWarningActivity.this, getString(R.string.falied), Toast.LENGTH_SHORT).show();
				}
				
				BatteryWarningService.startCheckTimer(mContext);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				if (!Utils.DEBUG) {
					MyApp.saveRemovedBatteryFlag(true);
					
					PackageManager pm = getPackageManager();
			        pm.setApplicationEnabledSetting(getPackageName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
				}
				
				if (pickStatus == 1) {
					Toast.makeText(BatteryWarningActivity.this,getString(R.string.success), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
