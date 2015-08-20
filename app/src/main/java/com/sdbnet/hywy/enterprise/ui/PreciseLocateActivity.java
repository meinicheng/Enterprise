package com.sdbnet.hywy.enterprise.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.model.Staff;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.UtilsError;
import com.sdbnet.hywy.enterprise.utils.UtilsJava;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class PreciseLocateActivity extends BaseActivity {
	LayoutInflater mInflater;
	private ImageView mBack;

	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private InfoWindow mInfoWindow;

	private OnInfoWindowClickListener infoWindowListener;
	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor orange = BitmapDescriptorFactory
			.fromResource(R.drawable.gps_orange);
	BitmapDescriptor gray = BitmapDescriptorFactory
			.fromResource(R.drawable.gps_gray);
	// 测试使用--start
	// BitmapDescriptor gray =
	// BitmapDescriptorFactory.fromResource(R.drawable.gps_orange);
	// 测试使用--end

	private MapStatusUpdate u;
	private String tel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_positioning);
		initControls();
		getLocation();
	}

	/**
	 * 获取定位信息
	 */
	private void getLocation() {
		String locpid = getIntent()
				.getStringExtra(Constants.Feild.KEY_STAFF_ID);
		tel = getIntent().getStringExtra(Constants.Feild.KEY_LOCA_TEL);

		AsyncHttpService.accuratePosi(locpid, new JsonHttpResponseHandler() {

			@Override
			public void onStart() {
				super.onStart();
				showLoading(getString(R.string.xlistview_header_hint_loading));
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONArray errorResponse) {
				dismissLoading();
				showShortToast(getResources().getString(R.string.httpisNull));
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					int errCode = response
							.getInt(Constants.Feild.KEY_ERROR_CODE);
					if (UtilsError.isErrorCode(PreciseLocateActivity.this,
							response)) {

						return;
					}
					JSONObject jsonObj = response
							.getJSONObject(Constants.Feild.KEY_STAFF);
					if (jsonObj == null) {
						showLongToast("未获取到相关的定位信息");
						return;
					}
					// 解析用户信息
					Staff staff = new Staff();
					staff.setPid(jsonObj
							.getString(Constants.Feild.KEY_STAFF_ID));
					staff.setPname(jsonObj
							.getString(Constants.Feild.KEY_STAFF_NAME));
					staff.setLoctime(jsonObj
							.getString(Constants.Feild.KEY_LOCA_TIME));
					staff.setLocaddress(jsonObj
							.getString(Constants.Feild.KEY_LOCA_ADDRESS));
					staff.setLongitude(jsonObj
							.getDouble(Constants.Feild.KEY_LOCA_LONGITUDE));
					staff.setLatitude(jsonObj
							.getDouble(Constants.Feild.KEY_LOCA_LATITUDE));
					staff.setTruckno(jsonObj
							.getString(Constants.Feild.KEY_STAFF_TRUCK_NO));
					staff.setTrucktype(jsonObj
							.getString(Constants.Feild.KEY_STAFF_TRUCK_TYPE));
					staff.setTrucklength(jsonObj
							.getDouble(Constants.Feild.KEY_STAFF_TRUCK_LENGTH));
					staff.setTruckweight(jsonObj
							.getDouble(Constants.Feild.KEY_STAFF_TRUCK_WEIGHT));
					staff.setStatus(jsonObj
							.getString(Constants.Feild.KEY_STAFF_STATUS));
					// 地图画点
					drawOverlay(staff);
				} catch (Exception e) {
					e.printStackTrace();
					showLongToast("网络异常，请稍后重试");
				} finally {
					dismissLoading();
				}
				super.onSuccess(statusCode, headers, response);
			}
		}, this);
	}

	/**
	 * 初始化控制
	 */
	private void initControls() {
		mBack = (ImageView) findViewById(R.id.about_imageview_gohome);
		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setBuildingsEnabled(false);
		for (int i = 0; i < mMapView.getChildCount(); i++) {
			View child = mMapView.getChildAt(i);
			// 隐藏缩放控件ZoomControl
			if (child instanceof ZoomControls) {
				child.setVisibility(View.INVISIBLE);
			}
		}

		infoWindowListener = new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick() {
				if (!UtilsJava.isMobile(tel)) {
					Toast.makeText(PreciseLocateActivity.this, "手机号码不合法，不能拨打",
							Toast.LENGTH_SHORT).show();
				} else {
					// 用intent启动拨打电话
					Intent intent = new Intent(Intent.ACTION_CALL,
							Uri.parse("tel:" + tel));
					startActivity(intent);
				}
			}
		};

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				overflyInfoWindow(marker);
				return true;
			}
		});

		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				mInfoWindow = null;
				mBaiduMap.hideInfoWindow();
			}
		});
	}

	/**
	 * 点击泡泡弹出提示层
	 * 
	 * @param marker
	 */
	private void overflyInfoWindow(Marker marker) {
		Staff staff = marker.getExtraInfo().getParcelable(
				Constants.Feild.KEY_STAFF);
		View v = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.layer_float_roles, null);

		((TextView) v.findViewById(R.id.tvPlate)).setText(staff.getTruckno());
		((TextView) v.findViewById(R.id.tvName)).setText(staff.getPname());

		// 测试使用 --start
		// String time = staff.getLoctime();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// String today = sdf.format(new Date());
		// try {
		// if(DateUtil.isBefore(sdf.parse(time), sdf.parse(today))){
		// sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// time = DateUtil.getDateByAddMinute(sdf.format(new Date()), 0 -
		// (Integer.parseInt(GenerateRandomUtil.generateNumber(1)) + 5));
		// }
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
		// ((TextView) v.findViewById(R.id.tvLocTime)).setText(time);
		// 测试使用 --end

		((TextView) v.findViewById(R.id.tvLocTime)).setText(staff.getLoctime());
		((TextView) v.findViewById(R.id.tvLocAddress)).setText(staff
				.getLocaddress());

		TextView tvModelDetail = (TextView) v.findViewById(R.id.tvModelDetail);
		if (!TextUtils.isEmpty(staff.getTruckno())) {
			// 为车辆用户，则显示相关信息
			tvModelDetail
					.setText(staff.getTrucktype()
							+ (TextUtils.isEmpty(String.valueOf(staff
									.getTrucklength())) ? "" : ","
									+ staff.getTrucklength() + "米")
							+ (TextUtils.isEmpty(String.valueOf(staff
									.getTruckweight())) ? "" : ","
									+ staff.getTruckweight() + "吨"));
			tvModelDetail.setVisibility(View.VISIBLE);
		} else {
			tvModelDetail.setVisibility(View.GONE);
		}

		LatLng ll = marker.getPosition();
		mInfoWindow = null;
		mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(v), ll,
				-67, infoWindowListener);
		mBaiduMap.showInfoWindow(mInfoWindow);
	}

	/**
	 * 地图画点
	 * 
	 * @param staff
	 */
	public void drawOverlay(Staff staff) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		if (staff.getLatitude().equals(0) || staff.getLongitude().equals(0)) {
			showLongToast("未获取到相关定位信息，请确认用户端是否启用货运无忧定位服务");
			return;
		}
		LatLng ll = new LatLng(staff.getLatitude(), staff.getLongitude());
		builder.include(ll);
		OverlayOptions ooA = null;
		if ("1".equals(staff.getStatus())) {
			ooA = new MarkerOptions().position(ll).icon(gray).zIndex(9)
					.draggable(true);
		} else {
			ooA = new MarkerOptions().position(ll).icon(orange).zIndex(9)
					.draggable(true);
		}
		Marker marker = (Marker) mBaiduMap.addOverlay(ooA);

		Bundle bundle = new Bundle();
		bundle.putParcelable(Constants.Feild.KEY_STAFF, staff);
		marker.setExtraInfo(bundle);
		overflyInfoWindow(marker);

		u = MapStatusUpdateFactory.newLatLngZoom(ll, 19);

		mMapView.post(new Runnable() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		});
		dismissLoading();
	}

	@Override
	public void onPause() {
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.setVisibility(View.INVISIBLE);
		mMapView.onPause();
		super.onPause();
	}

	@Override
	public void onResume() {
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		mMapView.setVisibility(View.VISIBLE);
		mMapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		mMapView.onDestroy();
		super.onDestroy();
		// 回收 bitmap 资源
		gray.recycle();
		orange.recycle();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mMapView.getMap().animateMapStatus(u);
				break;
			}
			super.handleMessage(msg);
		}
	};
}
