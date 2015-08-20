package com.sdbnet.hywy.enterprise.utils;

import android.location.Location;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;

public class UtilsBD {
	private final static String TAG = "UtilsBD";

	/**
	 * 将硬件gps坐标转换为百度坐标
	 * 
	 * @param location
	 */
	public static LatLng gpsToBD(Location location) {
		if (location == null) {
			// Toast.makeText(this, "未获取到GPS信息", Toast.LENGTH_SHORT).show();
			Log.e(TAG, "location==null");
			return null;
		}

		// 坐标转换
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordinateConverter.CoordType.GPS);
		LatLng sourceLatLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		converter.coord(sourceLatLng);
		LatLng desLatLng = converter.convert();
		return desLatLng;
	}

	/**
	 * 将其他坐标转换为百度坐标
	 * 
	 * @param location
	 */
	public static LatLng commonToBD(Location location, CoordType type) {
		if (location == null) {
			// Toast.makeText(this, "未获取到GPS信息", Toast.LENGTH_SHORT).show();
			Log.e(TAG, "location==null");
			return null;
		}

		// 坐标转换
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(type);
		LatLng sourceLatLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		converter.coord(sourceLatLng);
		LatLng desLatLng = converter.convert();
		return desLatLng;
	}

	public static LatLng commonToBD(double latitude, double longitude,
			CoordType type) {
		// 坐标转换
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(type);
		LatLng sourceLatLng = new LatLng(latitude, longitude);
		converter.coord(sourceLatLng);
		LatLng desLatLng = converter.convert();
		return desLatLng;

	}

	public static String getLocationData(BDLocation location) {

		final StringBuffer sb = new StringBuffer(256);
		sb.append("time : ");
		sb.append(location.getTime());
		sb.append("\nerror code : ");
		sb.append(location.getLocType());
		sb.append("\nlatitude : ");
		sb.append(location.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(location.getLongitude());
		sb.append("\nradius : ");
		sb.append(location.getRadius());
		sb.append("\naddress : ");
		sb.append(location.getAddrStr());
		sb.append("\ncity : ");
		sb.append(location.getCity());

		if (location.getLocType() == BDLocation.TypeGpsLocation) {
			sb.append("\nspeed : ");
			sb.append(location.getSpeed());
			sb.append("\nsatellite : ");
			sb.append(location.getSatelliteNumber());
			sb.append("\ndirection : ");
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
			// sb.append(location.getDirection());
		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
			// 运营商信息
			sb.append("\noperationers : ");
			// sb.append(location.getOperators());
		}

		return sb.toString();
	}

	public static void logLocationData(BDLocation location) {
		LogUtil.d("BaiduLocationApiDem", getLocationData(location));
	}

}
