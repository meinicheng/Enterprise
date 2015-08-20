package com.sdbnet.hywy.enterprise.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.ui.widget.ZoomImageView;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageBrowserActivity extends BaseActivity implements
		OnItemSelectedListener, OnItemClickListener, OnPageChangeListener {

	private Gallery gallery;
	private ImageAdapter imageAdapter;
	private int mCurrentPos = 1; // 设置一加载，就显示图片的第二张
	private HashMap<Integer, ImageView> mViewMap;
	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();

	private ViewPager vp_img_show; // 用于管理图片的滑动、浏览
	private LinearLayout loadingLayout;
	private ImageView mBack;
	private DisplayImageOptions options;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_gallery);

		options = new DisplayImageOptions.Builder()
				// .showImageOnLoading(R.drawable.no_photo)
				.showImageForEmptyUri(R.drawable.no_photo)
				.showImageOnFail(R.drawable.no_photo).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		mBack = (ImageView) findViewById(R.id.common_view_title_img);
		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView) findViewById(R.id.common_view_title_text))
				.setText(R.string.attachment_scan);

		loadingLayout = (LinearLayout) findViewById(R.id.ll_loading);

		String traceid = getIntent().getStringExtra(
				Constants.Feild.KEY_TRACE_ID); // 取得跟踪日志id

		gallery = (Gallery) findViewById(R.id.gallery);
		gallery.setOnItemSelectedListener(this);
		gallery.setOnItemClickListener(this);

		vp_img_show = (ViewPager) findViewById(R.id.vp_img_show);
		vp_img_show.setOnPageChangeListener(this);
		vp_img_show.setEnabled(false);

		loadAttachs(traceid);
	}

	/**
	 * 根据跟踪日志id获取相应的图片附件
	 *
	 * @param traceid
	 */
	private void loadAttachs(String traceid) {
		AsyncHttpService.getTraceAttachs(traceid,
				new JsonHttpResponseHandler() {

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						showShortToast(getResources().getString(
								R.string.httpError));
						loadingLayout.setVisibility(View.GONE);
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						System.out.println("attachs: " + response.toString());
						try {
							int errCode = response
									.getInt(Constants.Feild.KEY_ERROR_CODE);
							if (errCode != 0) {
								String msg = response
										.getString(Constants.Feild.KEY_MSG);
								switch (response
										.getInt(Constants.Feild.KEY_ERROR_CODE)) {
								case 41:
									returnLogin(ImageBrowserActivity.this, msg,
											null);
									break;
								case 42:
									returnLogin(ImageBrowserActivity.this, msg,
											null);
									break;
								default:
									showLongToast(msg);
									loadingLayout.setVisibility(View.GONE);
									break;
								}
								return;
							}

							JSONArray arrs = response
									.getJSONArray(Constants.Feild.KEY_PICS);
							JSONObject jsonObj = null;
							Map<String, String> map = null;
							list.clear();

							// 解析图片附件
							for (int i = 0; i < arrs.length(); i++) {
								jsonObj = arrs.getJSONObject(i);
								map = new HashMap<String, String>();
								map.put(Constants.Feild.KEY_PIC_ID, jsonObj
										.getString(Constants.Feild.KEY_PIC_ID));
								map.put(Constants.Feild.KEY_PIC_SMALL,
										jsonObj.getString(Constants.Feild.KEY_PIC_SMALL));
								map.put(Constants.Feild.KEY_PIC_BIG, jsonObj
										.getString(Constants.Feild.KEY_PIC_BIG));
								map.put(Constants.Feild.KEY_LOCA_TIME,
										jsonObj.getString(Constants.Feild.KEY_LOCA_TIME));
								map.put(Constants.Feild.KEY_LOCA_ADDRESS,
										jsonObj.getString(Constants.Feild.KEY_LOCA_ADDRESS));
								map.put(Constants.Feild.KEY_LOCA_LONGITUDE,
										String.valueOf(jsonObj
												.getDouble(Constants.Feild.KEY_LOCA_LONGITUDE)));
								map.put(Constants.Feild.KEY_LOCA_LATITUDE,
										String.valueOf(jsonObj
												.getDouble(Constants.Feild.KEY_LOCA_LATITUDE)));

								list.add(map);
							}

							if (list.size() == 0) {
								loadingLayout.setVisibility(View.GONE);
								findViewById(R.id.view_no_record)
										.setVisibility(View.VISIBLE);
								return;
							}

							if (mCurrentPos >= list.size()) {
								mCurrentPos = list.size() - 1;
							} else {
								mCurrentPos = 1;
							}

							// 设置缩略图
							imageAdapter = new ImageAdapter(
									ImageBrowserActivity.this, list.size());
							gallery.setAdapter(imageAdapter);
							gallery.setSelection(mCurrentPos);

							// 设置大图
							ViewPagerAdapter adapter = new ViewPagerAdapter();
							vp_img_show.setAdapter(adapter);
							vp_img_show.setCurrentItem(mCurrentPos);

							loadingLayout.setVisibility(View.GONE);
						} catch (Exception e) {
							e.printStackTrace();
							loadingLayout.setVisibility(View.GONE);
						}
						super.onSuccess(statusCode, headers, response);
					}

				}, this);
	}

	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;
		private int mCount;// 一共多少个item

		public ImageAdapter(Context context, int count) {
			mContext = context;
			mCount = count;
			mViewMap = new HashMap<Integer, ImageView>(count);
			TypedArray typedArray = obtainStyledAttributes(R.styleable.Gallery);
			// 设置边框的样式
			mGalleryItemBackground = typedArray.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0);
			// 定义可以重复使用.可回收
			typedArray.recycle();
		}

		@Override
		public int getCount() {
			return list.size();
			// return Integer.MAX_VALUE; // 无限循环滑动
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageview = mViewMap.get(position % mCount);
			if (imageview == null) {
				imageview = new ImageView(mContext);
				// imageview.setImageResource(resIds[position % resIds.length]);

				// 设置缩略图显示大小
				imageview.setScaleType(ImageView.ScaleType.FIT_XY);
				imageview
						.setLayoutParams(new Gallery.LayoutParams(
								UtilsAndroid.UI.dip2px(
										ImageBrowserActivity.this, 94),
								UtilsAndroid.UI.dip2px(
										ImageBrowserActivity.this, 130)));
				imageview.setBackgroundResource(mGalleryItemBackground);

				final ImageView iView = imageview;

				// 从网络异步加载缩略图片
				if (UtilsAndroid.Sdcard.hasSDCard()) {
					ImageLoader.getInstance().displayImage(
							list.get(position % mCount).get(
									Constants.Feild.KEY_PIC_SMALL), iView);
					// ImageLoader.getInstance().asynLoadImage(
					// list.get(position % mCount).get(
					// Constants.Feild.KEY_PIC_SMALL),
					// new AsyncLoadImageHandler() {
					//
					// @Override
					// public void onImageLoaded(Bitmap bitmap) {
					// if (bitmap != null) {
					// iView.setImageBitmap(bitmap);
					// }
					// }
					//
					// @Override
					// public void onLoadError() {
					// System.out.println("onLoadError.");
					// // 加载失败提示
					// runOnUiThread(new Runnable() {
					// @Override
					// public void run() {
					// Toast.makeText(
					// ImageBrowserActivity.this,
					// "图片未找到", Toast.LENGTH_LONG)
					// .show();
					// loadingLayout
					// .setVisibility(View.GONE);
					// }
					// });
					// }
					// });
				} else {
					Toast.makeText(ImageBrowserActivity.this, "未发现SD卡",
							Toast.LENGTH_SHORT).show();
				}
				mViewMap.put(position % mCount, imageview);
			}
			return imageview;
		}
	}

	// 滑动Gallery的时候，ImageView不断显示当前的item
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// imageSwitcher.setImageResource(resIds[position % resIds.length]);
	}

	// 设置点击Gallery的时候才切换到该图片
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mCurrentPos == position) {
			// 如果在显示当前图片，再点击，就不再加载。
			return;
		}
		mCurrentPos = position;
		vp_img_show.setCurrentItem(mCurrentPos);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	/**
	 * ViewPager的适配器
	 */
	class ViewPagerAdapter extends PagerAdapter {

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			View view = LayoutInflater.from(ImageBrowserActivity.this).inflate(
					R.layout.zoom_image_layout, null);
			ZoomImageView zoomImageView = (ZoomImageView) view
					.findViewById(R.id.zoom_image_view);
			zoomImageView.setBackgroundColor(0xff000000);
			ProgressBar progressBar = (ProgressBar) view
					.findViewById(R.id.progressBar);

			// 从网络异步加载大图
			if (UtilsAndroid.Sdcard.hasSDCard()) {
				System.out.println("big: "
						+ list.get(position).get(Constants.Feild.KEY_PIC_BIG));
				loadImage(position, zoomImageView, progressBar);
			}

			container.addView(view);
			return view;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;
			container.removeView(view);
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		mCurrentPos = position;
		gallery.setSelection(mCurrentPos);
	}

	@Override
	public void onPageSelected(int currentPage) {
	}

	public void loadImage(int position, final ZoomImageView imageView,
			final ProgressBar progressBar) {

		String imagUrl = list.get(position).get(Constants.Feild.KEY_PIC_BIG);
		ImageLoader.getInstance().displayImage(imagUrl, imageView, options,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap bitmap) {
						progressBar.setVisibility(View.GONE);
						if (bitmap != null) {
							imageView.setVisibility(View.VISIBLE);
							// imageView.setImageBitmap(bitmap);
							float width = 0;
							float height = 0;
							try {
								// 设置图片尺寸，以便匹配屏幕显示
								if (bitmap.getWidth() > bitmap.getHeight()) {
									width = imageView.getWidth();
									height = bitmap.getHeight()
											* (width / bitmap.getWidth());
								} else {
									height = imageView.getHeight();
									width = bitmap.getWidth()
											* (height / bitmap.getHeight());
								}
								bitmap = Bitmap.createScaledBitmap(bitmap,
										(int) width, (int) height, true);
								imageView.setImageBitmap(bitmap);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						progressBar.setVisibility(View.GONE);
					}
				}

		);

	}

}

