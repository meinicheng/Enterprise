package com.sdbnet.hywy.enterprise.location;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDGeofence;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.model.MyLocation;
import com.sdbnet.hywy.enterprise.utils.LogUtil;
import com.sdbnet.hywy.enterprise.utils.ToastUtil;
import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;
import com.sdbnet.hywy.enterprise.utils.UtilsBD;

public class LocationServiceUtils {
	private final String TAG = "LocationServiceUtils";
	private LocationClient locationClient;
	private MyLocationListener locationListener;
	private BDLocation bdLocation;
	private LocationMode tempMode;// = LocationMode.Hight_Accuracy;
	private String tempcoor;// "gcj02";
	private Context mContext;
	// private ScreenLockLocation sLocation;
	private IReceiveLocationHandler locationHandler;

	public boolean isFlushLocation;

	private final int DELAY_LOCATION_DAFLAUT = 1000;

	private final int MSG_AGAIN_REQUEST_BD_LOCATION = 11;

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.arg1) {
			case MSG_AGAIN_REQUEST_BD_LOCATION:
				requestBDLocation();
				break;

			default:
				break;
			}
			return false;
		}
	});

	private void sendMsgHandlerDelay(int type) {
		sendMsgHandlerDelay(type, DELAY_LOCATION_DAFLAUT);
	}

	private void sendMsgHandlerDelay(int type, long delayTime) {
		Message msg = new Message();
		msg.arg1 = type;
		mHandler.sendMessageDelayed(msg, delayTime);
	}

	private void sendMsgHandler(int type) {
		Message msg = new Message();
		msg.arg1 = type;
		mHandler.sendMessage(msg);
	}

	private LocationServiceUtils() {

	}

	private static LocationServiceUtils mServiceUtils;

	public static LocationServiceUtils getInstance() {
		if (mServiceUtils == null) {
			mServiceUtils = new LocationServiceUtils();
		}
		return mServiceUtils;
	}

	public LocationServiceUtils initLocation(Context context) {
		mContext = context;
		// 初始化百度服务
		initBDService();
		initGeoCoder();
		// 开启锁屏定位
		// openScreenLockLocatioin();
		// requestLocation();

		// 初始化GPS Location
		// startGpsService();
		return mServiceUtils;
	}

	public void initBDService() {
		Log.i(TAG, "iniBDService");
		// 高精度LocationMode.Hight_Accuracy，低功耗LocationMode.Battery_Saving，LocationMode.Device_Sensors
		tempMode = LocationMode.Hight_Accuracy;
		// BDGeofence. COORD_TYPE_BD09 坐标类型bd09（百度摩卡托坐标）
		// BDGeofence.COORD_TYPE_BD09LL 坐标类型bd09ll（百度经纬度坐标，可以在百度系统产品直接使用）
		// BDGeofence. COORD_TYPE_GCJ 坐标类型gcj02（国测局加密经纬度坐标
		tempcoor = BDGeofence.COORD_TYPE_BD09LL;// "gcj02";

		locationClient = new LocationClient(mContext);
		locationListener = new MyLocationListener();
		locationClient.registerLocationListener(locationListener);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setLocationMode(tempMode);// 设置定位模式
		option.setCoorType(tempcoor);// 返回的定位结果是百度经纬度，默认值gcj02
		// option.setScanSpan(5 * 60 * 1000);// 设置间隔时间小于1000ms，让它不自动上传定位
		option.setScanSpan(100);// 设置间隔时间小于1000ms，让它不自动上传定位
		option.setIsNeedAddress(true);
		locationClient.setLocOption(option);
		locationClient.start();
	}

	/**
	 * 开启定位
	 */
	// public void startLocation() {
	// if (isWorking()) {
	// requestLocation();
	// }
	//
	// if (sLocation == null) {
	// openScreenLockLocatioin();
	// }
	// if (!ScreenLockLocation.getInstance().isStartScrrenLock()) {
	// sLocation.start();
	// }
	//
	// }

	/**
	 * 判断百度定位服务是否开启
	 * 
	 * @return
	 */
	public boolean isBDLocationStarted() {
		return (locationClient != null && locationClient.isStarted());
	}

	/**
	 * 判断Gps定们是否开始；
	 * 
	 * @return
	 */
	public boolean isGpsLocationStarted() {
		return isGpsLocationStart;
	}

	/**
	 * 判断定位服务是否开启
	 * 
	 * @return
	 */
	public boolean isLocationStarted() {
		return isBDLocationStarted() && isGpsLocationStart;
	}

	// /**
	// * 开启锁屏定位，确保后台服务在用户锁屏后依然可用
	// */
	// private void openScreenLockLocatioin() {
	// if (sLocation == null) {
	// sLocation = ScreenLockLocation.getInstance().init(mContext,
	// new BroadcastReceiver() {
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// Log.i(TAG, "wakeup screenLock");
	// getTime2();
	// Toast.makeText(mContext, screen + "",
	// Toast.LENGTH_SHORT).show();
	// if (!isRunBDService()) {
	// Log.e(TAG,
	// "baidu server  die  .  init BD service");
	// // 如果服务已死，重新唤起
	// initBDService();
	// }
	// // 请求定位
	// if (isWorking()) {
	// requestLocation();
	// }
	// }
	// });
	// sLocation.start();
	// } else if (!sLocation.isStartScrrenLock()) {
	// sLocation.start();
	// }
	//
	// }

	/**
	 * 请求定位
	 */
	// public void requestLocation() {
	// Log.i(TAG, "requestLocation");
	//
	// if (!requestGpsLocation()) {
	// requestBDLocation();
	// }
	// }

	/**
	 * 请求GPS定位
	 */
	public boolean requestGpsLocation() {
		if (locationManager == null || lbsLocationListener == null
				|| gpsSatelliteListener == null || !isGpsLocationStart) {
			startGpsService();
			return false;
		}
		// 通过GPS获取位置LocationManager.GPS_PROVIDER
		gpsLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		gpsStatus = locationManager.getGpsStatus(null);

		if (gpsLocation != null) {
			// LogUtil.d(TAG, "location=" + gpsLocation + "\n ");
			LatLng latlng = UtilsBD.commonToBD(gpsLocation.getLatitude(),
					gpsLocation.getLongitude(), CoordType.GPS);

			getGeoAddress(latlng);
			// type = "(1)";
			// sendMsgHandler(GET_GEO_ADDRESS_GPS);
			return true;
		} else {
			stopGpsService();

			ToastUtil.show(mContext, "GPS Fail");
			Log.e(TAG, "onReceiveLocation:"
					+ "\n硬件gps未获取到有效定位信息，gpsLocation is null.");

			LogUtil.d(TAG, "gps location==null");
			// requestBDLocation();
			// try {
			// sendMsgHandlerDelay(DELAY_START_GPS_SERVER);
			// } catch (Exception e) {
			// ErrLogUtils.uploadErrLog(mContext, ErrLogUtils.toString(e));
			// }
			return false;
		}
	}

	/**
	 * 请求BD定位
	 */
	public int requestBDLocation() {
		if (locationClient == null || locationListener == null) {
			initBDService();
		}
		if (isBDLocationStarted()) {
			int result = locationClient.requestLocation();
			Log.i(TAG, "requestBDLocation=" + result);
			if (result == 0) {
				// sendMsgHandlerDelay(DELAY_BD_REQUEST);
				// 出现百度定位有时一直无回调，暂时用这个
				// locationClient.start();
			}
			return result;
		} else {
			Log.i(TAG, "locationClient.start");
			locationClient.start();
			return -1;
		}
	}

	public void requestBDLocation(IReceiveLocationHandler locationHandler) {
		this.locationHandler = locationHandler;
	}

	private void stopBDLocation() {
		// 停止BD 定位
		if (locationClient != null) {
			locationClient.stop();
		}
		locationClient = null;
		locationListener = null;
	}

	/**
	 * 停止定位服务
	 */
	public void abortLocation() {

		// 停止BD 定位
		stopBDLocation();
		// 停止GPS 定位
		stopGpsService();
		// 停止Geo
		stopGeoCoder();
	}

	/**
	 * 设置定位接收回调
	 * 
	 * @param handler
	 */
	public void setReceLocationHandler(IReceiveLocationHandler handler) {
		locationHandler = null;
		locationHandler = handler;
	}

	public interface IReceiveLocationHandler {
		void onReceiveHandler(MyLocation location);

		void onReceiveFail();
	}

	private void againRequestBD() {
		if (UtilsAndroid.Set.checkNetState(mContext)) {
			mHandler.sendEmptyMessageDelayed(MSG_AGAIN_REQUEST_BD_LOCATION,
					DELAY_LOCATION_DAFLAUT);
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.httpError),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void callBack(MyLocation location) {
		// 回调数据
		// 获取定位后的回调接口，供订单提交时上传定位信息使用
		if (locationHandler != null) {
			locationHandler.onReceiveHandler(location);
		}
	}

	private void callBackFailed() {
		if (locationHandler != null) {
			locationHandler.onReceiveFail();
		}
	}

	private void callBack(int errorType) {
		MyLocation location = new MyLocation();
		location.errorCode = errorType;
		callBack(location);
	}

	private boolean checkBDLocation(BDLocation location) {
		boolean flag = false;
		if (location == null) {
			againRequestBD();
		} else {
			MyLocation myLocation = new MyLocation(location.getLongitude(),
					location.getLatitude(), location.getAddrStr());
			if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				// TypeNetWorkLocation// 定位结果描述：网络定位结果

				if (TextUtils.isEmpty(location.getAddrStr())) {
					getGeoAddress(myLocation);
				} else {
					callBack(myLocation);
				}
				flag = true;
			} else if (location.getLocType() == BDLocation.TypeGpsLocation) {
				// TypeGpsLocation// 定位结果描述：GPS定位结果
				if (TextUtils.isEmpty(location.getAddrStr())) {
					getGeoAddress(myLocation);
				} else {
					callBack(myLocation);
				}
				flag = true;
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
				// TypeOffLineLocation// 定位结果描述：离线定位结果
				if (TextUtils.isEmpty(location.getAddrStr())) {
					getGeoAddress(myLocation);
				} else {
					callBack(myLocation);
				}
				flag = true;
			} else if (location.getLocType() == BDLocation.TypeNone) {
				// TypeNone// 定位结果描述：无效定位结果
				againRequestBD();
			} else if (location.getLocType() == BDLocation.TypeOffLineLocationFail) {
				// TypeOffLineLocationFail// 定位结果描述：离线定位失败
				againRequestBD();
			} else if (location.getLocType() == BDLocation.TypeOffLineLocationNetworkFail) {
				// TypeOffLineLocationNetworkFail// 定位结果描述：网络请求失败,基站离线定位结果
				againRequestBD();
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				// TypeNetWorkException// 定位结果描述：网络连接失败
				callBackFailed();
				ToastUtil.show(mContext, "网络连接失败");
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				// TypeServerError// 定位结果描述：server定位失败，没有对应的位置信息
				callBackFailed();
				ToastUtil.show(mContext, "定位失败");
			} else {
				againRequestBD();
			}

		}
		return flag;
	}

	/**
	 * 百度定位监听
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			bdLocation = location;
			checkBDLocation(location);
		}

	}

	// private void sendCityBroadcast(BDLocation location) {
	// private void sendCityBroadcast(String newCity) {
	// // 从定位信息中获取当前所在城市，便于获取天气信息
	// String oldCity = PreferencesUtil.getPrefString(
	// PreferencesUtil.KEY_CITY, "");
	// if (!TextUtils.isEmpty(newCity) && !newCity.equals(oldCity)) {
	// PreferencesUtil.setPrefString(PreferencesUtil.KEY_CITY, newCity);
	// Intent intent = new Intent();
	// intent.setAction(Constants.Action.ACTION_CITY_CHANGED);
	// mContext.sendBroadcast(intent);
	// }
	// }
	/**
	 * 
	 */
	private GeoCoder mGeoCoder;
	private MyGeoListener myGeoListener;

	private void initGeoCoder() {
		mGeoCoder = GeoCoder.newInstance();
		myGeoListener = new MyGeoListener();
		mGeoCoder.setOnGetGeoCodeResultListener(myGeoListener);
	}

	/**
	 * 百度地图反地理编码 获取地址信息
	 * 
	 * @param latLng
	 */
	private void getGeoAddress(MyLocation location) {
		getGeoAddress(new LatLng(location.latitude, location.longitude));
	}

	private void getGeoAddress(final LatLng latLng) {
		if (!UtilsAndroid.Set.checkNetState(mContext)) {
			Toast.makeText(mContext,
					mContext.getResources().getString(R.string.httpError),
					Toast.LENGTH_LONG).show();
			return;
		}

		ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
		reverseGeoCodeOption.location(latLng);
		boolean isReverseGeoCodeSuccess = mGeoCoder
				.reverseGeoCode(reverseGeoCodeOption);
		LogUtil.d(TAG, "ReversGeoSuccess=" + isReverseGeoCodeSuccess);
	}

	private void stopGeoCoder() {
		if (mGeoCoder != null) {
			mGeoCoder.destroy();
		}
		myGeoListener = null;
	}

	private class MyGeoListener implements OnGetGeoCoderResultListener {
		@Override
		public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
			LogUtil.d(TAG, "GeoCodeResult>>" + geoCodeResult.getAddress() + ":"
					+ geoCodeResult.getLocation());
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseResult) {

			String address = reverseResult.getAddress();
			double longitude = reverseResult.getLocation().longitude;
			double latitude = reverseResult.getLocation().latitude;
			if (TextUtils.isEmpty(address)) {
				String msg = String.format(
						mContext.getString(R.string.gps_location_empty_msg),
						latitude + "", longitude + "");
				Log.e(TAG, "GPS异常地址>>>Exception:" + msg);
				callBackFailed();
			} else {
				MyLocation location = new MyLocation(longitude, latitude,
						address);
				callBack(location);
			}

			// sendCityBroadcast(reverseResult.getAddressDetail().city);

		}
	}

	/**
	 * gps 定位
	 */
	// 30000ms --minimum time interval between location updates, in milliseconds
	private final long GPS_MIN_TIME = 60 * 1000;
	// 最小变更距离 10m --minimum distance between location updates, in meters
	private final float GPS_MIN_DISTANCE = 100;
	private LBSServiceListener lbsLocationListener;
	private GpsSatelliteListener gpsSatelliteListener;
	private Location gpsLocation;
	private LocationManager locationManager;
	private String provider = LocationManager.GPS_PROVIDER;
	private GpsStatus gpsStatus;
	private boolean isGpsLocationStart = false;

	// private void initGpsService() {
	//
	// startGpsService();
	// }

	private void startGpsService() {
		UtilsAndroid.Set.openGPSSettings(mContext);

		// 获取系统硬件gps定位服务.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager == null) {
			locationManager = (LocationManager) mContext
					.getSystemService(Context.LOCATION_SERVICE);
		}

		lbsLocationListener = new LBSServiceListener();
		gpsSatelliteListener = new GpsSatelliteListener();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				GPS_MIN_TIME, GPS_MIN_DISTANCE, lbsLocationListener);
		locationManager.addGpsStatusListener(gpsSatelliteListener);
		Log.i(TAG, "in startGpsService method.");
		// doJob();
		isGpsLocationStart = true;
	}

	private void stopGpsService() {
		if (locationManager != null && lbsLocationListener != null) {
			locationManager.removeUpdates(lbsLocationListener);
		}

		if (locationManager != null && gpsSatelliteListener != null) {
			locationManager.removeGpsStatusListener(gpsSatelliteListener);
		}
		Log.i(TAG, "in stopGpsService method.");
		isGpsLocationStart = false;
	}

}
