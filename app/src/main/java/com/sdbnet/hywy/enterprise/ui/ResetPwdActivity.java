package com.sdbnet.hywy.enterprise.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.UtilsError;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * 修改当前用户帐号密码
 * 
 * @author Arron.Zhang
 *
 */
public class ResetPwdActivity extends BaseActivity {

	private EditText edt_old_pwd;
	private EditText edt_new_pwd;
	private EditText edt_repeat_pwd;
	private Button bt_info_save;
	private View iv_go_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_pwd);
		initControls();
	}

	/**
	 * 初始化控件
	 */
	private void initControls() {
		iv_go_back = findViewById(R.id.common_view_title_img);
		iv_go_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView)findViewById(R.id.common_view_title_text)).setText(R.string.change_pwd);
		edt_old_pwd = (EditText) findViewById(R.id.edt_old_pwd);
		edt_new_pwd = (EditText) findViewById(R.id.edt_new_pwd);
		edt_repeat_pwd = (EditText) findViewById(R.id.edt_repeat_pwd);
		bt_info_save = (Button) findViewById(R.id.bt_info_save);
		bt_info_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				saveInfo();
			}

		});
	}

	private void saveInfo() {
		String oldPwd = edt_old_pwd.getText().toString().trim();
		final String newPwd = edt_new_pwd.getText().toString().trim();
		String repeatPwd = edt_repeat_pwd.getText().toString().trim();

		if (TextUtils.isEmpty(oldPwd)) {
			showLongToast(getString(R.string.please_input_you_current_pwd));// "请输入您的当前密码");
			return;
		}
		if (TextUtils.isEmpty(newPwd)) {
			showLongToast(getString(R.string.please_input_you_new_pwd));// "请输入您的新密码");
			return;
		}
		if (TextUtils.isEmpty(repeatPwd)) {
			showLongToast(getString(R.string.please_again_input_you_new_pwd));// "请重复输入您的新密码");
			return;
		}
		if (oldPwd.length() < 6 || newPwd.length() < 6
				|| repeatPwd.length() < 6) {
			showLongToast(getString(R.string.please_input_pwd_length_6));// "请输入密码长度大于6位的密码");
			edt_old_pwd.setText("");
			return;
		}
		if (oldPwd.length() > 13 || newPwd.length() > 13
				|| repeatPwd.length() > 13) {
			showLongToast(getString(R.string.please_input_pwd_length_13));// "请输入密码长度小于13位的密码");
			edt_old_pwd.setText("");
			return;
		}
		if (!PreferencesUtil.user_pwd.equals(oldPwd)) {
			showLongToast(getString(R.string.current_pwd_error));// "当前密码输入错误");
			edt_old_pwd.setText("");
			return;
		}
		if (!newPwd.equals(repeatPwd)) {
			showLongToast(getString(R.string.two_pwd_no_match_please_enter_again));// "两次输入的新密码不一致,请重新输入");
			edt_new_pwd.setText("");
			edt_repeat_pwd.setText("");
			return;
		}
		AsyncHttpService.modifyUserPwd(newPwd, new JsonHttpResponseHandler() {

			@Override
			public void onStart() {
				super.onStart();
				showLoading(getString(R.string.is_save_please_wait_ellipsis));
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				dismissLoading();
				showShortToast(getResources().getString(R.string.httpisNull));

			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				System.out.println("info: " + response.toString());
				super.onSuccess(statusCode, headers, response);
				try {
					if (UtilsError.isErrorCode(ResetPwdActivity.this, response)) {
						return;
					}
					PreferencesUtil.putValue(PreferencesUtil.KEY_USER_PWD,
							newPwd);
					PreferencesUtil.user_pwd = newPwd;
					showLongToast(getString(R.string.pwd_changed_success));
					ResetPwdActivity.this.finish();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dismissLoading();
				}
			}

		}, this);
	}

}
