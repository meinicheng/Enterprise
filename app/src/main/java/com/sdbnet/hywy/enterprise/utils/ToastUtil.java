package com.sdbnet.hywy.enterprise.utils;

import android.content.Context;
import android.widget.Toast;

import com.sdbnet.hywy.enterprise.MainApplication;

public class ToastUtil {
	private static boolean isDebug = false;

	public static void openShow() {

		isDebug = MainApplication.DEVELOPER_MODE;
		String tel = PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL);
		if (MainApplication.mAccounts.contains(tel)) {
			isDebug = true;
		}
		
	}

	// public static void addAccount(String account) {
	// mAccounts.add(account);
	// }

	public static void show(Context context, String msg) {
		if (isDebug) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}

	public static void show(Context context, int rid) {
		if (isDebug) {
			Toast.makeText(context, context.getString(rid), Toast.LENGTH_SHORT)
					.show();
		}
	}

	public static boolean isDebug() {
		return isDebug;
	}
}
