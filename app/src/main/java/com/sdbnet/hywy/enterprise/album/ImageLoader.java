//package com.sdbnet.hywy.enterprise.album;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.HashSet;
//import java.util.Set;
//
//import com.sdbnet.hywy.enterprise.R;
//import com.sdbnet.hywy.enterprise.utils.Constants;
//import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.AsyncTask;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.util.LruCache;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//
///**
// * 对图片进行管理的工具类。
// *
// * @author Tony
// */
//public class ImageLoader {
//	/**
//	 * SD 缓存中路径
//	 */
//	public static String IMAGE_CACHE_PATH = Constants.SDBNET.PATH_PHOTO;// Environment
//	// .getExternalStorageDirectory().getPath() + "/PhotoCache/";
//	/**
//	 * 默认图片
//	 */
//	private final static int Default_Img = R.drawable.bg_load_default;
//	/**
//	 * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
//	 */
//	private static LruCache<String, Bitmap> mMemoryCache;
//
//	private Handler mHandler = new Handler(new Handler.Callback() {
//
//		@Override
//		public boolean handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			return false;
//		}
//	});
//	/**
//	 * ImageLoader的实例。
//	 */
//	private static ImageLoader mImageLoader;
//
//	public ImageLoader() {
//		createDirs();
//		init(null);
//	}
//
//	private void createDirs() {
//		File file = new File(IMAGE_CACHE_PATH);
//		if (!file.exists()) {
//			file.mkdirs();
//		}
//	}
//
//	private Context mContext;
//
//	public void init(Context context) {
//		mContext = context;
//		// 获取应用程序最大可用内存
//		int maxMemory = (int) Runtime.getRuntime().maxMemory();
//		int cacheSize = maxMemory / 8;
//		// 设置图片缓存大小为程序最大可用内存的1/8
//		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
//			@Override
//			protected int sizeOf(String key, Bitmap bitmap) {
//				// return bitmap.getByteCount();
//				return bitmap.getRowBytes() * bitmap.getHeight();
//			}
//		};
//	}
//
//	/**
//	 * 获取ImageLoader的实例。
//	 *
//	 * @return ImageLoader的实例。
//	 */
//	public static ImageLoader getInstance() {
//		if (mImageLoader == null) {
//			mImageLoader = new ImageLoader();
//		}
//		return mImageLoader;
//	}
//
//	/**
//	 * 将一张图片存储到LruCache中。
//	 *
//	 * @param key
//	 *            LruCache的键，这里传入图片的URL地址。
//	 * @param bitmap
//	 *            LruCache的键，这里传入从网络上下载的Bitmap对象。
//	 */
//	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
//		if (getBitmapFromMemoryCache(key) == null) {
//			mMemoryCache.put(key, bitmap);
//		}
//	}
//
//	/**
//	 * 从LruCache中获取一张图片，如果不存在就返回null。
//	 *
//	 * @param key
//	 *            LruCache的键，这里传入图片的URL地址。
//	 * @return 对应传入键的Bitmap对象，或者null。
//	 */
//	public Bitmap getBitmapFromMemoryCache(String key) {
//		return mMemoryCache.get(key);
//	}
//
//	public static int calculateInSampleSize(BitmapFactory.Options options,
//			int reqWidth) {
//		// 源图片的宽度
//		final int width = options.outWidth;
//		int inSampleSize = 1;
//		if (width > reqWidth) {
//			// 计算出实际宽度和目标宽度的比率
//			final int widthRatio = Math.round((float) width / (float) reqWidth);
//			inSampleSize = widthRatio;
//		}
//		return inSampleSize;
//	}
//
//	public static Bitmap decodeSampledBitmapFromResource(String pathName,
//			int reqWidth) {
//		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
//		final BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(pathName, options);
//		// 调用上面定义的方法计算inSampleSize值
//		options.inSampleSize = calculateInSampleSize(options, reqWidth);
//		// 使用获取到的inSampleSize值再次解析图片
//		options.inJustDecodeBounds = false;
//		return BitmapFactory.decodeFile(pathName, options);
//	}
//
//	/**
//	 * 记录所有正在下载或等待下载的任务。
//	 */
//	private static Set<LoadImageTask> taskCollection = new HashSet<LoadImageTask>();
//
//	public void asynLoadImage(final View view, String imgURl,
//			AsynCallBack asynCallBack) {
//		asynLoadImage(null, imgURl, asynCallBack);
//	}
//
//	public void asynLoadImage(String imgURl, AsynCallBack asynCallBack) {
//		asynLoadImage(null, imgURl, asynCallBack);
//	}
//
//	public void asynLoadImage(final View view, String imgURl) {
//		asynLoadImage(null, imgURl, new AsynCallBack() {
//
//			@Override
//			public void onFinish(Bitmap bitmap) {
//				if (bitmap != null) {
//					if (mContext == null) {
//						view.setBackground(new BitmapDrawable(bitmap));
//					} else {
//						view.setBackground(new BitmapDrawable(mContext
//								.getResources(), bitmap));
//					}
//				}
//
//			}
//		});
//	}
//
//	public void asynLoadImage(ImageView imageView, String imgURl) {
//		asynLoadImage(imageView, imgURl, null);
//	}
//
//	public void asynLoadImage(ImageView imageView, String imgURl,
//			AsynCallBack asynCallBack) {
//
//		if (imgURl == null || imgURl.isEmpty()) {
//			return;
//		}
//		this.mAsynCallBack = asynCallBack;
//		LoadImageTask task = new LoadImageTask(imageView);
//		taskCollection.add(task);
//		task.execute(imgURl);
//
//	}
//
//	private AsynCallBack mAsynCallBack;
//
//	public static interface AsynCallBack {
//		public void onFinish(Bitmap bitmap);
//	}
//
//	public void asynDisplayImage(final ImageView imageView, final File file) {
//		new Thread() {
//			Bitmap thumb;
//
//			@Override
//			public void run() {
//
//				if (file.exists()) {
//					thumb = BitmapFactory.decodeFile(file.getAbsolutePath());
//					if (thumb != null) {
//						mHandler.post(new Runnable() {
//
//							@Override
//							public void run() {
//								imageView.setImageBitmap(thumb);
//							}
//						});
//					}
//				} else {
//
//				}
//
//				// put(path, thumb);
//
//			}
//		}.start();
//	}
//
//	// public void displayImageBmp(ImageView imageView ,String )
//
//	/**
//	 * 异步下载图片的任务。
//	 *
//	 * @author guolin
//	 */
//	class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
//
//		/**
//		 * 图片的URL地址
//		 */
//		private String mImageUrl;
//
//		/**
//		 * 可重复使用的ImageView
//		 */
//		private ImageView mImageView;
//
//		public LoadImageTask() {
//		}
//
//		/**
//		 * 将可重复使用的ImageView传入
//		 *
//		 * @param imageView
//		 */
//		public LoadImageTask(ImageView imageView) {
//			mImageView = imageView;
//		}
//
//		@Override
//		protected Bitmap doInBackground(String... params) {
//			mImageUrl = params[0];
//
//			Bitmap imageBitmap = ImageLoader.getInstance()
//					.getBitmapFromMemoryCache(mImageUrl);
//			if (imageBitmap == null) {
//				imageBitmap = loadImage(mImageUrl);
//
//			}
//			return imageBitmap;
//		}
//
//		@Override
//		protected void onPostExecute(Bitmap bitmap) {
//			if (bitmap != null) {
//				addImage(bitmap);
//			}
//			if (mAsynCallBack != null) {
//				mAsynCallBack.onFinish(bitmap);
//			}
//			taskCollection.remove(this);
//		}
//
//		/**
//		 * 根据传入的URL，对图片进行加载。如果这张图片已经存在于SD卡中，则直接从SD卡里读取，否则就从网络上下载。
//		 *
//		 * @param imageUrl
//		 *            图片的URL地址
//		 * @return 加载到内存的图片。
//		 */
//		private Bitmap loadImage(String imageUrl) {
//			File imageFile = new File(getImagePath(imageUrl));
//			if (!imageFile.exists()) {
//				downloadImage(imageUrl);
//			}
//			if (imageUrl != null) {
//				try {
//					Bitmap bitmap = BitmapFactory.decodeFile(imageFile
//							.getPath());
//
//					if (bitmap != null) {
//						ImageLoader.getInstance().addBitmapToMemoryCache(
//								imageUrl, bitmap);
//						return bitmap;
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			return null;
//		}
//
//		/**
//		 * 向ImageView中添加一张图片
//		 *
//		 * @param bitmap
//		 *            待添加的图片
//		 */
//		private void addImage(Bitmap bitmap) {
//			if (mImageView != null) {
//				mImageView.setImageBitmap(bitmap);
//
//			}
//		}
//
//		/**
//		 * 将图片下载到SD卡缓存起来。
//		 *
//		 * @param imageUrl
//		 *            图片的URL地址。
//		 */
//		private void downloadImage(String imageUrl) {
//			if (Environment.getExternalStorageState().equals(
//					Environment.MEDIA_MOUNTED)) {
//
//			} else {
//
//			}
//
//			HttpURLConnection con = null;
//			FileOutputStream fos = null;
//			BufferedOutputStream bos = null;
//			BufferedInputStream bis = null;
//			File imageFile = null;
//			try {
//				URL url = new URL(imageUrl);
//				con = (HttpURLConnection) url.openConnection();
//				con.setConnectTimeout(5 * 1000);
//				con.setReadTimeout(15 * 1000);
//				con.setDoInput(true);
//				con.setDoOutput(true);
//				bis = new BufferedInputStream(con.getInputStream());
//				imageFile = new File(getImagePath(imageUrl));
//				fos = new FileOutputStream(imageFile);
//				bos = new BufferedOutputStream(fos);
//				byte[] b = new byte[1024];
//				int length;
//				while ((length = bis.read(b)) != -1) {
//					bos.write(b, 0, length);
//					bos.flush();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					if (bis != null) {
//						bis.close();
//					}
//					if (bos != null) {
//						bos.close();
//					}
//					if (con != null) {
//						con.disconnect();
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			if (imageFile != null) {
//				Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
//				if (bitmap != null) {
//					ImageLoader.getInstance().addBitmapToMemoryCache(imageUrl,
//							bitmap);
//				}
//			}
//		}
//
//		/**
//		 * 获取图片的本地存储路径。
//		 *
//		 * @param imageUrl
//		 *            图片的URL地址。
//		 * @return 图片的本地存储路径。
//		 */
//		private String getImagePath(String imageUrl) {
//			String imagePath = null;
//			// 判断图片是否是SD卡中的
//			File imageFile = new File(imageUrl);
//			if (imageFile.exists()) {
//				String newName = imageUrl.substring(
//						imageUrl.lastIndexOf("/") + 1,
//						imageUrl.lastIndexOf("."))
//						+ "jpeg";
//
//				imagePath = IMAGE_CACHE_PATH + newName;
//				UtilsAndroid.Sdcard.copyFile(imageFile, imagePath);
//			} else {
//				// 判断图片是否是SD卡缓存中的
//				int lastSlashIndex = imageUrl.lastIndexOf("/");
//				String imageName = imageUrl.substring(lastSlashIndex + 1);
//
//				imagePath = IMAGE_CACHE_PATH + imageName;
//			}
//			return imagePath;
//		}
//	}
//
//	public Bitmap revitionImageSize(String path) {
//
//		Bitmap bitmap = null;
//		try {
//			BufferedInputStream in = new BufferedInputStream(
//					new FileInputStream(new File(path)));
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inJustDecodeBounds = true;
//			BitmapFactory.decodeStream(in, null, options);
//			in.close();
//			int i = 0;
//
//			while (true) {
//				if ((options.outWidth >> i <= 256)
//						&& (options.outHeight >> i <= 256)) {
//					in = new BufferedInputStream(new FileInputStream(new File(
//							path)));
//					options.inSampleSize = (int) Math.pow(2.0D, i);
//					options.inJustDecodeBounds = false;
//					bitmap = BitmapFactory.decodeStream(in, null, options);
//					in.close();
//					break;
//				}
//				i += 1;
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return bitmap;
//	}
//
//	public void clearDiskCache() {
//		// TODO Auto-generated method stub
//		File file = new File(IMAGE_CACHE_PATH);
//		if (file.exists())
//			file.delete();
//	}
//
//	public void clearMemoryCache() {
//		if (mMemoryCache != null && mMemoryCache.size() > 0) {
//			mMemoryCache.evictAll();
//		}
//
//	}
//
//	public long getCacheSize() {
//		long size = getFileDirecorySize(new File(IMAGE_CACHE_PATH));
//
//		return size;
//	}
//
//	// /**
//	// * 获取文件大小
//	// *
//	// * @param length
//	// * @return
//	// */
//	// private static String formatFileSize(long length) {
//	// String result = null;
//	// int sub_string = 0;
//	// float size = (float) length / 1048576;
//	// if (size < 0.01) {
//	// result = "0M";
//	// } else {
//	// sub_string = String.valueOf(size).indexOf(".");
//	// result = (size + "000").substring(0, sub_string + 3) + "M";
//	// }
//	//
//	// return result;
//	// }
//
//	private long getFileDirecorySize(File file) {
//		if (file.isFile()) {
//			return file.length();
//		}
//
//		long size = 0;
//		File flist[] = file.listFiles();
//		if (flist == null) {
//			return 0;
//		}
//		for (int i = 0; i < flist.length; i++) {
//			if (flist[i].isDirectory()) {
//				size = size + getFileDirecorySize(flist[i]);
//			} else {
//				size = size + flist[i].length();
//			}
//		}
//		return size;
//
//	}
//
//}
