package com.sdbnet.hywy.enterprise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.model.VehicleSortModel;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.ui.widget.ClearEditText;
import com.sdbnet.hywy.enterprise.ui.widget.PopMenu;
import com.sdbnet.hywy.enterprise.ui.widget.PopMenu.OnItemClickListener;
import com.sdbnet.hywy.enterprise.ui.widget.RTPullListView;
import com.sdbnet.hywy.enterprise.ui.widget.RTPullListView.OnRefreshListener;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;
import com.sdbnet.hywy.enterprise.wheel.adapter.CharacterParser;
import com.sdbnet.hywy.enterprise.wheel.adapter.PinyinComparator;
import com.sdbnet.hywy.enterprise.wheel.adapter.SortAdapter;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GroupMotorsActivity extends BaseActivity implements OnItemClickListener {
    private static final String TAG = "GroupMotorsActivity";
    private TextView tv_title;
    private ImageView mBack;

    private SortAdapter adapter;
    private ClearEditText mClearEditText;

    private LinearLayout noRecord;
    private RTPullListView plv_order_list;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<VehicleSortModel> sourceDataList = new ArrayList<VehicleSortModel>();
    private List<VehicleSortModel> showDataList = new ArrayList<VehicleSortModel>();
    private int ustatus = 0;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    private ProgressBar moreProgressBar;

    private static final int LOAD_MORE_SUCCESS = 3;
    private static final int LOAD_NEW_INFO = 5;

    boolean isUpdate = false;
    private String grpid;
    private String grpname;
    private String isContract;
    private int redict = 0;
    private TextView tv_order_statu;
    private String[] menus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_motors);
        menus = new String[]{getString(R.string.all), getString(R.string.unallocated),
                getString(R.string.allocated),
                getString(R.string.in_use),
                getString(R.string.completed)};
