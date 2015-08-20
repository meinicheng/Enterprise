package com.sdbnet.hywy.enterprise.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.ui.UserLoginActivity;

import org.json.JSONObject;

public class UtilsError {
	private static final String TAG = "UtilsError";

	public static boolean isErrorCode(Context context, JSONObject response)
			throws Exception {
		int errCode = response.getInt(Constants.Feild.KEY_ERROR_CODE);
		if (errCode != 0) {
			String msg = response.getString(Constants.Feild.KEY_MSG);
			switch (errCode) {
			case 41:
				returnLogin(context, msg);
				break;
			case 42:
				returnLogin(context, msg);
				break;
			default:
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				break;
			}
			return true;
		} else {
			return false;
		}
	}

	// public static void isExceptionCode(Context context, JSONObject response){
	//
	// }

	public static void returnLogin(final Context context, String msg) {
		PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS, false);
		new AlertDialog.Builder(context)
				.setTitle(context.getString(R.string.system_tip))
				// "系统提示")
				.setMessage(msg)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 点击ok，退出当前帐号
								PreferencesUtil.clearLocalData(PreferenceManager
										.getDefaultSharedPreferences(context));
								Intent intent = new Intent(context,
										UserLoginActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
										| Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(intent);
							}

						}).show();
	}

	public static boolean isSaveLocateLog() {
		if (TextUtils.isEmpty(PreferencesUtil.user_company)
				|| TextUtils.isEmpty(PreferencesUtil.item_id)
				|| TextUtils.isEmpty(PreferencesUtil.user_id)
				|| TextUtils.isEmpty(PreferencesUtil.user_tel)) {
			return false;
		} else {
			return true;
		}
	}
}
