package com.aura.batterywarning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.PrivateApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.SystemProperties;
import android.util.Log;

import com.wbj.sector.SectorHalService;

/**
 * 
 * @author Steven Zhang
 * @since 20160614
 */
public class Utils {

	public static final boolean DEBUG = false;
	
	public static final int BATTERY_DIALOG_DELAY = 10 * 60 * 1000; //延迟时间 10分钟
	public static final int BATTERY_DIALOG_PERIOD = 10 * 60 * 1000; //间隔时间 10分钟
	
	
	public static final String BATTERY_URL_TEST = "http://192.168.1.30:45232/webStatisticsSer/web/addDeviceServlet"; //上传电池扣取状态 本地测试网络地址
	public static final String BATTERY_URL = "http://112.206.228.34:45232/webStatisticsSer/web/addDeviceServlet"; //上传电池扣取状态 网络地址
	public static final String BATTERY_URL_USEING = DEBUG ? BATTERY_URL_TEST : BATTERY_URL;
	
	public static final String BATTERY_CHECKING_TIMER = "com.aura.batterywaring.CHECKING";
	
	public static final int DIALOG_TYPE_NONE = -1;
	public static final int DIALOG_TYPE_0 = 0;
	public static final int DIALOG_TYPE_1 = 1;
	public static final int DIALOG_TYPE_2 = 2;
	public static final int DIALOG_TYPE_3 = 3;
	public static final int DIALOG_TYPE_4 = 4;
	public static final int DIALOG_TYPE_5 = 5;
	public static final int DIALOG_TYPE_6 = 6;
	
	public static final String MODEL_MA7 = "Aura_TELPAD_MA7_tablet";
	public static final String MODEL_MT7 = "Aura_TELPAD_MT7_tablet";
	public static final String MODEL_HC7 = "Aura_TELPAD_HC7_tablet";
	public static final String MODEL_TM7 = "Aura_TELPAD_TM7_tablet";
	
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
				if (buffer != null) {
					try {
						buffer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					buffer = null;
				}

				if (fread != null) {
					try {
						fread.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					fread = null;
				}
			}
		}

		return sn.substring(sn.indexOf(':') + 1);
	}

	@PrivateApi
	private static String getProductSN() {
		String sn = SystemProperties.get("ro.serialno");
		if (sn == null || sn.length() == 0) {
			sn = "unknown";
		}

		return sn;
	}
	
	public static String getModelNumber() {
		String modelNumber = "";
		
		if (MODEL_MA7.equals(Build.MODEL)) {
			modelNumber = getRKSerial();
		} else if (MODEL_MT7.equals(Build.MODEL)) {
			modelNumber = Build.SERIAL;
		} else if (MODEL_HC7.equals(Build.MODEL)) {//4.2.2
			modelNumber = SectorHalService.getInstance().getSerialNumber();
		} else if (MODEL_TM7.equals(Build.MODEL)) {
			modelNumber = getRKSerial();
		} else {
			modelNumber = getRKSerial();
		}
		
		return modelNumber;
	}
	
	/**
	* 检测app是否启动
	*/
	public static boolean isClsRunning() {
		ActivityManager am = (ActivityManager)MyApp.mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		boolean isAppRunning = false;
		String MY_PKG_NAME = "com.android.launcher";
		for (RunningTaskInfo info : list) {
			String packageName = info.baseActivity.getPackageName();
			Log.d("TAG", "isClsRunning:"+packageName);
		    if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
		        isAppRunning = true;
		        break;
		    }
		}
			     
        return isAppRunning;
    }	
	
	public static String getCurTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss", Locale.CHINA);       
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
		String str = formatter.format(curDate);   
		return str;
	}
	
	public static void writeLogToSdcard(String msg) {
		if (!DEBUG) {
			return ;
		}
		
		String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		File file = new File(sdcardPath, "log.txt");
		try {
			FileOutputStream fos = new FileOutputStream(file, true);
			fos.write(msg.getBytes());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
