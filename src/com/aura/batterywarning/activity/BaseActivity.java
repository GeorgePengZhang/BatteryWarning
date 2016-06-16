package com.aura.batterywarning.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aura.batterywarning.R;

public abstract class BaseActivity extends Activity {

	
	private ScrollView content;
	private Button btn_ok;
	private Button btn_cancel;
	public CheckBox state_cb;
	public TextView state_tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.base_dialog);
		setFinishOnTouchOutside(false);
        getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_alert);
		
        
        content = (ScrollView) findViewById(R.id.id_scrollview);
        state_cb = (CheckBox) findViewById(R.id.notify_youwill_cb);
        state_tv = (TextView) findViewById(R.id.notify_youwill_tv);
        btn_ok = (Button) findViewById(R.id.button_ok);
		btn_cancel = (Button) findViewById(R.id.button_cancel);
		
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OnOK();
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OnCancel();
			}
		});
		state_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (state_cb.getVisibility() == View.VISIBLE) {
					btn_ok.setEnabled(isChecked);
				}
			}
		});
		
		AppManager.getAppManager().addActivity(this);
	}

	public void setContentLayout(int resid) {
		View.inflate(this, resid, content);
	}
	
	public void setOKText(String str) {
		btn_ok.setText(str);
		btn_ok.setEnabled(true);
		state_cb.setChecked(false);
		if (state_cb.getVisibility() == View.VISIBLE) {
			btn_ok.setEnabled(state_cb.isChecked());
		}
	}
	
	public void setCancelText(String str) {
		btn_cancel.setText(str);
	}
	
	public void setStateTVText(String msg) {
		state_tv.setVisibility(View.VISIBLE);
		state_cb.setVisibility(View.GONE);
		state_tv.setText(msg);
	}
	
	public void setStateCBText(String msg) {
		state_cb.setVisibility(View.VISIBLE);
		state_tv.setVisibility(View.GONE);
		state_cb.setText(msg);
	}
	
	public void setStateGone() {
		state_cb.setVisibility(View.GONE);
		state_tv.setVisibility(View.GONE);
	}
	
	protected abstract void OnOK();
	protected abstract void OnCancel();
}
