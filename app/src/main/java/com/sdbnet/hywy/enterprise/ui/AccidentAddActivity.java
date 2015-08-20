package com.sdbnet.hywy.enterprise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.album.AlbumHelper;
import com.sdbnet.hywy.enterprise.album.AlbumHelper.ImageItem;
import com.sdbnet.hywy.enterprise.album.Bimp;
import com.sdbnet.hywy.enterprise.album.ImageGridShowAdapter;
import com.sdbnet.hywy.enterprise.location.LocationServiceUtils;
import com.sdbnet.hywy.enterprise.location.LocationServiceUtils.IReceiveLocationHandler;
import com.sdbnet.hywy.enterprise.model.MyLocation;
import com.sdbnet.hywy.enterprise.model.ReportModel;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.ui.widget.CustomGridView;
import com.sdbnet.hywy.enterprise.ui.widget.DialogDateTimePick;
import com.sdbnet.hywy.enterprise.utils.LogUtil;
import com.sdbnet.hywy.enterprise.utils.ToastUtil;
import com.sdbnet.hywy.enterprise.utils.UtilsError;
import com.sdbnet.hywy.enterprise.utils.UtilsJava;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class AccidentAddActivity extends BaseActivity {
	private static final String TAG = "ReportAddActivity";

	private TextView mTextDate;
	private EditText mEditPlace;
	private EditText mEditExplain;
	private CustomGridView mGridPic;
	private Button mBtnSubmit;
	private ImageGridShowAdapter mGridAdapter;
	private ReportModel mReportModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActivityStackManager.getStackManager().pushActivity(this);
		setContentView(R.layout.activity_report_add);
		initBaseData();
		initUI();
		getPlaceData();
	}

	private void initBaseData() {
		mReportModel = new ReportModel();
		mReportModel.orders = getIntent().getExtras().getString(
				OrderTraceListActivity.ORDRE_NUM, "");
	}

	private void initUI() {

		mTextDate = (TextView) findViewById(R.id.activity_report_add__date_text);
		mEditPlace = (EditText) findViewById(R.id.activity_report_add_place_text);
		mEditExplain = (EditText) findViewById(R.id.activity_report_add__explain_msg_edit);
		mGridPic = (CustomGridView) findViewById(R.id.activity_report_add_pic_cgrid);
		mBtnSubmit = (Button) findViewById(R.id.activity_report_add_submit_btn);

		// 初始化GridImage适配器
		mGridAdapter = new ImageGridShowAdapter(this, null, true);
		mGridAdapter.setShowEvery(true);
		mGridPic.setAdapter(mGridAdapter);
		// Bimp.IMAGE_COUNT=5;
		((TextView) findViewById(R.id.common_view_title_text))
				.setText(R.string.accident_report);
		findViewById(R.id.common_view_title_img).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();

					}
				});
		mTextDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDateDialog(mTextDate);
			}
		});

		mBtnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				submitReport();
			}
		});
		mTextDate.setText(UtilsJava.getCurrentlyTime());
	}

	// @Override
	// protected void onResume() {
	// super.onResume();
	// getPlaceData();
	// }
	//
	// @Override
	// protected void onStart() {
	// super.onStart();
	// getPlaceData();
	// }

	private void getPlaceData() {

		LocationServiceUtils.getInstance().initLocation(this)
				.requestBDLocation(new IReceiveLocationHandler() {

					@Override
					public void onReceiveHandler(MyLocation location) {
						mReportModel.place = location.address;
						if (mEditPlace != null) {
							mEditPlace.setText(location.address);
						}
						ToastUtil.show(AccidentAddActivity.this, "Address="
								+ location.address);
						LocationServiceUtils.getInstance().abortLocation();
					}

					@Override
					public void onReceiveFail() {
						ToastUtil.show(AccidentAddActivity.this,
								"Get location failed");
						LocationServiceUtils.getInstance().abortLocation();
					}
				});

	}

	/**
	 * 添加用户需要上传的图片
	 */
	private void addUploadImages() {
		mReportModel.imgList = new ArrayList<AlbumHelper.ImageItem>();
		// 去掉重复的图片
		for (ImageItem imageItem : Bimp.imgPath) {
			String path = imageItem.imagePath;
			File file = new File(path);
			if (file.exists() && !mReportModel.imgList.contains(imageItem)) {
				mReportModel.imgList.add(imageItem);
			}
		}
	}

	private boolean checkReport() {
		// mReportModel.title = mTextTitle.getText().toString();
		mReportModel.date = mTextDate.getText().toString();
		mReportModel.explain = mEditExplain.getText().toString();
		mReportModel.place = mEditPlace.getText().toString();
		// mReportModel.theme = mEditTitle.getText().toString();
		// mReportModel.imgList = (ArrayList<ImageItem>) Bimp.imgPath;
		addUploadImages();
		// if (TextUtils.isEmpty(mReportModel.theme)) {
		// showShortToast(R.string.please_input_title);
		// return false;
		// }
		if (TextUtils.isEmpty(mReportModel.date)) {
			showShortToast(R.string.please_select_date);
			return false;
		}
		if (TextUtils.isEmpty(mReportModel.explain)) {
			showShortToast(R.string.please_explain_msg);
			return false;
		}
		return true;
	}

	private void submitReport() {
		if (!checkReport()) {
			return;
		}
		AsyncHttpService.uploadReportExection(mReportModel,
				new JsonHttpResponseHandler() {
					@Override
					public void onStart() {

						showLoading(getString(R.string.is_submitted_ellipsis));
						super.onStart();
					}

					@Override
					public void onProgress(int bytesWritten, int totalSize) {
						super.onProgress(bytesWritten, totalSize);
						Log.i("onProgress", "written=" + bytesWritten
								+ ",totalSize=" + totalSize);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						dismissLoading();
						try {
							// errcode 1 数字 错误编号
							// msg 1 字符串 错误消息
							LogUtil.d(response.toString());
							if (UtilsError.isErrorCode(
									AccidentAddActivity.this, response)) {
								return;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						showShortToast(R.string.accident_report_upload_success);
						// 先上传数据，然后再返回
						returnReport();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						dismissLoading();
						showShortToast(R.string.accident_report_upload_failed);
					}

					@Override
					public void onCancel() {
						dismissLoading();
						super.onCancel();

					}
				}, this);
	}

	// success
	private void returnReport() {
		// ReportModel test = new ReportModel();
		Bimp.clearCache();
		Intent intent = new Intent();
		intent.putExtra(AccidentCenterActivity.EXTRA_UPLOAD_REPORT,
				mReportModel);
		setResult(AccidentCenterActivity.RESULT_CODE_ADD_REPORT, intent);
		finish();
	}

	private String getItemTitle(String date) {
		return date.replace("-", "") + "情况反馈";
	}

	private void showDateDialog(TextView editText) {
		DialogDateTimePick dateTimePicKDialog = new DialogDateTimePick(this,
				editText.getText().toString().trim());
		dateTimePicKDialog.dateTimePicKDialog(editText);
	}

	@Override
	public void onDestroy() {
		Bimp.clearCache();
		super.onDestroy();
		// ActivityStackManager.getStackManager().popActivity(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "data=" + data);
		Bimp.loading();
		mGridAdapter.notifyDataSetChanged();

	}

}
