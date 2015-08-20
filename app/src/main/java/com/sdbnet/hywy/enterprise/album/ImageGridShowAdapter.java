package com.sdbnet.hywy.enterprise.album;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.album.AlbumHelper.ImageItem;
import com.sdbnet.hywy.enterprise.ui.ImageDetailsActivity;
import com.sdbnet.hywy.enterprise.ui.PopWindowActivity;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageGridShowAdapter extends BaseAdapter {
	public static final int REQUEST_CODE_SCAN = 110;
	private final String TAG = getClass().getSimpleName();
	private Activity mContext;
	private List<ImageItem> dataList;
	public Map<String, String> map = new HashMap<String, String>();
	private boolean isEdit;
	private boolean isShowEvery = false;
	private boolean isDelete = false;

	public ImageGridShowAdapter(Activity mContext, List<ImageItem> list,
			boolean isEdit) {
		this.mContext = mContext;
		dataList = list;
		this.isEdit = isEdit;

	}

	@Override
	public int getCount() {
		int count = 0;
		if (dataList == null) {
			dataList = new ArrayList<ImageItem>();
		}
		if (isShowEvery) {
			count = dataList.size() + Bimp.bmp.size();
		} else if (dataList.size() + Bimp.bmp.size() > Constants.Value.IMAGE_COUNT) {
			count = Constants.Value.IMAGE_COUNT;
		} else {
			count = dataList.size() + Bimp.bmp.size();
		}
		if (isEdit)
			count++;
		// Log.e("Grid", "count="+count+"");
		return count;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class Holder {
		private ImageView imgView;
		private ImageView imgDelete;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		final int pos;
		if (parent.getChildCount() != position) {
			pos = getCount() - 1;
		} else {
			pos = position;
		}
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_published_grida,
					null);
			holder = new Holder();
			holder.imgView = (ImageView) convertView
					.findViewById(R.id.item_grida_image);

			holder.imgDelete = (ImageView) convertView
					.findViewById(R.id.item_grid_delete_img);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.imgDelete.setVisibility(View.GONE);
		if (getCount() == pos + 1 && isEdit) {

			holder.imgView.setImageBitmap(BitmapFactory.decodeResource(
					mContext.getResources(), R.drawable.icon_addpic_unfocused));
			holder.imgView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					isDelete = false;
					notifyDataSetChanged();
					Bimp.loadCount = dataList.size();

					// 判断图片选择数量是否超过限制
					if ((Bimp.loadCount + Bimp.imgPath.size()) >= Constants.Value.IMAGE_COUNT) {
						String msg = mContext
								.getString(R.string.most_choose_num_images);
						Toast.makeText(
								mContext,
								String.format(msg, Constants.Value.IMAGE_COUNT),
								Toast.LENGTH_SHORT).show();
						return;
					}

					Intent intent = new Intent(mContext,
							PopWindowActivity.class);
					mContext.startActivityForResult(intent, 60);
				}
			});
		} else {
			if (pos < dataList.size()) {
				loadImage(position, holder.imgView);
			} else if (pos < getCount()) {

				holder.imgView.setImageBitmap(Bimp.bmp.get(pos
						- dataList.size()));
				// if (isDelete) {
				// holder.imgDelete.setVisibility(View.VISIBLE);
				// }

				// holder.imgDelete.setOnClickListener(new OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				// Bimp.bmp.remove(pos - dataList.size());
				// Bimp.imgPath.remove(pos - dataList.size());
				// notifyDataSetChanged();
				// }
				// });

			}
			holder.imgView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					isDelete = false;
					notifyDataSetChanged();
					Intent intent = new Intent(mContext,
							ImageDetailsActivity.class);
					intent.putExtra("list", dataList.toArray());
					intent.putExtra("image_position", pos);
					mContext.startActivityForResult(intent, REQUEST_CODE_SCAN);
				}
			});

			// holder.imgView.setOnLongClickListener(new OnLongClickListener() {
			//
			// @Override
			// public boolean onLongClick(View v) {
			// isDelete = !isDelete;
			// notifyDataSetChanged();
			// return false;
			// }
			// });
		}
		return convertView;
	}

	/**
	 * 开始加载图片，每张图片都会开启一个异步线程去下载。
	 */
	public void loadImage(int position, ImageView imageView) {
		if (UtilsAndroid.Sdcard.hasSDCard()) {
			String imgURl = dataList.get(position).thumbnailPath;
			// ImageLoader.getInstance().asynLoadImage(imageView, imgURl);

			ImageLoader.getInstance().displayImage(imgURl, imageView);
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.no_find_sd),
					Toast.LENGTH_SHORT).show();
		}
	}

	public boolean isShowEvery() {
		return isShowEvery;
	}

	public void setShowEvery(boolean isShowEvery) {
		this.isShowEvery = isShowEvery;
		notifyDataSetChanged();
	}

	public void setModleDelete(boolean isDelete) {
		this.isDelete = isDelete;
		notifyDataSetChanged();
	}

}
