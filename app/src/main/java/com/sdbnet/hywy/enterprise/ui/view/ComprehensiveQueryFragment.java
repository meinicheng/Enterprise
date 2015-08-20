package com.sdbnet.hywy.enterprise.ui.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.model.ExecuteAction;
import com.sdbnet.hywy.enterprise.ui.OrderListActivity;
import com.sdbnet.hywy.enterprise.ui.base.BaseFrament;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.SerializableMap;
import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;
import com.sdbnet.hywy.enterprise.utils.UtilsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComprehensiveQueryFragment extends BaseFrament implements OnClickListener {
	private static final String TAG = "OrderQueryFragment";
	private View view;
	private EditText mEditOrderId;
	private View mLlEnd;
	private View mLlStart;

	private static final int SHOW_DATAPICK_START = 0;
	private static final int SHOW_DATAPICK_END = 1;

	public static final String BUNDLE_ORDR_QUERY = "order_query";

	private int currentView = 10;
	private TextView mTextEndTime;
	private TextView mTextStartTime;
	private int mYear;
	private int mMonth;
	private int mDay;

	private Button mBtnQuery;
	private Button mBtnReset;
	private Spinner mSpnOrderType;
	private ArrayAdapter<CharSequence> adapterCity;
	private static String[] status;
	private Map<String, ExecuteAction> mapType = new HashMap<String, ExecuteAction>();
	private Map<String, String> mapStats = new HashMap<String, String>();
	private Map<String, List<ExecuteAction>> mapWorkflow = new HashMap<String, List<ExecuteAction>>();
	private ExecuteAction currentOrderType;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_query_order, null);

		initControls();

		return view;
	}

	/**
	 * 初始化控件
	 */
	private void initControls() {
		mLlStart = view
				.findViewById(R.id.fragment_query_order_startLayout);
		mLlStart.setOnClickListener(new DateButtonOnClickListener());

		mLlEnd = view
				.findViewById(R.id.fragment_query_order_endLayout);
		mLlEnd.setOnClickListener(new DateButtonOnClickListener());

		lay_order_statu = (LinearLayout) view
				.findViewById(R.id.fragment_query_order_lay_order_statu);
		lay_order_statu.removeAllViews();
		mLlOrderName= (LinearLayout) view.findViewById(R.id.fragment_query_order_ll_order_name);
		mTextOrderName= (TextView) view.findViewById(R.id.fragment_query_order_text_order_name);
		mImgOrderName= (ImageView) view.findViewById(R.id.fragment_query_order_img_order_name);
		mSpOrderName= (Spinner) view.findViewById(R.id.fragment_query_order_spinner_order_name);
//		mLlOrderName=view.findViewById(R.id.fragment_query_order_ll_order_name);


		mEditOrderId = (EditText) view
				.findViewById(R.id.fragment_query_order_et_order_id);
		mTextStartTime = (TextView) view
				.findViewById(R.id.fragment_query_order_tv_start_time);
		mTextEndTime = (TextView) view
				.findViewById(R.id.fragment_query_order_tv_end_time);

		mSpnOrderType = (Spinner) view
				.findViewById(R.id.fragment_query_order_sp_order_type);

		mBtnQuery = (Button) view
				.findViewById(R.id.fragment_query_order_btn_query_data);
		mBtnReset = (Button) view
				.findViewById(R.id.fragment_query_order_btn_reset_data);
		// 下拉按钮
		view.findViewById(R.id.fragment_query_order_type_spinner)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mSpnOrderType.performClick();
					}
				});

	}

	private void initData() {
		status = new String[] { "------",
				getActivity().getString(R.string.yes),
				getActivity().getString(R.string.no) };

		String execActions = PreferencesUtil.getValue(
				PreferencesUtil.KEY_EXECUTE_ACTION, null); // 获取动作权限
		Log.d(TAG, "execActions: " + execActions);

		if (!TextUtils.isEmpty(execActions)) {
			try {
				JSONArray array = new JSONArray(execActions);

				for (int i = 0; i < array.length(); i++) {
					JSONObject jsonObj = array.getJSONObject(i);
					ExecuteAction action = UtilsModel
							.jsonToExecuteAction(jsonObj);
					String[] workflows = action.getWorkflow().split(",");
					// 流程不存在，则创建流程
					for (String workflow : workflows) {
						if (mapWorkflow.get(workflow) == null) {
							mapWorkflow.put(workflow,
									new ArrayList<ExecuteAction>());
						}
					}
					if (Constants.Value.YES.equals(action.getSign())) {
						// 动作为起始节点
						for (String workflow : workflows) {
							mapWorkflow.get(workflow).add(action); // 将流程的各环节添加进相应的流程中
						}
						if (Constants.Value.YES.equals(action.getStartnode())) { // 流程的起点
							mapType.put(action.getBtnname(), action);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		String[] orderTypes = new String[mapType.keySet().size() + 1];
		orderTypes[0] = "--------------";
		int i = 1;

		// 为订单类型下拉框赋值
		for (String orderType : mapType.keySet()) {
			Log.i(TAG, "orderType: " + orderType);
			orderTypes[i++] = orderType;
		}
		adapterCity = new ArrayAdapter<CharSequence>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, orderTypes);
		mSpnOrderType.setAdapter(adapterCity);
		mSpnOrderType
				.setOnItemSelectedListener(new OnItemSelectedListenerImpl());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		mBtnQuery.setOnClickListener(this);
		mBtnReset.setOnClickListener(this);

	}

	// 下拉框选择事件
	private class OnItemSelectedListenerImpl implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			lay_order_statu.removeAllViews();
			// System.out.println("onItemSelected");
			String type = parent.getItemAtPosition(position).toString();
			if (TextUtils.isEmpty(type.replace("-", ""))) {
				return;
			}
			currentOrderType = mapType.get(type);
			List<ExecuteAction> list = mapWorkflow.get(mapType.get(type)
					.getWorkflow()); // 获取与订单类型相关的流程节点

			// 将流程节点填充在相应流程的下拉框中
			for (int i = 0; i < list.size(); i++) {
				ExecuteAction executeAction = list.get(i);
				LinearLayout layout = new LinearLayout(getActivity());
				layout.setOrientation(LinearLayout.HORIZONTAL);
				LayoutParams layParams = new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				layParams.bottomMargin = UtilsAndroid.UI.dip2px(getActivity(),
						10);
				layParams.gravity = Gravity.CENTER_VERTICAL;
				lay_order_statu.addView(layout, layParams);

				TextView tv = new TextView(getActivity());
				LayoutParams tvParams = new LayoutParams(0,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				tvParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
				tvParams.weight = 3;
				tv.setText(executeAction.getActname() + "：");
				tv.setTextColor(getResources().getColor(R.color.black));
				tv.setTextSize(15);
				tv.setGravity(Gravity.RIGHT);
				layout.addView(tv, tvParams);

				LinearLayout layChild = new LinearLayout(getActivity());
				LayoutParams layChildParams = new LayoutParams(0,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				layChildParams.weight = 7;

				LayoutInflater inflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				RelativeLayout statuLayout = (RelativeLayout) inflater.inflate(
						R.layout.view_order_statu, null, false);
				final Spinner spinner = (Spinner) statuLayout
						.findViewById(R.id.sp_order_statu);

				spinner.setTag(executeAction);

				// 下拉按钮
				ImageView iv_drop_down = (ImageView) statuLayout
						.findViewById(R.id.iv_drop_down);
				iv_drop_down.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						spinner.performClick();
					}
				});

				layChild.addView(statuLayout);
				layout.addView(layChild, layChildParams);

				ArrayAdapter adapter = new ArrayAdapter<CharSequence>(
						getActivity(),
						android.R.layout.simple_spinner_dropdown_item, status);
				spinner.setAdapter(adapter);
				spinner.setOnItemSelectedListener(new OnStatuItemSelectedListenerImpl());
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}

	}

	private class OnStatuItemSelectedListenerImpl implements
			OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			ExecuteAction action = (ExecuteAction) parent.getTag();
			if (action != null) {
				mapStats.put(action.getAction(),
						parent.getItemAtPosition(position).toString());
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}

	}

	/**
	 * 重置查询条件
	 */
	private void onReset() {
		mEditOrderId.setText("");
		mTextEndTime.setText("");
		mTextStartTime.setText("");
		mSpnOrderType.setSelection(0);
	}

	/**
	 * 点击查询
	 */
	private void onQuery() {
		String orderId = mEditOrderId.getText().toString().trim();
		String startTime = mTextStartTime.getText().toString();
		String endTime = mTextEndTime.getText().toString();

		if (getDiffDays(startTime.replace("-", ""), endTime.replace("-", "")) > 90) {
			showMsg(R.string.query_order_tip_msg);
			return;
		}

		// 拼装用户从下拉框选择的订单状态
		StringBuilder sb = new StringBuilder();
		for (String action : mapStats.keySet()) {
			String stats = mapStats.get(action).replace("-", "");
			if (!TextUtils.isEmpty(stats)) {
				stats = stats.equals(getString(R.string.yes)) ? Constants.Value.YES
						: Constants.Value.NO;
				sb.append(action).append(":").append(stats).append(",");
			}
		}
		String ostats = sb.toString();
		if (!TextUtils.isEmpty(ostats)) {
			ostats = ostats.substring(0, ostats.length() - 1);
		}
		Log.i(TAG, "stats: " + ostats);

		PreferencesUtil.initStoreData();

		// 封装查询参数
		Map<String, String> mapParams = new HashMap<String, String>();
		mapParams.put(Constants.Params.METHOD,
				Constants.Params.METHOD_ACCURATE_ORDERS);
		mapParams.put(Constants.Feild.KEY_COMPANY_ID,
				PreferencesUtil.user_company);
		mapParams.put(Constants.Feild.KEY_ITEM_ID, PreferencesUtil.item_id);
		mapParams.put(Constants.Feild.KEY_ORDER_NO, orderId);
		mapParams.put(Constants.Params.PARAM_CREATE_FROM, startTime);
		mapParams.put(Constants.Params.PARAM_CREATE_TO, endTime);
		if (currentOrderType != null) {
			mapParams.put(Constants.Params.PARAM_WORKFLOW,
					currentOrderType.getWorkflow());
		}
		mapParams.put(Constants.Params.PARAM_OSTATUS, ostats);

		SerializableMap map = new SerializableMap();
		map.setMap(mapParams);

		// 将参数传递给订单列表页
		Intent data = new Intent(getActivity(), OrderListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_ORDR_QUERY, map);
		data.putExtras(bundle);
		getActivity().startActivity(data);
	}

	/**
	 * 判断起止日期跨度是否超过三个月
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private long getDiffDays(String startDate, String endDate) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		long d = 0;
		try {
			Long c = sf.parse(endDate).getTime()
					- sf.parse(startDate).getTime();
			d = c / 1000 / 60 / 60 / 24;// 天
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}

	/**
	 * 
	 * 日期控件的事件
	 */

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};
	private LinearLayout lay_order_statu;
	private LinearLayout mLlOrderName;
	private TextView mTextOrderName;
	private ImageView mImgOrderName;
	private Spinner mSpOrderName;

	// private ImageView iv_order_drop_down;

	/**
	 * 
	 * 更新日期
	 */
	private void updateDisplay() {
		switch (currentView) {
		case SHOW_DATAPICK_START:
			mTextStartTime.setText(new StringBuilder()
					.append(mYear)
					.append("-")
					.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
							: (mMonth + 1)).append("-")
					.append((mDay < 10) ? "0" + mDay : mDay));
			break;
		case SHOW_DATAPICK_END:
			mTextEndTime.setText(new StringBuilder()
					.append(mYear)
					.append("-")
					.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
							: (mMonth + 1)).append("-")
					.append((mDay < 10) ? "0" + mDay : mDay));
			break;
		default:
			mTextStartTime.setText(new StringBuilder()
					.append(mYear)
					.append("-")
					.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
							: (mMonth + 1)).append("-")
					.append((mDay < 10) ? "0" + mDay : mDay));
			mTextEndTime.setText(new StringBuilder()
					.append(mYear)
					.append("-")
					.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
							: (mMonth + 1)).append("-")
					.append((mDay < 10) ? "0" + mDay : mDay));
			break;
		}
	}

	protected Dialog onCreateDialog() {
		Dialog dialog = null;
		Calendar c = Calendar.getInstance();
		dialog = new DatePickerDialog(getActivity(), mDateSetListener,
				c.get(Calendar.YEAR), // 传入年份
				c.get(Calendar.MONTH), // 传入月份
				c.get(Calendar.DAY_OF_MONTH) // 传入天数
		);
		return dialog;
	}

	/**
	 * 选择日期Button的事件处理
	 * 
	 */
	class DateButtonOnClickListener implements
			android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			Message msg = new Message();
			if (mLlStart.equals(v)) {
				currentView = SHOW_DATAPICK_START;
				msg.what = SHOW_DATAPICK_START;
			}
			if (mLlEnd.equals(v)) {
				currentView = SHOW_DATAPICK_END;
				msg.what = SHOW_DATAPICK_END;
			}
			onCreateDialog().show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_query_order_btn_query_data:
			onQuery();
			break;
		case R.id.fragment_query_order_btn_reset_data:
			onReset();
			break;
		default:
			break;
		}
	}

}
