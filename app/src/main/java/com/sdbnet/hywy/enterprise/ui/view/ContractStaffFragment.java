package com.sdbnet.hywy.enterprise.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.GroupMotorsActivity;
import com.sdbnet.hywy.enterprise.ui.base.BaseFrament;
import com.sdbnet.hywy.enterprise.ui.widget.DialogLoading;
import com.sdbnet.hywy.enterprise.ui.widget.RTPullListView;
import com.sdbnet.hywy.enterprise.ui.widget.RTPullListView.OnRefreshListener;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.UtilsError;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContractStaffFragment extends BaseFrament {
	LayoutInflater mInflater;
	protected View view;

	private RTPullListView lv_groups;
	private GroupsAdapter adapter;
	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();

	private TextView tvNoRecordMsg;
	private LinearLayout noRecord;
	private static final int LOAD_NEW_INFO = 15;

	private DialogLoading mLoadDailog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceonCreateViewState) {
		mInflater = inflater;
		view = inflater.inflate(R.layout.fragment_motor_vehicles, null);
		initControls();
		loadData();
		return view;
	}

	/**
	 * 初始化控件
	 */
	private void initControls() {
		lv_groups = (RTPullListView) view
				.findViewById(R.id.fragment_motor_vehicles_lv_groups);

		// 下拉刷新接口实现
		lv_groups.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				list.clear();
				loadData();
			}
		});

		noRecord = (LinearLayout) view.findViewById(R.id.view_no_record);
		tvNoRecordMsg = (TextView) view.findViewById(R.id.tvNoRecordMsg);
		tvNoRecordMsg.setText(getString(R.string.no_workgroup_tip_msg));
		mLoadDailog = new DialogLoading(getActivity(),
				getString(R.string.loading));
	}

	private void doFailure() {
		Toast.makeText(getActivity(),
				getResources().getString(R.string.httpisNull),
				Toast.LENGTH_LONG).show();
		mLoadDailog.dismiss();
	}

	/**
	 * 加载工作组信息
	 */
	private void loadData() {
		AsyncHttpService.getErpGroups("1", new JsonHttpResponseHandler() {

			@Override
			public void onStart() {
				super.onStart();
				mLoadDailog.show();
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

					if (UtilsError.isErrorCode(getActivity(), response)) {
						return;
					}
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
					if (list.size() > 0) {
						noRecord.setVisibility(View.GONE);
					} else {
						noRecord.setVisibility(View.VISIBLE);
					}

					myHandler.sendEmptyMessage(LOAD_NEW_INFO);
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					mLoadDailog.dismiss();
				}
			}

		}, getActivity());
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
				convertView = View.inflate(getActivity(),
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
					Intent data = new Intent();
					data.putExtra(Constants.Params.PARAM_REDICT, 2);
					data.putExtra(Constants.Feild.KEY_GROUP_ID,
							val.get(Constants.Feild.KEY_GROUP_ID));
					data.putExtra(Constants.Feild.KEY_GROUP_NAME,
							val.get(Constants.Feild.KEY_GROUP_NAME));
					data.setClass(getActivity(), GroupMotorsActivity.class);
					startActivity(data);
					return;
				}
			});
			return convertView;
		}
	}

	static class ViewHolder {
		TextView tv_group_name;
		LinearLayout layout;
	}

	private Handler myHandler;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		myHandler = new Handler() {
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
	}
}
