package com.sdbnet.hywy.enterprise.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.ui.widget.DialogLoading;
import com.sdbnet.hywy.enterprise.ui.widget.SegmentedRadioGroup;
import com.sdbnet.hywy.enterprise.utils.Constants;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 编辑用户资料
 * 
 * @author Administrator
 * 
 */
public class EditInfoActivity extends BaseActivity {

	private EditText et_plate;
	private EditText et_user_name;
	private EditText et_user_tel;

	private Button bt_info_save;

	private LinearLayout editLayout;

	private OnClickListener listener;
	private DialogLoading mLoadDialog;
	private View iv_go_back;

	private SegmentedRadioGroup segmentStatus;
	private TextView et_vehicle_load;
	private TextView et_vehicle_length;
	private TextView et_vehicle_model;
	private RelativeLayout lay_choose_load;
	private RelativeLayout lay_choose_length;
	private RelativeLayout lay_choose_model;
	private String conpid;
	private int ustatus = 0;
	private String userName;
	private String plate;
	private String type;
	private String length;
	private String weight;
	private String tel;
	private String sex;
	protected String sexTemp;
	protected int isUpdateSuccess = 0;
	private LinearLayout lay_driver_edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_edit_info);
		initControls();
		initDatas();
	}

	/**
	 * 初始化数据显示
	 */
	private void initDatas() {
		conpid = getIntent().getStringExtra(Constants.Feild.KEY_STAFF_ID);
		ustatus = getIntent().getIntExtra("ustatus", 0);
		AsyncHttpService.getContractUserInfo(conpid,
				new JsonHttpResponseHandler() {
					@Override
					public void onStart() {
						super.onStart();
						mLoadDialog.show();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						showShortToast(getResources().getString(
								R.string.httpisNull));
						mLoadDialog.dismiss();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						System.out.println("info: " + response.toString());
						super.onSuccess(statusCode, headers, response);
						try {
							int errCode = response
									.getInt(Constants.Feild.KEY_ERROR_CODE);
							if (errCode != 0) {
								String msg = response
										.getString(Constants.Feild.KEY_MSG);
								switch (response
										.getInt(Constants.Feild.KEY_ERROR_CODE)) {
								case 41:
									returnLogin(EditInfoActivity.this, msg,
											mLoadDialog);
									break;
								case 42:
									returnLogin(EditInfoActivity.this, msg,
											mLoadDialog);
									break;
								default:
									showLongToast(msg);
									mLoadDialog.dismiss();
									break;
								}
								return;
							}

							JSONObject jsonStaff = response
									.getJSONObject(Constants.Feild.KEY_STAFF);
							userName = jsonStaff
									.getString(Constants.Feild.KEY_STAFF_NAME);
							sex = jsonStaff
									.getString(Constants.Feild.KEY_STAFF_SEX);
							tel = jsonStaff
									.getString(Constants.Feild.KEY_LOCA_TEL);
							plate = jsonStaff
									.getString(Constants.Feild.KEY_STAFF_TRUCK_NO);
							type = jsonStaff
									.getString(Constants.Feild.KEY_STAFF_TRUCK_TYPE);
							length = jsonStaff
									.getString(Constants.Feild.KEY_STAFF_TRUCK_LENGTH);
							weight = jsonStaff
									.getString(Constants.Feild.KEY_STAFF_TRUCK_WEIGHT);
							fillDatas();
							mLoadDialog.dismiss();
						} catch (JSONException e) {
							mLoadDialog.dismiss();
							e.printStackTrace();
						}
					}

				}, this);
	}

	private void fillDatas() {
		if ("0".equals(sex)) {
			((RadioButton) editLayout.findViewById(R.id.rb_man))
					.setChecked(true);
		} else {
			((RadioButton) editLayout.findViewById(R.id.rb_woman))
					.setChecked(true);
		}

		et_user_name.setText(userName);
		et_plate.setText(plate);
		et_vehicle_model.setText(type);
		et_vehicle_length.setText(length);
		et_vehicle_load.setText(weight);
		et_user_tel.setText(tel);

		switch (ustatus) {
		case 1:
			bt_info_save.setText("分配并保存");
			break;
		case 2:
			bt_info_save.setText("回收并保存");
			break;
		case 3:
			bt_info_save.setText("回收并保存");
			break;
		case 4:
			bt_info_save.setText("回收并保存");
			break;
		default:
			break;
		}
	}

	/**
	 * 初始化控件
	 */
	private void initControls() {
		iv_go_back = findViewById(R.id.iv_go_back);
		iv_go_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		editLayout = (LinearLayout) findViewById(R.id.editLayout);
		bt_info_save = (Button) findViewById(R.id.bt_info_save);

		listener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = null;
				switch (view.getId()) {
				case R.id.bt_info_save: // 点击保存
					switch (ustatus) {
					case 2:
						popDialog("温馨提示", "当前帐号已分配，是否确认强行更改？");
						break;
					case 3:
						popDialog("温馨提示", "当前帐号正在使用中，是否确认强行更改？");
						break;
					default:
						updateInfoData();
						break;
					}
					break;
				case R.id.lay_choose_model: // 选择车型
					ChooseModelActivity.isChooseAll = false;
					intent = new Intent(EditInfoActivity.this,
							ChooseModelActivity.class);
					startActivityForResult(intent,
							Constants.RequestCode.QUERY_CHOOSE_MODEL);
					break;
				case R.id.lay_choose_length: // 选择车长
					ChooseModelActivity.isChooseAll = false;
					intent = new Intent(EditInfoActivity.this,
							ChooseLengthActivity.class);
					startActivityForResult(intent,
							Constants.RequestCode.QUERY_CHOOSE_LENGTH);
					break;
				case R.id.lay_choose_load: // 选择载重
					ChooseModelActivity.isChooseAll = false;
					intent = new Intent(EditInfoActivity.this,
							ChooseWeightActivity.class);
					startActivityForResult(intent,
							Constants.RequestCode.QUERY_CHOOSE_LOAD);
					break;
				default:
					break;
				}
			}

			private void popDialog(String title, String cont) {
				EditInfoActivity.this.showAlertDialog(title, cont,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// 点击ok，退出当前帐号
								updateInfoData();
							}
						}, new DialogInterface.OnClickListener() {
							// 点击cancel，取消操作
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								mLoadDialog.dismiss();
							}
						}, new OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface arg0) {
							}
						});
			}
		};

		bt_info_save.setOnClickListener(listener);

		mLoadDialog = new DialogLoading(this,getString(R.string.is_submitted_ellipsis));

		editLayout.setVisibility(View.VISIBLE);

		lay_choose_model = (RelativeLayout) findViewById(R.id.lay_choose_model);
		lay_choose_length = (RelativeLayout) findViewById(R.id.lay_choose_length);
		lay_choose_load = (RelativeLayout) findViewById(R.id.lay_choose_load);
		lay_choose_model.setOnClickListener(listener);
		lay_choose_length.setOnClickListener(listener);
		lay_choose_load.setOnClickListener(listener);

		// 性别单选控件
		segmentStatus = (SegmentedRadioGroup) findViewById(R.id.segment_sex);
		segmentStatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.rb_man) {
					// 男
					sexTemp = "0";
				} else if (checkedId == R.id.rb_woman) {
					// 女
					sexTemp = "1";
				}
			}
		});

		lay_driver_edit = (LinearLayout) editLayout
				.findViewById(R.id.lay_driver_edit);

		et_plate = (EditText) editLayout.findViewById(R.id.et_plate);
		et_vehicle_model = (TextView) editLayout
				.findViewById(R.id.et_vehicle_model);
		et_vehicle_length = (TextView) editLayout
				.findViewById(R.id.et_vehicle_length);
		et_vehicle_load = (TextView) editLayout
				.findViewById(R.id.et_vehicle_load);
		et_user_name = (EditText) editLayout.findViewById(R.id.et_user_name);
		et_user_tel = (EditText) editLayout.findViewById(R.id.et_user_tel);

		// 用户为车辆用户，显示相关信息
		// if (Constants.Value.YES.equals(PreferencesUtil.is_driver)) {
		lay_driver_edit.setVisibility(View.VISIBLE);
		// }
	}

	/**
	 * 更新资料编辑
	 */
	protected void updateInfoData() {
		// 判断用户是否修改了资料
		if (userName.equals(et_user_name.getText().toString().trim())
				&& sex.equals(sexTemp)) {
			// if (Constants.Value.YES.equals(PreferencesUtil.is_driver)) {
			if (et_plate.getText().toString().trim().equals(plate)
					&& et_vehicle_model.getText().toString().equals(type)
					&& et_vehicle_length.getText().toString().equals(length)
					&& et_vehicle_load.getText().toString().equals(weight)
					&& et_user_tel.getText().toString().trim().equals(tel)) {
				Toast.makeText(EditInfoActivity.this, "当前信息未发生变化，请修改后再提交",
						Toast.LENGTH_SHORT).show();
				return;
				// }
			}
			// else {
			// Toast.makeText(EditInfoActivity.this, "当前信息未发生变化，请修改后再提交",
			// Toast.LENGTH_SHORT).show();
			// return;
			// }
		}

		userName = et_user_name.getText().toString().trim();
		tel = et_user_tel.getText().toString().trim();

		// 如果用户为车辆用户，则获取相应信息
		// if (Constants.Value.YES.equals(PreferencesUtil.is_driver)) {
		plate = et_plate.getText().toString().trim();
		type = et_vehicle_model.getText().toString();
		length = et_vehicle_length.getText().toString();
		weight = et_vehicle_load.getText().toString();
		// }

		// 提交编辑请求
		AsyncHttpService.modifyContractUser(conpid, userName, sexTemp, tel,
				plate, type, length, weight, new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						mLoadDialog.show();
						super.onStart();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						Toast.makeText(
								EditInfoActivity.this,
								EditInfoActivity.this.getResources().getString(
										R.string.httpisNull),
								Toast.LENGTH_SHORT).show();
						mLoadDialog.dismiss();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						System.out.println("info: " + response.toString());
						mLoadDialog.dismiss();
						super.onSuccess(statusCode, headers, response);
						try {
							if (response.getInt(Constants.Feild.KEY_ERROR_CODE) != 0) {
								String msg = response
										.getString(Constants.Feild.KEY_MSG);
								switch (response
										.getInt(Constants.Feild.KEY_ERROR_CODE)) {
								case 41:
									returnLogin(EditInfoActivity.this, msg,
											mLoadDialog);
									break;
								case 42:
									returnLogin(EditInfoActivity.this, msg,
											mLoadDialog);
									break;
								default:
									showLongToast(msg);
									mLoadDialog.dismiss();
									break;
								}
								return;
							}
							isUpdateSuccess = 1;
							// PreferencesUtil.setPrefString(
							// PreferencesUtil.KEY_USER_NAME, userName);
							Toast.makeText(EditInfoActivity.this, "资料编辑成功",
									Toast.LENGTH_LONG).show();
							destroy();
						} catch (JSONException e) {
							mLoadDialog.dismiss();
							e.printStackTrace();
						}
					}

				}, this);
	}

	private void destroy() {
		setResult(20);
		// BasicNameValuePair bvp = new BasicNameValuePair("mTag", "6");
		// IntentUtil.start_activity(EditInfoActivity.this, MainActivity.class,
		// bvp);
		finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Constants.ResultCode.QUERY_CHOOSE_MODEL) {
			// 获取所选车型
			et_vehicle_model.setText(data.getStringExtra("model"));
		} else if (resultCode == Constants.ResultCode.QUERY_CHOOSE_LENGTH) {
			// 获取所选车长
			et_vehicle_length.setText(data.getStringExtra("length"));
		} else if (resultCode == Constants.ResultCode.QUERY_CHOOSE_LOAD) {
			// 获取所选车载重
			et_vehicle_load.setText(data.getStringExtra("weight"));
		}
	}
}
