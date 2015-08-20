package com.sdbnet.hywy.enterprise.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.ui.ChooseGroupsActivity;
import com.sdbnet.hywy.enterprise.ui.HistoryLocusActivity;
import com.sdbnet.hywy.enterprise.ui.base.BaseFrament;
import com.sdbnet.hywy.enterprise.ui.widget.DateTimePickDialogUtil;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.DateUtil;
import com.sdbnet.hywy.enterprise.utils.SerializableMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TrackQueryFragment extends BaseFrament implements OnClickListener {
	private View view;
	private TextView et_plate;
	private View endLayout;
	private View startLayout;

	private static final int SHOW_DATAPICK_START = 0;
	private static final int SHOW_DATAPICK_END = 1;

	private final int QUERY_DATE_INTERVAL = 2;
	private int currentView = 10;
	private TextView tv_end_time;
	private TextView tv_start_time;
	private int mYear;
	private int mMonth;
	private int mDay;

	private Button btn_query;
	private Button btn_reset;

	private Button btn_choose_motor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_motor_locus, null);
		initControls();
		return view;
	}

	/**
	 * 初始化控件
	 */
	private void initControls() {
		startLayout = view.findViewById(R.id.fragment_motor_locus_startLayout);
		// startLayout.setOnClickListener(new DateButtonOnClickListener());

		endLayout = view.findViewById(R.id.fragment_motor_locus_endLayout);
		// endLayout.setOnClickListener(new DateButtonOnClickListener());

		et_plate = (TextView) view
				.findViewById(R.id.fragment_motor_locus_et_plate);
		tv_start_time = (TextView) view
				.findViewById(R.id.fragment_motor_locus_tv_start_time);
		// tv_start_time.setText(DateUtil.getFormatDateAdd(new Date(), -7,
		// "yyyy-MM-dd HH:mm"));
		tv_end_time = (TextView) view
				.findViewById(R.id.fragment_motor_locus_tv_end_time);
		// tv_end_time.setText(DateUtil.getFormatDate("yyyy-MM-dd HH:mm"));
		resetData();

		btn_query = (Button) view
				.findViewById(R.id.fragment_motor_locus_btn_query_data);
		btn_reset = (Button) view
				.findViewById(R.id.fragment_motor_locus_btn_reset_data);

		btn_choose_motor = (Button) view
				.findViewById(R.id.fragment_motor_locus_btn_choose_motor);

		String plate = getActivity().getIntent().getStringExtra("plate");
		if (!TextUtils.isEmpty(plate))
			et_plate.setText(plate);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		startLayout.setOnClickListener(this);
		endLayout.setOnClickListener(this);
		btn_choose_motor.setOnClickListener(this);
		btn_query.setOnClickListener(this);
		btn_reset.setOnClickListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().getIntent().removeExtra("plate");
		getActivity().getIntent().removeExtra("mTag");
	}

	/**
	 * 重置查询条件
	 */
	private void resetData() {
		et_plate.setText("");
		// tv_start_time.setText(DateUtil.getFormatDateAdd(new Date(), -7,
		// "yyyy-MM-dd HH:mm"));
		//
		tv_start_time.setText(defaltTime());
		tv_end_time.setText(DateUtil.getFormatDate("yyyy-MM-dd HH:mm"));
	}

	/**
	 * 点击查询
	 */
	private void queryData() {
		String plate = et_plate.getText().toString().trim();
		String startTime = tv_start_time.getText().toString();
		String endTime = tv_end_time.getText().toString();

		if (TextUtils.isEmpty(plate)) {
			showMsg(R.string.car_num_cannot_empty);
			return;
		}
		if (TextUtils.isEmpty(startTime)) {
			showMsg(R.string.start_time_cannot_empty);
			return;
		}
		if (TextUtils.isEmpty(endTime)) {
			showMsg(R.string.end_time_cannot_empty);
			return;
		}
		// if (getDiffDays(startTime.replace("-", ""), endTime.replace("-", ""))
		// > 7) {
		// showMsg(R.string.query_time_span_up_seven_days);
		// return;
		// }
		if (getDiffDays(startTime.replace("-", ""), endTime.replace("-", "")) > QUERY_DATE_INTERVAL) {
			String msg = String.format(
					getString(R.string.query_time_span_up_seven_days_x),
					QUERY_DATE_INTERVAL + "");
			showMsg(msg);
			return;
		}

		// 封装查询参数
		Map<String, String> mapParams = new HashMap<String, String>();
		mapParams.put(Constants.Feild.KEY_STAFF_TRUCK_NO, plate);
		mapParams.put(Constants.Params.PARAM_DATE_FROM, startTime);
		mapParams.put(Constants.Params.PARAM_DATE_TO, endTime);

		SerializableMap map = new SerializableMap();
		map.setMap(mapParams);

		// 将参数传递给订单列表页
		// Intent data = new Intent(getActivity(), ContractLocusActivity.class);
		Intent data = new Intent(getActivity(), HistoryLocusActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(HistoryLocusActivity.FILED_QUERY_TYPE,
				HistoryLocusActivity.QUERY_TYPE_DATE);
		bundle.putSerializable("locus_query", map);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_motor_locus_btn_query_data:
			queryData();
			break;

		case R.id.fragment_motor_locus_btn_reset_data:
			resetData();
			break;
		case R.id.fragment_motor_locus_btn_choose_motor: {
			Intent intent = new Intent();
			intent.putExtra(Constants.Params.PARAM_REDICT, 1);
			intent.setClass(getActivity(), ChooseGroupsActivity.class);
			startActivity(intent);
		}
			break;

		case R.id.fragment_motor_locus_endLayout: {
			String time = tv_end_time.getText().toString().trim();
			if (TextUtils.isEmpty(time)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				time = sdf.format(new Date());
			}

			DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
					getActivity(), time);
			dateTimePicKDialog.dateTimePicKDialog(tv_end_time);
		}
			break;

		case R.id.fragment_motor_locus_startLayout: {

			String time = tv_start_time.getText().toString().trim();
			if (TextUtils.isEmpty(time)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				time = sdf.format(new Date());
			}
			Log.i("time", "time=" + time);
			DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
					getActivity(), time);
			dateTimePicKDialog.dateTimePicKDialog(tv_start_time);

		}
			break;

		default:

			break;
		}
	}

	private String defaltTime() {
		Calendar calendar = Calendar.getInstance();
		/* HOUR_OF_DAY 指示一天中的小时 */
		calendar.set(Calendar.HOUR_OF_DAY,
				calendar.get(Calendar.HOUR_OF_DAY) - 8);
		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return df.format(calendar.getTime());
	}
}
