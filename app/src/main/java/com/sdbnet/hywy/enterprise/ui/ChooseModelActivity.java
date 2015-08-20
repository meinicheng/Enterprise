package com.sdbnet.hywy.enterprise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 选择车型
 * @author Administrator
 *
 */
public class ChooseModelActivity extends BaseActivity {
	private ListView lv;
	private ImageView mBack;

	public static boolean isChooseAll = false;
	private String model = "";

	private final int CHOOSE_LENGTH = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.xuanche_main);
		// 取得ListView实例
		lv = (ListView) findViewById(R.id.lv1);
		// 要在ListView中显示的数据集合
		ArrayList<HashMap<String, Object>> items = getItems();
		SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.list_item_custom_choose, new String[] { "img", "car" }, new int[] { R.id.img, R.id.car });
		// 位ListView设置Adapter
		lv.setAdapter(adapter);
		setListener();
	}

	private void setListener() {

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				model = (String)((TextView) view.findViewById(R.id.car)).getText();
				Log.i("eeee", "----" + parent + position);
				Intent intent = new Intent();
				if (isChooseAll) {
					// BasicNameValuePair bnv = new BasicNameValuePair("model",
					// model);
					// IntentUtil.start_activity(ChooseModelActivity3.this,
					// ChooseLengthActivity.class, bnv);
					// finish();
					intent.setClass(ChooseModelActivity.this, ChooseLengthActivity.class);
					startActivityForResult(intent, CHOOSE_LENGTH);
					return;
				}
				intent.putExtra("model", model);// 取得是获得数据的item里面的值传过去
				setResult(Constants.ResultCode.QUERY_CHOOSE_MODEL, intent);
				finish();
				Log.i("wwww", "----" + model);
			}

		});

		mBack = (ImageView) findViewById(R.id.about_imageview_gohome);
		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 取得用于ListView的数据
	 * 
	 * @return
	 */
	private ArrayList<HashMap<String, Object>> getItems() {
		ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		String[] name = { "平板车", "爬梯车", "栏板车", "自卸车", "高栏车", "工程车", "厢式货车", "集装箱运输车", "商品运输车", "飞翼车", "保温车", "冷藏车", "罐车", "其他" };
		int[] a = { R.drawable.ping_ban_che, R.drawable.pa_ti_che, R.drawable.lan_ban_che, R.drawable.zi_xie_che, R.drawable.gao_lan_che, R.drawable.gong_cheng_che, R.drawable.xiang_shi_huo_che,
				R.drawable.ji_zhuang_xiang_che, R.drawable.shang_pin_yun_shu, R.drawable.fei_yi_che, R.drawable.bao_wen_che, R.drawable.leng_cang_che, R.drawable.you_guan_che,
				R.drawable.xiang_shi_huo_che, };
		map.put("img", a[0]);
		map.put("car", name[0]);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put("img", a[1]);
		map.put("car", name[1]);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put("img", a[2]);
		map.put("car", name[2]);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put("img", a[3]);
		map.put("car", name[3]);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put("img", a[4]);
		map.put("car", name[4]);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put("img", a[5]);
		map.put("car", name[5]);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put("img", a[6]);
		map.put("car", name[6]);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put("img", a[7]);
		map.put("car", name[7]);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put("img", a[8]);
		map.put("car", name[8]);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put("img", a[9]);
		map.put("car", name[9]);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put("img", a[10]);
		map.put("car", name[10]);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put("img", a[11]);
		map.put("car", name[11]);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put("img", a[12]);
		map.put("car", name[12]);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put("img", a[13]);
		map.put("car", name[13]);
		items.add(map);

		return items;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent inte) {
		super.onActivityResult(requestCode, resultCode, inte);
		if (resultCode > 0) {
			switch (requestCode) {
			case CHOOSE_LENGTH:
				inte.putExtra("model", model);
				setResult(10, inte);
				finish();
				break;

			default:
				break;
			}
		}
	}

}
