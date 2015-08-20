package com.sdbnet.hywy.enterprise.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.sdbnet.hywy.enterprise.ActivityStackManager;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.ui.widget.DialogUpdateApp;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemSettingActivity extends BaseActivity {

	private ImageView mBack;
	private ListView lvSettings;
	private SimpleAdapter sAdapter;
	private final String SETTING_NAME = "setting_name";
	private final String SETTING_MSG = "setting_msg";
	private List<Map<String, String>> datas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_setting);
		initControls();
	}

	/**
	 * 初始化控件
	 */
	private void initControls() {
		mBack = (ImageView) findViewById(R.id.system_set_back_img);
		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		lvSettings = (ListView) findViewById(R.id.lvSetting);

		datas = getDatas();
		sAdapter = new SimpleAdapter(this, getDatas(),
				R.layout.list_item_system_setting_show, new String[] {
						SETTING_NAME, SETTING_MSG }, new int[] {
						R.id.tvSettingName, R.id.item_system_set_text_msg });

		lvSettings.setAdapter(sAdapter);

		// 给设置菜单列表添加点击事件
		lvSettings.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map<String, String> map = datas.get(position);
				if (getString(R.string.version_update).equals(
						map.get(SETTING_NAME))) {
					initUpdate();
				} else if (getString(R.string.change_pwd).equals(
						map.get(SETTING_NAME))) {
					openActivity(ResetPwdActivity.class);
				}
			}
		});
	}

	/**
	 * 点击退出按钮
	 * 
	 * @param v
	 */
	public void existSystem(View v) {
		showAlertDialog(getString(R.string.warm_prompt),
				getString(R.string.confirm_exit_current_account),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// 点击ok，退出当前帐号
						PreferencesUtil.putValue(
								PreferencesUtil.KEY_WORK_STATS, false);
						PreferencesUtil.clearLocalData(PreferenceManager
								.getDefaultSharedPreferences(SystemSettingActivity.this));
						Intent intent = new Intent(SystemSettingActivity.this,
								UserLoginActivity.class);
//						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//								| Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						ActivityStackManager.getStackManager().popAllActivitys();
//						finish();
					}
				}, new OnClickListener() {
					// 点击cancel，取消操作
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						mAlertDialog.dismiss();
					}
				}, new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface arg0) {

					}
				});
	}

	/**
	 * 设置菜单列表
	 * 
	 * @return
	 */
	private List<Map<String, String>> getDatas() {
		List<Map<String, String>> datas = new ArrayList<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		map.put(SETTING_NAME, getString(R.string.traffic_statistics));// "流量统计");
		map.put(SETTING_MSG, initTrafficStats());
		datas.add(map);
		map = new HashMap<String, String>();
		map.put(SETTING_NAME, getString(R.string.version_update));// "版本更新");
		datas.add(map);
		map = new HashMap<String, String>();
		map.put(SETTING_NAME, getString(R.string.suggestion_feedback));// "意见反馈");
		datas.add(map);
		map = new HashMap<String, String>();
		map.put(SETTING_NAME, getString(R.string.clear_cache));// "清理缓存");
		datas.add(map);
		map = new HashMap<String, String>();
		map.put(SETTING_NAME, getString(R.string.change_pwd));// "修改密码");
		datas.add(map);
		return datas;
	}

	private String initTrafficStats() {

		long recordTrafficStats = PreferencesUtil.getValue(
				PreferencesUtil.KEY_TRAFFIC_STATS, 0l);
		int mAppUid = UtilsAndroid.Set.getAppUid(this);
		long lastRxTrafficStats = TrafficStats.getUidRxBytes(mAppUid);
		long lastTxTrafficStats = TrafficStats.getUidTxBytes(mAppUid);
		return formetFileSize(lastRxTrafficStats + lastTxTrafficStats
				+ recordTrafficStats);
	}

	private String formetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize = "0B";
		if (fileS == 0) {
			return wrongSize;
		}
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		Log.e("FormetFileSize", fileSizeString);

		return fileSizeString;
	}

	private DialogUpdateApp mDialogUpdateApp;

	private void initUpdate() {
		if (!UtilsAndroid.Set.checkNetState(this)) {
			showShortToast(R.string.httpError);
			return;
		}
		if (mDialogUpdateApp == null)
			mDialogUpdateApp = new DialogUpdateApp(this);
		MobclickAgent.updateOnlineConfig(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateAutoPopup(false);

		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update
					mDialogUpdateApp.setMsg(updateInfo);
					mDialogUpdateApp.show();
					break;
				case UpdateStatus.No: // has no update
					showShortToast(R.string.updated_latest_version);
					break;
				default:
					showShortToast(R.string.updated_latest_version);
					break;
				}
			}

		});

		UmengUpdateAgent.update(this);
	}

}
