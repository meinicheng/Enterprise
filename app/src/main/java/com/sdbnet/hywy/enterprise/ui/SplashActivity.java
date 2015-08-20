package com.sdbnet.hywy.enterprise.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.sdbnet.hywy.enterprise.ui.widget.DialogUpdateApp;
import com.sdbnet.hywy.enterprise.utils.LogUtil;
import com.sdbnet.hywy.enterprise.ActivityStackManager;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.listener.MyAnimationListener;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;
import com.sdbnet.hywy.enterprise.utils.UtilsCommon;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jpush.android.api.JPushInterface;

public class SplashActivity extends Activity {
	private static final String TAG = "SplashActivity";
	private Handler mHandler = new Handler();
	private String finishTime = "2014-12-01";
	private Dialog dia;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityStackManager.getStackManager().pushActivity(this);
		initView();
		installShortcut(); // 创建桌面快捷图标
	}

	private void initView() {

		View view = View.inflate(this, R.layout.activity_splash, null);
		setContentView(view);

		// 欢迎动画
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		view.startAnimation(animation);
		animation.setAnimationListener(new MyAnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				if (!UtilsAndroid.Set.checkNetState(SplashActivity.this)) {
					showLongToast(R.string.httpisNull);
					enterMain();
				} else {
					initUpdate();
				}
			}
		});
	}

	/**
	 * 创建桌面快捷图标
	 */
	private void installShortcut() {
		boolean isShortcut = PreferencesUtil.getValue(
				PreferencesUtil.KEY_SHORTCUT + "_22", false);
		if (isShortcut)
			return;

		Intent intent = new Intent();
		intent.setClass(this, this.getClass());
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		// Then, set up the container intent (the response to the caller)

		Intent shortcutIntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		Parcelable icon = Intent.ShortcutIconResource.fromContext(this,
				R.drawable.logo);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.app_name));

		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// duplicate created
		shortcutIntent.putExtra("duplicate", false);
		sendBroadcast(shortcutIntent);
		PreferencesUtil.putValue(PreferencesUtil.KEY_SHORTCUT + "_22", true);
	}

	/**
	 * 友盟更新
	 */

	private DialogUpdateApp mDialogUpdateApp;

	private void initUpdate() {
		mDialogUpdateApp = new DialogUpdateApp(this);

		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateAutoPopup(false);

		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				LogUtil.d("updateStatus=" + updateStatus + "");
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update
					mDialogUpdateApp.setMsg(updateInfo);
					mDialogUpdateApp.show();
					break;
				case UpdateStatus.No: // has no update
					enterMain();
					// goMain();
					break;
				default:
					enterMain();
					// goMain();
					break;
				}
			}
		});
		UmengUpdateAgent.update(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 延迟1秒后进入主界面
	 */
	private void enterMain() {
		System.out.println("RegistrationID: "
				+ JPushInterface.getRegistrationID(this));
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				goHome();
			}
		}, 1000);
	}

	/**
	 * 进入主界面
	 */
	private void goHome() {
		PreferencesUtil.initStoreData();
		if (!PreferencesUtil.hasKey(Constants.Feild.KEY_OPTIONS)) {
			// 更新用户权限……。
			startActivity(new Intent(this, UserLoginActivity.class));

		} else if (PreferencesUtil.hasKey(PreferencesUtil.KEY_USER_TEL)
				&& PreferencesUtil.hasKey(PreferencesUtil.KEY_USER_PWD)
				&& PreferencesUtil.hasKey(PreferencesUtil.KEY_USER_TOKEN)) {
			// 如果保存了帐号密码，则直接登录
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			// 用户登录
			UtilsCommon.start_activity(this,UserLoginActivity.class);
//			openActivity(UserLoginActivity.class);
		}
		finish();
	}

	protected void dialog() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);

		if (!isDateBefore(finishTime, dateString)) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					goHome();
				}
			}, 1000);
			return;
		}

		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(getString(R.string.beta_tip));// "货运无忧客户端Beta版试用截止，请更新为正式版本，点击确定前往下载");
		builder.setTitle(getString(R.string.update_remind));// "更新提醒");
		builder.setPositiveButton(getString(R.string.confirm),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						Uri uri = Uri
								.parse("http://www.sdbnet.com/download/company/android/hywy-company.apk");
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(intent);
					}
				});
		builder.create().show();
	}

	public boolean isDateBefore(String date1, String date2) {
		try {
			DateFormat df = DateFormat.getDateTimeInstance();
			return df.parse(date1).before(df.parse(date2));
		} catch (ParseException e) {
			return false;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ActivityStackManager.getStackManager().popActivity(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}
	private void showLongToast(int resId){
		Toast.makeText(this,resId,Toast.LENGTH_SHORT).show();
	}
	private void showLongToast(String msg){
		Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
	}
}