//				"未分配","已分配","使用中","已完成"};
        initControls();
        initDatas();
    }

    private void initDatas() {
        if (!UtilsAndroid.Set.checkNetState(this)) {
//			Toast.makeText(this, getResources().getString(R.string.httpisNull), Toast.LENGTH_LONG).show();
            showLongToast(R.string.httpError);
            return;
        }
        getParams();
        getVehicleDatas();
    }

    private void getParams() {
        redict = getIntent().getIntExtra(Constants.Params.PARAM_REDICT, 0);
        if (redict == 2) {
            isContract = "1";
            tv_order_statu.setVisibility(View.VISIBLE);
        } else {
            isContract = "0";
        }
    }

    private void getVehicleDatas() {
        AsyncHttpService.getGroupUsers(grpid, isContract, null,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        setLoadViewVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        System.out.println("query order: " + response);
                        super.onSuccess(statusCode, headers, response);
                        parseJSON(true, response);
                    }

                }, this);
    }

    /**
     * 解析json
     *
     * @param isRefresh
     * @param response
     */
    private void parseJSON(boolean isRefresh, JSONObject response) {
        try {
            int errCode = response.getInt("errcode");
            if (errCode != 0) {
                String msg = response.getString(Constants.Feild.KEY_MSG);
                switch (response.getInt(Constants.Feild.KEY_ERROR_CODE)) {
                    case 41:
                        returnLogin(GroupMotorsActivity.this, msg, null);
                        break;
                    case 42:
                        returnLogin(GroupMotorsActivity.this, msg, null);
                        break;
                    default:
                        showLongToast(msg);
                        break;
                }
                noRecord.setVisibility(View.VISIBLE);
                setLoadViewVisibility(View.GONE);
                return;
            }
            plv_order_list.setVisibility(View.VISIBLE);
            JSONArray arrs = response.getJSONArray("staffs");
            JSONObject jsonObj = null;
            sourceDataList.clear();
            for (int i = 0; i < arrs.length(); i++) {
                jsonObj = arrs.getJSONObject(i);
                VehicleSortModel sortModel = new VehicleSortModel();
                StringBuffer sb = new StringBuffer("");
                sb.append(TextUtils.isEmpty(jsonObj.getString("pname")) ? ""
                        : jsonObj.getString("pname"));
                sb.append(TextUtils.isEmpty(jsonObj.getString("loctel")) ? ""
                        : "," + jsonObj.getString("loctel"));
                sb.append(TextUtils.isEmpty(jsonObj.getString("truckno")) ? ""
                        : "," + jsonObj.getString("truckno"));
                sortModel.setHdiId(jsonObj.getString("pid"));
                sortModel.setHdiPlate(jsonObj.getString("truckno"));
                sortModel.setUstatus(jsonObj.getInt("ustatus"));
                sortModel.setSortContent(sb.toString());

                // 汉字转换成拼音
                String pinyin = characterParser.getSelling(sb.toString());
                String sortString = pinyin.substring(0, 1).toUpperCase();
                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    sortModel.setSortLetters(sortString.toUpperCase());
                } else {
                    sortModel.setSortLetters("#");
                }
                sourceDataList.add(sortModel);
            }
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
            setLoadViewVisibility(View.GONE);
        }
    }

    private LinearLayout lay_loading;

    private void setLoadViewVisibility(int isVisible) {
        if (null == lay_loading) {
            lay_loading = ((LinearLayout) findViewById(R.id.ll_loading));
        }
        lay_loading.setVisibility(isVisible);
    }

    private void initControls() {
        mBack = (ImageView) findViewById(R.id.common_view_title_img);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        plv_order_list = (RTPullListView) findViewById(R.id.plv_order_list);

        // 下拉刷新接口实现
        plv_order_list.setonRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                myHandler.sendEmptyMessage(LOAD_NEW_INFO);
            }
        });

        tv_order_statu = (TextView) findViewById(R.id.common_view_title_btn);
        tv_order_statu.setVisibility(View.VISIBLE);
        tv_order_statu.setText(R.string.all);
        tv_order_statu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 初始化弹出菜单
                PopMenu popMenu = new PopMenu(GroupMotorsActivity.this);
                popMenu.addItems(menus);
                popMenu.setOnItemClickListener(GroupMotorsActivity.this);
                popMenu.showAsDropDown(v);
            }
        });

        noRecord = (LinearLayout) findViewById(R.id.view_no_record);
        // tvNoRecordMsg = (TextView) view.findViewById(R.id.tvNoRecordMsg);
        // tvNoRecordMsg.setText("车队未找到可用车辆,赶快为车队添加车辆吧");

        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        tv_title =
                ((TextView) findViewById(R.id.common_view_title_text));
        grpname = getIntent().getStringExtra(Constants.Feild.KEY_GROUP_NAME);
        if (!TextUtils.isEmpty(grpname))
            tv_title.setText(grpname);
        else
            tv_title.setText(R.string.group_motors_user);
        grpid = getIntent().getStringExtra(Constants.Feild.KEY_GROUP_ID);
        if (TextUtils.isEmpty(grpid))
            return;
    }

    private void initViews() {
        filterData();
        // 根据a-z进行排序
        adapter = new SortAdapter(this, showDataList, redict);
        plv_order_list.setAdapter(adapter);
        if (showDataList.size() > 0) {
            noRecord.setVisibility(View.GONE);
        } else {
            noRecord.setVisibility(View.VISIBLE);
        }
        setLoadViewVisibility(View.GONE);
    }

    private void filterData() {
        Collections.sort(showDataList, pinyinComparator);
        showDataList.clear();
        if (ustatus == 0) {
            showDataList.addAll(sourceDataList);
            return;
        }
        Iterator<VehicleSortModel> it = sourceDataList.iterator();
        VehicleSortModel vsm = null;
        while (it.hasNext()) {
            vsm = it.next();
            if (ustatus == vsm.getUstatus())
                showDataList.add(vsm);
        }
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<VehicleSortModel> filterDateList = new ArrayList<VehicleSortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = showDataList;
        } else {
            filterDateList.clear();
            for (VehicleSortModel sortModel : showDataList) {
                String content = sortModel.getSortContent();
                if (content.indexOf(filterStr.toString()) != -1
                        || characterParser.getSelling(content.toLowerCase())
                        .indexOf(filterStr.toString().toLowerCase()) != -1) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    private void refreshData() {
        AsyncHttpService.getGroupUsers(grpid, isContract, null,
                new JsonHttpResponseHandler() {
                    private int errCode;
                    private String msg;

                    @Override
                    public void onStart() {
                        super.onStart();
                        setLoadViewVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            errCode = response.getInt("errcode");
                            if (errCode != 0) {
                                msg = response.getString(Constants.Feild.KEY_MSG);
                                switch (response
                                        .getInt(Constants.Feild.KEY_ERROR_CODE)) {
                                    case 41:
                                        returnLogin(GroupMotorsActivity.this, msg, null);
                                        break;
                                    case 42:
                                        returnLogin(GroupMotorsActivity.this, msg, null);
                                        break;
                                    default:
                                        showLongToast(msg);
                                        break;
                                }
                                noRecord.setVisibility(View.VISIBLE);
                                setLoadViewVisibility(View.GONE);
                                return;
                            }
                            plv_order_list.setVisibility(View.VISIBLE);
                            JSONArray arrs = response.getJSONArray("staffs");
                            JSONObject jsonObj = null;
                            sourceDataList.clear();
                            for (int i = 0; i < arrs.length(); i++) {
                                jsonObj = arrs.getJSONObject(i);
                                VehicleSortModel sortModel = new VehicleSortModel();
                                StringBuffer sb = new StringBuffer("");
                                sb.append(TextUtils.isEmpty(jsonObj
                                        .getString("pname")) ? "" : jsonObj
                                        .getString("pname"));
                                sb.append(TextUtils.isEmpty(jsonObj
                                        .getString("loctel")) ? "" : ","
                                        + jsonObj.getString("loctel"));
                                sb.append(TextUtils.isEmpty(jsonObj
                                        .getString("truckno")) ? "" : ","
                                        + jsonObj.getString("truckno"));
                                sortModel.setHdiId(jsonObj.getString("pid"));
                                sortModel.setHdiPlate(jsonObj.getString("truckno"));
                                sortModel.setUstatus(jsonObj.getInt("ustatus"));
                                sortModel.setSortContent(sb.toString());

                                // 汉字转换成拼音
                                String pinyin = characterParser.getSelling(sb
                                        .toString());
                                String sortString = pinyin.substring(0, 1).toUpperCase();
                                // 正则表达式，判断首字母是否是英文字母
                                if (sortString.matches("[A-Z]")) {
                                    sortModel.setSortLetters(sortString.toUpperCase());
                                } else {
                                    sortModel.setSortLetters("#");
                                }
                                sourceDataList.add(sortModel);
                            }

                            filterData();
                            adapter.notifyDataSetChanged();
                            plv_order_list.onRefreshComplete();
                            if (showDataList.size() > 0) {
                                noRecord.setVisibility(View.GONE);
                            } else {
                                noRecord.setVisibility(View.VISIBLE);
                            }
                            setLoadViewVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            setLoadViewVisibility(View.GONE);
                        }
                    }
                }, this);
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD_MORE_SUCCESS:
                    moreProgressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    plv_order_list.setSelectionfoot();
                    break;

                case LOAD_NEW_INFO:
                    refreshData();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20) {
            refreshData();
        }
    }

    @Override
    public void onItemClick(int index) {
        if (index == ustatus)
            return;
        tv_order_statu.setText(menus[index]);
        ustatus = index;
        loadQueryData(true);
    }

    /**
     * 查询菜单数据
     *
     * @param isRefresh
     */
    private void loadQueryData(final boolean isRefresh) {
        filterData();
        // 根据a-z进行排序
        Collections.sort(showDataList, pinyinComparator);
        adapter.notifyDataSetChanged();
        if (showDataList.size() > 0) {
            noRecord.setVisibility(View.GONE);
        } else {
            noRecord.setVisibility(View.VISIBLE);
        }
    }
}
