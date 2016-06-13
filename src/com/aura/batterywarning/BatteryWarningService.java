package com.aura.batterywarning;

import java.util.Timer;
import java.util.TimerTask;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BatteryWarningService extends Service{
	
	private AlertDialog myDialog = null;
	private AlertDialog myDialog2 = null;
	private AlertDialog myDialog3 = null;
	private AlertDialog myDialog4 = null;
	private AlertDialog myDialog5 = null;
	private AlertDialog myDialog6 = null;
	
	
	private long delay;
	private long period;
	
	private  Timer checkBatteryTimer;
	private CheckBatteryTask checkBatteryTask;
	
//	public static boolean removedBatteryFlag = false ;
	
	class CheckBatteryTask extends TimerTask{

		@Override
		public void run() {
			
			Looper.prepare();
		
			warningDialog();
			
			Looper.loop();
			
		}
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}  
	
	 @Override  
	    public void onCreate() {  
	        super.onCreate();  
	    }  
	  
	    @Override  
	    public void onStart(Intent intent, int startId) {  
	        super.onStart(intent, startId);  
	    }  
	  
	  
	    @Override  
	    public int onStartCommand(Intent intent, int flags, int startId) {  
	        
//	        Toast.makeText(BatteryWarningService.this, "服务开启了" , 1).show();
//	        
	        warningDialog();
	        
	        
	        return START_STICKY;
	    }
	    
	    private void warningDialog5(){
	
	    }
	    
	    private void warningDialog4(){
    	myDialog4 = new AlertDialog.Builder(BatteryWarningService.this).create();  
			
    	myDialog4.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);			
    	myDialog4.show();  
            
    	myDialog4.getWindow().setContentView(R.layout.alertdialog_layout5);  
    	myDialog4.setCancelable(false);

	    	Button btn = (Button) myDialog4.getWindow().findViewById(R.id.positiveButton);
	    	btn.setText("back"); 
	    	
	    	myDialog4.getWindow()  
                .findViewById(R.id.positiveButton)   //上一步()
                .setOnClickListener(new View.OnClickListener() {  
                @Override  
                public void onClick(View v) {  
                	myDialog4.dismiss(); 
                	warningDialog3();

                }  
            });  
            
	    	Button btn2 = (Button) myDialog4.getWindow().findViewById(R.id.negativeButton);
	    	btn2.setText("confirm"); 
	    	
	    	myDialog4.getWindow()  
            .findViewById(R.id.negativeButton)  //	Confirm（点确定时，提交数据到后台）
            .setOnClickListener(new View.OnClickListener() {  
            	@Override  
            	public void onClick(View v) {  
            		myDialog4.dismiss();  
            		
            		 HttpUtils http = new HttpUtils();
                     
                   RequestParams params = new RequestParams();
                   
                   String deviceId2 = Secure.getString(getContentResolver(), Secure.ANDROID_ID);//序列号
                   System.out.println("设备的序列号为：" + deviceId2);
                   
//                   TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
//                   String deviceId = TelephonyMgr.getDeviceId(); 
//                   System.out.println("设备的序列号为：" + deviceId);
//                   Toast.makeText(BatteryWarningService.this, "设备的序列号为：" + deviceId, 1).show();
                                                    
                   String deviceModel = android.os.Build.MODEL;//设置型号
                   System.out.println("设备的型号为：" + deviceModel);
//                   Toast.makeText(BatteryWarningService.this, "设备的型号为：" + deviceModel, 1).show();
                   
                   params.addBodyParameter("Model", deviceModel);
                   params.addBodyParameter("SerialNum", deviceId2);
                   params.addBodyParameter("pickStatus", "1");  //1：已经摘取 0：未摘取
                   
                   
//                   http://192.168.1.30:45231/webStatisticsSer/
//                   http.send(HttpRequest.HttpMethod.POST, "ip:45231/webStatisticsSer/web/addDeviceServlet", params, callBack)
                   http.send(HttpRequest.HttpMethod.POST, "http://192.168.1.30:45231/webStatisticsSer/web/addDeviceServlet", params, new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException error, String msg) {
							Toast.makeText(BatteryWarningService.this, "The operation failure" , 1).show();
							
							//上传失败时，开启定时器
							delay = 15*60*1000;
			        		period = 15*60*1000; 
//			        		period = 20*1000;
			        		checkBatteryTimer = new Timer();
			        		checkBatteryTask = new CheckBatteryTask();           		
			        		checkBatteryTimer.schedule(checkBatteryTask, delay, period);//每隔15分钟
							
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							
//							removedBatteryFlag = true ;
							MyApp.saveRemovedBatteryFlag(true);
							
						
							Toast.makeText(BatteryWarningService.this, "Operation is successful" , 1).show();
							
							//上传成功后看是否需要将服务关闭
							
						}
					});
            	}  
            });
	    }
	    
	    private void warningDialog3(){
	    	myDialog3 = new AlertDialog.Builder(BatteryWarningService.this).create();  
			
	    	myDialog3.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);			
	    	myDialog3.show();  
            
	    	myDialog3.getWindow().setContentView(R.layout.alertdialog_layout4);  
	    	myDialog3.setCancelable(false);
	    	
	    	Button btn = (Button) myDialog3.getWindow().findViewById(R.id.positiveButton);
	    	btn.setText("back"); 
	    	
	    	myDialog3.getWindow()  
                .findViewById(R.id.positiveButton)   
                .setOnClickListener(new View.OnClickListener() {  
                @Override  
                public void onClick(View v) {  
                	myDialog3.dismiss();            
                	warningDialog2();
                }  
            });  
            
	    	myDialog3.getWindow()  
            .findViewById(R.id.negativeButton)  
            .setOnClickListener(new View.OnClickListener() {  
            	@Override  
            	public void onClick(View v) {  
            		myDialog3.dismiss();    
            		warningDialog4();
            	}  
            });
	    }
	    
	    private void warningDialog2(){
	    	myDialog2 = new AlertDialog.Builder(BatteryWarningService.this).create();  
			
	    	myDialog2.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);			
	    	myDialog2.show();  
            
	    	myDialog2.getWindow().setContentView(R.layout.alertdialog_layout);  
	    	myDialog2.setCancelable(false);
	    	TextView tv = (TextView) myDialog2.getWindow().findViewById(R.id.message);
	    	tv.setText("In order to ensure your safety,we would like to advise you to remove the battery from your Telpad dock. The Telpad will continue to operate via a plugged power adapter.");
	    	
	    	Button btn = (Button) myDialog2.getWindow().findViewById(R.id.positiveButton);
	    	btn.setText("back");   //previous
	    	
	    	myDialog2.getWindow()  
                .findViewById(R.id.positiveButton)   //
                .setOnClickListener(new View.OnClickListener() {  
                @Override  
                public void onClick(View v) {  
                	myDialog2.dismiss();  
                	warningDialog();

                }  
            });  
            
	    	myDialog2.getWindow()  
            .findViewById(R.id.negativeButton)  //	
            .setOnClickListener(new View.OnClickListener() {  
            	@Override  
            	public void onClick(View v) {  
            		myDialog2.dismiss();     
            		warningDialog3();
            	}  
            });
	    }
	    
	    private void warningDialog() {
			myDialog = new AlertDialog.Builder(BatteryWarningService.this).create();  
			
			myDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);			
            myDialog.show();  
            
            myDialog.getWindow().setContentView(R.layout.alertdialog_layout);  
            TextView tv = (TextView) myDialog.getWindow().findViewById(R.id.message);
            if (MyApp.isNetConnect()) {
            	tv.setText(getResources().getString(R.string.step_one));
			}else {
				
				tv.setText(getResources().getString(R.string.connectTheInternetTip));
			}
            myDialog.setCancelable(false);
            myDialog.getWindow()  
                .findViewById(R.id.positiveButton)   //取消（取消时开启定时器15分钟后，再次弹框）
                .setOnClickListener(new View.OnClickListener() {  
                @Override  
                public void onClick(View v) {  
                    myDialog.dismiss();     
                    
            	delay = 15*60*1000;
        		period = 15*60*1000; 
//        		delay = 15*1000;
//        		period = 15*1000; 
        		checkBatteryTimer = new Timer();
        		checkBatteryTask = new CheckBatteryTask();           		
        		checkBatteryTimer.schedule(checkBatteryTask, delay, period);//每隔15分钟

                }  
            });  
            
            myDialog.getWindow()  
            .findViewById(R.id.negativeButton)  	
            .setOnClickListener(new View.OnClickListener() {  
            	@Override  
            	public void onClick(View v) { 
            		myDialog.dismiss(); 
            		
            		if (MyApp.isNetConnect()) {
                		warningDialog2();
					}else {
						openSettings();
					}

            		   
            	}  
            });
            
		}

	    private void openSettings(){
	    	Intent intent = new Intent();
	    	ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings");
	    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	intent.setComponent(cn);
	    	startActivity(intent);
	    	
	    }
		

		

	

}
