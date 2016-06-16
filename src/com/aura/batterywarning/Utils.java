package com.aura.batterywarning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.os.SystemProperties;
import android.util.Log;

/**
 * 
 * @author Steven Zhang
 * @since 20160614
 */
public class Utils {

	public static final int BATTERY_DIALOG_DELAY = 10 * 60 * 1000; //延迟时间 10分钟
	public static final int BATTERY_DIALOG_PERIOD = 10 * 60 * 1000; //间隔时间 10分钟
	public static final String BATTERY_URL_TEST = "http://192.168.1.30:45231/webStatisticsSer/web/addDeviceServlet"; //上传电池扣取状态 本地测试网络地址
	public static final String BATTERY_URL = "http://112.206.228.34:45266/webStatisticsSer/web/addDeviceServlet"; //上传电池扣取状态 网络地址
	
	public static final String BATTERY_CHECKING_TIMER = "com.aura.batterywaring.CHECKING";
	
	public static final int DIALOG_TYPE_NONE = -1;
	public static final int DIALOG_TYPE_0 = 0;
	public static final int DIALOG_TYPE_1 = 1;
	public static final int DIALOG_TYPE_2 = 2;
	public static final int DIALOG_TYPE_3 = 3;
	
	public static String getRKSerial() {
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

	public static String getProductSN() {
		String sn = SystemProperties.get("ro.serialno");
		if (sn == null || sn.length() == 0) {
			sn = "unknown";
		}

		return sn;
	}
}
