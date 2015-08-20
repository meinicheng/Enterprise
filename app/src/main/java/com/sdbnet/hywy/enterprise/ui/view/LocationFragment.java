package com.sdbnet.hywy.enterprise.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.model.Coordinate;
import com.sdbnet.hywy.enterprise.model.Staff;
import com.sdbnet.hywy.enterprise.model.WorkGroup;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.CaptureActivity;
import com.sdbnet.hywy.enterprise.ui.ChooseGroupsActivity;
import com.sdbnet.hywy.enterprise.ui.InfoActivity;
import com.sdbnet.hywy.enterprise.ui.base.BaseFrament;
import com.sdbnet.hywy.enterprise.ui.widget.BadgeView;
import com.sdbnet.hywy.enterprise.ui.widget.DialogLoading;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.LogUtil;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;
import com.sdbnet.hywy.enterprise.utils.UtilsCommon;
import com.sdbnet.hywy.enterprise.utils.UtilsError;
import com.sdbnet.hywy.enterprise.utils.UtilsJava;
import com.sdbnet.hywy.enterprise.utils.UtilsModel;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class LocationFragment extends BaseFrament {
    private static final String TAG = "LocationFragment";
    /**
     * MapView 是地图主控件
     */
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private InfoWindow mInfoWindow;

    // private LinearLayout myOrderLayout;
    private LinearLayout mLlSwitchRoles;
    private LinearLayout mLlOrderScan;

    private Staff mCurrentStaff;
    private OnInfoWindowClickListener infoWindowListener;

    private LatLng mInfowindLatlng;
    private Dialog mLoadDialog;
    private String mTelephone;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor orange = BitmapDescriptorFactory
            .fromResource(R.drawable.gps_orange);
    BitmapDescriptor gray = BitmapDescriptorFactory
            .fromResource(R.drawable.gps_gray);

    private BadgeView badView;
    // private List<Marker> markers;
    private MapStatusUpdate u;

    private LinearLayout mLlMyLoc;
    private LinearLayout mLlOldLoc;
    private LinearLayout mLlRefresh;
    private LinearLayout mLlSearch;
    private WorkGroup group;

    private String groupId = "";
    private Handler handler;

    private Button mPrevMarker;
    private Button mNextMarker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceonCreateViewState) {

        View view = inflater.inflate(R.layout.fragment_location, null);

        initControls(view);
        initListener();
        locNowPlace();
        loadDatas();
        isDisplay();
        return view;

    }


    /**
     * 加载工作组成员的位置信息
     */
    private void loadDatas() {
        AsyncHttpService.switchGroup(groupId, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                mLoadDialog.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                showMsg(R.string.httpError);
                mLoadDialog.dismiss();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.d(TAG, "location: " + response.toString());
                try {
                    if (!UtilsError.isErrorCode(getActivity(), response)) {
                        // 解析工作组信息
                        JSONObject jsonGroup = response
                                .getJSONObject(Constants.Feild.KEY_WORK_GROUP);
                        group = new WorkGroup();
                        group.setGrpid(jsonGroup
                                .getString(Constants.Feild.KEY_GROUP_ID));
                        group.setGrpname(jsonGroup
                                .getString(Constants.Feild.KEY_GROUP_NAME));
                        group.setLocation(jsonGroup
                                .getInt(Constants.Feild.KEY_GROUP_LOCATION));

                        // 解析工作组成员信息
                        JSONArray arrs = jsonGroup
                                .getJSONArray(Constants.Feild.KEY_STAFFS);
                        for (int i = 0; i < arrs.length(); i++) {
                            JSONObject jsonObj = arrs.getJSONObject(i);
//							Staff staff = UtilsModel.jsonToStaff(jsonObj);
                            Staff staff = new Staff();

                            staff.setPid(jsonObj.getString(Constants.Feild.KEY_STAFF_ID));
                            staff.setPname(jsonObj.getString(Constants.Feild.KEY_STAFF_NAME));
                            staff.setLatitude(jsonObj
                                    .getDouble(Constants.Feild.KEY_LOCA_LATITUDE));
                            staff.setLocaddress(jsonObj
                                    .getString(Constants.Feild.KEY_LOCA_ADDRESS));
                            staff.setLoctel(jsonObj.getString(Constants.Feild.KEY_LOCA_TEL));
                            staff.setLoctime(jsonObj.getString(Constants.Feild.KEY_LOCA_TIME));
                            staff.setLongitude(jsonObj
                                    .getDouble(Constants.Feild.KEY_LOCA_LONGITUDE));
                            staff.setTrucklength(jsonObj
                                    .getDouble(Constants.Feild.KEY_STAFF_TRUCK_LENGTH));
                            staff.setTruckno(jsonObj
                                    .getString(Constants.Feild.KEY_STAFF_TRUCK_NO));
                            staff.setTrucktype(jsonObj
                                    .getString(Constants.Feild.KEY_STAFF_TRUCK_TYPE));
                            staff.setTruckweight(jsonObj
                                    .getDouble(Constants.Feild.KEY_STAFF_TRUCK_WEIGHT));
                            staff.setStatus(jsonObj.getString(Constants.Feild.KEY_STAFF_STATUS));

                            group.getStaffs().add(staff);

                        }
                        mBaiduMap.clear();
                        // 地图画点
                        drawOverlay(group);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mLoadDialog.dismiss();
                }
            }

        }, getActivity());
    }

    /**
     * 是否加载引导界面;
     *
     * @param act
     * @param viewId
     * @param imageId
     * @param preferenceName
     */
    private void setGuidImage(Activity act, int viewId, int imageId,
                              String preferenceName) {
        @SuppressWarnings("static-access")
        SharedPreferences preferences = act.getSharedPreferences(
                preferenceName, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        final String key = act.getClass().getName() + "_firstLogin";
        if (!preferences.contains(key)) {
            editor.putBoolean(key, true);
            editor.commit();
        }

        // 判断是否首次登陆
        if (!preferences.getBoolean(key, true))
            return;

        View view = act.getWindow().getDecorView().findViewById(viewId);
        ViewParent viewParent = view.getParent();
        if (viewParent instanceof FrameLayout) {
            final FrameLayout frameLayout = (FrameLayout) viewParent;
            final ImageView guideImage = new ImageView(act.getApplication());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            guideImage.setLayoutParams(params);
            guideImage.setScaleType(ImageView.ScaleType.FIT_XY);
            guideImage.setImageResource(imageId);
            guideImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.removeView(guideImage);
                    editor.putBoolean(key, false);
                    editor.commit();
                }
            });
            frameLayout.addView(guideImage);// 添加引导图片

        }
    }

    /**
     * 初始化控件
     */
    private void initControls(View view) {
        setGuidImage(getActivity(), R.id.r1, R.drawable.main_above, "mainFirst");

        mMapView = (MapView) view.findViewById(R.id.fragment_location_bmapView);
        mBaiduMap = mMapView.getMap();
        // MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        // mBaiduMap.setMapStatus(msu);

        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setBuildingsEnabled(false);
        mLoadDialog = new DialogLoading(getActivity(),
                getString(R.string.tip_loading));
        for (int i = 0; i < mMapView.getChildCount(); i++) {
            View child = mMapView.getChildAt(i);
            LogUtil.i("LocationFragment", "child:" + child.toString());
            // 隐藏百度logo和缩放控件ZoomControl
            if (child instanceof ZoomControls) {
                child.setVisibility(View.INVISIBLE);
            }
        }

        mLlOrderScan = (LinearLayout) view
                .findViewById(R.id.fragment_location_lay_order_scan);

        mLlSwitchRoles = (LinearLayout) view
                .findViewById(R.id.fragment_location_ll_switch_roles);

        mLlMyLoc = (LinearLayout) view
                .findViewById(R.id.fragment_location_layoutMyLocation);

        mLlOldLoc = (LinearLayout) view
                .findViewById(R.id.fragment_location_layoutOldLocation);

        mLlRefresh = (LinearLayout) view
                .findViewById(R.id.fragment_location_layoutRefresh);

        mLlSearch = (LinearLayout) view
                .findViewById(R.id.fragment_location_layoutSearch);

    }

    private void initListener() {
        infoWindowListener = new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick() {
                LogUtil.d(TAG, "mTelephone:" + mTelephone);
                if (!UtilsJava.isMobile(mTelephone)) {
                    showMsg(R.string.phone_num_illegal_cannot_dial);
                } else {
                    // // 用intent启动拨打电话
                    // Intent intent = new Intent(Intent.ACTION_CALL,
                    // Uri.parse("tel:" + mTelephone));
                    // startActivity(intent);
                    // jump to InfoActivity.class
                    Intent intent = new Intent(getActivity(),
                            InfoActivity.class);
                    intent.putExtra(InfoActivity.STAFF_INFO, mCurrentStaff);
                    startActivity(intent);
                }
            }
        };
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                final Staff staff = marker.getExtraInfo()
                        .getParcelable("staff");
                mCurrentStaff = staff;
                View inforWindowView = LayoutInflater.from(
                        getActivity().getApplicationContext()).inflate(
                        R.layout.layer_float_roles, null);

                ((TextView) inforWindowView.findViewById(R.id.tvPlate))
                        .setText(staff.getTruckno());
                ((TextView) inforWindowView.findViewById(R.id.tvName))
                        .setText(staff.getPname());
                TextView tvModelDetail = (TextView) inforWindowView
                        .findViewById(R.id.tvModelDetail);
                if (!TextUtils.isEmpty(staff.getTruckno())) {

                    tvModelDetail.setText(staff.getTrucktype()
                            + (TextUtils.isEmpty(String.valueOf(staff
                            .getTrucklength())) ? "" : ","
                            + staff.getTrucklength()
                            + getString(R.string.m))
                            + (TextUtils.isEmpty(String.valueOf(staff
                            .getTruckweight())) ? "" : ","
                            + staff.getTruckweight()
                            + getString(R.string.t)));
                    tvModelDetail.setVisibility(View.VISIBLE);
                } else {
                    tvModelDetail.setVisibility(View.GONE);
                }
                // 测试使用 --start
                // String time = staff.getLoctime();
                // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                // String today = sdf.format(new Date());
                // try {
                // if(DateUtil.isBefore(sdf.parse(time), sdf.parse(today))){
                // sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // time = DateUtil.getDateByAddMinute(sdf.format(new Date()), 0
                // - (Integer.parseInt(GenerateRandomUtil.generateNumber(1)) +
                // 5));
                // }
                // } catch (ParseException e) {
                // e.printStackTrace();
                // }
                // ((TextView) v.findViewById(R.id.tvLocTime)).setText(time);
                // 测试使用 --end
                ((TextView) inforWindowView.findViewById(R.id.tvLocTime))
                        .setText(staff.getLoctime());
                ((TextView) inforWindowView.findViewById(R.id.tvLocAddress))
                        .setText(staff.getLocaddress());
                mTelephone = staff.getLoctel();
                mInfowindLatlng = marker.getPosition();
                // mInfoWindow = null;
                mInfoWindow = new InfoWindow(BitmapDescriptorFactory
                        .fromView(inforWindowView), mInfowindLatlng,
                        -UtilsAndroid.UI.dip2px(getActivity(), 35),
                        infoWindowListener);

                mBaiduMap.showInfoWindow(mInfoWindow);
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
        mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus arg0) {
            }

            @Override
            public void onMapStatusChange(MapStatus arg0) {
            }
        });

        mLlOrderScan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                UtilsCommon
                        .start_activity(getActivity(), CaptureActivity.class);
            }
        });
        mLlSwitchRoles.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(),
                        ChooseGroupsActivity.class);
                startActivityForResult(intent,
                        Constants.RequestCode.CHOOSE_GROUP);
            }
        });
        mLlMyLoc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mBaiduMap.setMyLocationEnabled(false);
                if (null != group && group.getStaffs().size() > 0) {
                    mLoadDialog.show();
                    mBaiduMap.clear();
                    // 地图画点
                    drawOverlay(group);
                } else {
                    mLoadDialog.dismiss();
                }
            }
        });
        mLlOldLoc.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }
        });
        mLlRefresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mBaiduMap.setMyLocationEnabled(false);
                if (null != group) {
                    loadDatas();
                }
            }
        });
        mLlSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mBaiduMap.setMyLocationEnabled(false);
            }
        });
    }

    // ??
    // public static Bitmap convertViewToBitmap(View view) {
    // view.setDrawingCacheEnabled(true);
    // view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
    // MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    // view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    // view.buildDrawingCache();
    // Bitmap bitmap = view.getDrawingCache();
    // return bitmap;
    // }

    /**
     * 地图画点
     *
     * @param group
     */
    private void drawOverlay(WorkGroup group) {
        if (group == null || group.getStaffs().size() == 0) {
//            if (mLoadDialog.isShowing())
            mLoadDialog.dismiss();
            showMsg(R.string.no_group_msg);
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        // markers = new ArrayList<Marker>();
        LatLng latLng = null;
        boolean isDisplay=isDisplay();
        for (int i = 0; i < group.getStaffs().size(); i++) {
            Staff staff = group.getStaffs().get(i);
            latLng = new LatLng(staff.getLatitude(), staff.getLongitude());
            builder.include(latLng);
            OverlayOptions ooA = null;
            if ("1".equals(staff.getStatus())) { // 离线

                if (isDisplay) {
                    staff.setLoctime(getRandomTime());
                    ooA = new MarkerOptions().position(latLng).icon(orange)
                            .zIndex(9).draggable(true);
                } else {
                    ooA = new MarkerOptions().position(latLng).icon(gray).zIndex(9)
                            .draggable(true);
                }
            } else { // 在线
                ooA = new MarkerOptions().position(latLng).icon(orange)
                        .zIndex(9).draggable(true);
            }
            Marker mMarker = (Marker) (mBaiduMap.addOverlay(ooA));
            Bundle bundle = new Bundle();
            bundle.putParcelable("staff", staff);
            mMarker.setExtraInfo(bundle);
            // markers.add(mMarker);
        }
        LatLngBounds bounds = builder.build();
        // 根据工作组成员数量控制缩放级别
        if (group.getStaffs().size() == 1) {
            u = MapStatusUpdateFactory.newLatLngZoom(latLng, 19);
        } else {
            u = MapStatusUpdateFactory.newLatLngBounds(bounds);
        }
        // mMapView.getMap().setMapStatus(u);
        // mBaiduMap.animateMapStatus(u);
        mMapView.post(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        });
        ((TextView) getActivity().findViewById(R.id.common_view_title_text))
                .setText(group.getGrpname());
        // 去掉loading
        ((LinearLayout) getActivity().findViewById(R.id.ll_loading))
                .setVisibility(View.GONE);
//        if (mLoadDialog.isShowing())
//            mLoadDialog.dismiss();
    }

