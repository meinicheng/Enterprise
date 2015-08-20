package com.sdbnet.hywy.enterprise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class WhatsnewActivity extends BaseActivity {

	private ViewPager mViewPager;
	private ImageView mPage0;
	private ImageView mPage1;
	private ImageView mPage2;
	private ImageView mPage3;

	private int currIndex = 0;
	private int keyBackClickCount = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.whatsnew_viewpager);
		mViewPager = (ViewPager) findViewById(R.id.whatsnew_viewpager);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

		mPage0 = (ImageView) findViewById(R.id.page0);
		mPage1 = (ImageView) findViewById(R.id.page1);
		mPage2 = (ImageView) findViewById(R.id.page2);
		mPage3 = (ImageView) findViewById(R.id.page3);

		// 将要分页显示的View装入数组中
		LayoutInflater mLi = LayoutInflater.from(this);
		View view1 = mLi.inflate(R.layout.whats1, null);
		View view2 = mLi.inflate(R.layout.whats2, null);
		View view3 = mLi.inflate(R.layout.whats3, null);
		View view4 = mLi.inflate(R.layout.whats4, null);

		// 每个页面的view数据
		final ArrayList<View> views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);

		// 填充ViewPager的数据适配器
		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		};

		mViewPager.setAdapter(mPagerAdapter);
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				mPage0.setImageDrawable(getResources().getDrawable(R.drawable.green));
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.white));
				break;
			case 1:
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.orange));
				mPage0.setImageDrawable(getResources().getDrawable(R.drawable.white));
				mPage2.setImageDrawable(getResources().getDrawable(R.drawable.white));
				break;
			case 2:
				mPage2.setImageDrawable(getResources().getDrawable(R.drawable.yellow));
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.white));
				mPage3.setImageDrawable(getResources().getDrawable(R.drawable.white));
				break;
			case 3:
				mPage3.setImageDrawable(getResources().getDrawable(R.drawable.blue));
				mPage2.setImageDrawable(getResources().getDrawable(R.drawable.white));
				break;
			}
			currIndex = arg0;
			// animation.setFillAfter(true);// True:图片停在动画结束位置
			// animation.setDuration(300);
			// mPageImg.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	public void startbutton(View v) {
		Intent intent = new Intent(WhatsnewActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			switch (keyBackClickCount++) {
			case 0:
				Toast.makeText(this, getResources().getString(R.string.press_again_exit), Toast.LENGTH_SHORT).show();
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						keyBackClickCount = 0;
					}
				}, 3000);
				break;
			case 1:
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
				return true;
			default:
				break;
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
