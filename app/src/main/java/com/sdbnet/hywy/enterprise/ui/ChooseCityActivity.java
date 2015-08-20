package com.sdbnet.hywy.enterprise.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.wheel.OnWheelChangedListener;
import com.sdbnet.hywy.enterprise.wheel.OnWheelScrollListener;
import com.sdbnet.hywy.enterprise.wheel.WheelView;
import com.sdbnet.hywy.enterprise.wheel.adapter.ArrayWheelAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author zyj
 * 
 */
public class ChooseCityActivity extends Activity implements
		OnWheelChangedListener, OnWheelScrollListener {
	/**
	 * 把全国的省市区的信息以json的格式保存，解析完成后赋值为null
	 */
	private static JSONObject mJsonObj;
	/**
	 * 省的WheelView控件
	 */
	private WheelView mProvince;
	/**
	 * 市的WheelView控件
	 */
	private WheelView mCity;
	/**
	 * 区的WheelView控件
	 */
	private WheelView mArea;

	/**
	 * 所有省
	 */
	private String[] mProvinceDatas;
	/**
	 * key - 省 value - 市s
	 */
	private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	/**
	 * key - 市 values - 区s
	 */
	private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();

	/**
	 * 当前省的名称
	 */
	private String mCurrentProviceName;
	/**
	 * 当前市的名称
	 */
	private String mCurrentCityName;

	/**
	 * 当前区的名称
	 */
	// private String mCurrentAreaName ="";

	private LinearLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_city);

		initJsonData();

		mProvince = (WheelView) findViewById(R.id.id_province);
		mCity = (WheelView) findViewById(R.id.id_city);
		// mArea = (WheelView) findViewById(R.id.id_area);

		layout = (LinearLayout) findViewById(R.id.pop_layout);

		// 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
						Toast.LENGTH_SHORT).show();
			}
		});

		initDatas();

		mProvince.setViewAdapter(new ArrayWheelAdapter<String>(this,
				mProvinceDatas));
		// 添加change事件
		// mProvince.addScrollingListener(this);
		// mProvince.addChangingListener(this);
		// // 添加change事件
		// mCity.addChangingListener(this);
		mProvince.addScrollingListener(this);
		// 添加change事件
		mCity.addScrollingListener(this);
		// 添加change事件
		// mArea.addChangingListener(this);

		mProvince.setVisibleItems(5);
		// mProvince.setCyclic(true);
		mCity.setVisibleItems(5);
		// mCity.setCyclic(true);
		// mArea.setVisibleItems(5);
		updateCities();
		int pCurrentCity = mCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrentCity];

	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	// private void updateAreas()
	// {
	// int pCurrent = mCity.getCurrentItem();
	// mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
	// String[] areas = mAreaDatasMap.get(mCurrentCityName);
	//
	// if (areas == null)
	// {
	// areas = new String[] { "" };
	// }
	// mArea.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
	// mArea.setCurrentItem(0);
	// }

	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		mCity.setCurrentItem(0);

		int pCurrentCity = mCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrentCity];
	}

	/**
	 * 解析整个Json对象，完成后释放Json对象的内存
	 */
	private void initDatas() {
		try {
			JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
			mProvinceDatas = new String[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象
				String province = jsonP.getString("p");// 省名字

				mProvinceDatas[i] = province;

				JSONArray jsonCs = null;
				try {
					/**
					 * Throws JSONException if the mapping doesn't exist or is
					 * not a JSONArray.
					 */
					jsonCs = jsonP.getJSONArray("c");
				} catch (Exception e1) {
					continue;
				}
				String[] mCitiesDatas = new String[jsonCs.length()];
				for (int j = 0; j < jsonCs.length(); j++) {
					JSONObject jsonCity = jsonCs.getJSONObject(j);
					String city = jsonCity.getString("n");// 市名字
					mCitiesDatas[j] = city;
					JSONArray jsonAreas = null;
					try {
						/**
						 * Throws JSONException if the mapping doesn't exist or
						 * is not a JSONArray.
						 */
						jsonAreas = jsonCity.getJSONArray("a");
					} catch (Exception e) {
						continue;
					}

					// String[] mAreasDatas = new String[jsonAreas.length()];//
					// 当前市的所有区
					// for (int k = 0; k < jsonAreas.length(); k++)
					// {
					// String area =
					// jsonAreas.getJSONObject(k).getString("s");// 区域的名称
					// mAreasDatas[k] = area;
					// }
					// mAreaDatasMap.put(city, mAreasDatas);
				}

				mCitisDatasMap.put(province, mCitiesDatas);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		mJsonObj = null;
	}

	/**
	 * 从assert文件夹中读取省市区的json文件，然后转化为json对象
	 */
	private void initJsonData() {
		try {
			if (mJsonObj != null && mJsonObj.length() > 0)
				return;
			StringBuffer sb = new StringBuffer();
			InputStream is = getAssets().open("city.json");
			int len = -1;
			byte[] buf = new byte[1024];
			while ((len = is.read(buf)) != -1) {
				sb.append(new String(buf, 0, len, "UTF-8"));
			}
			is.close();
			mJsonObj = new JSONObject(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * change事件的处理
	 */
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// if (!scrolling) {
		if (wheel == mProvince) {
			updateCities();
		} else if (wheel == mCity) {
			int pCurrentCity = mCity.getCurrentItem();
			mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrentCity];
			// } else if (wheel == mArea)
			// {
			// mCurrentAreaName =
			// mAreaDatasMap.get(mCurrentCityName)[newValue];
		}
		// }
	}

	public void showChoose(View view) {
		Intent intent = new Intent();
		if ("不限".equals(mCurrentCityName))
			intent.putExtra("city", mCurrentProviceName);
		else
			intent.putExtra("city", mCurrentCityName);
		setResult(100, intent);
		finish();
	}

	@Override
	public void onScrollingStarted(WheelView wheel) {
		// scrolling = true;
	}

	@Override
	public void onScrollingFinished(WheelView wheel) {
		if (wheel == mProvince) {
			updateCities();
		} else if (wheel == mCity) {
			int pCurrentCity = mCity.getCurrentItem();
			mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrentCity];
		}

	}
}
