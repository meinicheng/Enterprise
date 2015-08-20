//package com.sdbnet.hywy.enterprise.location;
//
//
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//
//public class ScreenLockLocation {
//	private final static String TAG = "ScreenLockLocation";
//	private AlarmManager alarmManger = null;
//	private PendingIntent pi = null;
//	private Context context = null;
//	private BroadcastReceiver timerReceiver;
//	private static String action = "cn.hywy.center.request.location";
//	// private static final int INTERVAL = 5 * 60 * 1000; // 设置发起定位请求的间隔时间
//	// // private static final int INTERVAL = 30 * 1000;// Test
//	private static ScreenLockLocation location = new ScreenLockLocation();
//
//	private ScreenLockLocation() {
//	}
//
//	public static ScreenLockLocation getInstance() {
//		return location;
//	}
//
//	public ScreenLockLocation init(Context context,
//			BroadcastReceiver timerReceiver) {
//		this.context = context.getApplicationContext();
//		this.timerReceiver = timerReceiver;
//		return location;
//	}
//
//	/**
//	 * 启动后台闹钟服务
//	 */
//	public void start() {
//		context.registerReceiver(timerReceiver, new IntentFilter(action));
//		pi = PendingIntent.getBroadcast(context, 0, new Intent(action),
//				PendingIntent.FLAG_UPDATE_CURRENT);
//		alarmManger = (AlarmManager) context
//				.getSystemService(Context.ALARM_SERVICE);
//		alarmManger.setRepeating(AlarmManager.RTC_WAKEUP, 0,
//				TimingUpLocateService.ALARM_INTERVAL, pi);
//
//	}
//
//	/**
//	 * 停止后台闹钟服务
//	 */
//	public void stop() {
//		if (context != null && timerReceiver != null) {
//			context.unregisterReceiver(timerReceiver);
//			context = null;
//			timerReceiver = null;
//		}
//		if (alarmManger != null && pi != null) {
//			alarmManger.cancel(pi);
//		}
//		alarmManger = null;
//		pi = null;
//
//	}
//
//}