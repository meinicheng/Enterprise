package com.sdbnet.hywy.enterprise.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.album.AlbumHelper;
import com.sdbnet.hywy.enterprise.album.Bimp;
import com.sdbnet.hywy.enterprise.album.ImageGridShowAdapter;
import com.sdbnet.hywy.enterprise.album.AlbumHelper.ImageItem;
import com.sdbnet.hywy.enterprise.model.Staff;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.ui.widget.CustomGridView;
import com.sdbnet.hywy.enterprise.utils.UtilsError;
import com.sdbnet.hywy.enterprise.utils.UtilsJava;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class InfoActivity extends BaseActivity implements OnClickListener {

	private TextView mTextTitle;
	private ImageView mImgBack;
	private ListView mListView;

	public static final String STAFF_INFO = "staff_info";

	private TextView mTextName;
	private TextView mTextSex;
	private TextView mTextTel;
	private ImageView mImgTel;
	private TextView mTextTruckPlate;
	private TextView mTextTruckLength;
	private TextView mTextTruckModle;
	private TextView mTextTruckLoad;
	private TextView mTextIsContract;
	private TextView mTextWorkState;
	private TextView mTextUseState;
	private TextView mTextPhoneState;
	private TextView mTextGps;
	private TextView mTextGprs;
	private TextView mTextWifi;
	private TextView mTextElectricity;
	private TextView mTextIsOnline;

	private Staff mStaff;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		mStaff = getIntent().getExtras().getParcelable(STAFF_INFO);
		initControls();
		// initListView();
		loadDatas();

	}

	/**
	 * 加载个人资料
	 */
	private void loadDatas() {
		AsyncHttpService.getUserState(mStaff.getPid(),
				new JsonHttpResponseHandler() {
					@Override
					public void onStart() {
						super.onStart();
						showLoading(R.string.loading);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						dismissLoading();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							initData(response);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							dismissLoading();
						}
					}
				}, this);
		// initData();
	}

	private void initControls() {
		mTextTitle = (TextView) findViewById(R.id.common_view_title_text);
		mTextTitle.setText(getString(R.string.user_information));
		mImgBack = (ImageView) findViewById(R.id.common_view_title_img);
		mImgBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// mListView = (ListView) findViewById(R.id.activity_info_list);

		mTextName = (TextView) findViewById(R.id.activity_info_name_text);
		mTextSex = (TextView) findViewById(R.id.activity_info_sex_text);
		mTextTel = (TextView) findViewById(R.id.activity_info_phone_text);
		mImgTel = (ImageView) findViewById(R.id.activity_info_img_phone);
		mImgTel.setOnClickListener(this);
		mTextTruckPlate = (TextView) findViewById(R.id.activity_info_truck_plate_text);
		mTextTruckModle = (TextView) findViewById(R.id.activity_info_truck_modle_text);
		mTextTruckLength = (TextView) findViewById(R.id.activity_info_truck_length_text);
		mTextTruckLoad = (TextView) findViewById(R.id.activity_info_truck_load_text);

		mTextIsContract = (TextView) findViewById(R.id.activity_info_iscontract_text);
		mTextWorkState = (TextView) findViewById(R.id.activity_info_isworking_text);
		mTextUseState = (TextView) findViewById(R.id.activity_info_ustatus_text);
		mTextPhoneState = (TextView) findViewById(R.id.activity_info_isshutdown_text);

		mTextGps = (TextView) findViewById(R.id.activity_info_gps_text);
		mTextGprs = (TextView) findViewById(R.id.activity_info_gprs_text);
		mTextWifi = (TextView) findViewById(R.id.activity_info_wifi_text);

		mTextElectricity = (TextView) findViewById(R.id.activity_info_electricity_text);
		mTextIsOnline = (TextView) findViewById(R.id.activity_info_state_text);

		mListFoot = (CustomGridView) findViewById(R.id.activity_info_grid_img);

	}

	private CustomGridView mListFoot;
	private SimpleAdapter mSimpleAdapter;
	private ImageGridShowAdapter mImgAdapter;
	private List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
	private List<AlbumHelper.ImageItem> mImageItems = new ArrayList<AlbumHelper.ImageItem>();

	private void initData(JSONObject response) throws Exception {
		Log.e(TAG, response.toString());
		if (UtilsError.isErrorCode(this, response)) {
			return;
		}
		// errcode 1 数字 错误编号
		// msg 1 字符串 错误消息
		if (response.isNull("staff"))
			return;
		JSONObject jsonObject = response.getJSONObject("staff");
		// staff 1 对象 用户资料
		// pname 1 字符串 用户姓名
		if (!jsonObject.isNull("pname"))
			mTextName.setText(jsonObject.getString("pname"));

		// sex 1 数字 性别 0：男 1：女
		if (!jsonObject.isNull("sex")) {
			int sex = jsonObject.getInt("sex");
			mTextSex.setText(sex == 0 ? getString(R.string.man)
					: getString(R.string.woman));
		}

		// loctel 1 字符串 电话
		if (!jsonObject.isNull("loctel"))
			mTextTel.setText(jsonObject.getString("loctel"));

		// truckno 1 字符串 车牌号 车辆用户专用信息，不为车辆用户时该信息不显示
		if (!jsonObject.isNull("truckno"))
			mTextTruckPlate.setText(jsonObject.getString("truckno"));

		// trucktype 1 字符串 车型
		if (!jsonObject.isNull("trucktype"))
			mTextTruckModle.setText(jsonObject.getString("trucktype"));

		// trucklength 1 浮点 车长
		if (!jsonObject.isNull("trucklength"))
			mTextTruckLength.setText(jsonObject.getDouble("trucklength") + ""
					+ getString(R.string.m));

		// truckweight 1 浮点 载重
		if (!jsonObject.isNull("truckweight"))
			mTextTruckLoad.setText(jsonObject.getDouble("truckweight") + ""+getString(R.string.t));

		// iscontract 1 浮点 是否为合约车辆 1：是 0：否
		if (!jsonObject.isNull("iscontract")) {
			int type = jsonObject.getInt("iscontract");
			if (type == 0) {
				mTextIsContract.setTextColor(Color.GRAY);
			} else {
				mTextIsContract.setTextColor(Color.RED);
			}
			mTextIsContract.setText(type == 0 ? getString(R.string.yes)
					: getString(R.string.no));
		}
		// isworking 1 浮点 是否上班中
		if (!jsonObject.isNull("isworking")) {
			int isworking = jsonObject.getInt("isworking");
			if (isworking == 0) {
				mTextWorkState.setTextColor(Color.GRAY);
			} else {
				mTextWorkState.setTextColor(Color.RED);
			}
			mTextWorkState.setText(isworking == 0 ? getString(R.string.yes)
					: getString(R.string.no));
		}
		// ustatus 1 浮点 使用状态 1表示未分配，2表示使用中，3表示已完成
		if (!jsonObject.isNull("ustatus")) {
			int ustatus = jsonObject.getInt("ustatus");
			if (ustatus == 2) {
				mTextUseState.setText(getString(R.string.in_use));
			} else if (ustatus == 3) {
				mTextUseState.setText(getString(R.string.completed));
			} else {
				mTextUseState.setText(getString(R.string.undistributed));
			}
		}

		// isshutdown 1 浮点 是否关机 1：是 0：否
		if (!jsonObject.isNull("isshutdown")) {
			int isshutdown = jsonObject.getInt("isshutdown");
			if (isshutdown == 0) {
				mTextPhoneState.setTextColor(Color.GRAY);
			} else {
				mTextPhoneState.setTextColor(Color.RED);
			}
			mTextPhoneState.setText(isshutdown == 0 ? getString(R.string.yes)
					: getString(R.string.no));
		}
		// gpsstatus 1 浮点 pgs状态
		if (!jsonObject.isNull("gpsstatus")) {
			int gpsstatus = jsonObject.getInt("gpsstatus");
			if (gpsstatus == 0) {
				mTextGps.setTextColor(Color.GRAY);
			} else {
				mTextGps.setTextColor(Color.RED);
			}
			mTextGps.setText(gpsstatus == 0 ? getString(R.string.yes)
					: getString(R.string.no));
		}
		// wifistatus 1 浮点 wifi状态
		if (!jsonObject.isNull("wifistatus")) {
			int wifistatus = jsonObject.getInt("wifistatus");
			if (wifistatus == 0) {
				mTextWifi.setTextColor(Color.GRAY);
			} else {
				mTextWifi.setTextColor(Color.RED);
			}
			mTextWifi.setText(wifistatus == 0 ? getString(R.string.yes)
					: getString(R.string.no));
		}
		// electricity 1 浮点 当前电量
		if (!jsonObject.isNull("electricity"))
			mTextElectricity.setText(jsonObject.getDouble("electricity") + "%");

		// gprsstatus 1 浮点 gprs状态 1：是 0：否
		if (!jsonObject.isNull("gprsstatus")) {
			int gprsstatus = jsonObject.getInt("gprsstatus");
			if (gprsstatus == 0) {
				mTextGps.setTextColor(Color.GRAY);
			} else {
				mTextGps.setTextColor(Color.RED);
			}
			mTextGprs.setText(gprsstatus == 0 ? getString(R.string.yes)
					: getString(R.string.no));
		}
		// isonline 1 浮点 是否在线
		if (!jsonObject.isNull("isonline")) {
			int isonline = jsonObject.getInt("isonline");
			if (isonline == 0) {
				mTextIsOnline.setTextColor(Color.GRAY);
			} else {
				mTextIsOnline.setTextColor(Color.RED);
			}
			mTextIsOnline.setText(isonline == 0 ? getString(R.string.yes)
					: getString(R.string.no));
		}
		// imgArr 1 数组 附件
		// if (!jsonObject.isNull("pname"))
		JSONArray jsonArray = jsonObject.getJSONArray("imgArr");
		for (int i = 0; i < jsonArray.length(); i++) {
			ImageItem imageItem = new ImageItem();
			JSONObject image = jsonArray.getJSONObject(i);
			imageItem.thumbnailPath = image.getString("smallimg");
			imageItem.imagePath = image.getString("bigimg");
			mImageItems.add(imageItem);
		}
		// --smallimg 1 字符串 大图
		// --bigimg 1 字符串 缩略图
		//
		mImgAdapter = new ImageGridShowAdapter(this, mImageItems, false);
		mListFoot.setAdapter(mImgAdapter);
	}

	// private void initListView() {
	// mListFoot = new CustomGridView(this);
	// // 显示用户图片
	// mImgAdapter = new ImageGridShowAdapter(this, mImageItems, false);
	// mListFoot.setAdapter(mImgAdapter);
	//
	// mListView.addFooterView(mListFoot);
	// mSimpleAdapter = new SimpleAdapter(this, mapList,
	// R.layout.item_staff_info, new String[] { INFO_NAME, INFO_MSG },
	// new int[] { R.id.item_staff_info_title_text,
	// R.id.item_staff_info_msg_text });
	//
	// mListView.setAdapter(mSimpleAdapter);
	//
	// // 给设置菜单列表添加点击事件
	// mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> parent, View view,
	// int position, long id) {
	// if (mapList.get(position).get(INFO_NAME)
	// .equals(getString(R.string.phone_colon))) {
	// if (UtilsJava.isNumeric(mStaff.getLoctel())) {
	// // 用intent启动拨打电话
	// Intent intent = new Intent(Intent.ACTION_CALL, Uri
	// .parse("tel:" + mStaff.getLoctel()));
	// startActivity(intent);
	// }
	// }
	// }
	// });
	// }

	// /**
	// * 将数据填充界面
	// */
	// private final String INFO_NAME = "info_name";
	// private final String INFO_MSG = "info_msg";
	//
	// private void initData() {
	// Log.e(TAG, mStaff.toString());
	// Map<String, String> map;
	// mapList.clear();
	// // name
	// map = new HashMap<String, String>();
	// map.put(INFO_NAME, getString(R.string.name_colon));
	// map.put(INFO_MSG, "LL");
	// mapList.add(map);
	// // sex
	// map = new HashMap<String, String>();
	// map.put(INFO_NAME, getString(R.string.sex_colon));
	// // String sex=(mStaff.sex.equals("0") ? getString(R.string.man)
	// // : getString(R.string.woman));
	// map.put(INFO_MSG, "LL");
	// mapList.add(map);
	// // tel
	// map = new HashMap<String, String>();
	// map.put(INFO_NAME, getString(R.string.phone_colon));
	// map.put(INFO_MSG, "LL");
	// mapList.add(map);
	// // plate
	// map = new HashMap<String, String>();
	// map.put(INFO_NAME, getString(R.string.truck_plate_colon));
	// map.put(INFO_MSG, "LL");
	// mapList.add(map);
	// // modle
	// map = new HashMap<String, String>();
	// map.put(INFO_NAME, getString(R.string.truck_modle_colon));
	// map.put(INFO_MSG, "LL");
	// mapList.add(map);
	// // length
	// map = new HashMap<String, String>();
	// map.put(INFO_NAME, getString(R.string.truck_length_colon));
	// map.put(INFO_MSG, "LL");
	// mapList.add(map);
	// // load
	// map = new HashMap<String, String>();
	// map.put(INFO_NAME, getString(R.string.truck_load_colon));
	// map.put(INFO_MSG, "LL");
	// mapList.add(map);
	// // gprs
	// map = new HashMap<String, String>();
	// map.put(INFO_NAME, getString(R.string.gprs_colon));
	// map.put(INFO_MSG, "LL");
	// mapList.add(map);
	// // gps
	// map = new HashMap<String, String>();
	// map.put(INFO_NAME, getString(R.string.gps_colon));
	// map.put(INFO_MSG, "LL");
	// mapList.add(map);
	// // wifi
	// map = new HashMap<String, String>();
	// map.put(INFO_NAME, getString(R.string.wifi_colon));
	// map.put(INFO_MSG, "LL");
	// mapList.add(map);
	// // isOnline
	// map = new HashMap<String, String>();
	// map.put(INFO_NAME, getString(R.string.state_colon));
	// map.put(INFO_MSG, "LL");
	// mapList.add(map);
	// mSimpleAdapter.notifyDataSetChanged();
	// // mImageItems
	// mImgAdapter.notifyDataSetChanged();
	//
	// }

	@Override
	public void onDestroy() {
		super.onDestroy();
		Bimp.clearCache();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_view_title_img:
			finish();
			break;
		case R.id.activity_info_img_phone:
			call();
			break;

		default:
			break;
		}

	}

	private void call() {
		// 用intent启动拨打电话
		String phone = mTextTel.getText().toString();
		if (UtilsJava.isNumeric(phone)) {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ phone));
			startActivity(intent);
		}
	}

}
