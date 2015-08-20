package com.sdbnet.hywy.enterprise.album;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Bimp {
	private final static String TAG = "Bimp";
	public static final int IMAGE_COUNT = 10;
	public static int loadCount = 0;
	public static int max = 0;
	public static boolean act_bool = true;
	public static List<Bitmap> bmp = new ArrayList<Bitmap>();

	// 图片sd地址 上传服务器时把图片调用下面方法压缩后 保存到临时文件夹 图片压缩后小于100KB，失真度不明显
	// public static List<String> drr = new ArrayList<String>();

	public static List<AlbumHelper.ImageItem> imgPath = new ArrayList<AlbumHelper.ImageItem>();

	public static void clearCache() {
		Bimp.bmp.clear();
		Bimp.imgPath.clear();
		// Bimp.drr.clear();
		Bimp.max = 0;
		Bimp.loadCount = 0;
		// IMAGE_COUNT=10;
	}

	/**
	 * 添加用户需要上传的图片
	 */
//	public static void addUploadImages() {
//		List<String> uploadImgPath = new ArrayList<String>();
//		// uploadImgPath.clear();
//		// 去掉重复的图片
//
//		for (ImageItem imageItem : Bimp.imgPath) {
//			String path = imageItem.imagePath;
//			path = path.substring(path.lastIndexOf("/") + 1,
//					path.lastIndexOf("."));
//			path = ImageLoader.IMAGE_CACHE_PATH + path + ".JPEG";
//			File file = new File(path);
//			Log.i(TAG, "ipath:" + path);
//			if (file.exists()) {
//				uploadImgPath.add(path);
//			}
//		}
//	}

	//
	/**
	 * 加载用户从相册选择或拍照的图片
	 */
	public static int loading() {
		Bimp.bmp.clear();
		int selectedCount = Bimp.imgPath.size() - Bimp.max;
		Log.d(TAG, "selectedCount:" + selectedCount);
		Bitmap bm = null;
		for (AlbumHelper.ImageItem imageItem : Bimp.imgPath) {
			Log.i(TAG, imageItem.toString());
			if (imageItem.thumbnailPath != null) {
				bm = BitmapFactory.decodeFile(imageItem.thumbnailPath);
				Log.d(TAG, "缩图>>" + new File(imageItem.thumbnailPath).length()
						/ 1024 + ";");
				revitionImageSize(imageItem);
			} else {
				bm = revitionImageSize(imageItem);

			}
			if (bm != null) {
				Bimp.bmp.add(bm);
				// String newStr = path.substring(path.lastIndexOf("/") + 1,
				// path.lastIndexOf("."));

			}
		}

		Bimp.max += selectedCount;
		return selectedCount;
	}

	private static Bitmap revitionImageSize(AlbumHelper.ImageItem imageItem) {

		File file = new File(imageItem.imagePath);
		Log.d(TAG, "原图>>" + file.length() / 1024 + ";");
		Bitmap bm = null;
		try {
			bm = UtilsAndroid.Media.revitionImageSize(imageItem.imagePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (file.length() > 50 * 1024) {
			// 压缩图片
			String picName = imageItem.imagePath.substring(
					imageItem.imagePath.lastIndexOf("/") + 1,
					imageItem.imagePath.lastIndexOf("."));
			imageItem.imagePath = UtilsAndroid.Sdcard.saveBitmap(bm, picName);
			Log.d(TAG, "压缩>>" + new File(imageItem.imagePath).length() / 1024
					+ ";");
		}

		return bm;
	}
}
