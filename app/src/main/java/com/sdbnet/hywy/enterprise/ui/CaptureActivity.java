package com.sdbnet.hywy.enterprise.ui;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.text.method.ReplacementTransformationMethod;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.UtilsError;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.camera.CaptureCallBackHandler;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.IOException;

public class CaptureActivity extends BaseActivity implements Callback,
		CaptureCallBackHandler {

	private CaptureActivityHandler handler;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.50f;
	private boolean vibrate;
	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private RelativeLayout mContainer = null;
	private RelativeLayout mCropLayout = null;
	private boolean isNeedCapture = false;
	private EditText mEditBarCode;
	private View mImgBack;
	private TextView mTextScanOrderNum;
	private LinearLayout mLlInputBarCode;

	@Override
	public boolean isNeedCapture() {
		return isNeedCapture;
	}

	public void setNeedCapture(boolean isNeedCapture) {
		this.isNeedCapture = isNeedCapture;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}

	@Override
	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int getCropWidth() {
		return cropWidth;
	}

	public void setCropWidth(int cropWidth) {
		this.cropWidth = cropWidth;
	}

	@Override
	public int getCropHeight() {
		return cropHeight;
	}

	public void setCropHeight(int cropHeight) {
		this.cropHeight = cropHeight;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_bar_code_scan);
		initCamera();
		initUI();
		initAnimation();
	}

	private void initCamera() {
		// 初始化 CameraManager
		CameraManager.init(getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	private void initUI() {
		mContainer = (RelativeLayout) findViewById(R.id.activity_bar_code_scan_capture_containter);
		mCropLayout = (RelativeLayout) findViewById(R.id.activity_bar_code_scan_capture_crop_layout);
		mLlInputBarCode = (LinearLayout) findViewById(R.id.activity_bar_code_scan_lay_input_bar_code);
		mLlInputBarCode.getBackground().setAlpha(80);

		mTextScanOrderNum = (TextView) findViewById(R.id.activity_bar_code_scan_tv_scan_order_number);
		mEditBarCode = (EditText) findViewById(R.id.activity_bar_code_scan_et_bar_code);
		mEditBarCode.setTransformationMethod(new AllCapTransformationMethod());
		mEditBarCode.setKeyListener(new MyNumberKeyListener());

		mImgBack = findViewById(R.id.common_view_title_img);
		mImgBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView) findViewById(R.id.common_view_title_text))
				.setText(R.string.menuScan);

	}

	private void initAnimation() {
		// 设置扫描线动画
		ImageView mQrLineView = (ImageView) findViewById(R.id.activity_bar_code_scan_capture_scan_line);
		TranslateAnimation mAnimation = new TranslateAnimation(
				Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, 0f,
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0.9f);
		mAnimation.setDuration(1500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mAnimation.setInterpolator(new LinearInterpolator());
		mQrLineView.setAnimation(mAnimation);
	}

	boolean flag = true;
	private String scanOrderNumber;

	protected void light() {
		if (flag == true) {
			flag = false;
			// 开闪光灯
			CameraManager.get().openLight();
		} else {
			flag = true;
			// 关闪光灯
			CameraManager.get().offLight();
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		mTextScanOrderNum.setText("");
		mTextScanOrderNum.setBackground(null);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.activity_bar_code_scan_capture_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			// 铃音非正常模式
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	public void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	/**
	 * 查看扫描订单
	 * 
	 * @param view
	 */
	public void lookForScanOrder(View view) {
		if (TextUtils.isEmpty(scanOrderNumber)) {
			if (TextUtils.isEmpty(mEditBarCode.getText().toString().trim())) {
				showShortToast(R.string.please_input_order_num);
				// Toast.makeText(this, "请先输入订单号", Toast.LENGTH_SHORT).show();
				return;
			} else {
				scanOrderNumber = mEditBarCode.getText().toString().trim();
			}
		}

		if (scanOrderNumber.length() != PreferencesUtil.code_length) {
			scanOrderNumber = "";
			String msg = String.format("订单号长度不是%d个字符，不符合长度要求",
					PreferencesUtil.code_length);
			showShortToast(msg);

			retryScan();
			return;
		}

		// scanOrderNumber =
		// scanOrderNumber.replace(PreferencesUtil.user_company +
		// PreferencesUtil.item_id, "");
		// scanOrderNumber =
		// scanOrderNumber.replace(PreferencesUtil.user_company.toLowerCase() +
		// PreferencesUtil.item_id.toLowerCase(), "");
		scanOrderNumber = scanOrderNumber.replace("-", "").toUpperCase();
		System.out.println("scanOrderNumber " + scanOrderNumber);
		mTextScanOrderNum.setText(scanOrderNumber);
		mTextScanOrderNum.setBackgroundResource(R.drawable.bg_line);

		String sysnox = getOriginalPlatformOrderId(); // 拼接平台订单号
		AsyncHttpService.traceOrder(sysnox, new JsonHttpResponseHandler() {
			private int errCode;
			private String msg;

			@Override
			public void onStart() {
				super.onStart();
				showLoading(getString(R.string.xlistview_header_hint_loading));
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				dismissLoading();
				scanOrderNumber = "";
				retryScan();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				System.out.println("response:" + response.toString());
				super.onSuccess(statusCode, headers, response);
				try {
					errCode = response.getInt(Constants.Feild.KEY_ERROR_CODE);
					if (UtilsError.isErrorCode(CaptureActivity.this, response)) {

						return;
					}

					// JSONObject jsonObject =
					// response.getJSONObject(Constants.Feild.KEY_ORDER);
					PreferencesUtil.putValue(PreferencesUtil.KEY_SCAN_ACTIONS,
							response.getString(Constants.Feild.KEY_ORDER));

					// 跳转到订单跟踪日志界面
					Intent data = new Intent(CaptureActivity.this,
							OrderTraceListActivity.class);
					data.putExtra(Constants.Feild.KEY_ORDER_NO, scanOrderNumber);
					startActivity(data);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					scanOrderNumber = "";
					dismissLoading();
				}
			}
		}, CaptureActivity.this);

		mEditBarCode.setText("");
		// finish();
	}

	/**
	 * 获取原始平台订单号
	 * 
	 * @return
	 */
	private String getOriginalPlatformOrderId() {
		return PreferencesUtil.user_company + PreferencesUtil.item_id + "-"
				+ scanOrderNumber;
	}

	/**
	 * 处理扫描结果
	 */
	@Override
	public void handleDecode(String result) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		scanOrderNumber = result;
		lookForScanOrder(null);
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);

			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;

			int x = mCropLayout.getLeft() * width / mContainer.getWidth();
			int y = mCropLayout.getTop() * height / mContainer.getHeight();

			int cropWidth = mCropLayout.getWidth() * width
					/ mContainer.getWidth();
			int cropHeight = mCropLayout.getHeight() * height
					/ mContainer.getHeight();

			setX(x);
			setY(y);
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);
			// 设置是否需要截图
			// setNeedCapture(true);

		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(CaptureActivity.this);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	@Override
	public Handler getHandler() {
		return handler;
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private void retryScan() {
		// 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
		if (handler != null)
			handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	public class AllCapTransformationMethod extends
			ReplacementTransformationMethod {

		@Override
		protected char[] getOriginal() {
			char[] aa = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
					'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
					'w', 'x', 'y', 'z' };
			return aa;
		}

		@Override
		protected char[] getReplacement() {
			char[] cc = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
					'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
					'W', 'X', 'Y', 'Z' };
			return cc;
		}
	}

	private class MyNumberKeyListener extends NumberKeyListener {

		@Override
		protected char[] getAcceptedChars() {
			char[] numberChars = new char[] { 'a', 'b', 'c', 'd', 'e', 'f',
					'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
					's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
					'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
					'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2',
					'3', '4', '5', '6', '7', '8', '9', '0' };
			return numberChars;
		}

		@Override
		public int getInputType() {
			// TODO Auto-generated method stub
			return InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
		}

	}
	//
	// private void addOrderData(String orderId) {
	//
	// if (scanList.contains(orderId)) {
	// showShortToast(R.string.order_alreader_add);
	// // 延迟1秒，减少扫描的过分灵敏度
	// handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
	// } else if (scanList.size() < Constants.Value.SCAN_COUNT) {
	//
	// scanList.add(0, orderId);
	// OperateLogUtil.saveOperate(this, "扫描单号：" + orderId, null);
	// Set<String> set = new HashSet<String>();
	// set.addAll(scanList);
	//
	// String msg = String.format(getString(R.string.added_x_list),
	// PreferencesUtil.ordtitle);
	// // "已添加到" + PreferencesUtil.ordtitle + "列表中",
	// showShortToast(msg);
	// // 延迟1秒，减少扫描的过分灵敏度
	// handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
	// } else {
	// showShortToast(R.string.one_time_add_100);
	// }
	//
	// mTextScanCount.setText(String.format(getString(R.string.barcode_total),
	// PreferencesUtil.ordtitle, scanList.size()));
	// mTextOrderNum.setText(scanList.get(0));
	// mTextOrderNum.setBackgroundResource(R.drawable.bg_line);
	//
	// }
	//
	// private void barCodeScan(String barCode) {
	// barCode = barCode.replace("-", "").toUpperCase();
	// addOrderData(barCode);
	// // 延迟1秒，减少扫描的过分灵敏度
	// handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
	// }
	//
	// // 二维码判断;
	// private boolean QRcodeScan(String jsonQR) {
	// // {"cmpid":"8888","itemid":"01",
	// // "ordernos":[{"orderno":"1234567890123"},
	// // {"orderno":"1234567890123"},
	// // {"orderno":"1234567890123"},..]}
	//
	// try {
	// JSONObject jsonObject = new JSONObject(jsonQR);
	// String cmpid = jsonObject.getString("cmpid");
	// String itemid = jsonObject.getString("itemid");
	// JSONArray jsonOrdernos = jsonObject.getJSONArray("ordernos");
	// if (scanList.size() + jsonOrdernos.length() > Constants.Value.SCAN_COUNT)
	// {
	// showShortToast(R.string.one_time_add_100);
	// } else {
	// for (int i = 0; i < jsonOrdernos.length(); i++) {
	// JSONObject jsonOrder = (JSONObject) jsonOrdernos.get(i);
	// String orderno = jsonOrder.getString("orderno")
	// .toUpperCase();
	// if (!scanList.contains(orderno.toUpperCase())) {
	// scanList.add(0, orderno.toUpperCase());
	// }
	// }
	// }
	// mTextScanCount.setText(String.format(
	// getString(R.string.barcode_total),
	// PreferencesUtil.ordtitle, scanList.size()));
	// mTextOrderNum.setText(scanList.get(0));
	// mTextOrderNum.setBackgroundResource(R.drawable.bg_line);
	// if (scanList.size() < Constants.Value.SCAN_COUNT) {
	// // 延迟1秒，减少扫描的过分灵敏度
	// handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
	// }
	//
	// } catch (Exception ex) {
	// handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
	// String msg = "不是正确的二维码格式或"
	// + String.format("%s号长度不是%d个字符，不符合长度要求",
	// PreferencesUtil.ordtitle,
	// PreferencesUtil.code_length);
	// showShortToast(msg);
	// ex.printStackTrace();
	// return false;
	// }
	// return true;
	//
	// }
}
