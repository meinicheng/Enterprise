package com.sdbnet.hywy.enterprise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.ui.widget.RTPullListView;
import com.sdbnet.hywy.enterprise.ui.widget.RTPullListView.OnRefreshListener;
import com.sdbnet.hywy.enterprise.utils.Constants;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseGroupsActivity extends BaseActivity {

	private RTPullListView lv_groups;
	private GroupsAdapter adapter;
	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();

	private LinearLayout loadingLayout;
	private TextView tvNoRecordMsg;
	private LinearLayout noRecord;
	private static final int LOAD_NEW_INFO = 15;
	private int redict = 0;

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_NEW_INFO:
				if (lv_groups.getAdapter() == null) {
					lv_groups.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
					lv_groups.onRefreshComplete();
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_roles);
		initControls();
		loadData();
	}

	/**
	 * 初始化控件
	 */
	private void initControls() {
		lv_groups = (RTPullListView) findViewById(R.id.lv_groups);

		// 下拉刷新接口实现
		lv_groups.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				list.clear();
				loadData();
			}
		});

		findViewById(R.id.common_view_title_img).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						finish();
					}
				});
		((TextView) findViewById(R.id.common_view_title_text)).setText("选择工作组");

		noRecord = (LinearLayout) findViewById(R.id.view_no_record);
		tvNoRecordMsg = (TextView) findViewById(R.id.tvNoRecordMsg);
		tvNoRecordMsg.setText("没有可以使用的工作组，赶快添加自己的工作组吧");
		loadingLayout = (LinearLayout) findViewById(R.id.ll_loading);

		getParams();
	}

	private void getParams() {
		redict = getIntent().getIntExtra(Constants.Params.PARAM_REDICT, 0);
	}

	private void doFailure() {
		showLongToast(getResources().getString(R.string.httpisNull));
		loadingLayout.setVisibility(View.GONE);
	}

	/**
	 * 加载工作组信息
	 */
	private void loadData() {
		AsyncHttpService.getErpGroups("1", new JsonHttpResponseHandler() {

			@Override
			public void onStart() {
				super.onStart();
				loadingLayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				doFailure();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				System.out.println("group: " + response.toString());
				super.onSuccess(statusCode, headers, response);
				try {
					errorHandler(response);

					list.clear();

					JSONArray arrs = response
							.getJSONArray(Constants.Feild.KEY_WORK_GROUPS);
					JSONObject jsonObj = null;
					Map<String, String> map = null;
					for (int i = 0; i < arrs.length(); i++) {
						jsonObj = arrs.getJSONObject(i);
						map = new HashMap<String, String>();
						map.put(Constants.Feild.KEY_GROUP_ID,
								jsonObj.getString(Constants.Feild.KEY_GROUP_ID));
						map.put(Constants.Feild.KEY_GROUP_NAME, jsonObj
								.getString(Constants.Feild.KEY_GROUP_NAME));
						map.put(Constants.Feild.KEY_GROUP_IS_LOCATION,
								jsonObj.getString(Constants.Feild.KEY_GROUP_IS_LOCATION));
						list.add(map);
					}
					adapter = new GroupsAdapter();
					lv_groups.setAdapter(adapter);

					System.out.println("list.size: " + list.size());
					loadingLayout.setVisibility(View.GONE);
					if (list.size() > 0) {
						noRecord.setVisibility(View.GONE);
					} else {
						noRecord.setVisibility(View.VISIBLE);
					}

					myHandler.sendEmptyMessage(LOAD_NEW_INFO);
				} catch (Exception ex) {
					ex.printStackTrace();
					loadingLayout.setVisibility(View.GONE);
				}
			}

		}, this);
	}

	private void errorHandler(JSONObject response) throws JSONException {
		if (response.getInt(Constants.Feild.KEY_ERROR_CODE) != 0) {
			String msg = response.getString(Constants.Feild.KEY_MSG);
			switch (response.getInt(Constants.Feild.KEY_ERROR_CODE)) {
			case 41:
				returnLogin(ChooseGroupsActivity.this, msg, null);
				break;
			case 42:
				returnLogin(ChooseGroupsActivity.this, msg, null);
				break;
			default:
				showLongToast(msg);
				break;
			}
			loadingLayout.setVisibility(View.GONE);
			return;
		}
	}

	class GroupsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Map<String, String> map = list.get(position);
			ViewHolder viewHolder;
			if (null == convertView) {
				viewHolder = new ViewHolder();
				convertView = View.inflate(ChooseGroupsActivity.this,
						R.layout.list_item_roles, null);
				viewHolder.tv_group_name = (TextView) convertView
						.findViewById(R.id.tv_group_name);
				viewHolder.layout = (LinearLayout) convertView
						.findViewById(R.id.layout_AutoPlay);
				convertView.setTag(viewHolder);
				// convertView.setOnClickListener();
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.tv_group_name.setText(map
					.get(Constants.Feild.KEY_GROUP_NAME));
			viewHolder.layout.setTag(map);
			viewHolder.layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Map<String, String> val = (Map<String, String>) v.getTag();
					if (redict == 1) {
						Intent data = new Intent();
						data.putExtra(Constants.Params.PARAM_REDICT, redict);
						data.putExtra(Constants.Feild.KEY_GROUP_ID,
								val.get(Constants.Feild.KEY_GROUP_ID));
						data.putExtra(Constants.Feild.KEY_GROUP_NAME,
								val.get(Constants.Feild.KEY_GROUP_NAME));
						data.setClass(ChooseGroupsActivity.this,
								GroupMotorsActivity.class);
						startActivity(data);
						return;
					} else if (redict == 2) {
						Intent data = new Intent();
						data.putExtra(Constants.Params.PARAM_REDICT, redict);
						data.putExtra(Constants.Feild.KEY_GROUP_ID,
								val.get(Constants.Feild.KEY_GROUP_ID));
						data.putExtra(Constants.Feild.KEY_GROUP_NAME,
								val.get(Constants.Feild.KEY_GROUP_NAME));
						data.setClass(ChooseGroupsActivity.this,
								GroupMotorsActivity.class);
						startActivity(data);
						return;
					}
					// 将所选工作组信息传回location界面
					Intent data = new Intent();
					data.putExtra(Constants.Feild.KEY_GROUP_ID,
							val.get(Constants.Feild.KEY_GROUP_ID));
					setResult(Constants.ResultCode.CHOOSE_GROUP, data);
					finish();
				}
			});
			return convertView;
		}
	}

	static class ViewHolder {
		TextView tv_group_name;
		LinearLayout layout;
	}
}