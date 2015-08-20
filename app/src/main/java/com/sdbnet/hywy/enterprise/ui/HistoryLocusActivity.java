package com.sdbnet.hywy.enterprise.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.listener.MyRountePlanListener;
import com.sdbnet.hywy.enterprise.model.Coordinate;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.LogUtil;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.SerializableMap;
import com.sdbnet.hywy.enterprise.utils.ToastUtil;
import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;
import com.sdbnet.hywy.enterprise.utils.UtilsError;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryLocusActivity extends BaseActivity implements
        OnClickListener {
    private final String TAG = getClass().getSimpleName();
    public static final int QUERY_TYPE_ORDER = 1;
    public static final int QUERY_TYPE_DATE = 2;
    public static final String FILED_QUERY_TYPE = "filed_query_type";
    private ImageView mImgMenu;

    /**
     * MapView 是地图主控件
     */
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private InfoWindow mInfoWindow;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    private BitmapDescriptor imgStart = BitmapDescriptorFactory
            .fromResource(R.drawable.gps_starting); // 起点图标
    private BitmapDescriptor imgTerminal = BitmapDescriptorFactory
            .fromResource(R.drawable.gps_terminal); // 终点图标
    private BitmapDescriptor imgMiddle = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_track_map_bar); // 中间点图片

    private LinearLayout mLlMarker;
    private Button mPrevMarker;
    private Button mNextMarker;

    private boolean isRoaePlan = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_locus);
        initUI();
        initListener();
        initRoutePlan();
        getLocation();

    }

    /**
     * 初始化控件
     */
    private void initUI() {

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setBuildingsEnabled(false);
        for (int i = 0; i < mMapView.getChildCount(); i++) {
            View child = mMapView.getChildAt(i);
            // 隐藏百度logo和缩放控件ZoomControl
            if (child instanceof ZoomControls) {
                child.setVisibility(View.INVISIBLE);
            }
        }

        findViewById(R.id.common_view_title_img).setOnClickListener(this);
        ((TextView) findViewById(R.id.common_view_title_text))
                .setText(R.string.historical_track);
        mImgMenu = (ImageView) findViewById(R.id.common_view_title_img_menu);
        if (isLinePlan())
            mImgMenu.setVisibility(View.VISIBLE);
        LayoutParams lp = mImgMenu.getLayoutParams();
        lp.width = UtilsAndroid.UI.dip2px(this, 40);
        lp.height = UtilsAndroid.UI.dip2px(this, 40);
        mImgMenu.setLayoutParams(lp);
        mImgMenu.setImageResource(R.drawable.img_road_plan_selector);


        mLlMarker = (LinearLayout) findViewById(R.id.activity_history_loc_marker_ll);
        mPrevMarker = (Button) findViewById(R.id.activity_history_loc_prev_marker_btn);
        mNextMarker = (Button) findViewById(R.id.activity_history_loc_next_marker_btn);

        //init ui click listener
        mImgMenu.setOnClickListener(this);
        mPrevMarker.setOnClickListener(this);
        mNextMarker.setOnClickListener(this);
    }

    private void initListener() {

        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker == null
                        || marker.getExtraInfo() == null
                        || marker.getExtraInfo().getSerializable("coordinate") == null) {
                    return true;
                }
                View mInforwindowView = LayoutInflater.from(
                        HistoryLocusActivity.this).inflate(
                        R.layout.layer_locus_float, null);
                final Coordinate coor = (Coordinate) marker.getExtraInfo()
                        .getSerializable("coordinate");
                boolean isMid = marker.getExtraInfo().getBoolean("isMiddle");
                ((TextView) mInforwindowView.findViewById(R.id.tvLocTime))
                        .setText(coor.getNewDate());
                ((TextView) mInforwindowView.findViewById(R.id.tvLocAddress))
                        .setText(coor.getAddress());
                LatLng ll = marker.getPosition();
                mInfoWindow = null;

                if (isMid) {
                    mInfoWindow = new InfoWindow(BitmapDescriptorFactory
                            .fromView(mInforwindowView), ll, -UtilsAndroid.UI
                            .dip2px(HistoryLocusActivity.this, 15), null);

                } else {
                    mInfoWindow = new InfoWindow(BitmapDescriptorFactory
                            .fromView(mInforwindowView), ll, -UtilsAndroid.UI
                            .dip2px(HistoryLocusActivity.this, 35), null);

                }
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
                LatLngBounds bound = mBaiduMap.getMapStatus().bound;
                if (!isRoaePlan)
                    drawMarker(bound);
            }

            @Override
            public void onMapStatusChange(MapStatus arg0) {

            }
        });

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
        // recycleBitmap();

    }

    private void recycleBitmap() {
        // 回收 bitmap 资源
        imgStart.recycle();
        imgTerminal.recycle();
        imgMiddle.recycle();
    }

    // ///////////////////////////////

    private void getLocation() {
        int type = getIntent().getIntExtra(FILED_QUERY_TYPE, 0);
        if (type == QUERY_TYPE_DATE) {
            Bundle bundle = getIntent().getExtras();
            SerializableMap serializableMap = (SerializableMap) bundle
                    .get("locus_query");
            Map<String, String> mapParams = serializableMap.getMap();
            // String cmpid = PreferencesUtil.user_company;
            // String itemid = PreferencesUtil.item_id;
            String truckno = mapParams.get(Constants.Feild.KEY_STAFF_TRUCK_NO);
            String datefrom = mapParams.get(Constants.Params.PARAM_DATE_FROM);
            String dateto = mapParams.get(Constants.Params.PARAM_DATE_TO);
            getContractMotorLocus(truckno, datefrom, dateto);
        } else if (type == QUERY_TYPE_ORDER) {
            String sysnox = getIntent().getStringExtra(
                    Constants.Feild.KEY_PLATFORM_ORIGINAL_NO);
            String lineno = getIntent().getStringExtra(
                    Constants.Feild.KEY_LINE_ID);
            getHistoryLocus(sysnox, lineno);
        }
    }

    /**
     * 获取相关历史轨迹 以订单来查询
     */
    private void getHistoryLocus(String sysnox, String lineno) {

        if (TextUtils.isEmpty(lineno)) {
            showLongToast(R.string.not_get_related_historical_track);
            return;
        }
        AsyncHttpService.getHistoryLocus(sysnox, lineno,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoading(getString(R.string.is_qurey_ellipsis));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                        showShortToast(getResources().getString(
                                R.string.httpisNull));
                        dismissLoading();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            Log.i("his: ", response.toString());
                            if (UtilsError.isErrorCode(
                                    HistoryLocusActivity.this, response)) {
                                return;
                            }
                            parseJsonOrder(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showLongToast(R.string.not_get_related_historical_track);
                        } finally {
                            dismissLoading();
                        }

                    }
                }, this);

    }

    private List<LatLng> mLatLngs = new ArrayList<LatLng>();
    // private List<LatLng> mSelectLatLngs = new ArrayList<LatLng>();
    private List<Coordinate> mCoordinates = new ArrayList<Coordinate>();
    private List<Coordinate> mSelectCoordinates = new ArrayList<Coordinate>();

    // private Map<String,Object> map=new HashMap<String, Object>();
    // private final String LATLNG_LIST="latlng_list";
    // private final String ADDRESS_LIST="address_list";
    // private final String DATE_LIST="date_list";

    // List<Coordinate> coorList = new ArrayList<Coordinate>();

    // 解析历史轨迹中的坐标点信息

    private LatLngBounds.Builder builder = new LatLngBounds.Builder();

    private void parseJsonOrder(JSONObject response) throws Exception {

        JSONObject jsonObj;
        Coordinate coor;
        LatLng latLng;
        JSONArray jsonArr;
        // LogUtil.e(TAG, "jsonArr=" + jsonArr.length() + "");

        jsonArr = response.getJSONArray(Constants.Feild.KEY_STAFFS);
        if (jsonArr.length() == 0) {
            showLongToast(getString(R.string.no_upload_info_tip_msg));
            return;
        }
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject staffObj = jsonArr.getJSONObject(i);
            JSONArray coorArr = staffObj
                    .getJSONArray(Constants.Feild.KEY_LOCA_COORDINATES);
            if (coorArr.length() == 0) {
                continue;
            }
            // 解析历史轨迹中的坐标点信息
            for (int j = 0; j < coorArr.length(); j++) {
                jsonObj = coorArr.getJSONObject(j);
                // Log.e(TAG, jsonObj.toString());
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
                latLng = new LatLng(latitude, longitude);
                mCoordinates.add(coor);
                addRoute(latLng);
                mLatLngs.add(latLng);
                builder.include(latLng);

            }

        }

        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(builder
                .build());
        mMapView.getMap().animateMapStatus(u);

        drawOverlay();

    }

    private void drawOverlay() {
        if (isRoaePlan) {
            showLoading(R.string.loading, false);
            if (mRotePlanNodes.size() >= 3) {
                mSearch.drivingSearch((new DrivingRoutePlanOption())
                        .from(mRotePlanNodes.get(0))
                        .passBy(mRotePlanNodes.subList(1,
                                mRotePlanNodes.size() - 1))
                        .to(mRotePlanNodes.get(mRotePlanNodes.size() - 1)));
            } else if (mRotePlanNodes.size() == 2) {
                mSearch.drivingSearch((new DrivingRoutePlanOption()).from(
                        mRotePlanNodes.get(0)).to(
                        mRotePlanNodes.get(mRotePlanNodes.size() - 1)));
            } else {
                dismissLoading();
            }
        } else {
            mLlMarker.setVisibility(View.VISIBLE);
            mBaiduMap.clear();
            if (mLatLngs.size() > 2)
                mBaiduMap.addOverlay(new PolylineOptions().points(mLatLngs).color(Color.BLUE));
            drawMarker(builder.build());
            if (mCoordinates.size() == 1) {
                drawMarkerStart(mCoordinates.get(0));
            } else if (mCoordinates.size() >= 2) {
                drawMarkerStart(mCoordinates.get(0));
                drawMarkerEnd(mCoordinates.get(mCoordinates.size() - 1));
            }
        }
    }

    private void switchOverlay() {
        isRoaePlan = !isRoaePlan;
        drawOverlay();
    }

    private void drawMarker(LatLngBounds bound) {
        mSelectCoordinates.clear();
        // mBaiduMap.clear();
        clearMarker();

        // LatLngBounds bound = mBaiduMap.getMapStatus().bound;
        // LatLngBounds bound = builder.build();
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
            for (int i = currentPage - 1; i < interval * MARK_NUM; i += interval)
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
            showShortToast("没有下一页");
            return;
        }

        if (length % MARK_NUM == 0) {
            if (currentPage <= interval) {
                clearMarker();
                for (int i = currentPage; i < mSelectCoordinates.size(); i += interval)
                    addMarker(mSelectCoordinates.get(i));
                currentPage++;
            } else {
                currentPage = interval;
                showShortToast("没有下一页");
            }
        } else {
            if (currentPage == interval) {
                clearMarker();
                currentPage++;
                for (int i = interval * MARK_NUM; i < mSelectCoordinates.size(); i++)
                    addMarker(mSelectCoordinates.get(i));

            } else if (currentPage < interval) {
                clearMarker();
                currentPage++;
                for (int i = currentPage; i < interval * MARK_NUM; i += interval)
                    addMarker(mSelectCoordinates.get(i));

            } else {
                currentPage = interval + 1;
                showShortToast("没有下一页");
            }
        }

    }

    private void drawPrevMarker() {
        if (mCoordinates.size() <= MARK_NUM
                || mSelectCoordinates.size() <= MARK_NUM) {
            showShortToast("没有上一页");
            return;
        }

        if (currentPage > 1) {
            clearMarker();
            currentPage--;
            for (int i = currentPage; i < interval * MARK_NUM; i += interval)
                addMarker(mSelectCoordinates.get(i));

        } else {
            currentPage = 1;
            showShortToast("没有上一页");
        }

    }

    List<Marker> markers = new ArrayList<Marker>();
    private int markerIndex;

    private void addMarker(Coordinate coorList) {
        if (mCoordinates.size() < 2 || coorList.equals(mCoordinates.get(0))
                || coorList.equals(mCoordinates.get(mCoordinates.size() - 1))) {
            return;
        }
        LatLng ll = new LatLng(coorList.latitude, coorList.longitude);
        Bundle bundle = new Bundle();

        Marker marker = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
                .position(ll).icon(imgMiddle).zIndex(9).draggable(true));
        bundle.putBoolean("isMiddle", true);

        bundle.putSerializable("coordinate", coorList);
        marker.setExtraInfo(bundle);
        markers.add(markerIndex, marker);
        markerIndex++;
    }

    private void clearMarker() {
        markerIndex = 0;
        for (int i = 0; i < markers.size(); i++) {
            markers.get(i).remove();
        }
    }

    private Marker markerStart;
    private Marker markerEnd;

    private void drawMarkerStart(Coordinate coordinate) {
        Bundle bundle = new Bundle();
        LatLng ll = new LatLng(coordinate.latitude, coordinate.longitude);
        markerStart = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
                .position(ll).icon(imgStart).zIndex(9).draggable(true));
        bundle.putBoolean("isMiddle", false);

        bundle.putSerializable("coordinate", coordinate);
        markerStart.setExtraInfo(bundle);
    }

    private void drawMarkerEnd(Coordinate coordinate) {
        Bundle bundle = new Bundle();
        LatLng ll = new LatLng(coordinate.latitude, coordinate.longitude);
        markerEnd = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
                .position(ll).icon(imgTerminal).zIndex(9).draggable(true));
        bundle.putBoolean("isMiddle", false);

        bundle.putSerializable("coordinate", coordinate);
        markerEnd.setExtraInfo(bundle);
    }

    private RoutePlanSearch mSearch;
    private Map<Object, String> map = new HashMap<Object, String>();

    private void initDebugData() {
        if (!ToastUtil.isDebug())
            return;
        // AMBIGUOUS_KEYWORD// 检索词有岐义
        map.put(SearchResult.ERRORNO.AMBIGUOUS_KEYWORD, "检索词有岐义");
        // AMBIGUOUS_ROURE_ADDR// 检索地址有岐义
        map.put(SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR, "检索地址有岐义");
        // KEY_ERROR// key有误
        map.put(SearchResult.ERRORNO.KEY_ERROR, "key有误");
        // NETWORK_ERROR// 网络错误
        map.put(SearchResult.ERRORNO.NETWORK_ERROR, "网络错误");
        // NETWORK_TIME_OUT// 网络超时
        map.put(SearchResult.ERRORNO.NETWORK_TIME_OUT, "网络超时");
        // NO_ERROR// 检索结果正常返回
        map.put(SearchResult.ERRORNO.NO_ERROR, "检索结果正常返回");
        // NOT_SUPPORT_BUS// 该城市不支持公交搜索
        map.put(SearchResult.ERRORNO.NOT_SUPPORT_BUS, "该城市不支持公交搜索");
        // NOT_SUPPORT_BUS_2CITY// 不支持跨城市公交
        map.put(SearchResult.ERRORNO.NOT_SUPPORT_BUS_2CITY, "不支持跨城市公交");
        // PERMISSION_UNFINISHED// 授权未完成
        map.put(SearchResult.ERRORNO.PERMISSION_UNFINISHED, "授权未完成");
        // RESULT_NOT_FOUND// 没有找到检索结果
        map.put(SearchResult.ERRORNO.RESULT_NOT_FOUND, "没有找到检索结果");
        // ST_EN_TOO_NEAR// 起终点太近
        map.put(SearchResult.ERRORNO.ST_EN_TOO_NEAR, "起终点太近");
    }

    private void initRoutePlan() {
        initDebugData();
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(new MyRountePlanListener() {
            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult result) {
                // ToastUtil.show(HistoryLocusActivity.this,
                // map.get(result.error));
                if (result != null
                        && result.error == SearchResult.ERRORNO.NO_ERROR) {
                    mBaiduMap.clear();
                    mLlMarker.setVisibility(View.INVISIBLE);
                    DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(
                            mBaiduMap);
                    // mBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(result.getRouteLines().get(0));
                    overlay.addToMap();

                    overlay.zoomToSpan();
                    isRoaePlan = true;
                    dismissLoading();
                }
                // else if (result.error ==
                // SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // // result.getSuggestAddrInfo();
                // }
                else {

                    showShortToast(map.get(result.error));
                    isRoaePlan = false;
                    dismissLoading();
                }

            }
        });
    }

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }
    }

    // private void routePlan() {
    // if (mRotePlanNodes.size() >= 3) {
    // mSearch.drivingSearch((new DrivingRoutePlanOption())
    // .from(mRotePlanNodes.get(0))
    // .passBy(mRotePlanNodes.subList(1, mRotePlanNodes.size() - 1))
    // .to(mRotePlanNodes.get(mRotePlanNodes.size() - 1)));
    // } else if (mRotePlanNodes.size() == 2) {
    // mSearch.drivingSearch((new DrivingRoutePlanOption()).from(
    // mRotePlanNodes.get(0)).to(
    // mRotePlanNodes.get(mRotePlanNodes.size() - 1)));
    // } else {
    //
    // }
    // // mSearch.drivingSearch((new DrivingRoutePlanOption()).from(mStNode)
    // // .passBy(mRotePlanNodes).to(mEnNode));
    // }

    java.util.List<PlanNode> mRotePlanNodes = new ArrayList<PlanNode>();

    private void addRoute(LatLng point) {
        PlanNode node = PlanNode.withLocation(point);
        mRotePlanNodes.add(node);

    }

    /**
     * 获取相关历史轨迹 以事件 来查询；
     */
    private void getContractMotorLocus(String truckNum, String dateFrom,
                                       String dateTo) {
        AsyncHttpService.getContractMotorLocus(truckNum, dateFrom, dateTo,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoading(getString(R.string.is_qurey_ellipsis));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                        showShortToast(getResources().getString(
                                R.string.httpisNull));
                        dismissLoading();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {

                        super.onSuccess(statusCode, headers, response);
                        try {
                            Log.i("his: ", response.toString());
                            if (UtilsError.isErrorCode(
                                    HistoryLocusActivity.this, response)) {
                                return;
                            }
                            parseJsonDate(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showLongToast(R.string.not_get_related_historical_track);
                        } finally {
                            dismissLoading();
                        }
                    }
                }, this);

    }

    private void parseJsonDate(JSONObject response) throws Exception {

        JSONObject jsonObj;
        Coordinate coor;
        LatLng latLng;

        JSONObject jsonObject = response
                .getJSONObject(Constants.Feild.KEY_STAFF);
        if (jsonObject == null || jsonObject.length() == 0) {
            showLongToast(getString(R.string.no_find_vehicle_info_tip_msg));

            return;
        }
        JSONArray jsonArr = jsonObject
                .getJSONArray(Constants.Feild.KEY_LOCA_COORDINATES);
        LogUtil.e(TAG, "jsonArr=" + jsonArr.length() + "");
        if (jsonArr.length() == 0) {
            showLongToast(getString(R.string.no_upload_info_tip_msg));
            return;
        }

        // 解析历史轨迹中的坐标点信息
        for (int j = 0; j < jsonArr.length(); j++) {
            jsonObj = jsonArr.getJSONObject(j);
            // Log.e(TAG, jsonObj.toString());
            coor = new Coordinate();
            double latitude = jsonObj
                    .getDouble(Constants.Feild.KEY_LOCA_LATITUDE);
            coor.setLatitude(latitude);

            double longitude = jsonObj
                    .getDouble(Constants.Feild.KEY_LOCA_LONGITUDE);
            coor.setLongitude(longitude);

            coor.setAddress(jsonObj.getString(Constants.Feild.KEY_LOCA_ADDRESS));
            coor.setNewDate(jsonObj.getString(Constants.Feild.KEY_LOCA_TIME));
            latLng = new LatLng(latitude, longitude);
            mCoordinates.add(coor);

            addRoute(latLng);
            mLatLngs.add(latLng);
            builder.include(latLng);

        }

        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(builder
                .build());
        mMapView.getMap().animateMapStatus(u);

        drawOverlay();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_view_title_img:
                finish();
                break;
            case R.id.common_view_title_img_menu:
                switchOverlay();
                break;
            case R.id.activity_history_loc_prev_marker_btn:
                drawPrevMarker();
                break;
            case R.id.activity_history_loc_next_marker_btn:
                drawNextMarker();
                break;

            default:
                break;
        }

    }

    private boolean isLinePlan() {
        String options = PreferencesUtil.getValue(Constants.Feild.KEY_OPTIONS);
//        Log.d(TAG, "options=" + options);
//        options={"data":"0","isDisplay":"1","workModel":"1","scanModel":"0","isLinePlan":"1"}
        try {
            JSONObject jsonObject = new JSONObject(options);
            if (jsonObject.isNull("isLinePlan")) {
                return false;
            }
            String isLinePlan = jsonObject.getString("isLinePlan");
            if (TextUtils.equals(isLinePlan, Constants.Value.YES)) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
