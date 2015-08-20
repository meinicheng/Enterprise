package com.sdbnet.hywy.enterprise.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.db.DBHelper;
import com.sdbnet.hywy.enterprise.slidingmenu.SlidingMenu;
import com.sdbnet.hywy.enterprise.ui.base.BaseSlidingFragmentActivity;
import com.sdbnet.hywy.enterprise.ui.view.ComprehensiveQueryFragment;
import com.sdbnet.hywy.enterprise.ui.view.ContractStaffFragment;
import com.sdbnet.hywy.enterprise.ui.view.InfoFragment;
import com.sdbnet.hywy.enterprise.ui.view.LocationFragment;
import com.sdbnet.hywy.enterprise.ui.view.TrackQueryFragment;
import com.sdbnet.hywy.enterprise.ui.widget.CustomButton;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.LogUtil;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;
import com.sdbnet.hywy.enterprise.utils.UtilsCommon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseSlidingFragmentActivity implements
        OnClickListener, AnimationListener {
    private static final String LIST_TEXT = "text";
    private static final String LIST_IMAGEVIEW = "img";
    private static final String TAG = "MainActivity";
    /**
     * 数字代表列表顺序
     */
    private int mTag = 0;

    private CustomButton cb_setting;
    private CustomButton cb_about;
    private View title;
    private LinearLayout ll_fragement_container;
    private List<String> navs;

    // private LinearLayout ll_query_btn;

    private LinearLayout ll_loading;
    private FragmentManager fragmentManager;

    private ListView lv_slidingmenu;
    private SimpleAdapter adapter;
    private ImageView mImgTitle;
    private ImageView mImgLogo;

    private TextView tv_above_title;
    private SlidingMenu sm;
    private boolean mIsTitleHide = false;
    private boolean mIsAnim = false;

    private TextView totalName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesUtil.initStoreData();

        initSlidingMenu();
        setContentView(R.layout.activity_slidingmenu);
        initNav();
        initControl();
        initViewPager();
        initListView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            DBHelper db = DBHelper.getInstance(this);
            db.closeDb();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSlidingMenu() {
        setBehindContentView(R.layout.view_slidingmenu);
        // customize the SlidingMenu
        sm = getSlidingMenu();
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // sm.setFadeDegree(0.35f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        sm.setShadowDrawable(R.drawable.slidingmenu_shadow);
        // sm.setShadowWidth(20);
        sm.setBehindScrollScale(0);
    }

    private void initControl() {
        // 进度条(loading图标layout)
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        // 顶部导航栏标题文本
        tv_above_title = (TextView) findViewById(R.id.common_view_title_text);
        // 标题初始化
        tv_above_title.setText(navs.get(mTag));

        // 顶部导航栏查询按钮
        // ll_query_btn = (LinearLayout) findViewById(R.id.ll_query_btn);

        lv_slidingmenu = (ListView) findViewById(R.id.lv_slidingmenu);
        mImgTitle = (ImageView) findViewById(R.id.common_view_title_img);
        mImgTitle.setImageResource(R.drawable.dishes_btn_selector);
        mImgTitle.setOnClickListener(this);
        mImgLogo = (ImageView) findViewById(R.id.iv_login);

        // 异步加载公司logo图片
        ImageLoader.getInstance().displayImage(PreferencesUtil.company_logo,
                mImgLogo);

        cb_setting = (CustomButton) findViewById(R.id.cb_setting);
        cb_setting.setOnClickListener(this);
        cb_about = (CustomButton) findViewById(R.id.cb_about);
        cb_about.setOnClickListener(this);

        title = findViewById(R.id.main_title);
        ll_fragement_container = (LinearLayout) findViewById(R.id.ll_fragement_container);
    }

    // private void initgoHome() {
    //
    // }

    /**
     * 获取菜单列表
     *
     * @return
     */
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = null;

        for (String menu : navs) {
            map = new HashMap<String, Object>();
            map.put(LIST_TEXT, menu);
            if (menu.equals(getResources().getString(R.string.menu_location))) {
                map.put(LIST_IMAGEVIEW, R.drawable.location_btn_selector);
            } else if (menu
                    .equals(getResources().getString(R.string.menu_info))) {
                map.put(LIST_IMAGEVIEW, R.drawable.info_btn_selector);
            } else if (menu.equals(getResources().getString(
                    R.string.menu_order_list))) {
                map.put(LIST_IMAGEVIEW, R.drawable.trace_btn_selector);
            } else if (menu.equals(getResources().getString(
                    R.string.menu_order_trace))) {
                map.put(LIST_IMAGEVIEW, R.drawable.order_btn_selector);
            } else if (menu.equals(getResources().getString(
                    R.string.menu_order_query))) {
                map.put(LIST_IMAGEVIEW, R.drawable.manage_btn_selector);
            } else if (menu.equals(getResources().getString(
                    R.string.menu_contract_users))) {
                map.put(LIST_IMAGEVIEW, R.drawable.manage_btn_selector);
            } else if (menu.equals(getResources().getString(
                    R.string.menu_motor_locus))) {
                map.put(LIST_IMAGEVIEW, R.drawable.manage_btn_selector);
            }
            list.add(map);
        }

        return list;
    }

    private void initListView() {
        adapter = new SimpleAdapter(this, getData(),
                R.layout.list_item_slidingmenu, new String[]{LIST_TEXT,
                LIST_IMAGEVIEW}, new int[]{R.id.tv_menu_title,
                R.id.iv_menu_icon}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == mTag) {
                    // view.setBackgroundResource(R.drawable.back_behind_list);
                    lv_slidingmenu.setTag(view);
                } else {
                    // view.setBackgroundColor(Color.TRANSPARENT);
                }
                return view;
            }
        };
        lv_slidingmenu.setAdapter(adapter);
        lv_slidingmenu.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (3 != position && position != 2) {
                    String navTitle = navs.get(position);
                    if (lv_slidingmenu.getTag() != null) {
                        if (lv_slidingmenu.getTag() == view) {
                            MainActivity.this.showContent();
                            return;
                        }
                    }
                    tv_above_title.setText(navTitle);
                    lv_slidingmenu.setTag(view);
                }
                new MyTask().execute(position);
            }
        });

        totalName = (TextView) findViewById(R.id.totalName);
        totalName.setText(PreferencesUtil.user_parent_name);
    }

    private void exitAccount() {
        PreferencesUtil.clearLocalData(PreferenceManager
                .getDefaultSharedPreferences(this));
        Intent intent = new Intent(this, UserLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    private void initNav() {
        String execMenus = PreferencesUtil.getValue(
                PreferencesUtil.KEY_EXECUTE_MENU, null);

        if (!"".equals(getIntent().getStringExtra("mTag"))
                && getIntent().getStringExtra("mTag") != null) {
            mTag = Integer.parseInt(getIntent().getStringExtra("mTag"));
        }

        LogUtil.d(TAG, "mTag=" + getIntent().getStringExtra("mTag") + ":>>");
        if (TextUtils.isEmpty(execMenus)) {
            exitAccount();
            return;
        }
        navs = new ArrayList<String>();

        String[] menus = execMenus.split(",");
        for (String menu : menus) {
            if (Constants.MENU_MAP.get(menu) == null) {
                continue;
            }
            navs.add(getString(Constants.MENU_MAP.get(menu)));
        }
        // navs.add(getResources().getString(R.string.menu_order_query));
        // navs.add(getResources().getString(R.string.menu_contract_users));
        // navs.add(getResources().getString(R.string.menu_motor_locus));
    }

    private void initViewPager() {
        fragmentManager = getSupportFragmentManager();
        new MyTask().execute(mTag);
    }

    public class MyTask extends AsyncTask<Integer, String, Map<String, Object>> {

        @Override
        protected void onPreExecute() {
            ll_loading.setVisibility(View.VISIBLE);
            MainActivity.this.showContent();
            super.onPreExecute();
        }

        @Override
        protected Map<String, Object> doInBackground(Integer... params) {
            mTag = params[0];
            Map<String, Object> map = new HashMap<String, Object>();
            if (mTag == 0) {
                map.put("tabs", new LocationFragment());
            } else if (mTag == 1) {
                map.put("tabs", new InfoFragment());
                // } else if (mTag == 2) {
                // map.put("tabs", new OrderStatisticsFragment());
                // } else if (mTag == 3) {
                // map.put("tabs", new CaptureFragment());
            } else if (mTag == 4) {
                map.put("tabs", new ComprehensiveQueryFragment());
            } else if (mTag == 5) {
                map.put("tabs", new TrackQueryFragment());
            } else if (mTag == 6) {
                map.put("tabs", new ContractStaffFragment());
            }
            // Message message = new Message();
            // message.what = mTag;
            // handler.sendMessage(message);
            return map;
        }

        @Override
        protected void onPostExecute(Map<String, Object> result) {
            super.onPostExecute(result);
            if (2 == mTag) {
                UtilsCommon.start_activity(MainActivity.this,
                        OrderStatisticsActivity.class);

            } else if (3 == mTag) {
                UtilsCommon.start_activity(MainActivity.this,
                        CaptureActivity.class);
            } else if (result != null && !result.isEmpty()) {
                showFragments((Fragment) result.get("tabs"));
            }
            ll_loading.setVisibility(View.GONE);
        }
    }

    private void showFragments(Fragment fragment) {
        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.replace(R.id.ll_fragement_container, fragment);
        trans.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_view_title_img: // 显示菜单
                showMenu();
                break;
            case R.id.cb_setting: // 点击设置
                UtilsCommon.start_activity(this, SystemSettingActivity.class);
                break;
            case R.id.cb_about: // 点击关于
                UtilsCommon.start_activity(this, AboutActivity.class);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//		if (null != totalName
//				& !PreferencesUtil.user_name.equals(totalName.getText()
//						.toString())) {
        if (null != totalName & !TextUtils.equals(PreferencesUtil.user_name, totalName.getText()
                .toString())) {
            totalName.setText(PreferencesUtil.user_name);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (UtilsAndroid.Common.isFastDoubleClick()) {
                // 回到桌面
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            } else {
                // 连续按两次返回键就退出
                showMsg(R.string.press_again_exit);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            // 展开或关闭菜单
            if (sm.isMenuShowing()) {
                toggle();
            } else {
                showMenu();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        if (mIsAnim) {
            return false;
        }
        final int action = event.getAction();

        float x = event.getX();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastY = y;
                lastX = x;
                return false;
            case MotionEvent.ACTION_MOVE:
                float dY = Math.abs(y - lastY);
                float dX = Math.abs(x - lastX);
                boolean down = y > lastY ? true : false;
                lastY = y;
                lastX = x;
                if (dX < 8 && dY > 8 && !mIsTitleHide && !down) {
                    Animation anim = AnimationUtils.loadAnimation(
                            MainActivity.this, R.anim.push_top_in);
                    // anim.setFillAfter(true);
                    anim.setAnimationListener(MainActivity.this);
                    // title动画
                    // title.startAnimation(anim);
                } else if (dX < 8 && dY > 8 && mIsTitleHide && down) {
                    Animation anim = AnimationUtils.loadAnimation(
                            MainActivity.this, R.anim.push_top_out);
                    // anim.setFillAfter(true);
                    anim.setAnimationListener(MainActivity.this);
                    // title动画
                    // title.startAnimation(anim);
                } else {
                    return false;
                }
                mIsTitleHide = !mIsTitleHide;
                mIsAnim = true;
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (mIsTitleHide) {
            title.setVisibility(View.GONE);
        } else {

        }
        mIsAnim = false;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationStart(Animation animation) {
        title.setVisibility(View.VISIBLE);
        if (mIsTitleHide) {
            FrameLayout.LayoutParams lp = (LayoutParams) ll_fragement_container
                    .getLayoutParams();
            lp.setMargins(0, 0, 0, 0);
            ll_fragement_container.setLayoutParams(lp);
        } else {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) title
                    .getLayoutParams();
            lp.setMargins(0, 0, 0, 0);
            title.setLayoutParams(lp);
            FrameLayout.LayoutParams lp1 = (LayoutParams) ll_fragement_container
                    .getLayoutParams();
            lp1.setMargins(0,
                    getResources().getDimensionPixelSize(R.dimen.title_height),
                    0, 0);
            ll_fragement_container.setLayoutParams(lp1);
        }
    }

    private float lastX = 0;
    private float lastY = 0;

    // private Handler handler = new Handler() {
    // @Override
    // public void handleMessage(Message msg) {
    // switch (msg.what) {
    // case 0:
    // case 1:
    // ll_query_btn.setVisibility(View.GONE);
    // break;
    // case 2:
    // ll_query_btn.setVisibility(View.VISIBLE);
    // break;
    // }
    // super.handleMessage(msg);
    // }
    // };

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showMsg(int resId) {
        showMsg(getString(resId));
    }
}
