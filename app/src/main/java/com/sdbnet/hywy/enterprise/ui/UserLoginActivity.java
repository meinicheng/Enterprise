package com.sdbnet.hywy.enterprise.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.ActivityStackManager;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.LogUtil;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;
import com.sdbnet.hywy.enterprise.utils.UtilsError;
import com.sdbnet.hywy.enterprise.utils.UtilsJava;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserLoginActivity extends BaseActivity {
	private static final String TAG = "UserLoginActivity";

	private EditText mEditTel;
	private EditText mEditPwd;
	private SharedPreferences share;

	private Button mBtnLogin;

	private CheckBox mCkbAgree;
	private TextView mTextLink;

	public static List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	private TextView mTextReg;
	private TextView mTextSeek;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_login);
		initControl();
	}

	/**
	 * 初始化控件
	 */
	private void initControl() {
		mEditTel = (EditText) findViewById(R.id.activity_login_tel_edt);
		mEditPwd = (EditText) findViewById(R.id.activity_login_pwd_edt);

		mTextReg = (TextView) findViewById(R.id.activity_login_tvReg);
		mTextSeek = (TextView) findViewById(R.id.activity_login_tvSeek);

		mBtnLogin = (Button) findViewById(R.id.activity_login_btn);
		mCkbAgree = (CheckBox) findViewById(R.id.activity_login_pactCheck);
		mCkbAgree.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					mBtnLogin.setEnabled(true);
					mBtnLogin
							.setBackgroundResource(R.drawable.blue_btn_selector);
				} else {
					mBtnLogin.setEnabled(false);
					mBtnLogin
							.setBackgroundResource(R.drawable.btn_validation_disable);
				}
			}
		});

		mTextLink = (TextView) findViewById(R.id.activity_login_tvPact);
		mTextLink
				.setText(Html
						.fromHtml(getString(R.string.have_read_and_agreed_to_the_deal)));
		mTextLink.setMovementMethod(LinkMovementMethod.getInstance());

		if (!TextUtils.isEmpty(PreferencesUtil.user_tel)) {
			mEditTel.setText(PreferencesUtil.user_tel);
		}
	}

	/**
	 * 点击登录
	 * 
	 * @param view
	 */
	public void onLogin(View view) {
		String pwd = mEditPwd.getText().toString().trim();
		String nTel = mEditTel.getText().toString().trim();
		if (TextUtils.isEmpty(nTel)) {
			showLongToast(getString(R.string.please_input_phone_num));// "请输入手机号码！");
		} else if (!UtilsJava.isMobile(nTel)) {
			showLongToast(getString(R.string.phone_number_illegal));// "电话号码不合法！");
		} else if (TextUtils.isEmpty(pwd)) {
			showLongToast(getString(R.string.please_enter_pwd));// "请输入密码！");
		} else if (pwd.length() < 4 || pwd.length() > 15) {
			showLongToast(getString(R.string.please_enter_4_15_valid_pwd));// "请输入4-15位有效密码！");
		} else {
			UtilsAndroid.Set.hideSoftInput(this);
			doLogin(nTel, pwd);
		}
	}

	/**
	 * 登录验证
	 * 
	 * @param tel
	 * @param pwd
	 * @param loginType
	 */
	private void doLogin(final String tel, final String pwd) {
		AsyncHttpService.login(tel, pwd, new JsonHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				showLoading(getString(R.string.is_landing_ellipsis));
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				throwable.printStackTrace();
				super.onFailure(statusCode, headers, throwable, errorResponse);
				dismissLoading();
				showShortToast(getResources().getString(R.string.httpisNull));
			}

			@Override
			public void onCancel() {
				super.onCancel();
				dismissLoading();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {

				LogUtil.e(TAG, "response: " + response.toString());
				super.onSuccess(statusCode, headers, response);
				try {
					if (UtilsError
							.isErrorCode(UserLoginActivity.this, response)) {
						dismissLoading();
						return;
					}
					// 登录成功，保存用户信息
					PreferencesUtil.putValue(PreferencesUtil.KEY_USER_TEL, tel);
					PreferencesUtil.putValue(PreferencesUtil.KEY_USER_PWD, pwd);
					PreferencesUtil.putValue(PreferencesUtil.KEY_USER_TOKEN,
							response.getString(Constants.Feild.KEY_TOKEN));

					JSONArray arrs = response
							.getJSONArray(Constants.Feild.KEY_ITEMS);
					PreferencesUtil.putValue(PreferencesUtil.KEY_ITEMS,
							arrs.toString());

					if (arrs.length() == 1) {
						// 如果用户所属项目只有1个，获取对该项目的操作权限
						JSONObject jsonObj = arrs.getJSONObject(0);
						String cmpid = jsonObj
								.getString(Constants.Feild.KEY_COMPANY_ID);
						String cmpname = jsonObj
								.getString(Constants.Feild.KEY_COMPANY_NAME);
						String itemid = jsonObj
								.getString(Constants.Feild.KEY_ITEM_ID);
						String itemname = jsonObj
								.getString(Constants.Feild.KEY_ITEM_NAME);
						String pid = jsonObj
								.getString(Constants.Feild.KEY_STAFF_ID);
						String pname = jsonObj
								.getString(Constants.Feild.KEY_STAFF_NAME);

						PreferencesUtil.putValue(
								PreferencesUtil.KEY_COMPANY_ID, cmpid);
						PreferencesUtil.putValue(
								PreferencesUtil.KEY_COMPANY_NAME, cmpname);
						PreferencesUtil.putValue(PreferencesUtil.KEY_ITEM_ID,
								itemid);
						PreferencesUtil.putValue(PreferencesUtil.KEY_ITEM_NAME,
								itemname);
						PreferencesUtil.putValue(PreferencesUtil.KEY_USER_NAME,
								pname);
						PreferencesUtil.putValue(PreferencesUtil.KEY_USER_ID,
								pid);

						PreferencesUtil.initStoreData();

						getPermission(cmpid, itemid, pid);
					} else if (arrs.length() != 0) {
						mEditPwd.setText("");
						// 有多个项目，则跳转到项目列表，选择项目
						Intent intent = new Intent(UserLoginActivity.this,
								SelectProjectActivity.class);
						startActivity(intent);
						dismissLoading();

					}

				} catch (Exception ex) {
					ex.printStackTrace();
					dismissLoading();
					showShortToast(R.string.login_failed_tip_msg);// "登录未成功，请确认账号和密码后重试");
				}
			}
		}, this);
	}

	/**
	 * 获取对项目的操作权限
	 * 
	 * @param cmpid
	 *            公司编码
	 * @param itemid
	 *            项目特征码
	 * @param pid
	 *            用户ID
	 */
	private void getPermission(String cmpid, String itemid, String pid) {
		AsyncHttpService.getPermissionWithProject(cmpid, itemid, pid,
				new JsonHttpResponseHandler() {
					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						dismissLoading();
						showShortToast(getString(R.string.httpisNull));

					}

					@Override
					public void onCancel() {
						super.onCancel();
						dismissLoading();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {

						LogUtil.d(TAG, "Permission: " + response.toString());
						super.onSuccess(statusCode, headers, response);
						try {
							if (UtilsError.isErrorCode(UserLoginActivity.this,
									response)) {
								dismissLoading();
								return;
							}

							JSONObject jsonObj = response
									.getJSONObject(Constants.Feild.KEY_STAFF);
							String menu = jsonObj
									.getString(Constants.Feild.KEY_MENU);
							PreferencesUtil.putValue(
									PreferencesUtil.KEY_EXECUTE_MENU, menu); // 保存菜单权限

							int codeLength = jsonObj
									.getInt(Constants.Feild.KEY_CODE_LENGTH);
							PreferencesUtil
									.putValue(PreferencesUtil.KEY_CODE_LENGTH,
											codeLength);

							String logo = jsonObj
									.getString(Constants.Feild.KEY_COMPANY_LOGO);
							PreferencesUtil.putValue(
									PreferencesUtil.KEY_COMPANY_LOGO, logo);

							String executeActions = jsonObj
									.getString(Constants.Feild.KEY_EXECUTE_ACTION); // 保存扫描动作权限
							PreferencesUtil.putValue(
									PreferencesUtil.KEY_EXECUTE_ACTION,
									executeActions);

							PreferencesUtil.putValue(
									PreferencesUtil.KEY_ORDTITLE,
									jsonObj.getString(Constants.Feild.KEY_COMPANY_ORDTITLE));
							PreferencesUtil.putValue(
									PreferencesUtil.KEY_CNOTITLE,
									jsonObj.getString(Constants.Feild.KEY_COMPANY_CNOTITLE));
							PreferencesUtil.putValue(
									PreferencesUtil.KEY_WORK_STATS, true);

							// options 1 数组 操作权限
							// ---data 1 字符串 是否第三方交互 目前用不上
							// ---scanModel 1 字符串 扫描模式 0：普通模式 1：更新模式
							// ---workModel 1 字符串 是否需要上下班功能 0：不需要 1：需要

							if (!jsonObj.isNull(Constants.Feild.KEY_OPTIONS)) {
								String options = jsonObj
										.getString(Constants.Feild.KEY_OPTIONS);
								PreferencesUtil.putValue(
										Constants.Feild.KEY_OPTIONS, options);
							}
							PreferencesUtil.initStoreData();
							goHome();
						} catch (Exception ex) {
							ex.printStackTrace();
							dismissLoading();
							showShortToast(getString(R.string.network_busy_please_try_again_later));// "网络繁忙，请稍后重试");
						}

					}
				}, UserLoginActivity.this);
	}

	/**
	 * 上传设备信息
	 */
	private void uploadDeviceInfo() {
		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		AsyncHttpService.upDeviceInfo(PreferencesUtil.user_tel, "",
				tm.getDeviceId(), new JsonHttpResponseHandler() {

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						showShortToast(getResources().getString(
								R.string.httpisNull));
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						LogUtil.d(TAG, "onsuccess: " + response.toString());
						super.onSuccess(statusCode, headers, response);
						try {
							UtilsError.isErrorCode(UserLoginActivity.this,
									response);
						} catch (Exception ex) {
							ex.printStackTrace();
							showShortToast(getString(R.string.network_busy_please_try_again_later));// "网络繁忙，请稍后重试");
						}
					}

				}, this);
	}

	/**
	 * 进入主界面
	 */
	private void goHome() {
		// 上传设备信息
		uploadDeviceInfo();
		String version = UtilsAndroid.Set.getVersionName(this);
		dismissLoading();
		if (version == null) {
			Log.e(TAG, "Version==null");
		} else if (!TextUtils.isEmpty(PreferencesUtil.system_version)
				&& version.equals(PreferencesUtil.system_version)) {
			PreferencesUtil.initStoreData();
			Intent intent = new Intent(UserLoginActivity.this,
					MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			PreferencesUtil.putValue(PreferencesUtil.KEY_SYSTEM_VERSION,
					version);
			PreferencesUtil.initStoreData();
			openActivity(WhatsnewActivity.class);
		}
		ActivityStackManager.getStackManager().popAllActivitys();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AsyncHttpService.cancelRequests(this);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
