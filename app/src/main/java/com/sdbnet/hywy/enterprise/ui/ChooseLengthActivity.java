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
 * 选择车长
 * @author Administrator
 *
 */
public class ChooseLengthActivity extends Activity {
	private ImageView mBack;
	private ListView lv;
	private String car;
	private String length = "";
	private final int CHOOSE_WEIGHT = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_length);
		initView();
		initListener();
	}

	private void initView() {
		lv = (ListView) findViewById(R.id.lv2);
	}

	private void initListener() {
		final String[] a = { "不限", "4.2", "4.5", "5", "5.5", "5.8", "6", "6.2", "6.5", "6.8", "7", "7.2", "7.5", "8", "8.5", "8.6", "9", "9.5", "9.6", "10", "10.5", "11", "11.5", "12", "12.5", "13",
				"13.5", "14", "14.5", "15", "15.5", "16", "16.5", "17", "17.5", "18", "18.5", "19", "19.5", "20", "20.5", "21", "21.5", "22", "22.5", "23", "23.5", "24", "24.5", "25" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_choose_number_show, R.id.content, a);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (0 != position)
					length = a[position];
				Log.i("eeee", "----" + parent + position);
				Intent intent = new Intent();
				if (ChooseModelActivity.isChooseAll) {
					intent.setClass(ChooseLengthActivity.this, ChooseWeightActivity.class);
					startActivityForResult(intent, CHOOSE_WEIGHT);
					return;
				}
				intent.putExtra("length", length);// 取得是获得数据的item里面的值传过去
				setResult(Constants.ResultCode.QUERY_CHOOSE_LENGTH, intent);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent inte) {
		super.onActivityResult(requestCode, resultCode, inte);
		if (resultCode > 0) {
			switch (requestCode) {
			case CHOOSE_WEIGHT:
				inte.setClass(ChooseLengthActivity.this, ChooseModelActivity.class);
				inte.putExtra("length", length);
				setResult(10, inte);
				finish();
				break;

			default:
				break;
			}
		}
	}
}