//    /**
//     * 清除所有Overlay
//     *
//     * @param view
//     */
    // private void clearOverlay(View view) {
    // mBaiduMap.clear();
    // }

    //    /**
//     * 重新添加Overlay
//     *
//     * @param view
//     */
    // public void resetOverlay(View view) {
    // clearOverlay(null);
    // drawOverlay(group);
    // }
    @Override
    public void onPause() {
        LogUtil.d(TAG, "LocationFragment: onPause");
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.setVisibility(View.INVISIBLE);
        mMapView.onPause();
        if (badView != null)
            badView.toggle();
        super.onPause();
    }

    @Override
    public void onResume() {
        LogUtil.d(TAG, "LocationFragment: onResume");
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.setVisibility(View.VISIBLE);
        mMapView.onResume();
        if (badView != null)
            badView.toggle();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "LocationFragment: onDestroy");
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
        super.onDestroy();
        // 回收 bitmap 资源
        orange.recycle();
        gray.recycle();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "LocationFragment: onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        mMapView.getMap().animateMapStatus(u);
                        break;
                    case 2:
                        mLocationClient.start();
                        mLocationClient.requestLocation();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Constants.RequestCode.CHOOSE_GROUP == requestCode
                && resultCode == Constants.ResultCode.CHOOSE_GROUP) {
            // System.out.println("groupid: "
            // + data.getStringExtra(Constants.Feild.KEY_GROUP_ID));
            LogUtil.d(
                    TAG,
                    "groupid: "
                            + data.getStringExtra(Constants.Feild.KEY_GROUP_ID));

            mLoadDialog.show();

            // 获取切换的工作组，重新加载并画点
            groupId = data.getStringExtra(Constants.Feild.KEY_GROUP_ID);
            loadDatas();
        }
    }

    private LocationClient mLocationClient;// 定位SDK的核心类
    private MyLocationListener mLocationListener;

    private void locNowPlace() {
        mLocationClient = new LocationClient(getActivity());
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);// 注册监听函数
        // 设置定位参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setAddrType("all");// 返回的定位结果包含地址信息
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        // locationOption.disableCache(true);//禁止启用缓存定位
        // option.setPriority(LocationClientOption.GpsFirst);// 设置定位方式的优先级
        option.setAddrType("all");// 返回的定位结果包含地址信息
        // option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
        // locationOption.setScanSpan(2000);//设置定时定位的时间间隔。单位ms
        option.setProdName("自我定位");
        mLocationClient.setLocOption(option);
    }

    // BDLocationListener获取定位结果，获取POI信息
    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mBaiduMap == null) {
                return;
            }
            // 将我的当前位置移动到地图的中心点
            try {
                mBaiduMap.setMyLocationEnabled(true);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            LatLng latLng = new LatLng(location.getLatitude(),
                    location.getLongitude());
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(latLng,
                    19);

            // mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(new
            // MapStatus.Builder().zoom(18).build()));
            try {
                mBaiduMap.animateMapStatus(msu);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            // mLocationClient.stop();
            // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
            // MyLocationConfiguration config = new
            // mBaiduMap.setMyLocationConfigeration(config);
            // 当不需要定位图层时关闭定位图层
            // mBaiduMap.setMyLocationEnabled(false);
        }

    }

    // ///////////////////////////////////
