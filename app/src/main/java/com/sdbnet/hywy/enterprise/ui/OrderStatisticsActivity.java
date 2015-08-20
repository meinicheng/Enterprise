package com.sdbnet.hywy.enterprise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.OrderListActivity;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.ui.widget.PullToRefreshLayout;
import com.sdbnet.hywy.enterprise.ui.widget.PullToRefreshLayout.OnRefreshListener;
import com.sdbnet.hywy.enterprise.ui.widget.PullableExpandableListView;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.SerializableMap;
import com.sdbnet.hywy.enterprise.utils.UtilsError;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderStatisticsActivity extends BaseActivity {
	private static final String TAG = "OrderStatisticsActivity";
	private PullableExpandableListView mPlvOrderList;
	private PullToRefreshLayout mPullToRefreshLayout;
	// private boolean isFirstLoad = true;
	private LinearLayout mLlNoRecord;

	private int pageNo = 1;
	private int pageCount = 0;

	private MyExpandListAdapter expandListAdapter;

	private final int MSG_REFRESH_FINISH = 10;
	private final int MSG_LOAD_MORE_FINISH = 11;
	private Handler mHandler;

	private void initHandler() {
		if (mHandler == null)
			mHandler = new Handler(new Handler.Callback() {

				@Override
				public boolean handleMessage(Message msg) {

					Log.e(TAG, "handleMessage=" + msg.what + "");
					switch (msg.what) {
					case MSG_REFRESH_FINISH:
						mPullToRefreshLayout
								.refreshFinish(PullToRefreshLayout.SUCCEED);
						expandListAdapter.notifyDataSetChanged();
						break;
					case MSG_LOAD_MORE_FINISH:
						mPullToRefreshLayout
								.loadmoreFinish(PullToRefreshLayout.SUCCEED);
						expandListAdapter.notifyDataSetChanged();
						break;
					default:
						break;
					}
					return false;
				}
			});
	}

	private View mLayLoading;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_statistics_list);
		initControls();
		initHandler();
		initExpandableListView();
		loadQueryData(true);
	}

	private void initControls() {
		findViewById(R.id.common_view_title_img).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();

					}
				});
		((TextView) findViewById(R.id.common_view_title_text))
				.setText(R.string.menu_order_list);
		mPlvOrderList = (PullableExpandableListView) findViewById(R.id.fragment_order_list_expand_list);
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.fragment_order_list_refresh_view);
		mLayLoading = (findViewById(R.id.common_ll_loading));
		mLlNoRecord = (LinearLayout) findViewById(R.id.view_no_record);

	}

	public class MyListener implements OnRefreshListener {

		@Override
		public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
			// 下拉刷新操作
			mPullToRefreshLayout = pullToRefreshLayout;
			Log.v(TAG, "refresh");
			pageNo = 1;
			pageCount = 0;
			// list.clear();
			// mGroupModels.clear();
			// mChildModelsList.clear();
			loadQueryData(true);
		}

		@Override
		public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
			// 加载操作
			mPullToRefreshLayout = pullToRefreshLayout;
			Log.v(TAG, "onLoadMore");
			if (pageNo < pageCount) {
				pageNo++;
				loadQueryData(false);
			} else {
				mPullToRefreshLayout.noData();
			}
		}

	}

	private void initExpandableListView() {
		mPullToRefreshLayout.setOnRefreshListener(new MyListener());
		expandListAdapter = new MyExpandListAdapter();
		mPlvOrderList.setAdapter(expandListAdapter);

		mPlvOrderList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent data = new Intent(OrderStatisticsActivity.this,
						OrderListActivity.class);
				ChildModel childModel = mChildModelsList.get(groupPosition)
						.get(childPosition);

				Map<String, String> mapParams = new HashMap<String, String>();
				mapParams.put(Constants.Params.METHOD,
						Constants.Params.METHOD_DETAIL_STATISTICAL);
				mapParams.put(Constants.Feild.KEY_COMPANY_ID,
						PreferencesUtil.user_company);
				mapParams.put(Constants.Feild.KEY_ITEM_ID,
						PreferencesUtil.item_id);
				mapParams.put(Constants.Feild.KEY_ORDER_CREATE_TIME,
						childModel.ordtime);
				mapParams.put(Constants.Params.PARAM_WORKFLOW,
						childModel.workflow);

				SerializableMap map = new SerializableMap();
				map.setMap(mapParams);

				Bundle bundle = new Bundle();
				bundle.putSerializable("order_query", map);
				data.putExtras(bundle);

				OrderStatisticsActivity.this.startActivity(data);
				return true;
			}
		});

		mPlvOrderList.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// right_arrow
				// ((BaseExpandableListAdapter)parent.getAdapter()).getGroup(groupPosition)
				if (parent.isGroupExpanded(groupPosition)) {
					// 如果展开则关闭
					parent.collapseGroup(groupPosition);
				} else {
					// 如果关闭则打开，注意这里是手动打开不要默认滚动否则会有bug
					parent.expandGroup(groupPosition);
				}
				return true;
			}
		});
	}

	private void loadQueryData(final boolean isRefresh) {
		AsyncHttpService.getStatisticalInfos(pageNo,
				new JsonHttpResponseHandler() {
					@Override
					public void onStart() {
						super.onStart();
						setViewVisibility(VIEW_LOADING_VISIBLE);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						loadError(isRefresh);
						// isFirstLoad = false;
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						System.out.println("query order: " + response);
						super.onSuccess(statusCode, headers, response);
						// parseJSON(isRefresh, response);
						try {
							parseJson(response, isRefresh);
						} catch (Exception e) {
							loadError(isRefresh);
							// isFirstLoad = false;
							e.printStackTrace();
						}
					}

				}, this);
	}

	private void loadError(boolean isRefresh) {
		// if (mHandler != null && !isFirstLoad) {
		if (mHandler != null) {
			if (isRefresh) {

				mHandler.sendEmptyMessageDelayed(MSG_REFRESH_FINISH, 5 * 1000);
			} else {
				mHandler.sendEmptyMessageDelayed(MSG_LOAD_MORE_FINISH, 5 * 1000);
			}
		}
		showShortToast(R.string.httpisNull);
		setViewVisibility(VIEW_LIST_REFRESH_VISIBLE);
	}

	private final int VIEW_LOADING_VISIBLE = 1;
	private final int VIEW_NO_RECORD_VISIBLE = 2;
	private final int VIEW_LIST_REFRESH_VISIBLE = 3;

	private void setViewVisibility(int type) {
		switch (type) {
		case VIEW_LOADING_VISIBLE:
			mLayLoading.setVisibility(View.VISIBLE);
			mLlNoRecord.setVisibility(View.GONE);
//			mPullToRefreshLayout.setVisibility(View.GONE);
			mPlvOrderList.setVisibility(View.GONE);
			break;
		case VIEW_NO_RECORD_VISIBLE:
			mLayLoading.setVisibility(View.GONE);
			mLlNoRecord.setVisibility(View.VISIBLE);
//			mPullToRefreshLayout.setVisibility(View.GONE);
			mPlvOrderList.setVisibility(View.GONE);
			break;
		case VIEW_LIST_REFRESH_VISIBLE:
			mLayLoading.setVisibility(View.GONE);
			mLlNoRecord.setVisibility(View.GONE);
//			mPullToRefreshLayout.setVisibility(View.VISIBLE);
			mPlvOrderList.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void parseJson(JSONObject response, boolean isRefresh)
			throws Exception {
		if (UtilsError.isErrorCode(this, response)
				|| response.getInt("pageCount") == 0) {
			setViewVisibility(VIEW_NO_RECORD_VISIBLE);
			return;
		}
		if (isRefresh) {
			mGroupModels.clear();
			mChildModelsList.clear();
		}

		pageNo = response.getInt("pageNo");
		pageCount = response.getInt("pageCount");

		JSONArray arrs = response.getJSONArray(Constants.Feild.KEY_ORDERS);

		List<ChildModel> childModels = null;
		JSONObject jsonObj;
		GroupModel groupModel = new GroupModel();

		ChildModel childModel;

		// 解析所有订单数据
		for (int i = 0; i < arrs.length(); i++) {
			jsonObj = arrs.getJSONObject(i);
			childModel = new ChildModel();

			childModel.ordtime = jsonObj
					.getString(Constants.Feild.KEY_ORDER_CREATE_TIME);
			childModel.count = jsonObj
					.getInt(Constants.Feild.KEY_STATISTICAL_COUNT);
			childModel.workflow = jsonObj
					.getString(Constants.Feild.KEY_WORKFLOW);
			childModel.wfname = jsonObj
					.getString(Constants.Feild.KEY_WORKFLOW_NAME);
			Log.i(TAG, childModel + ":" + groupModel.ordtime);
			if (TextUtils.equals(childModel.ordtime, groupModel.ordtime)) {
				groupModel.count += childModel.count;
				childModels.add(childModel);

			} else {
				if (childModels != null) {
					mChildModelsList.add(childModels);
				}

				childModels = new ArrayList<OrderStatisticsActivity.ChildModel>();
				childModels.add(childModel);

				groupModel = new GroupModel();
				groupModel.ordtime = childModel.ordtime;
				groupModel.count = childModel.count;
				mGroupModels.add(groupModel);
			}
			if (i == arrs.length() - 1) {
				if (childModel != null) {
					mChildModelsList.add(childModels);
				}
			}
		}
		setViewVisibility(VIEW_LIST_REFRESH_VISIBLE);
		Log.e(TAG, mGroupModels.size() + "," + mChildModelsList.size() + "<"
				+ mHandler);

		// if (mHandler != null && !isFirstLoad) {
		if (mHandler != null) {
			if (isRefresh) {
				mHandler.sendEmptyMessageDelayed(MSG_REFRESH_FINISH, 1000); // 刷新
			} else {
				mHandler.sendEmptyMessageDelayed(MSG_LOAD_MORE_FINISH, 1000); // 加载更多
			}
		}
//		for (GroupModel model : mGroupModels) {
//			Log.i(TAG, "GroupModel=>" + model.count + "<" + model.ordtime);
//		}
//		for (List<ChildModel> child : mChildModelsList) {
//			if (child != null)
//				for (ChildModel model : child) {
//					Log.i(TAG, model + ":");
//				}
//			else {
//				Log.e(TAG, "child==null");
//			}
//		}
		// isFirstLoad = false;
	}

	List<GroupModel> mGroupModels = new ArrayList<OrderStatisticsActivity.GroupModel>();
	List<List<ChildModel>> mChildModelsList = new ArrayList<List<ChildModel>>();

	class GroupModel {
		String ordtime;
		int count;

	}

	class ChildModel implements Serializable {
		// {"count":2,"workflow":"1","wfname":"纯配送订单","ordtime":"2015-06-24"}
		int count;
		String workflow;
		String wfname;
		String ordtime;

		@Override
		public String toString() {
			return "ChildModel [count=" + count + ", workflow=" + workflow
					+ ", wfname=" + wfname + ", ordtime=" + ordtime + "]";
		}

	}

	class MyExpandListAdapter extends BaseExpandableListAdapter {
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			HolderGroup group;
			GroupModel model = mGroupModels.get(groupPosition);
			// Log.i(TAG, model + "," + groupPosition + "," + getGroupCount());
			if (convertView == null) {
				group = new HolderGroup();
				convertView = View.inflate(OrderStatisticsActivity.this,
						R.layout.list_item_order_statistics, null);
				group.tv = (TextView) convertView
						.findViewById(R.id.tv_time_statistics);
				group.img = (ImageView) convertView
						.findViewById(R.id.iv_arrow_right);
				convertView.setTag(group);
			} else {
				group = (HolderGroup) convertView.getTag();
			}
			if (model != null) {
				group.tv.setText(String.format(
						getString(R.string.x_time_total_x_num), model.ordtime,
						model.count));
			}
			if (isExpanded) {
				group.img.setImageResource(R.drawable.down_arrow);
			} else {
				group.img.setImageResource(R.drawable.right_arrow);
			}
			return convertView;

		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			ChildModel model = mChildModelsList.get(groupPosition).get(
					childPosition);
			// Log.i(TAG, model + "," + groupPosition + "," + childPosition +
			// "<"
			// + getChildrenCount(groupPosition));
			HolderChild child;
			if (convertView == null) {
				child = new HolderChild();
				convertView = LayoutInflater.from(OrderStatisticsActivity.this)
						.inflate(R.layout.expand_child_item, null);
				child.tv = (TextView) convertView
						.findViewById(R.id.expand_child_item_text_msg);
				child.view = convertView
						.findViewById(R.id.expand_child_item_line);
				convertView.setTag(child);
			} else {
				child = (HolderChild) convertView.getTag();

			}
			if (model != null) {
				child.tv.setText(String.format(
						getString(R.string.x_name_total_x_num), model.wfname
								+ "", model.count));

			}
			if (mChildModelsList.get(groupPosition).size() == (childPosition + 1)) {
				child.view.setVisibility(View.INVISIBLE);
			} else {
				child.view.setVisibility(View.VISIBLE);
			}
			return convertView;

		}

		@Override
		public int getGroupCount() {
			if (mGroupModels == null) {
				mGroupModels = new ArrayList<OrderStatisticsActivity.GroupModel>();
				return 0;
			} else {
				return mGroupModels.size();
			}
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (mChildModelsList == null) {
				mChildModelsList = new ArrayList<List<ChildModel>>();
				Log.e(TAG, "groupPosition=" + groupPosition);
				return 0;
			} else if (mChildModelsList.get(groupPosition) == null) {
				Log.e(TAG,
						"groupPosition=" + groupPosition + ","
								+ mChildModelsList + "<"
								+ mChildModelsList.get(groupPosition));
				return 0;
			} else {
				return mChildModelsList.get(groupPosition).size();
			}
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mGroupModels.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mChildModelsList.get(groupPosition).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		class HolderGroup {
			TextView tv;
			ImageView img;
		}

		class HolderChild {
			TextView tv;
			View view;
		}
	}
}
