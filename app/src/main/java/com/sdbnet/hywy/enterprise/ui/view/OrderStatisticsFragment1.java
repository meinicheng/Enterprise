package com.sdbnet.hywy.enterprise.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
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
import com.sdbnet.hywy.enterprise.ui.widget.RTPullListView;
import com.sdbnet.hywy.enterprise.ui.widget.RTPullListView.OnAutoLoadingListener;
import com.sdbnet.hywy.enterprise.ui.widget.RTPullListView.OnRefreshListener;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.LogUtil;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.SerializableMap;
import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;
import com.sdbnet.hywy.enterprise.utils.UtilsError;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderStatisticsFragment1 extends BaseFrament implements
		OnItemClickListener {
	private static final String TAG = "OrderQuantityStatisticsFragment";
	private RTPullListView mPlvOrderList;

	private LinearLayout mLlNoRecord;

	private TextView mTextOrderStatu;

	private List<Statistical> list = new ArrayList<Statistical>();
	private Map<String, Map<Integer, Integer>> mapDateStatistical = new HashMap<String, Map<Integer, Integer>>();
	private SparseArray<String> workflowMap = new SparseArray<String>(); // 保存工作流

	private int pageNo = 1;
	private int pageCount = 0;
	private RelativeLayout footerView;
	private static final int LOAD_NEW_INFO = 15;
	private static final int LOAD_MORE_SUCCESS = 6;
	private TextView mTextFootTip;

	private StatisticalAdapter adapter;
	private OnClickListener listener;
	protected PopMenu popMenu;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_order_list, null);
		initControls(view);
		loadQueryData(true);
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
				popMenu.setOnItemClickListener(OrderStatisticsFragment1.this);
				popMenu.showAsDropDown(v);
			}
		});
		mPlvOrderList = (RTPullListView) view
				.findViewById(R.id.fragment_order_list_plv_order_list);

		// 下拉刷新接口实现
		mPlvOrderList.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				pageNo = 1;
				pageCount = 0;
				list.clear();
				loadQueryData(true);
			}
		});

		// 滑动至底部加载更多接口实现
		mPlvOrderList.setonAutoLoadingListener(new OnAutoLoadingListener() {
			@Override
			public void onAutoLoading() {
				footerView.setVisibility(View.VISIBLE);
				if (pageNo < pageCount) {
					pageNo++;
					loadQueryData(false);
				} else {
					mTextFootTip.setText(getString(R.string.no_more));
				}
			}
		});

		mLlNoRecord = (LinearLayout) view.findViewById(R.id.view_no_record);

		// 滑动至底部刷新相关组件
		initFooterView();

		listener = new OnClickListener() {
			@Override
			public void onClick(final View v) {
				View view = v.findViewById(R.id.lay_order_statistics);
				view.setVisibility(View.VISIBLE);
			}
		};

		adapter = new StatisticalAdapter();
	}

	/**
	 * 给下拉刷新列表底部添加"获取更多"按钮（可自定义）
	 */
	private void initFooterView() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		footerView = (RelativeLayout) inflater.inflate(R.layout.list_footview,
				null);
		mTextFootTip = (TextView) footerView
				.findViewById(R.id.list_footview_tv_tip);
		footerView.setVisibility(View.GONE);
		mPlvOrderList.addFooterView(footerView);
	}

	/**
	 * 解析json
	 * 
	 * @param isRefresh
	 * @param response
	 */
	private void parseJSON(boolean isRefresh, JSONObject response) {
		Log.d(TAG, "response: " + response.toString());
		try {
			if (UtilsError.isErrorCode(getActivity(), response)) {
				mLlNoRecord.setVisibility(View.VISIBLE);
				footerView.setVisibility(View.GONE);
				setLoadViewVisibility(View.GONE);
				return;
			}
			if (response.getInt("pageCount") == 0) {
				footerView.setVisibility(View.GONE);
				mLlNoRecord.setVisibility(View.VISIBLE);
				setLoadViewVisibility(View.GONE);
				return;
			}
			pageNo = response.getInt("pageNo");
			pageCount = response.getInt("pageCount");

			JSONArray arrs = response.getJSONArray(Constants.Feild.KEY_ORDERS);
			JSONObject jsonObj = null;
			if (isRefresh) {
				list.clear();
				mapDateStatistical.clear();
			}

			String prefTime = "";

			// 解析所有订单数据
			for (int i = 0; i < arrs.length(); i++) {
				jsonObj = arrs.getJSONObject(i);
				String ordtime = jsonObj
						.getString(Constants.Feild.KEY_ORDER_CREATE_TIME);
				Map<Integer, Integer> workflowCountMap = mapDateStatistical
						.get(ordtime);

				// 根据工作流程统计订单数
				if (workflowCountMap == null) {
					workflowCountMap = new HashMap<Integer, Integer>();
					mapDateStatistical.put(ordtime, workflowCountMap);

					if (!TextUtils.isEmpty(prefTime)) {
						Statistical statistical = new Statistical(prefTime,
								mapDateStatistical.get(prefTime));
						list.add(statistical);
					}
				}

				int workflow = jsonObj.getInt(Constants.Feild.KEY_WORKFLOW);
				Log.i(TAG, String.format("ordtime %s; workflow %d; count %d; ",
						ordtime, workflow,
						jsonObj.getInt(Constants.Feild.KEY_STATISTICAL_COUNT)));
				workflowCountMap.put(workflow,
						jsonObj.getInt(Constants.Feild.KEY_STATISTICAL_COUNT));
				prefTime = ordtime;
				workflowMap.put(workflow,
						jsonObj.getString(Constants.Feild.KEY_WORKFLOW_NAME));
			}

			if (!TextUtils.isEmpty(prefTime)) {
				Statistical statistical = new Statistical(prefTime,
						mapDateStatistical.get(prefTime));
				list.add(statistical);
			}

			mLlNoRecord.setVisibility(View.GONE);
			footerView.setVisibility(View.GONE);
			setLoadViewVisibility(View.GONE);

			if (isRefresh) {
				myHandler.sendEmptyMessage(LOAD_NEW_INFO); // 刷新
			} else {
				myHandler.sendEmptyMessage(LOAD_MORE_SUCCESS); // 加载更多
			}
		} catch (Exception e) {
			e.printStackTrace();
			setLoadViewVisibility(View.GONE);
		}
	}

	private void errorHandler() {
		showMsgLong(R.string.httpisNull);
		footerView.setVisibility(View.GONE);
		setLoadViewVisibility(View.GONE);
	}

	private Handler myHandler;
	private View lay_loading;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case LOAD_MORE_SUCCESS: // 加载更多
					adapter.notifyDataSetChanged();
					mPlvOrderList.setSelectionfoot();
					footerView.setClickable(true);
					break;
				case LOAD_NEW_INFO: // 刷新
					if (mPlvOrderList.getAdapter() == null) {
						mPlvOrderList.setAdapter(adapter);
					} else {
						adapter.notifyDataSetChanged();
						mPlvOrderList.onRefreshComplete();
					}
					break;
				default:
					break;
				}
			}
		};
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
						errorHandler();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						System.out.println("query order: " + response);
						super.onSuccess(statusCode, headers, response);
						parseJSON(isRefresh, response);
					}

				}, getActivity());
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

	class StatisticalAdapter extends BaseAdapter {

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
			final View view = View.inflate(getActivity(),
					R.layout.list_item_order_statistics, null);
			final Statistical statistical = list.get(position);
			LogUtil.d(TAG, statistical + "ordtime:" + statistical.getDate());

			TextView tv_time_statistics = (TextView) view
					.findViewById(R.id.tv_time_statistics);

			int total = 0;
			for (Integer workflow : statistical.getWorkflowMap().keySet()) {
				total += statistical.getWorkflowMap().get(workflow);
			}
			tv_time_statistics.setText(String.format(
					getString(R.string.x_time_total_x_num),
					statistical.getDate(), total));

			final LinearLayout layout = (LinearLayout) view
					.findViewById(R.id.lay_item_click);
			if (position % 2 == 0) {
				layout.setBackgroundResource(R.drawable.list_choose_selector2);
			}

			final LinearLayout lay_order_statistics = (LinearLayout) view
					.findViewById(R.id.lay_order_statistics);
			lay_order_statistics.removeAllViews();

			int i = 1;
			for (Integer workflow : statistical.getWorkflowMap().keySet()) {
				int count = statistical.getWorkflowMap().get(workflow);
				String wfName = workflowMap.get(workflow);

				RelativeLayout rLay = new RelativeLayout(getActivity());
				LayoutParams rLayParams = new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						UtilsAndroid.UI.dip2px(getActivity(), 40));
				rLayParams.leftMargin = UtilsAndroid.UI.dip2px(getActivity(),
						25);
				rLayParams.gravity = Gravity.CENTER_VERTICAL;
				lay_order_statistics.addView(rLay, rLayParams);

				TextView tv = new TextView(getActivity());
				android.widget.RelativeLayout.LayoutParams tvParams = new android.widget.RelativeLayout.LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				tvParams.addRule(RelativeLayout.CENTER_VERTICAL);
				tv.setText(String.format(
						getString(R.string.x_name_total_x_num), wfName + "",
						count));
				tv.setTextColor(getResources().getColor(R.color.normal));
				rLay.addView(tv, tvParams);
				rLay.setTag(workflow);

				if (i != statistical.getWorkflowMap().size()) {
					View line = new View(getActivity());
					android.widget.RelativeLayout.LayoutParams lineParams = new android.widget.RelativeLayout.LayoutParams(
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
							UtilsAndroid.UI.dip2px(getActivity(), 0.5f));
					lineParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					lineParams.rightMargin = UtilsAndroid.UI.dip2px(
							getActivity(), 10);
					line.setBackgroundResource(R.color.grey2);
					rLay.addView(line, lineParams);
				}

				rLay.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							v.setBackgroundColor(getActivity().getResources()
									.getColor(R.color.backColor));
							break;

						case MotionEvent.ACTION_MOVE:
							v.setBackgroundColor(getActivity().getResources()
									.getColor(R.color.transparent));
							break;

						case MotionEvent.ACTION_UP:
							v.setBackgroundColor(getActivity().getResources()
									.getColor(R.color.transparent));

							PreferencesUtil.initStoreData();

							Map<String, String> mapParams = new HashMap<String, String>();
							mapParams.put(Constants.Params.METHOD,
									Constants.Params.METHOD_DETAIL_STATISTICAL);
							mapParams.put(Constants.Feild.KEY_COMPANY_ID,
									PreferencesUtil.user_company);
							mapParams.put(Constants.Feild.KEY_ITEM_ID,
									PreferencesUtil.item_id);
							mapParams.put(
									Constants.Feild.KEY_ORDER_CREATE_TIME,
									statistical.getDate());
							mapParams.put(Constants.Params.PARAM_WORKFLOW,
									String.valueOf(v.getTag()));

							SerializableMap map = new SerializableMap();
							map.setMap(mapParams);

							Intent data = new Intent(getActivity(),
									OrderListActivity.class);
							Bundle bundle = new Bundle();
							bundle.putSerializable("order_query", map);
							data.putExtras(bundle);

							getActivity().startActivity(data);
							break;
						}
						return true;
					}
				});
				i++;
			}
			layout.setTag(list.get(position));
			layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					if (lay_order_statistics.getVisibility() == View.VISIBLE) {
						lay_order_statistics.setVisibility(View.GONE);
						layout.findViewById(R.id.iv_arrow_right).setVisibility(
								View.VISIBLE);
						layout.findViewById(R.id.iv_arrow_down).setVisibility(
								View.GONE);
					} else {
						lay_order_statistics.setVisibility(View.VISIBLE);
						layout.findViewById(R.id.iv_arrow_right).setVisibility(
								View.GONE);
						layout.findViewById(R.id.iv_arrow_down).setVisibility(
								View.VISIBLE);
					}
				}
			});
			return view;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mTextOrderStatu.setVisibility(View.GONE);
	}