// ImageLoader.getInstance().asynLoadImage(
// list.get(position).get(Constants.Feild.KEY_PIC_BIG),
// new AsyncLoadImageHandler() {
//
// @Override
// public void onImageLoaded(Bitmap bitmap) {
// progressBar.setVisibility(View.GONE);
// loadingLayout.setVisibility(View.GONE);
// if (bitmap == null) {
// return;
// }
// float width = 0;
// float height = 0;
// try {
// // 设置图片尺寸，以便匹配屏幕显示
// if (bitmap.getWidth() > bitmap.getHeight()) {
// width = zoomImageView.getWidth();
// height = bitmap.getHeight()
// * (width / bitmap.getWidth());
// } else {
// height = zoomImageView.getHeight();
// width = bitmap.getWidth()
// * (height / bitmap.getHeight());
// }
// bitmap = Bitmap.createScaledBitmap(bitmap,
// (int) width, (int) height, true);
// zoomImageView.setImageBitmap(bitmap);
// } catch (Exception e) {
// e.printStackTrace();
// } catch (Throwable e) {
// e.printStackTrace();
// }
// }
//
// @Override
// public void onLoadError() {
// System.out.println("onLoadError.");
// // 图片加载错误提示
// runOnUiThread(new Runnable() {
// @Override
// public void run() {
// Toast.makeText(
// ImageBrowserActivity.this,
// "图片未找到", Toast.LENGTH_LONG)
// .show();
// loadingLayout.setVisibility(View.GONE);
// }
// });
// }
// });