package com.sdbnet.hywy.enterprise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.ui.widget.DialogOrderOperate;
import com.sdbnet.hywy.enterprise.ui.widget.RTPullListView;
import com.sdbnet.hywy.enterprise.ui.widget.RTPullListView.OnRefreshListener;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.LogUtil;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
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

public class OrderTraceListActivity extends BaseActivity {

    private ImageView mBack;
    private RTPullListView lv_trace_list;
    private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    private TraceListAdapter adapter;
    private TextView tv_order_number;
    private TextView tv_create_time;
    private String orderNo;

    private LinearLayout noRecord;
    private LinearLayout lay_step;
    private LinearLayout lay_order_info;
    private Button mBtnLook;
    private static final int LOAD_NEW_INFO = 15;
    public static final String ORDRE_NUM = "order_num";
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD_NEW_INFO:
                    if (lv_trace_list.getAdapter() == null) {
                        lv_trace_list.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                        lv_trace_list.onRefreshComplete();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取订单号
        orderNo = getIntent().getStringExtra(Constants.Feild.KEY_ORDER_NO);
        setContentView(R.layout.activity_trace_list);
        initControls();
        loadDatas();
    }

    /**
     * 初始化控件
     */
    private void initControls() {
        mBack = (ImageView) findViewById(R.id.common_view_title_img);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.common_view_title_text)).setText(R.string.order_tracking);
        mBtnLook = (Button) findViewById(R.id.common_view_title_btn);
        mBtnLook.setVisibility(View.VISIBLE);
        mBtnLook.setBackgroundResource(R.drawable.btn_bg_red_selector);
        mBtnLook.setText(R.string.accident_center);
        mBtnLook.setTextSize(16);
        mBtnLook.setPadding(UtilsAndroid.UI.dip2px(this, 10), 0, UtilsAndroid.UI.dip2px(this, 10), 0);
        mBtnLook.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderTraceListActivity.this,
                        AccidentCenterActivity.class);
                intent.putExtra(ORDRE_NUM, orderNo);
                startActivity(intent);
            }
        });
        if (checkPermissions()) {
            mBtnLook.setVisibility(View.VISIBLE);
        } else {
//			 mBtnLook.setVisibility(View.INVISIBLE);
        }
        tv_order_number = (TextView) findViewById(R.id.activity_trace_list_tv_order_number);
        tv_order_number.setText(orderNo);
        // tv_platform_order_number = (TextView)
        // findViewById(R.id.tv_platform_order_number);
        tv_create_time = (TextView) findViewById(R.id.activity_trace_list_tv_create_time);

        lv_trace_list = (RTPullListView) findViewById(R.id.activity_trace_list_lv_trace_list);

        // 下拉刷新接口实现
        lv_trace_list.setonRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                list.clear();
                loadDatas();
            }
        });

        noRecord = (LinearLayout) findViewById(R.id.view_no_record);
        lay_step = (LinearLayout) findViewById(R.id.activity_trace_list_lay_step);
        lay_order_info = (LinearLayout) findViewById(R.id.activity_trace_list_lay_order_info);

        adapter = new TraceListAdapter();
    }

    private boolean checkPermissions() {
        // 检查权限
        String execMenus = PreferencesUtil.getValue(
                PreferencesUtil.KEY_EXECUTE_MENU, null);

        if (TextUtils.isEmpty(execMenus)) {
            return false;
        }
        Log.d("checkPermissions", execMenus);
        String[] menus = execMenus.split(",");
        for (String menu : menus) {
            // if (Constants.MENU_MAP.get(menu) == null) {
            // continue;
            // }
            Log.d("checkPermissions", menu);
            if ("HYWY005".equals(menu)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 加载数据
     */
    private void loadDatas() {
        if (TextUtils.isEmpty(orderNo)) {
            return;
        }
        if (TextUtils.isEmpty(PreferencesUtil.user_company)) {
            System.out.println("compid:"
                    + PreferencesUtil.getValue(PreferencesUtil.KEY_COMPANY_ID,
                    null));
            PreferencesUtil.initStoreData();
        }
        String sysnox = getOriginalPlatformOrderId(); // 拼接平台订单号
        System.out.println(String.format("compid:%s; itemid:%s; sysnox:%s",
                PreferencesUtil.user_company, PreferencesUtil.item_id, sysnox));
        AsyncHttpService.traceOrder(sysnox, new JsonHttpResponseHandler() {
            private int lastActidex;

            @Override
            public void onStart() {
                showLoading(getString(R.string.xlistview_header_hint_loading));
                super.onStart();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                dismissLoading();
                showShortToast(getResources().getString(R.string.httpError));

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    LogUtil.d("trace:" + response.toString());
                    if (UtilsError.isErrorCode(OrderTraceListActivity.this,
                            response)) {
                        return;
                    }

                    JSONObject jsonObject = response
                            .getJSONObject(Constants.Feild.KEY_ORDER);
                    tv_create_time.setText(jsonObject
                            .getString(Constants.Feild.KEY_ORDER_CREATE_TIME));

                    JSONObject jsonObj = null;
                    JSONArray arrs = jsonObject
                            .getJSONArray(Constants.Feild.KEY_TRACES);
                    Map<String, String> map = null;
                    int currentActidex = Integer.valueOf(jsonObject
                            .getString(Constants.Feild.KEY_ACTION_ID));
                    String currentAction = jsonObject
                            .getString(Constants.Feild.KEY_ACTION);

                    for (int i = 0; i < arrs.length(); i++) {
                        // 解析追踪日志
                        jsonObj = arrs.getJSONObject(i);
                        // Log.i(TAG, jsonObj.toString());

                        map = new HashMap<String, String>();
                        // "traceid":"251052",
                        map.put(Constants.Feild.KEY_TRACE_ID,
                                jsonObj.getString(Constants.Feild.KEY_TRACE_ID));
                        // ,"actidx":"2",
                        map.put(Constants.Feild.KEY_ACTION_ID, jsonObj
                                .getString(Constants.Feild.KEY_ACTION_ID));
                        // "acttime":"2015-07-07 18:25:45",
                        map.put(Constants.Feild.KEY_ACTION_TIME, jsonObj
                                .getString(Constants.Feild.KEY_ACTION_TIME));
                        // ,"action":"A001"
                        map.put(Constants.Feild.KEY_ACTION,
                                jsonObj.getString(Constants.Feild.KEY_ACTION));
                        // ,"actname":"订单提交"
                        map.put(Constants.Feild.KEY_ACTION_NAME, jsonObj
                                .getString(Constants.Feild.KEY_ACTION_NAME));
                        // "actmemo":"您提交了订单，货物等待配送出库",
                        map.put(Constants.Feild.KEY_ACTION_CONTENT, jsonObj
                                .getString(Constants.Feild.KEY_ACTION_CONTENT));
                        // "sign":"1",
                        map.put(Constants.Feild.KEY_SIGN,
                                jsonObj.getString(Constants.Feild.KEY_SIGN));
                        // {"pid":"9001","itemid":"88","cmpid":"8888", //
                        // ,"pname":"张宇",
                        map.put(Constants.Feild.KEY_COMPANY_ID, jsonObj
                                .getString(Constants.Feild.KEY_COMPANY_ID));
                        map.put(Constants.Feild.KEY_ITEM_ID,
                                jsonObj.getString(Constants.Feild.KEY_ITEM_ID));
                        map.put(Constants.Feild.KEY_STAFF_ID,
                                jsonObj.getString(Constants.Feild.KEY_STAFF_ID));
                        map.put(Constants.Feild.KEY_STAFF_NAME, jsonObj
                                .getString(Constants.Feild.KEY_STAFF_NAME));

                        // "locaddress":"广东省深圳市宝安区民宝路"
                        map.put(Constants.Feild.KEY_LOCA_ADDRESS, jsonObj
                                .getString(Constants.Feild.KEY_LOCA_ADDRESS));
                        // "islocate":1, // "lineno":"",
                        map.put(Constants.Feild.KEY_IS_LOCAATE, jsonObj
                                .getString(Constants.Feild.KEY_IS_LOCAATE));
                        map.put(Constants.Feild.KEY_LINE_ID,
                                jsonObj.getString(Constants.Feild.KEY_LINE_ID));
                        // "linename":"",
                        map.put(Constants.Feild.KEY_LINE_NAME, jsonObj
                                .getString(Constants.Feild.KEY_LINE_NAME));
                        // "truckno":"6955699",
                        map.put(Constants.Feild.KEY_STAFF_TRUCK_NO, jsonObj
                                .getString(Constants.Feild.KEY_STAFF_TRUCK_NO));
                        // "actmemoinner":"",
                        map.put(Constants.Feild.KEY_ACTMEMOINNER, jsonObj
                                .getString(Constants.Feild.KEY_ACTMEMOINNER));
                        // "isscan":1, // "imgcount":0,
                        map.put(Constants.Feild.KEY_IS_SCAN,
                                jsonObj.getString(Constants.Feild.KEY_IS_SCAN));
                        map.put(Constants.Feild.KEY_IMG_COUNT, jsonObj
                                .getString(Constants.Feild.KEY_IMG_COUNT));
                        // "iscall":1,"loctel":"18802691909"}
                        map.put(Constants.Feild.KEY_IS_CALL,
                                jsonObj.getString(Constants.Feild.KEY_IS_CALL));
                        map.put(Constants.Feild.KEY_LOCA_TEL,
                                jsonObj.getString(Constants.Feild.KEY_LOCA_TEL));

                        list.add(map);
                    }
                    lay_step.removeAllViews();

                    Map<String, String> actionMap = createStep(jsonObject
                                    .getJSONArray(Constants.Feild.KEY_ACTIONS),
                            currentActidex, currentAction);

                    lay_order_info.setVisibility(View.VISIBLE);
                    if (arrs.length() == 0) {
                        lv_trace_list.setVisibility(View.GONE);
                        noRecord.setVisibility(View.VISIBLE);
                    } else {
                        noRecord.setVisibility(View.GONE);
                        lv_trace_list.setVisibility(View.VISIBLE);
                        lv_trace_list.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    dismissLoading();
                }

                myHandler.sendEmptyMessage(LOAD_NEW_INFO);
            }

            /**
             * 显示总体进度节点
             *
             * @param array
             * @param actidex
             * @param currentAction
             * @return
             * @throws JSONException
             */
            private Map<String, String> createStep(JSONArray array,
                                                   int actidex, String currentAction) throws JSONException {
                JSONObject jsonObj = null;
                Map<String, String> actions = new HashMap<String, String>();
                for (int i = 0; i < array.length(); i++) {
                    jsonObj = array.getJSONObject(i);
                    String key = jsonObj.getString("actidex");
                    String value = jsonObj
                            .getString(Constants.Feild.KEY_ACTION_NAME);
                    String action = jsonObj
                            .getString(Constants.Feild.KEY_ACTION);

                    LinearLayout layout = new LinearLayout(
                            OrderTraceListActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    LayoutParams layParams = new LayoutParams(0,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    layParams.gravity = Gravity.CENTER_HORIZONTAL;
                    layParams.weight = 1;
                    lay_step.addView(layout, layParams);

                    RelativeLayout rLay = new RelativeLayout(
                            OrderTraceListActivity.this);
                    LayoutParams rLayParams = new LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    rLayParams.gravity = Gravity.CENTER;
                    layout.addView(rLay, rLayParams);

                    ImageView iv = new ImageView(OrderTraceListActivity.this);
                    iv.setId(Integer.valueOf(key + "001"));
                    android.widget.RelativeLayout.LayoutParams ivParams = new android.widget.RelativeLayout.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    ivParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    rLay.addView(iv, ivParams);

                    TextView tv = new TextView(OrderTraceListActivity.this);
                    tv.setId(Integer.valueOf(key + "003"));
                    android.widget.RelativeLayout.LayoutParams tvParams = new android.widget.RelativeLayout.LayoutParams(
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    tvParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    tv.setText(String.valueOf(i + 1));
                    tv.setTextColor(getResources().getColor(R.color.white));
                    rLay.addView(tv, tvParams);

                    TextView tv2 = new TextView(OrderTraceListActivity.this);
                    tv2.setId(Integer.valueOf(key + "002"));
                    LayoutParams tv2Params = new LayoutParams(
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    tv2Params.topMargin = UtilsAndroid.UI.dip2px(
                            OrderTraceListActivity.this, 3);
                    tv2Params.gravity = Gravity.CENTER_HORIZONTAL;
                    tv2.setText(value);
                    tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                    layout.addView(tv2, tv2Params);

                    if (Integer.valueOf(key) < actidex) {
                        // 执行完毕，节点显示蓝色
                        tv2.setTextColor(getResources().getColor(R.color.blue2));
                        iv.setBackgroundResource(R.drawable.pointl_blue);
                    } else if (Integer.valueOf(key) == actidex
                            && action.equals(currentAction)) {
                        // 执行完毕，节点显示蓝色
                        tv2.setTextColor(getResources().getColor(R.color.blue2));
                        iv.setBackgroundResource(R.drawable.pointl_blue);
                    } else {
                        // 未执行，节点显示灰色
                        tv2.setTextColor(getResources().getColor(R.color.grey));
                        iv.setBackgroundResource(R.drawable.pointl_grey);
                    }

                    // 判断最后一个环节，即是否签收
                    if (i == array.length() - 1) {
                        lastActidex = Integer.valueOf(key);
                        if (actidex == lastActidex
                                && action.equals(currentAction)) {
                            System.out.println(action + ">" + currentAction);
                            tv.setText("");
                            tv2.setTextColor(getResources().getColor(
                                    R.color.blue2));
                            iv.setBackgroundResource(R.drawable.pointl_ok);
                        }
                    }
                }
                return actions;

            }
        }, OrderTraceListActivity.this);
    }

    /**
     * 获取原始平台订单号
     *
     * @return
     */
    private String getOriginalPlatformOrderId() {
        return PreferencesUtil.user_company + PreferencesUtil.item_id + "-"
                + orderNo;
    }

    class TraceListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Map<String, String> map = list.get(position);
            for (java.util.Map.Entry<String, String> set : map.entrySet()) {
                Log.i("getView", set.getKey() + ":" + set.getValue());
            }
            final View view = View.inflate(OrderTraceListActivity.this,
                    R.layout.list_item_trace_info, null);

            final LinearLayout lay_trace_info = (LinearLayout) view
                    .findViewById(R.id.lay_trace_info);
            if (position % 2 == 0) {
                view.setBackgroundColor(getResources().getColor(R.color.trace1));
                lay_trace_info
                        .setBackgroundResource(R.drawable.trace_choose_selector);
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.trace2));
                lay_trace_info
                        .setBackgroundResource(R.drawable.trace_choose_selector2);
            }

            LinearLayout lay_user_plate = (LinearLayout) view
                    .findViewById(R.id.lay_user_plate);
            LinearLayout lay_actmemoinner = (LinearLayout) view
                    .findViewById(R.id.lay_actmemoinner);
            LinearLayout lay_action_content = (LinearLayout) view
                    .findViewById(R.id.lay_action_content);

            TextView tv_hboRecePlace = (TextView) view
                    .findViewById(R.id.tv_hboRecePlace);
            TextView tv_hboReceTime = (TextView) view
                    .findViewById(R.id.tv_hboReceTime);
            TextView tv_hdiName = (TextView) view.findViewById(R.id.tv_hdiName);
            TextView tv_hdiPlate = (TextView) view
                    .findViewById(R.id.tv_hdiPlate);
            TextView tv_loc_address = (TextView) view
                    .findViewById(R.id.tv_loc_address);

            ImageView iv_proc = (ImageView) view.findViewById(R.id.iv_proc);
            ImageView iv_proc_end = (ImageView) view
                    .findViewById(R.id.iv_proc_end);
            ImageView iv_proc_start = (ImageView) view
                    .findViewById(R.id.iv_proc_start);
            ImageView iv_proc_single = (ImageView) view
                    .findViewById(R.id.iv_proc_single);
            final ImageView iv_call = (ImageView) view
                    .findViewById(R.id.iv_call);
            final ImageView iv_local = (ImageView) view
                    .findViewById(R.id.iv_local);
            final ImageView iv_broswer = (ImageView) view
                    .findViewById(R.id.iv_broswer);

            lay_trace_info.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 点击日志列表，弹出操作窗口
                    if (!(iv_call.getVisibility() != View.VISIBLE
                            && iv_local.getVisibility() != View.VISIBLE && iv_broswer
                            .getVisibility() != View.VISIBLE)) {
                        map.put(Constants.Feild.KEY_PLATFORM_ORIGINAL_NO,
                                getOriginalPlatformOrderId());
                        DialogOrderOperate ood = new DialogOrderOperate(
                                OrderTraceListActivity.this, map);
                        ood.show();
                    }
                }
            });

            tv_hboReceTime.setText(map.get(Constants.Feild.KEY_ACTION_TIME));
            tv_hdiName.setText(String.format(
                    getString(R.string.scan_person_colon_x),
                    map.get(Constants.Feild.KEY_STAFF_NAME)));// "扫描人：" +
            // map.get(Constants.Feild.KEY_STAFF_NAME));
            tv_hdiPlate.setText(map.get(Constants.Feild.KEY_STAFF_TRUCK_NO));
            String address = TextUtils.isEmpty(map
                    .get(Constants.Feild.KEY_LOCA_ADDRESS)) ? getString(R.string.scan_address_colon_nothing)
                    : map.get(Constants.Feild.KEY_LOCA_ADDRESS);
            tv_loc_address.setText(address);

            // 动作说明为空则不显示
            String actmemo = map.get(Constants.Feild.KEY_ACTION_CONTENT);
            if (TextUtils.isEmpty(actmemo)) {
                lay_action_content.setVisibility(View.GONE);
            } else {
                lay_action_content.setVisibility(View.VISIBLE);
                tv_hboRecePlace.getPaint().setFakeBoldText(true);
                // tv_hboRecePlace.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                tv_hboRecePlace.setText(String.format(
                        getString(R.string.milestone_colon_x), actmemo));
            }

            // 动作备注为空则不显示
            String actmemoinner = map.get(Constants.Feild.KEY_ACTMEMOINNER);
            if (TextUtils.isEmpty(actmemoinner)) {
                lay_actmemoinner.setVisibility(View.GONE);
            } else {
                lay_actmemoinner.setVisibility(View.VISIBLE);
                TextView tv_loc_actmemoinner = (TextView) view
                        .findViewById(R.id.tv_loc_actmemoinner);
                tv_loc_actmemoinner.setText(String.format(
                        getString(R.string.remark_colon_x), actmemoinner));// "备注："
                // +
                // actmemoinner);
            }

            if (list.size() == 1) {
                iv_proc_start.setVisibility(View.GONE);
                iv_proc.setVisibility(View.GONE);
                iv_proc_end.setVisibility(View.GONE);
                iv_proc_single.setVisibility(View.VISIBLE);
            } else {
                if (0 == position) { // 显示起点图标
                    iv_proc_start.setVisibility(View.GONE);
                    iv_proc.setVisibility(View.GONE);
                    iv_proc_single.setVisibility(View.GONE);
                    iv_proc_end.setVisibility(View.VISIBLE);
                } else if (position == list.size() - 1) { // 显示终点图片
                    iv_proc_start.setVisibility(View.VISIBLE);
                    iv_proc.setVisibility(View.GONE);
                    iv_proc_single.setVisibility(View.GONE);
                    iv_proc_end.setVisibility(View.GONE);
                } else { // 显示中间图标
                    iv_proc_start.setVisibility(View.GONE);
                    iv_proc_single.setVisibility(View.GONE);
                    iv_proc_end.setVisibility(View.GONE);
                    iv_proc.setVisibility(View.VISIBLE);
                }
            }

            // 如果定位电话为空，电话图标不显示
            if (Constants.Value.YES
                    .equals(map.get(Constants.Feild.KEY_IS_CALL))
                    && !TextUtils
                    .isEmpty(map.get(Constants.Feild.KEY_LOCA_TEL))) {
                iv_call.setVisibility(View.VISIBLE);
                lay_user_plate.setVisibility(View.VISIBLE);
            } else {
                iv_call.setVisibility(View.GONE);
                lay_user_plate.setVisibility(View.GONE);
            }

            // 如果线路编号为空，定位图标不显示
            if (Constants.Value.YES.equals(map
                    .get(Constants.Feild.KEY_IS_LOCAATE))
                    && !TextUtils.isEmpty(map.get(Constants.Feild.KEY_LINE_ID))) {
                iv_local.setVisibility(View.VISIBLE);
            } else {
                iv_local.setVisibility(View.GONE);
            }

            // 附件浏览图标
            int imgCount = 0;
            try {
                imgCount = Integer.parseInt(map
                        .get(Constants.Feild.KEY_IMG_COUNT));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (Constants.Value.YES
                    .equals(map.get(Constants.Feild.KEY_IS_SCAN))
                    && imgCount > 0) {
                iv_broswer.setVisibility(View.VISIBLE);
            } else {
                iv_broswer.setVisibility(View.GONE);
            }

            return view;
        }
    }

    static class ViewHolder {
        LinearLayout lay_trace_info;
        LinearLayout lay_user_plate;
        TextView tv_hboRecePlace;
        TextView tv_hdiName;
        TextView tv_hboReceTime;
        TextView tv_hdiPlate;
        TextView tv_loc_address;
        ImageView iv_proc;
        ImageView iv_proc_end;
        ImageView iv_proc_start;
        ImageView iv_proc_single;
        ImageView iv_call;
        ImageView iv_local;
        ImageView iv_broswer;
    }
}
