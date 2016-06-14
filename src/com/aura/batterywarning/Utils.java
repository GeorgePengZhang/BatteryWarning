package com.aura.batterywarning;

/**
 * 
 * @author Steven Zhang
 * @since 20160614
 */
public class Utils {

	public static final int BATTERY_DIALOG_DELAY = 15 * 60 * 1000; //延迟时间 15分钟
	public static final int BATTERY_DIALOG_PERIOD = 15 * 60 * 1000; //间隔时间 15分钟
	public static final String BATTERY_URL = "http://192.168.1.30:45231/webStatisticsSer/web/addDeviceServlet"; //上传电池扣取状态 网络地址
	
	public static final int DIALOG_TYPE_NONE = -1;
	public static final int DIALOG_TYPE_0 = 0;
	public static final int DIALOG_TYPE_1 = 1;
	public static final int DIALOG_TYPE_2 = 2;
}
