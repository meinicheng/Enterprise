package com.sdbnet.hywy.enterprise.receiver;

import java.io.File;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.util.Log;

public class SystemBroadcastReceiver extends BroadcastReceiver {
	private final static String TAG = "SystemBroadcastReceiver";
	private final String ACTION_BATTERY_CHANGED = "android.intent.action.BATTERY_CHANGED";
	private final String ACTION_NET_CON_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	private final String ACTION_NET_WIFI_CHANGED = "android.net.wifi.WIFI_STATE_CHANGED";
	private final String ACTION_NET_WIFI_CHANGE = "android.net.wifi.STATE_CHANGE";

	private final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	private final String ACTION_SHUT_DOWN = "android.intent.action.ACTION_SHUTDOWN";
	private int lastBattery;
	private File file = new File(Constants.SDBNET.BASE_PATH + "battery.txt");

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.e(TAG, "action=" + action);
		if (ACTION_SHUT_DOWN.equals(action)) {
			saveTrafficStats(context);
		}
	}

	private void saveTrafficStats(Context context) {
		Log.e("ShutdownReceiver", "exit");
		int mAppUid = UtilsAndroid.Set.getAppUid(context);
		long lastRxTrafficStats = TrafficStats.getUidRxBytes(mAppUid);
		long lastTxTrafficStats = TrafficStats.getUidTxBytes(mAppUid);
		long recordTrafficStats = PreferencesUtil.getValue(
				PreferencesUtil.KEY_TRAFFIC_STATS, 0l);
		PreferencesUtil.putValue(PreferencesUtil.KEY_TRAFFIC_STATS,
				lastRxTrafficStats + lastTxTrafficStats + recordTrafficStats);

	}

}
