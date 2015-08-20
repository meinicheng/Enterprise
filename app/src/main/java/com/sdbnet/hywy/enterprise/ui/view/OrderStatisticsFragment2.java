package com.sdbnet.hywy.enterprise.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
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
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.model.Statistical;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.OrderListActivity;
import com.sdbnet.hywy.enterprise.ui.base.BaseFrament;
import com.sdbnet.hywy.enterprise.ui.widget.PopMenu;
import com.sdbnet.hywy.enterprise.ui.widget.PopMenu.OnItemClickListener;
import com.sdbnet.hywy.enterprise.ui.widget.PullToRefreshLayout;
import com.sdbnet.hywy.enterprise.ui.widget.PullToRefreshLayout.OnRefreshListener;
import com.sdbnet.hywy.enterprise.ui.widget.PullableExpandableListView;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.UtilsError;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderStatisticsFragment2 extends BaseFrament implements
		OnItemClickListener {
	private static final String TAG = "OrderQuantityStatisticsFragment";
	private PullableExpandableListView mPlvOrderList;
	private PullToRefreshLayout mPullToRefreshLayout;
	private boolean isFirstLoad = true;
	private LinearLayout mLlNoRecord;

	private TextView mTextOrderStatu;

	private List<Statistical> list = new ArrayList<Statistical>();
	private Map<String, Map<Integer, Integer>> mapDateStatistical = new HashMap<String, Map<Integer, Integer>>();
	private SparseArray<String> workflowMap = new SparseArray<String>(); // 保存工作流

	private int pageNo = 1;
	private int pageCount = 0;

	// private StatisticalAdapter adapter;
	private OnClickListener listener;
	protected PopMenu popMenu;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_order_list, null);
		initControls(view);
		// loadQueryData(true);
		return view;
	}

	private void initControls(View view) {
		mTextOrderStatu = (TextView) getActivity().findViewById(
				R.id.common_view_title_btn);
		// mTextOrderStatu.setVisibility(View.VISIBLE);
		mTextOrderStatu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 初始化弹出菜单
				popMenu = new PopMenu(getActivity());
				popMenu.addItems(new String[] { "菜单一", "菜单二", "菜单三", "菜单四" });
				popMenu.setOnItemClickListener(OrderStatisticsFragment2.this);
				popMenu.showAsDropDown(v);
			}
		});
		mPlvOrderList = (PullableExpandableListView) view
				.findViewById(R.id.fragment_order_list_expand_list);
		mPullToRefreshLayout = (PullToRefreshLayout) view
				.findViewById(R.id.fragment_order_list_refresh_view);
		mLlNoRecord = (LinearLayout) view.findViewById(R.id.view_no_record);

	}

	private MyExpandListAdapter expandListAdapter;

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

	private final int MSG_REFRESH_FINISH = 10;
	private final int MSG_LOAD_MORE_FINISH = 11;
	private Handler mHandler;

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
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
			};
		};
	}

	private View lay_loading;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initHandler();
		initExpandableListView();
		loadQueryData(true);
	}

	private void initExpandableListView() {
		mPullToRefreshLayout.setOnRefreshListener(new MyListener());
		expandListAdapter = new MyExpandListAdapter();
		mPlvOrderList.setAdapter(expandListAdapter);

		mPlvOrderList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent data = new Intent(getActivity(), OrderListActivity.class);
				ChildModel childModel = mChildModelsList.get(groupPosition)
						.get(childPosition);
				Log.e(TAG, childModel + "");
				// Bundle bundle = new Bundle();
				// bundle.putSerializable("order_query", childModel);
				// data.putExtras(bundle);
				//
				// getActivity().startActivity(data);
				return true;
			}
		});

		mPlvOrderList.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
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
						setLoadViewVisibility(View.VISIBLE);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						errorHolder(isRefresh);
						isFirstLoad = false;
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
							errorHolder(isRefresh);
							isFirstLoad = false;
							e.printStackTrace();
						}
					}

				}, getActivity());
	}

	private void errorHolder(boolean isRefresh) {
		if (mHandler != null && !isFirstLoad) {
			if (isRefresh) {

				mHandler.sendEmptyMessageDelayed(MSG_REFRESH_FINISH, 5 * 1000);
			} else {
				mHandler.sendEmptyMessageDelayed(MSG_LOAD_MORE_FINISH, 5 * 1000);
			}
		}
		showMsgLong(R.string.httpisNull);
		setLoadViewVisibility(View.GONE);
	}

	private void setLoadViewVisibility(int isVisible) {
		if (null == lay_loading) {
			lay_loading = (getActivity().findViewById(R.id.ll_loading));
		}
		lay_loading.setVisibility(isVisible);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onItemClick(int index) {
		Toast.makeText(getActivity(), "item clicked " + index + "!",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mTextOrderStatu.setVisibility(View.GONE);
	}

	private void parseJson(JSONObject response, boolean isRefresh)
			throws Exception {
		if (UtilsError.isErrorCode(getActivity(), response)
				|| response.getInt("pageCount") == 0) {
			mLlNoRecord.setVisibility(View.VISIBLE);
			setLoadViewVisibility(View.GONE);
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

				childModels = new ArrayList<OrderStatisticsFragment2.ChildModel>();
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
		// mLlNoRecord.setVisibility(View.GONE);
		setLoadViewVisibility(View.GONE);
		Log.e(TAG, mGroupModels.size() + "," + mChildModelsList.size() + "<"
				+ mHandler);

		if (mHandler != null && !isFirstLoad) {
			if (isRefresh) {
				mHandler.sendEmptyMessageDelayed(MSG_REFRESH_FINISH, 5 * 1000); // 刷新
			} else {
				mHandler.sendEmptyMessageDelayed(MSG_LOAD_MORE_FINISH, 5 * 1000); // 加载更多
			}
		}
		for (GroupModel model : mGroupModels) {
			Log.i(TAG, "GroupModel=>" + model.count + "<" + model.ordtime);
		}
		for (List<ChildModel> child : mChildModelsList) {
			if (child != null)
				for (ChildModel model : child) {
					Log.i(TAG, model + ":");
				}
			else {
				Log.e(TAG, "child==null");
			}
		}
		isFirstLoad = false;
	}

	List<GroupModel> mGroupModels = new ArrayList<OrderStatisticsFragment2.GroupModel>();
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
				convertView = View.inflate(getActivity(),
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
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.expand_child_item, null);
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
			return convertView;

		}

		@Override
		public int getGroupCount() {
			if (mGroupModels == null) {
				mGroupModels = new ArrayList<OrderStatisticsFragment2.GroupModel>();
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