//    private RoutePlanSearch mSearch;
//    private MyRoutePlanListener myRoutePlanListener;
//    private PlanNode mStartNode;
//    private PlanNode mEndNode;
//    private List<PlanNode> mPassByNode;
//
//    private void initSearch() {
//        // 初始化搜索模块，注册事件监听
//        mSearch = RoutePlanSearch.newInstance();
//        myRoutePlanListener = new MyRoutePlanListener();
//        mSearch.setOnGetRoutePlanResultListener(myRoutePlanListener);
//        mPassByNode = new ArrayList<PlanNode>();
//    }
//
    // public void SearchButtonProcess(View v) {
    // // // 重置浏览节点的路线数据
    // // route = null;
    // // mBtnPre.setVisibility(View.INVISIBLE);
    // // mBtnNext.setVisibility(View.INVISIBLE);
    // // mBaidumap.clear();
    // // // 处理搜索按钮响应
    // // EditText editSt = (EditText) findViewById(R.id.start);
    // // EditText editEn = (EditText) findViewById(R.id.end);
    // // 设置起终点信息，对于tranist search 来说，城市名无意义
    // mStartNode = PlanNode.withCityNameAndPlaceName("北京", editSt.getText()
    // .toString());
    // mEndNode = PlanNode.withCityNameAndPlaceName("北京", editEn.getText()
    // .toString());
    // // java.util.List<PlanNode> wayPoints=new ArrayList<PlanNode>();
    // // PlanNode node=PlanNode.withLocation(new LatLng(arg0, arg1));
    // // 实际使用中请对起点终点城市进行正确的设定
    // if (v.getId() == R.id.drive) {
    // mSearch.drivingSearch((new DrivingRoutePlanOption()).from(mStartNode)
    // .passBy(wayPoints).to(mEndNode));
    // } else if (v.getId() == R.id.transit) {
    // mSearch.transitSearch((new TransitRoutePlanOption()).from(mStartNode)
    // .city("北京").to(mEndNode));
    // } else if (v.getId() == R.id.walk) {
    // mSearch.walkingSearch((new WalkingRoutePlanOption()).from(mStartNode)
    // .to(mEndNode));
    // }
    // // mPassByNode.clear();
    // }

    private class MyRoutePlanListener implements OnGetRoutePlanResultListener {

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult arg0) {
            // if (result == null || result.error !=
            // SearchResult.ERRORNO.NO_ERROR) {
            // Toast.makeText(RoutePlanDemo.this, "抱歉，未找到结果",
            // Toast.LENGTH_SHORT)
            // .show();
            // }
            // if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // // result.getSuggestAddrInfo()
            // return;
            // }
            // if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            // nodeIndex = -1;
            // mBtnPre.setVisibility(View.VISIBLE);
            // mBtnNext.setVisibility(View.VISIBLE);
            // route = result.getRouteLines().get(0);
            // DrivingRouteOverlay overlay = new
            // MyDrivingRouteOverlay(mBaidumap);
            // routeOverlay = overlay;
            // mBaidumap.setOnMarkerClickListener(overlay);
            // overlay.setData(result.getRouteLines().get(0));
            // overlay.addToMap();
            // overlay.zoomToSpan();
            // }
        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult arg0) {
        }

        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
        }

    }

	/*
     *
	 */

    private List<LatLng> mLatLngs = new ArrayList<LatLng>();
    // private List<LatLng> mSelectLatLngs = new ArrayList<LatLng>();
    private List<Coordinate> mCoordinates = new ArrayList<Coordinate>();
    private List<Coordinate> mSelectCoordinates = new ArrayList<Coordinate>();

    // private Map<String,Object> map=new HashMap<String, Object>();
    // private final String LATLNG_LIST="latlng_list";
    // private final String ADDRESS_LIST="address_list";
    // private final String DATE_LIST="date_list";
    private void fillData() {
        JSONArray coorArr = null;
        JSONObject jsonObj;
        Coordinate coor;
        try {
            for (int j = 0; j < coorArr.length(); j++) {
                jsonObj = coorArr.getJSONObject(j);
                coor = new Coordinate();
                double latitude = jsonObj
                        .getDouble(Constants.Feild.KEY_LOCA_LATITUDE);
                coor.setLatitude(latitude);

                double longitude = jsonObj
                        .getDouble(Constants.Feild.KEY_LOCA_LONGITUDE);
                coor.setLongitude(longitude);

                coor.setAddress(jsonObj
                        .getString(Constants.Feild.KEY_LOCA_ADDRESS));
                coor.setNewDate(jsonObj
                        .getString(Constants.Feild.KEY_LOCA_TIME));
                mCoordinates.add(coor);
                mLatLngs.add(new LatLng(latitude, longitude));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void drawLine() {
        mBaiduMap
                .addOverlay(new PolylineOptions().width(10)
                        .color(getResources().getColor(R.color.green))
                        .points(mLatLngs));
    }

    private void drawMarker() {
        mSelectCoordinates.clear();
        mBaiduMap.clear();
        drawLine();
        LatLngBounds bound = mBaiduMap.getMapStatus().bound;
        if (mCoordinates.size() <= MARK_NUM) {
            for (int i = 0; i < mCoordinates.size(); i++)
                addMarker(mCoordinates.get(i));
            return;
        }
        LatLng latLng;
        Coordinate coordinate;
        for (int i = 0; i < mCoordinates.size(); i++) {
            coordinate = mCoordinates.get(i);
            latLng = new LatLng(coordinate.latitude, coordinate.longitude);
            if (bound.contains(latLng)) {
                mSelectCoordinates.add(mCoordinates.get(i));
            }
        }
        if (mSelectCoordinates.size() <= MARK_NUM) {
            for (int i = 0; i < mSelectCoordinates.size(); i++)
                addMarker(mSelectCoordinates.get(i));
        } else {
            length = mSelectCoordinates.size();
            interval = length / MARK_NUM;
            currentPage = 1;
            for (int i = currentPage - 1; i < length; i += interval)
                addMarker(mSelectCoordinates.get(i));

        }
    }

    private final int MARK_NUM = 15;

    private int interval;
    private int length;
    private int currentPage;

    private void drawNextMarker() {
        if (mCoordinates.size() <= MARK_NUM
                || mSelectCoordinates.size() <= MARK_NUM) {
            return;
        }

        if (length % MARK_NUM == 0) {
            if (currentPage <= interval) {
                mBaiduMap.clear();
                drawLine();
                for (int i = currentPage; i < mSelectCoordinates.size(); i += interval)
                    addMarker(mSelectCoordinates.get(i));
            } else {
                showMsg("没有下一页");
            }
        } else {
            if (currentPage == interval + 1) {
                mBaiduMap.clear();
                drawLine();
                for (int i = interval * MARK_NUM; i < mSelectCoordinates.size(); i++)
                    addMarker(mSelectCoordinates.get(i));
            } else if (currentPage <= interval) {
                mBaiduMap.clear();
                drawLine();
                for (int i = currentPage; i < mSelectCoordinates.size(); i += interval)
                    addMarker(mSelectCoordinates.get(i));
            } else {
                showMsg("没有下一页");
            }
        }

        currentPage++;
    }

    private void drawPrevMarker() {
        if (mCoordinates.size() <= MARK_NUM
                || mSelectCoordinates.size() <= MARK_NUM) {
            return;
        }

        if (length % MARK_NUM == 0) {
            if (currentPage <= interval) {
                mBaiduMap.clear();
                drawLine();
                for (int i = currentPage; i < mSelectCoordinates.size(); i += interval)
                    addMarker(mSelectCoordinates.get(i));
            } else {
                showMsg("没有下一页");
            }
        } else {
            if (currentPage == interval + 1) {
                mBaiduMap.clear();
                drawLine();
                for (int i = interval * MARK_NUM; i < mSelectCoordinates.size(); i++)
                    addMarker(mSelectCoordinates.get(i));
            } else if (currentPage <= interval) {
                mBaiduMap.clear();
                drawLine();
                for (int i = currentPage; i < mSelectCoordinates.size(); i += interval)
                    addMarker(mSelectCoordinates.get(i));
            } else {
                showMsg("没有下一页");
            }
        }

        currentPage++;
    }

    private void addMarker(Coordinate coorList) {
        Marker mMarker;
        LatLng ll = new LatLng(coorList.latitude, coorList.longitude);
        Bundle bundle = new Bundle();

        mMarker = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
                .position(ll).icon(imgMiddle).zIndex(9).draggable(true));
        bundle.putBoolean("isMiddle", true);

        bundle.putSerializable("coordinate", coorList);
        mMarker.setExtraInfo(bundle);
    }

    // private void drawMarker() {
    // mSelectLatLngs.clear();
    // LatLngBounds bound = mBaiduMap.getMapStatus().bound;
    // if (mLatLngs.size() <= 15) {
    //
    // return;
    // }
    // for (int i = 0; i < mLatLngs.size(); i++) {
    // if (bound.contains(mLatLngs.get(i))) {
    // mSelectLatLngs.add(mLatLngs.get(i));
    // }
    // }
    // if (mSelectLatLngs.size() <= 15) {
    //
    // } else {
    //
    // }
    // }
    //
    // private void addMarker(Coordinate coorList, LatLng ll) {
    // Marker mMarker;
    // Bundle bundle = new Bundle();
    // // if (0 == i) { // 起点
    // // mMarker = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
    // // .position(ll).icon(imgStart).zIndex(9).draggable(true));
    // // bundle.putBoolean("isMiddle", false);
    // // } else if (coorList.size() - 1 == i) { // 终点
    // // mMarker = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
    // // .position(ll).icon(imgTerminal).zIndex(9).draggable(true));
    // // bundle.putBoolean("isMiddle", false);
    // // } else { // 中间点
    // mMarker = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
    // .position(ll).icon(imgMiddle).zIndex(9).draggable(true));
    // bundle.putBoolean("isMiddle", true);
    // // }
    // bundle.putSerializable("coordinate", (Serializable) coorList);
    // mMarker.setExtraInfo(bundle);
    // }

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor imgStart = BitmapDescriptorFactory
            .fromResource(R.drawable.gps_starting); // 起点图标
    BitmapDescriptor imgTerminal = BitmapDescriptorFactory
            .fromResource(R.drawable.gps_terminal); // 终点图标
    BitmapDescriptor imgMiddle = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_track_map_bar); // 中间点图片

    private boolean isDisplay() {
        String options = PreferencesUtil.getValue(Constants.Feild.KEY_OPTIONS);
        Log.d(TAG, "options=" + options);
//        options={"data":"0","isDisplay":"1","workModel":"1","scanModel":"0","isLinePlan":"1"}
        try {
            JSONObject jsonObject = new JSONObject(options);
            if (jsonObject.isNull("isDisplay")) {
                return false;
            }
            String isDisplay = jsonObject.getString("isDisplay");
            if (TextUtils.equals(isDisplay, Constants.Value.YES)) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getRandomTime() {
        long sysTime = System.currentTimeMillis();
        sysTime -= Math.random() * 1000 * 60 * 60 * 2;
        return UtilsJava.translate2SessionMessageData(sysTime);
    }
}
