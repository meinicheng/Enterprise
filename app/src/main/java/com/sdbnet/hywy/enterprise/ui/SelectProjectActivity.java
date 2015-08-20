package com.sdbnet.hywy.enterprise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.ActivityStackManager;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.UtilsError;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectProjectActivity extends BaseActivity {

	private List<String> list = new ArrayList<String>();
	private List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
	private ProjectsAdapter adapter;
	private ListView lv_projects;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_projects);
		initControls();
		loadDatas();
	}

	private void initControls() {
		lv_projects = (ListView) findViewById(R.id.lv_projects);
	}

	/**
	 * 加载用户所属项目
	 */
	private void loadDatas() {
		String items = PreferencesUtil.getValue(PreferencesUtil.KEY_ITEMS, "");
		if (!TextUtils.isEmpty(items)) {
			try {
				Map<String, String> map = null;
				JSONObject jsonObj = null;
				JSONArray arrs = new JSONArray(items);
				for (int i = 0; i < arrs.length(); i++) {
					jsonObj = arrs.getJSONObject(i);
					map = new HashMap<String, String>();
					map.put(Constants.Feild.KEY_COMPANY_ID,
							jsonObj.getString(Constants.Feild.KEY_COMPANY_ID));

					String cmpname = jsonObj
							.getString(Constants.Feild.KEY_COMPANY_NAME);
					map.put(Constants.Feild.KEY_COMPANY_NAME, cmpname);

					String itemname = jsonObj
							.getString(Constants.Feild.KEY_ITEM_NAME);
					map.put(Constants.Feild.KEY_ITEM_NAME, itemname);

					map.put(Constants.Feild.KEY_ITEM_ID,
							jsonObj.getString(Constants.Feild.KEY_ITEM_ID));
					map.put(Constants.Feild.KEY_STAFF_ID,
							jsonObj.getString(Constants.Feild.KEY_STAFF_ID));
					map.put(Constants.Feild.KEY_STAFF_NAME,
							jsonObj.getString(Constants.Feild.KEY_STAFF_NAME));

					maps.add(map);
					list.add(String.format("%s - %s", cmpname, itemname));
				}

				adapter = new ProjectsAdapter();
				lv_projects.setAdapter(adapter);
			} catch (JSONException e) {
				// dia.dismiss();
				e.printStackTrace();
			}
		}
	}

	/**
	 * 选择项目
	 * 
	 * @param selected
	 */
	public void confirmSelected(Map<String, String> selected) {
		if (selected != null) {
			String cmpid = selected.get(Constants.Feild.KEY_COMPANY_ID);
			String itemid = selected.get(Constants.Feild.KEY_ITEM_ID);
			String pid = selected.get(Constants.Feild.KEY_STAFF_ID);
			PreferencesUtil.putValue(PreferencesUtil.KEY_COMPANY_ID, cmpid);
			PreferencesUtil.putValue(PreferencesUtil.KEY_COMPANY_NAME,
					selected.get(Constants.Feild.KEY_COMPANY_NAME));
			PreferencesUtil.putValue(PreferencesUtil.KEY_ITEM_ID, itemid);
			PreferencesUtil.putValue(PreferencesUtil.KEY_ITEM_NAME,
					selected.get(Constants.Feild.KEY_ITEM_NAME));
			PreferencesUtil.putValue(PreferencesUtil.KEY_USER_ID, pid);
			PreferencesUtil.putValue(PreferencesUtil.KEY_USER_NAME,
					selected.get(Constants.Feild.KEY_STAFF_NAME));

			PreferencesUtil.initStoreData();

			getPermission(cmpid, itemid, pid);
		}
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
					public void onStart() {
						showLoading(getString(R.string.xlistview_header_hint_loading));
						super.onStart();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						dismissLoading();
						showShortToast(getResources().getString(
								R.string.httpisNull));

					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						System.out.println("onsuccess: " + response.toString());
						super.onSuccess(statusCode, headers, response);
						try {

							if (UtilsError.isErrorCode(
									SelectProjectActivity.this, response)) {
								return;
							}

							JSONObject jsonObj = response
									.getJSONObject(Constants.Feild.KEY_STAFF);
							String menu = jsonObj
									.getString(Constants.Feild.KEY_MENU);
							if (TextUtils.isEmpty(menu)) {
								Toast.makeText(SelectProjectActivity.this,
										"您暂无操作该项目的权限，请重新选择", Toast.LENGTH_SHORT)
										.show();
								return;
							}
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
									.getString(Constants.Feild.KEY_EXECUTE_ACTION); // 保存动作权限
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
							if (!jsonObj.isNull(Constants.Feild.KEY_OPTIONS)) {
								String options = jsonObj
										.getString(Constants.Feild.KEY_OPTIONS);
								PreferencesUtil.putValue(
										Constants.Feild.KEY_OPTIONS, options);
							}
							PreferencesUtil.initStoreData();

							// 上传设备信息
							uploadDeviceInfo();

							// 进入主界面
							Intent intent = new Intent(
									SelectProjectActivity.this,
									MainActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
							ActivityStackManager.getStackManager()
									.popAllActivitys();
						} catch (Exception ex) {
							ex.printStackTrace();
							showShortToast("网络繁忙，请稍后重试");

						} finally {
							dismissLoading();
						}
					}
				}, SelectProjectActivity.this);
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
						System.out.println("onsuccess: " + response.toString());
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
									returnLogin(SelectProjectActivity.this,
											msg, null);
									break;
								case 42:
									returnLogin(SelectProjectActivity.this,
											msg, null);
									break;
								default:
									break;
								}
								return;
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							showShortToast("网络繁忙，请稍后重试");
						}
					}

				}, this);
	}

	class ProjectsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Map<String, String> map = maps.get(position);
			ViewHolder viewHolder;
			if (null == convertView) {
				viewHolder = new ViewHolder();
				convertView = View.inflate(SelectProjectActivity.this,
						R.layout.list_item_projects, null);
				viewHolder.tv_project = (TextView) convertView
						.findViewById(R.id.tv_project);

				viewHolder.layout = (LinearLayout) convertView
						.findViewById(R.id.layout_AutoPlay);
				convertView.setTag(viewHolder);
				// 项目列表项单击事件
				viewHolder.layout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						confirmSelected(map);
					}
				});
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.tv_project.setText(list.get(position));
			return convertView;
		}
	}

	static class ViewHolder {
		TextView tv_project;
		LinearLayout layout;
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
