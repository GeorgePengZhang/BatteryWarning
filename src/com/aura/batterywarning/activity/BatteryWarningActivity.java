package com.aura.batterywarning.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
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
	private EditText name;
	private String userName = "";
	private SharedPreferences preferences;
	
	private static final String PREFERENCES_STATE_OPERATE = "state_operate";
	private static final String PREFERENCES_TYPE = "type";
	private static final String PREFERENCES_NAME = "name";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		
		int type = Utils.DIALOG_TYPE_0;
		
		preferences = getSharedPreferences(PREFERENCES_STATE_OPERATE, Context.MODE_PRIVATE);
		if (preferences != null) {
			type = preferences.getInt(PREFERENCES_TYPE, Utils.DIALOG_TYPE_0);
		}
		 
		setContentMsg(type);
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
			if (name != null) {
				userName = name.getText().toString().trim();
			}
			
			setContentMsg(Utils.DIALOG_TYPE_2);
			if (preferences != null) {
				Editor edit = preferences.edit();
				edit.putInt(PREFERENCES_TYPE, Utils.DIALOG_TYPE_2);
				edit.putString(PREFERENCES_NAME, userName);
				edit.commit();
			}
			
			break;
		case Utils.DIALOG_TYPE_2:
			if (MyApp.isNetConnect()) {
				setContentMsg(Utils.DIALOG_TYPE_3);
			} else {
				BatteryWarningService.openSettings(mContext);
				AppManager.getAppManager().finishActivity(this);
			}
			break;
		case Utils.DIALOG_TYPE_3:
			setContentMsg(Utils.DIALOG_TYPE_4);
			break;
		case Utils.DIALOG_TYPE_4:
			setContentMsg(Utils.DIALOG_TYPE_5);
			break;
		case Utils.DIALOG_TYPE_5:
			setContentMsg(Utils.DIALOG_TYPE_6);
			break;
		case Utils.DIALOG_TYPE_6:
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
			case Utils.DIALOG_TYPE_4:
			case Utils.DIALOG_TYPE_5:
			case Utils.DIALOG_TYPE_6:
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
		case Utils.DIALOG_TYPE_1:
			setContentLayout(R.layout.userinfo_layout);
			name = (EditText) this.findViewById(R.id.id_name);
			name.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					setOKEnable(count > 0);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
				}
				
				@Override
				public void afterTextChanged(Editable s) {
				}
			});
			break;

		default:
			setContentLayout(R.layout.notify_layout);
			break;
		}
		
		txt = (TextView)this.findViewById(R.id.notify);
		
		switch (type) {
		case Utils.DIALOG_TYPE_0:
			if (MyApp.isNetConnect()) {
				showUserinfo();
			} else {
				showConnectWifi();
			}
			break;
		case Utils.DIALOG_TYPE_1:
			showUserinfoSure();
			setOKEnable(false);
			break;
			
		case Utils.DIALOG_TYPE_2:
			if (MyApp.isNetConnect()) {
				showBatterywarning();
			} else {
				showConnectWifi();
			}
			break;
			
		case Utils.DIALOG_TYPE_3:
			showInstructions();
			break;
			
		case Utils.DIALOG_TYPE_4:
			showBatteryIsRemoved();
			break;
			
		case Utils.DIALOG_TYPE_5:
			showBatteryRemove();
			break;
			
		case Utils.DIALOG_TYPE_6:
			showLegalNotices();
			break;

		default:
			break;
		}
		
	}


	//*********************************************************************************
	private void showUserinfoSure() {
		txt.setText(R.string.userinfo_sure_hint);
		setStateGone();
		setOKText(getString(R.string.next));
		setCancelText(getString(R.string.cancel));
	}

	
	private void showUserinfo() {
		txt.setText(R.string.userinfo_hint);
		setStateGone();
		setOKText(getString(R.string.yes));
		setCancelText(getString(R.string.no));
	}
	
	
	private void showLegalNotices() {
		txt.setText(R.string.legal_notices);
		setStateCBText(getString(R.string.state_step_c));
		setOKText(getString(R.string.agree));
		setCancelText(getString(R.string.reject));
	}


	private void showBatteryRemove() {
		txt.setText(R.string.battery_remove_hint);
		setStateGone();
		setOKText(getString(R.string.yes));
		setCancelText(getString(R.string.not_yet));
	}

	private void showBatteryIsRemoved() {
		txt.setText(R.string.battery_is_removed_hint);
		setStateGone();
		setOKText(getString(R.string.next));
		setCancelText(getString(R.string.cancel));
	}

	private void showInstructions() {
		txt.setText(getString(R.string.instructions1_hint)+"\n\n"+getString(R.string.instructions2_hint));
		setStateCBText(getString(R.string.state_step_b));
		setOKText(getString(R.string.remove_now));
		setCancelText(getString(R.string.remove_later));
	}


	private void showConnectWifi() {
		txt.setText(getString(R.string.connect_wifi_hint));
		setStateGone();
		setOKText(getString(R.string.next));
		setCancelText(getString(R.string.cancel));
	}
	
	private void showBatterywarning() {
		txt.setText(getString(R.string.batterywarning_hint));
    	setStateTVText(getString(R.string.state_step_a));
    	setOKText(getString(R.string.read_now));
    	setCancelText(getString(R.string.read_later));
	}
	//*********************************************************************************
	
	
	private void sendDialogResult(final int pickStatus) {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		String deviceId2 = Utils.getModelNumber();
		Log.d("TAG","sendDialogResult deviceId2:" + deviceId2);

		if (preferences != null) {
			userName = preferences.getString(PREFERENCES_NAME, "");
		}
		
		String deviceModel = android.os.Build.MODEL;// 设置型号
		Log.d("TAG","sendDialogResult deviceModel:" + deviceModel+",name:"+userName);

		params.addBodyParameter("Model", deviceModel);
		params.addBodyParameter("SerialNum", deviceId2);
		params.addBodyParameter("pickStatus", pickStatus+""); // 1：已经摘取  0：未摘取
		params.addBodyParameter("user_name", userName);
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
				if (!Utils.DEBUG && pickStatus == 1) {
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
