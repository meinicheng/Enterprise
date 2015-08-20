package com.sdbnet.hywy.enterprise.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.album.AlbumHelper;
import com.sdbnet.hywy.enterprise.album.AlbumHelper.ImageItem;
import com.sdbnet.hywy.enterprise.album.ImageGridShowAdapter;
import com.sdbnet.hywy.enterprise.model.ReportModel;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.ui.widget.CustomGridView;
import com.sdbnet.hywy.enterprise.ui.widget.RTPullListView;
import com.sdbnet.hywy.enterprise.ui.widget.RTPullListView.OnRefreshListener;
import com.sdbnet.hywy.enterprise.utils.LogUtil;
import com.sdbnet.hywy.enterprise.utils.ToastUtil;
import com.sdbnet.hywy.enterprise.utils.UtilsError;
import com.sdbnet.hywy.enterprise.utils.UtilsJava;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccidentCenterActivity extends BaseActivity implements
		OnClickListener {
	private List<ReportModel> mReportModels = new ArrayList<ReportModel>();
	private MyAdapter myAdapter;
	private View mViewNoRecord;
	public static final String EXTRA_UPLOAD_REPORT = "upload_report";

	private RTPullListView mReportList;
	private String mOrderNum;

	private ImageView mImgBack;
	private ImageView mImgAdd;
	private TextView mTextTitle;

	// private Dialog mLoadDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActivityStackManager.getStackManager().pushActivity(this);

		setContentView(R.layout.activity_report_execption);
		initBaseData();
		initUI();
		initListView();
		loadData();

	}

	private Map<String, String> sourceByMap = new HashMap<String, String>();

	private void initBaseData() {
		// 1：司机 2：客户
		sourceByMap.put("1", getString(R.string.driver));
		sourceByMap.put("2", getString(R.string.customer));
		sourceByMap.put("3", getString(R.string.customer_service));
		mOrderNum = getIntent().getExtras().getString(
				OrderTraceListActivity.ORDRE_NUM, "");

	}

	private void initUI() {
		mViewNoRecord = findViewById(R.id.activity_report_execption_no_record);
		mViewNoRecord.setVisibility(View.GONE);
		((TextView) findViewById(R.id.common_view_title_text))
				.setText(R.string.accident_center);
		mImgAdd = (ImageView) findViewById(R.id.common_view_title_img_menu);
		mImgAdd.setVisibility(View.VISIBLE);
		mImgAdd.setImageResource(R.drawable.bt_add_report_pic);
		mImgAdd.setOnClickListener(this);
		
		findViewById(R.id.common_view_title_img).setOnClickListener(this);
	}

	private void initListView() {

		mReportList = (RTPullListView) findViewById(R.id.activity_report_explain_list_pull);
		mReportList.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				loadData();
			}
		});
		myAdapter = new MyAdapter();
		mReportList.setAdapter(myAdapter);
		// addExplain();
		myAdapter.notifyDataSetChanged();
		mReportList.onRefreshComplete();
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.item_report_execption, null);

				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_report_text_inner_name_msg);
				holder.ivPhone = (ImageView) convertView
						.findViewById(R.id.item_accident_report_img_phone);

				holder.tvDate = (TextView) convertView
						.findViewById(R.id.item_report_execption_date_text);
				holder.llPlace = (LinearLayout) convertView
						.findViewById(R.id.item_report_execption_place_ll);
				holder.tvPlace = (TextView) convertView
						.findViewById(R.id.item_report_execption_place_text);
				holder.tvExplain = (TextView) convertView
						.findViewById(R.id.item_report_execption_explain_text);
				holder.gridPic = (CustomGridView) convertView
						.findViewById(R.id.item_report_execption_grid_pic);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tvDate.setText(mReportModels.get(position).date + "");
			String place = mReportModels.get(position).place + "";
			if (TextUtils.isEmpty(place)) {
				holder.llPlace.setVisibility(View.GONE);
			} else {
				holder.llPlace.setVisibility(View.VISIBLE);
				holder.tvPlace.setText(place);
			}

			holder.tvExplain.setText(mReportModels.get(position).explain + "");
			String nameSource = mReportModels.get(position).pname + "("
					+ sourceByMap.get(mReportModels.get(position).sourceby)
					+ ")";

			SpannableStringBuilder style = new SpannableStringBuilder(
					nameSource);
			int left = nameSource.indexOf("(");
			int right = nameSource.indexOf(")");
			for (int i = 0; i < nameSource.length(); i++) {
				if (i > left && i < right) {
					// 设置指定位置文字的颜色
					// style.setSpan(new ForegroundColorSpan(getResources()
					// .getColor(R.color.blue)), i, i + 1,
					// Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
					style.setSpan(new ForegroundColorSpan(Color.BLUE), i,
							i + 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

				} else {
					style.setSpan(new ForegroundColorSpan(Color.BLACK), i,
							i + 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
				}
			}
			holder.tvName.setText(style);

			holder.ivPhone.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 用intent启动拨打电话
					String phone = mReportModels.get(position).loctel;
					if (UtilsJava.isNumeric(phone)) {
						Intent intent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:" + phone));
						startActivity(intent);
					}
				}
			});
			// 显示用户图片
			ImageGridShowAdapter mImgAdapter = new ImageGridShowAdapter(
					AccidentCenterActivity.this,
					mReportModels.get(position).imgList, false);
			holder.gridPic.setAdapter(mImgAdapter);

			return convertView;
		}

		class ViewHolder {
			TextView tvName;
			ImageView ivPhone;
			TextView tvDate;
			TextView tvPlace;
			LinearLayout llPlace;
			TextView tvExplain;
			CustomGridView gridPic;

		}

		@Override
		public int getCount() {
			return mReportModels.size();
		}

		@Override
		public Object getItem(int position) {
			return mReportModels.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	private String getItemTitle(String date) {
		return date.replace("-", "") + "情况反馈";
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Bimp.clearCache();
		// ActivityStackManager.getStackManager().popActivity(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.d("data=" + data + "," + requestCode + "," + resultCode);

		if (resultCode == RESULT_CODE_ADD_REPORT) {
			try {
				// ReportModel model = (ReportModel) data
				// .getSerializableExtra(EXTRA_UPLOAD_REPORT);
				// if (model != null) {
				// mReportModels.add(model);
				// myAdapter.notifyDataSetChanged();
				// }
				loadData();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public static final int REQUEST_CODE_ADD_REPORT = 11;
	public static final int RESULT_CODE_ADD_REPORT = 12;

	private void loadData() {
		if (TextUtils.isEmpty(mOrderNum)) {
			ToastUtil.show(this, "No orders");

			return;
		}
		AsyncHttpService.getReportExecption(mOrderNum,
				new JsonHttpResponseHandler() {
					@Override
					public void onStart() {
						// mLoadDialog.show();
						showLoading(getString(R.string.xlistview_header_hint_loading));
						super.onStart();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							LogUtil.d(response.toString());
							if (UtilsError.isErrorCode(
									AccidentCenterActivity.this, response)) {
								setView();
								return;
							}
							initData(response);
							mReportList.onRefreshComplete();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							dismissLoading();
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						Log.e(TAG, errorResponse.toString());
						dismissLoading();
						mReportList.onRefreshComplete();
						setView();
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}

					@Override
					public void onCancel() {
						dismissLoading();
						mReportList.onRefreshComplete();
						super.onCancel();
					}
				}, this);

	}

	private void initData(JSONObject response) {
		mReportModels.clear();
		// errcode 1 数字 请求状态编号
		// msg 1 字符串 请求返回消息
		// accides 1 数组 订单数组
		// accid 1 字符串 异常编号
		// ordno 1 字符串 订单号
		// accdesc 1 字符串 异常描述
		// acctime 1 字符串 异常发生时间
		// pname 1 字符串 申请人名称
		// loctel 1 字符串 联系方式
		// locaddr 1 字符串 地址
		// sourceby 1 字符串 来源 1：司机 2：客户
		// images 1 字符串 图片对象数组
		// picid 1 字符串 图片id
		// smallimg 1 字符串 缩略图路径
		// bigimg 1 字符串 原图路径

		try {
			if (response.isNull("accides")) {
				showLongToast("服务器数据ERROR");
				return;
			}
			JSONArray jsonArray = response.getJSONArray("accides");
			ReportModel model;
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				model = new ReportModel();
				// must
				if (!jsonObject.isNull("ordno")) {
					model.orders = jsonObject.getString("ordno");
				}
				if (!jsonObject.isNull("acctime")) {
					model.date = jsonObject.getString("acctime");
				}
				if (!jsonObject.isNull("accdesc")) {
					model.explain = "                      "
							+ jsonObject.getString("accdesc");
				}

				// no
				if (!jsonObject.isNull("accid")) {
					model.accid = jsonObject.getString("accid");
				}
				if (!jsonObject.isNull("sourceby")) {
					model.sourceby = jsonObject.getString("sourceby");
				}
				if (!jsonObject.isNull("pname")) {
					model.pname = jsonObject.getString("pname");
				}
				if (!jsonObject.isNull("loctel")) {
					model.loctel = jsonObject.getString("loctel");
				}
				if (!jsonObject.isNull("locaddr")) {
					model.place = jsonObject.getString("locaddr");
				}

				// img
				if (!jsonObject.isNull("images")) {
					JSONArray imgArray = jsonObject.getJSONArray("images");
					model.imgList = new ArrayList<AlbumHelper.ImageItem>();
					for (int j = 0; j < imgArray.length(); j++) {
						JSONObject imgObject = imgArray.getJSONObject(j);
						ImageItem imageItem = new ImageItem();
						imageItem.imageId = imgObject.getString("picid");
						imageItem.thumbnailPath = imgObject
								.getString("smallimg");
						imageItem.imagePath = imgObject.getString("bigimg");
						model.imgList.add(imageItem);
					}
				}
				mReportModels.add(model);
			}

			myAdapter.notifyDataSetChanged();

		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			setView();
		}
	}

	private void setView() {
		if (mReportModels.size() > 0) {
			mReportList.setVisibility(View.VISIBLE);
			mViewNoRecord.setVisibility(View.GONE);
		} else {
			mReportList.setVisibility(View.GONE);
			mViewNoRecord.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_view_title_img_menu:
			// addExplain();
			Intent intent = new Intent(AccidentCenterActivity.this,
					AccidentAddActivity.class);
			intent.putExtra(OrderTraceListActivity.ORDRE_NUM, mOrderNum);
			startActivityForResult(intent, REQUEST_CODE_ADD_REPORT);
			break;
		case R.id.common_view_title_img:
			finish();
			break;

		default:
			break;
		}

	}
}