//
//	private void add(JSONArray arrs) throws JSONException {
//		mGroupModels = new ArrayList<OrderStatisticsFragment.GroupModel>();
//		mChildModelsList = new ArrayList<List<ChildModel>>();
//		List<ChildModel> childModels = null;
//		JSONObject jsonObj;
//		GroupModel groupModel = new GroupModel();
//		;
//		ChildModel childModel;
//
//		// 解析所有订单数据
//		for (int i = 0; i < arrs.length(); i++) {
//			jsonObj = arrs.getJSONObject(i);
//			childModel = new ChildModel();
//			groupModel = new GroupModel();
//			childModel.ordtime = jsonObj
//					.getString(Constants.Feild.KEY_ORDER_CREATE_TIME);
//			childModel.count = jsonObj
//					.getInt(Constants.Feild.KEY_STATISTICAL_COUNT);
//			childModel.workflow = jsonObj
//					.getString(Constants.Feild.KEY_WORKFLOW);
//			childModel.wfname = jsonObj
//					.getString(Constants.Feild.KEY_WORKFLOW_NAME);
//
//			if (TextUtils.equals(childModel.ordtime, groupModel.ordtime)) {
//				groupModel.count += childModel.count;
//				childModels.add(childModel);
//
//			} else {
//				if (childModel != null) {
//					mChildModelsList.add(childModels);
//				}
//
//				childModels = new ArrayList<OrderStatisticsFragment.ChildModel>();
//				childModels.add(childModel);
//
//				groupModel = new GroupModel();
//				groupModel.ordtime = childModel.ordtime;
//				groupModel.count = childModel.count;
//				mGroupModels.add(groupModel);
//			}
//			if (i == arrs.length() - 1) {
//				if (childModel != null) {
//					mChildModelsList.add(childModels);
//				}
//			}
//
//		}
//
//	}
//
//	List<GroupModel> mGroupModels;
//	List<List<ChildModel>> mChildModelsList;
//
//	class GroupModel {
//		String ordtime;
//		int count;
//	}
//
//	class ChildModel {
//		// {"count":2,"workflow":"1","wfname":"纯配送订单","ordtime":"2015-06-24"}
//		int count;
//		String workflow;
//		String wfname;
//		String ordtime;
//	}
}
