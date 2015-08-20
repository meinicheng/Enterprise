package com.sdbnet.hywy.enterprise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.ui.view.ComprehensiveQueryFragment;
import com.sdbnet.hywy.enterprise.ui.widget.PopMenu;
import com.sdbnet.hywy.enterprise.ui.widget.PopMenu.OnItemClickListener;
import com.sdbnet.hywy.enterprise.ui.widget.RTPullListView;
import com.sdbnet.hywy.enterprise.ui.widget.RTPullListView.OnAutoLoadingListener;
import com.sdbnet.hywy.enterprise.ui.widget.RTPullListView.OnRefreshListener;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.LogUtil;
import com.sdbnet.hywy.enterprise.utils.SerializableMap;
import com.sdbnet.hywy.enterprise.utils.UtilsError;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderListActivity extends BaseActivity implements
		OnItemClickListener {
	private static final String TAG = "OrderListActivity";

	private RTPullListView mPlvOrderList;

	private LinearLayout noRecord;

	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	private String[] menus;
	private Map<String, String> menuMap = new HashMap<String, String>();
	private int pageNo = 1;
	private int pageCount = 0;
	private int actCount = 0;
	private int nowIndex = 0;
	private RelativeLayout footerView;
	private static final int LOAD_NEW_INFO = 15;
	private static final int LOAD_MORE_SUCCESS = 6;
	private TextView tv_tip;
	private SimpleAdapter adapter;
	private ImageView mImgBack;

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			Map<String, String> map = (Map<String, String>) v.getTag();
			Intent intent = new Intent(OrderListActivity.this,
					OrderTraceListActivity.class);
			intent.putExtra(Constants.Feild.KEY_ORDER_NO,
					map.get(Constants.Feild.KEY_ORDER_NO));
			startActivity(intent);
		}
	};

	private Handler myHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_MORE_SUCCESS: // 加载更多
				adapter.notifyDataSetChanged();
				mPlvOrderList.setSelectionfoot();
				footerView.setClickable(true);
				break;
			case LOAD_NEW_INFO: // 初次加载
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
			return false;
		}
	});

	private LinearLayout lay_loading;
	private TextView mTextOrderStatu;

	private Map<String, String> mapParams;

	private String ostatus = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_list);

		Bundle bundle = getIntent().getExtras();
		SerializableMap serializableMap = (SerializableMap) bundle
				.get(ComprehensiveQueryFragment.BUNDLE_ORDR_QUERY);// "order_query"
		mapParams = serializableMap.getMap();
		Log.e(TAG, mapParams + "");
		initControls();
		// initListView();
		initListerner();
		loadQueryData(true);
	}

	/**
	 * 初始化控件
	 */
	private void initControls() {
		mImgBack = (ImageView) findViewById(R.id.common_view_title_img);
		mTextOrderStatu = (TextView) findViewById(R.id.common_view_title_btn);
		mTextOrderStatu.setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.common_view_title_text))
				.setText(R.string.history_order);
		mPlvOrderList = (RTPullListView) findViewById(R.id.activity_order_list_plv_order_list);
		noRecord = (LinearLayout) findViewById(R.id.view_no_record);
		// 滑动至底部刷新相关组件
		initFooterView();

		adapter = new SimpleAdapter(this, list, R.layout.list_item_order,
				new String[] { Constants.Feild.KEY_ORDER_NO,
						Constants.Feild.KEY_ORDER_CREATE_TIME,
						Constants.Feild.KEY_WORKFLOW_NAME,
						Constants.Feild.KEY_ACTION_NAME }, new int[] {
						R.id.tv_order_number, R.id.tv_create_time,
						R.id.tv_order_type, R.id.tv_order_status }) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				LinearLayout layout = (LinearLayout) view
						.findViewById(R.id.lay_item_click);
				layout.setTag(list.get(position));
				layout.setOnClickListener(listener);
				if (position % 2 == 0) {
					layout.setBackgroundResource(R.drawable.list_choose_selector2);
				} else {
					layout.setBackgroundResource(R.drawable.list_choose_selector);
				}
				TextView tv = (TextView) view
						.findViewById(R.id.tv_order_status);
				tv.setTextColor(getResources().getColor(R.color.green));
				return view;
			}
		};
	}

	private void initListView() {

	}

	private void initListerner() {
		mImgBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTextOrderStatu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 初始化弹出菜单
				PopMenu popMenu = new PopMenu(OrderListActivity.this);
				popMenu.addItems(menus);
				popMenu.setOnItemClickListener(OrderListActivity.this);
				popMenu.showAsDropDown(v);
			}
		});

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
					String tip = getString(R.string.no_more);
					tv_tip.setText(tip);
				}
			}
		});

	}

	/**
	 * 给下拉刷新列表添加底部"获取更多"按钮（可自定义）
	 */
	private void initFooterView() {
		LayoutInflater inflater = LayoutInflater.from(this);
		footerView = (RelativeLayout) inflater.inflate(R.layout.list_footview,
				null);
		tv_tip = (TextView) footerView.findViewById(R.id.list_footview_tv_tip);
		footerView.setVisibility(View.GONE);
		mPlvOrderList.addFooterView(footerView);
	}

	/**
	 * 查询菜单数据
	 * 
	 * @param isRefresh
	 */
	private void loadQueryData(final boolean isRefresh) {
		if (mapParams == null) {
			return;
		}
		if (Constants.Params.METHOD_ACCURATE_ORDERS.equals(mapParams
				.get(Constants.Params.METHOD))) {
			queryDataCondition(isRefresh);
		} else if (Constants.Params.METHOD_DETAIL_STATISTICAL.equals(mapParams
				.get(Constants.Params.METHOD))) {
			queryDataStatisticsList(isRefresh);
		}
	}

	private void queryDataCondition(final boolean isRefresh) {
		// 按条件查询订单
		String ordno = mapParams.get(Constants.Feild.KEY_ORDER_NO);
		String createFrom = mapParams.get(Constants.Params.PARAM_CREATE_FROM);
		String createTo = mapParams.get(Constants.Params.PARAM_CREATE_TO);
		String ostatus = mapParams.get(Constants.Params.PARAM_OSTATUS);
		String workflow = mapParams.get(Constants.Params.PARAM_WORKFLOW);

		LogUtil.d(TAG, "loadQueryData>>>:" + "ordno: " + ordno + "\n"
				+ "createFrom: " + createFrom + "\n" + "createTo: " + createTo
				+ "\n" + "workflow: " + workflow + "\n" + "ostatus: " + ostatus);
		AsyncHttpService.getOrdersByQuery(pageNo, ordno, createFrom, createTo,
				ostatus, workflow, new JsonHttpResponseHandler() {

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

						LogUtil.d(TAG, "query order: " + response);
						super.onSuccess(statusCode, headers, response);
						parseJSON(isRefresh, response);
					}

				}, this);
	}

	private void queryDataStatisticsList(final boolean isRefresh) {
		// 根据统计列表查询订单
		String ordtime = mapParams.get(Constants.Feild.KEY_ORDER_CREATE_TIME);
		String workflow = mapParams.get(Constants.Params.PARAM_WORKFLOW);
		AsyncHttpService.getOrdersWithStatistical(pageNo, workflow, ostatus,
				ordtime, new JsonHttpResponseHandler() {

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

				}, this);
	}

	/**
	 * 解析json
	 * 
	 * @param isRefresh
	 * @param response
	 */
	private void parseJSON(boolean isRefresh, JSONObject response) {
		try {

			if (UtilsError.isErrorCode(this, response)) {
				noRecord.setVisibility(View.VISIBLE);
				footerView.setVisibility(View.GONE);
				setLoadViewVisibility(View.GONE);
				return;
			}
			if (response.getInt("pageCount") == 0) {
				footerView.setVisibility(View.GONE);
				noRecord.setVisibility(View.VISIBLE);
				mPlvOrderList.setVisibility(View.GONE);
				setLoadViewVisibility(View.GONE);
				return;
			}
			mPlvOrderList.setVisibility(View.VISIBLE);
			pageNo = response.getInt("pageNo");
			pageCount = response.getInt("pageCount");

			JSONObject jsonObj = null;
			if (response.has(Constants.Feild.KEY_ACTIONS)) {
				// 获取订单状态，用作右上角的弹出菜单
				JSONArray acts = response
						.getJSONArray(Constants.Feild.KEY_ACTIONS);
				menus = new String[acts.length() + 1];
				actCount = 0;
				for (int i = 1; i <= acts.length(); i++) {
					jsonObj = acts.getJSONObject(i - 1);
					actCount += jsonObj
							.getInt(Constants.Feild.KEY_ACTION_COUNT);
					menus[i] = jsonObj
							.getString(Constants.Feild.KEY_ACTION_NAME)
							+ "("
							+ jsonObj.getInt(Constants.Feild.KEY_ACTION_COUNT)
							+ ")"; // 保存菜单名
					menuMap.put(menus[i],
							jsonObj.getString(Constants.Feild.KEY_ACTION)); // 保存菜单名与对应的值
				}
				menus[0] = "全部(" + actCount + ")";
			}

			JSONArray arrs = response.getJSONArray(Constants.Feild.KEY_ORDERS);
			Map<String, String> map = null;
			if (isRefresh) {
				list.clear();
			}

			// 解析菜单信息
			for (int i = 0; i < arrs.length(); i++) {
				jsonObj = arrs.getJSONObject(i);
				map = new HashMap<String, String>();
				map.put(Constants.Feild.KEY_ORDER_NO,
						jsonObj.getString(Constants.Feild.KEY_ORDER_NO).trim());
				map.put(Constants.Feild.KEY_PLATFORM_NO,
						jsonObj.getString(Constants.Feild.KEY_PLATFORM_NO));
				map.put(Constants.Feild.KEY_PLATFORM_ORIGINAL_NO, jsonObj
						.getString(Constants.Feild.KEY_PLATFORM_ORIGINAL_NO));
				map.put(Constants.Feild.KEY_ORDER_CREATE_TIME, jsonObj
						.getString(Constants.Feild.KEY_ORDER_CREATE_TIME));
				map.put(Constants.Feild.KEY_WORKFLOW,
						jsonObj.getString(Constants.Feild.KEY_WORKFLOW));
				map.put(Constants.Feild.KEY_WORKFLOW_NAME,
						jsonObj.getString(Constants.Feild.KEY_WORKFLOW_NAME));
				map.put(Constants.Feild.KEY_ACTION,
						jsonObj.getString(Constants.Feild.KEY_ACTION));
				map.put(Constants.Feild.KEY_ACTION_NAME,
						jsonObj.getString(Constants.Feild.KEY_ACTION_NAME));
				list.add(map);
			}
			noRecord.setVisibility(View.GONE);
			footerView.setVisibility(View.GONE);
			setLoadViewVisibility(View.GONE);

			if (menus == null) {
				mTextOrderStatu.setVisibility(View.GONE);
			} else {
				mTextOrderStatu.setVisibility(View.VISIBLE);
				mTextOrderStatu.setText(menus[nowIndex]);
			}

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
		Toast.makeText(this, getResources().getString(R.string.httpisNull),
				Toast.LENGTH_SHORT).show();
		footerView.setVisibility(View.GONE);
		setLoadViewVisibility(View.GONE);
	}

	private void setLoadViewVisibility(int isVisible) {
		if (null == lay_loading) {
			lay_loading = ((LinearLayout) findViewById(R.id.ll_loading));
		}
		lay_loading.setVisibility(isVisible);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (Constants.RequestCode.QUERY_ORDER == requestCode
				&& Constants.ResultCode.QUERY_ORDER == resultCode) {
			pageNo = 1;
			list.clear();
			loadQueryData(true);
		}
	}

	@Override
	public void onItemClick(int index) {
		nowIndex = index;
		mTextOrderStatu.setText(menus[index]);
		ostatus = menuMap.get(menus[index]);
		ostatus = ostatus == null ? "" : ostatus;
		loadQueryData(true);
		// Toast.makeText(this, "clicked " + menus[index] + "!",
		// Toast.LENGTH_SHORT).show();
	}
}
