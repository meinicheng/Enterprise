package com.sdbnet.hywy.enterprise.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.utils.Constants;


/**
 * 选择车载重
 * @author Administrator
 *
 */
public class ChooseWeightActivity extends Activity {
	private ImageView mBack;
	private ListView lv;
	private String car;
	private String weight = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.zhong);
		initView();
		initListener();
	}

	private void initView() {
		lv = (ListView) findViewById(R.id.lv3);
	}

	private void initListener() {
		final String[] a = { "不限", "2", "3", "4", "5", "6", "7", "8", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "25", "28", "30", "32", "34", "35", "36", "37", "38", "40",
				"42", "45", "48", "50", "55", "60", "65", "70", "75", "80", "85", "90", "95", "100", "300", "400", "500", "600", "700", "800", "900", "1000", "1500" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_choose_number_show, R.id.content, a);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (0 != position)
					weight = a[position];
				Log.i("eeee", "----" + parent + position);
				Intent intent = new Intent();
				if (ChooseModelActivity.isChooseAll) {
					intent.setClass(ChooseWeightActivity.this, ChooseLengthActivity.class);
					intent.putExtra("weight", weight);
					setResult(30, intent);
					finish();
					return;
				}
				intent.putExtra("weight", weight);// 取得是获得数据的item里面的值传过去
				setResult(Constants.ResultCode.QUERY_CHOOSE_LOAD, intent);
				finish();
				Log.i("wwww", "----" + car);
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

}
